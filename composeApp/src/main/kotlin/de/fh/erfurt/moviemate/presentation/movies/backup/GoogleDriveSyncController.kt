package de.fh.erfurt.moviemate.presentation.movies.backup

import android.accounts.Account
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.RevokeAccessRequest
import com.google.android.gms.common.api.Scope
import de.fh.erfurt.moviemate.movies.domain.sync.SyncAccountMismatchException
import de.fh.erfurt.moviemate.movies.presentation.backup.BackupAccountState
import de.fh.erfurt.moviemate.presentation.app.throwIfCancellation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GoogleDriveAuthorizedAccount(
    val accessToken: String,
    val accountId: String,
    val accountName: String?,
    val displayName: String?,
    val email: String?,
)

data class GoogleDriveStoredSession(
    val accountId: String,
    val accountName: String?,
    val displayName: String?,
    val email: String?,
    val lastSyncEpochMillis: Long?,
)

data class GoogleDriveSyncState(
    val accountState: BackupAccountState = BackupAccountState.SignedOut,
    val accountId: String? = null,
    val accountName: String? = null,
    val accountDisplayName: String? = null,
    val accountEmail: String? = null,
    val isSyncing: Boolean = false,
    val isSigningOut: Boolean = false,
    val lastSyncEpochMillis: Long? = null,
    val errorMessage: String? = null,
)

interface BackupSyncCoordinator {
    val state: StateFlow<GoogleDriveSyncState>

    fun storedSession(): GoogleDriveStoredSession?
    fun authorizationStarted()
    fun authorizationSucceeded(account: GoogleDriveAuthorizedAccount)
    fun authorizationCancelled(preserveStoredSession: Boolean = false)
    fun authorizationFailed(message: String, preserveStoredSession: Boolean = false)
    fun syncNow()
    fun signOutStarted()
    fun signOutCompleted()
    fun signOutFailed(message: String)
}

class GoogleDriveSyncController(
    context: Context,
    private val coroutineScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val syncBackup: suspend (accessToken: String, accountId: String) -> Unit,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) : BackupSyncCoordinator {
    private val sessionStore = GoogleDriveSessionStore(context.applicationContext)
    private val _state = MutableStateFlow(sessionStore.load().toSignedOutState())
    override val state: StateFlow<GoogleDriveSyncState> = _state.asStateFlow()

    private var accessToken: String? = null
    private var syncJob: Job? = null
    private var pendingAccount: GoogleDriveAuthorizedAccount? = null
    private var sessionBeforeAuthorization: GoogleDriveStoredSession? = null

    override fun storedSession(): GoogleDriveStoredSession? = sessionStore.load()

    override fun authorizationStarted() {
        if (_state.value.accountState == BackupAccountState.SigningIn) return
        _state.update {
            it.copy(
                accountState = BackupAccountState.SigningIn,
                errorMessage = null,
            )
        }
    }

    override fun authorizationSucceeded(account: GoogleDriveAuthorizedAccount) {
        accessToken = account.accessToken
        sessionBeforeAuthorization = sessionStore.load()
        pendingAccount = account
        val existingLastSync = sessionBeforeAuthorization
            ?.takeIf { stored -> stored.accountId == account.accountId }
            ?.lastSyncEpochMillis
        _state.value = GoogleDriveSyncState(
            accountState = BackupAccountState.SignedIn,
            accountId = account.accountId,
            accountName = account.accountName,
            accountDisplayName = account.displayName,
            accountEmail = account.email,
            lastSyncEpochMillis = existingLastSync,
        )
        syncNow()
    }

    override fun authorizationCancelled(preserveStoredSession: Boolean) {
        accessToken = null
        pendingAccount = null
        sessionBeforeAuthorization = null
        if (!preserveStoredSession) sessionStore.clear()
        _state.value = sessionStore.load().toSignedOutState()
    }

    override fun authorizationFailed(message: String, preserveStoredSession: Boolean) {
        accessToken = null
        pendingAccount = null
        sessionBeforeAuthorization = null
        if (!preserveStoredSession) sessionStore.clear()
        _state.value = sessionStore.load().toSignedOutState().copy(errorMessage = message)
    }

    override fun syncNow() {
        if (_state.value.isSyncing || _state.value.isSigningOut) return

        val token = accessToken ?: return
        val accountId = _state.value.accountId ?: return
        syncJob?.cancel()
        syncJob = coroutineScope.launch {
            _state.update {
                it.copy(
                    isSyncing = true,
                    errorMessage = null,
                )
            }
            runCatching {
                withContext(ioDispatcher) { syncBackup(token, accountId) }
            }.onSuccess {
                val syncedAt = currentTimeMillis()
                val accountWaitingForValidation = pendingAccount
                if (accountWaitingForValidation == null) {
                    sessionStore.updateLastSync(syncedAt)
                } else {
                    sessionStore.save(accountWaitingForValidation, syncedAt)
                    pendingAccount = null
                    sessionBeforeAuthorization = null
                }
                _state.update {
                    it.copy(
                        isSyncing = false,
                        lastSyncEpochMillis = syncedAt,
                        errorMessage = null,
                    )
                }
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                if (throwable is SyncAccountMismatchException) {
                    val previousSession = sessionBeforeAuthorization
                    accessToken = null
                    pendingAccount = null
                    sessionBeforeAuthorization = null
                    _state.value = previousSession.toSignedOutState().copy(
                        errorMessage = throwable.syncMessage(),
                    )
                } else {
                    _state.update {
                        it.copy(
                            isSyncing = false,
                            errorMessage = throwable.syncMessage(),
                        )
                    }
                }
            }
        }
    }

    override fun signOutStarted() {
        if (_state.value.accountState != BackupAccountState.SignedIn || _state.value.isSigningOut) return
        syncJob?.cancel()
        syncJob = null
        _state.update {
            it.copy(
                isSyncing = false,
                isSigningOut = true,
                errorMessage = null,
            )
        }
    }

    override fun signOutCompleted() {
        accessToken = null
        pendingAccount = null
        sessionBeforeAuthorization = null
        sessionStore.clear()
        _state.value = GoogleDriveSyncState()
    }

    override fun signOutFailed(message: String) {
        _state.update {
            it.copy(
                isSigningOut = false,
                errorMessage = message,
            )
        }
    }
}

class GoogleDriveAuthorizationHandler(context: Context) {
    private val authorizationClient = Identity.getAuthorizationClient(context.applicationContext)
    private val requestedScopes = listOf(Scope(GoogleDriveAppDataScope))

    fun authorize(
        preferredAccountName: String?,
        forceAccountSelection: Boolean,
        onResolutionRequired: (PendingIntent) -> Unit,
        onAuthorized: (GoogleDriveAuthorizedAccount) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val requestBuilder = AuthorizationRequest.builder()
            .setRequestedScopes(requestedScopes)

        when {
            forceAccountSelection -> requestBuilder.setPrompt(AuthorizationRequest.Prompt.SELECT_ACCOUNT)
            !preferredAccountName.isNullOrBlank() -> requestBuilder.setAccount(
                Account(preferredAccountName, GoogleAccountType)
            )
        }

        authorizationClient.authorize(requestBuilder.build())
            .addOnSuccessListener { result ->
                if (result.hasResolution()) {
                    val pendingIntent = result.pendingIntent
                    if (pendingIntent == null) {
                        onFailure("Google Drive authorization could not be opened.")
                    } else {
                        onResolutionRequired(pendingIntent)
                    }
                } else {
                    runCatching { result.toAuthorizedAccount() }
                        .onSuccess(onAuthorized)
                        .onFailure { throwable ->
                            onFailure(throwable.authorizationMessage())
                        }
                }
            }
            .addOnFailureListener { throwable ->
                onFailure(throwable.authorizationMessage())
            }
    }

    fun authorizationResult(intent: Intent): Result<GoogleDriveAuthorizedAccount> {
        return runCatching {
            authorizationClient.getAuthorizationResultFromIntent(intent).toAuthorizedAccount()
        }
    }

    fun revoke(
        accountName: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        if (accountName.isNullOrBlank()) {
            onSuccess()
            return
        }

        val request = RevokeAccessRequest.builder()
            .setAccount(Account(accountName, GoogleAccountType))
            .setScopes(requestedScopes)
            .build()
        authorizationClient.revokeAccess(request)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { throwable ->
                onFailure(throwable.authorizationMessage("Google Drive could not be disconnected."))
            }
    }
}

@Suppress("DEPRECATION")
private fun AuthorizationResult.toAuthorizedAccount(): GoogleDriveAuthorizedAccount {
    val token = accessToken?.takeIf(String::isNotBlank)
        ?: error("Google Drive did not return an access token.")
    require(grantedScopes.contains(GoogleDriveAppDataScope)) {
        "Google Drive app-data access was not granted."
    }

    val googleAccount = toGoogleSignInAccount()
        ?: error("Google Drive did not return an account.")
    val email = googleAccount.email?.takeIf(String::isNotBlank)
    val accountName = googleAccount.account?.name?.takeIf(String::isNotBlank) ?: email
    // AuthorizationClient may omit profile id and email for a drive.appdata-only grant.
    // The shared sync layer validates and binds the canonical Drive permission id, so this
    // platform value is only session metadata used to restore the selected Android account.
    val accountId = googleAccount.id?.takeIf(String::isNotBlank)
        ?: email
        ?: accountName
        ?: AuthorizedDriveAccountFallback

    return GoogleDriveAuthorizedAccount(
        accessToken = token,
        accountId = accountId,
        accountName = accountName,
        displayName = googleAccount.displayName,
        email = email,
    )
}

private class GoogleDriveSessionStore(context: Context) {
    private val preferences = context.getSharedPreferences(SessionPreferencesName, Context.MODE_PRIVATE)

    fun load(): GoogleDriveStoredSession? {
        val accountId = preferences.getString(AccountIdKey, null)?.takeIf(String::isNotBlank)
            ?: return null
        val accountName = preferences.getString(AccountNameKey, null)?.takeIf(String::isNotBlank)
        val lastSync = preferences.getLong(LastSyncKey, MissingTimestamp).takeUnless { it == MissingTimestamp }
        return GoogleDriveStoredSession(
            accountId = accountId,
            accountName = accountName,
            displayName = preferences.getString(DisplayNameKey, null),
            email = preferences.getString(EmailKey, null),
            lastSyncEpochMillis = lastSync,
        )
    }

    fun save(account: GoogleDriveAuthorizedAccount, lastSyncEpochMillis: Long?) {
        preferences.edit()
            .putString(AccountIdKey, account.accountId)
            .putString(AccountNameKey, account.accountName?.takeIf(String::isNotBlank))
            .putString(DisplayNameKey, account.displayName)
            .putString(EmailKey, account.email)
            .apply {
                if (lastSyncEpochMillis == null) remove(LastSyncKey) else putLong(LastSyncKey, lastSyncEpochMillis)
            }
            .apply()
    }

    fun updateLastSync(timestamp: Long) {
        preferences.edit().putLong(LastSyncKey, timestamp).apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }
}

private fun GoogleDriveStoredSession?.toSignedOutState(): GoogleDriveSyncState {
    return GoogleDriveSyncState(
        accountState = BackupAccountState.SignedOut,
        accountId = this?.accountId,
        accountName = this?.accountName,
        accountDisplayName = this?.displayName,
        accountEmail = this?.email,
        lastSyncEpochMillis = this?.lastSyncEpochMillis,
    )
}

private fun Throwable.syncMessage(): String {
    return when (this) {
        is SyncAccountMismatchException -> {
            "This library is already linked to a different Google account. Please use the originally linked account."
        }

        else -> message?.takeIf(String::isNotBlank) ?: "Google Drive sync failed."
    }
}

private fun Throwable.authorizationMessage(
    fallback: String = "Google Drive authorization failed."
): String {
    return message?.takeIf(String::isNotBlank) ?: fallback
}

const val GoogleDriveAppDataScope = "https://www.googleapis.com/auth/drive.appdata"
private const val GoogleAccountType = "com.google"
private const val AuthorizedDriveAccountFallback = "authorized-google-drive-account"
private const val SessionPreferencesName = "google-drive-sync-session"
private const val AccountIdKey = "account-id"
private const val AccountNameKey = "account-name"
private const val DisplayNameKey = "display-name"
private const val EmailKey = "email"
private const val LastSyncKey = "last-sync"
private const val MissingTimestamp = Long.MIN_VALUE
