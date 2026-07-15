package de.fh.erfurt.moviemate.presentation.movies.backup

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.movies.presentation.backup.BackupUiState
import de.fh.erfurt.moviemate.presentation.app.movieMateViewModelFactory
import de.fh.erfurt.moviemate.presentation.app.throwIfCancellation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Composable
fun BackupRoute(
    appContainer: AppContainer?,
    syncCoordinator: BackupSyncCoordinator?,
    authorizationHandler: GoogleDriveAuthorizationHandler?,
    modifier: Modifier = Modifier,
    ioDispatcher: CoroutineDispatcher,
) {
    if (appContainer == null || syncCoordinator == null || authorizationHandler == null) {
        BackupScreen(
            state = BackupUiState(errorMessage = PreviewUnavailableMessage),
            onAction = {},
            modifier = modifier,
        )
        return
    }

    val context = LocalContext.current
    val viewModel: BackupScreenViewModel = viewModel(
        factory = movieMateViewModelFactory {
            BackupScreenViewModel(
                exportBackupUseCase = appContainer.exportBackupUseCase,
                importBackupUseCase = appContainer.importBackupUseCase,
                syncCoordinator = syncCoordinator,
                ioDispatcher = ioDispatcher,
            )
        }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var pendingExportJson by remember { mutableStateOf<String?>(null) }
    var pendingExportUri by remember { mutableStateOf<Uri?>(null) }
    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }

    val exportDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(BackupMimeType)
    ) { uri ->
        if (uri == null) {
            pendingExportJson = null
            pendingExportUri = null
            viewModel.onAction(BackupAction.ExportCancelled)
        } else {
            pendingExportUri = uri
        }
    }

    val importDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> pendingImportUri = uri }

    val authorizationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        val intent = activityResult.data
        if (activityResult.resultCode != Activity.RESULT_OK || intent == null) {
            viewModel.onAction(BackupAction.AuthorizationCancelled)
            return@rememberLauncherForActivityResult
        }

        authorizationHandler.authorizationResult(intent)
            .onSuccess { account ->
                viewModel.onAction(BackupAction.AuthorizationSucceeded(account))
            }
            .onFailure { throwable ->
                viewModel.onAction(
                    BackupAction.AuthorizationFailed(
                        throwable.backupMessage("Google Drive authorization failed.")
                    )
                )
            }
    }

    LaunchedEffect(
        viewModel,
        exportDocumentLauncher,
        importDocumentLauncher,
        authorizationLauncher,
        authorizationHandler,
    ) {
        viewModel.effect.collect { effect ->
            when (effect) {
                BackupScreenEffect.AuthorizeGoogleDrive -> {
                    authorizationHandler.authorize(
                        preferredAccountName = null,
                        forceAccountSelection = true,
                        onResolutionRequired = { pendingIntent ->
                            runCatching {
                                authorizationLauncher.launch(
                                    IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                                )
                            }.onFailure { throwable ->
                                viewModel.onAction(
                                    BackupAction.AuthorizationFailed(
                                        throwable.backupMessage("Google Drive authorization could not be opened.")
                                    )
                                )
                            }
                        },
                        onAuthorized = { account ->
                            viewModel.onAction(BackupAction.AuthorizationSucceeded(account))
                        },
                        onFailure = { message ->
                            viewModel.onAction(BackupAction.AuthorizationFailed(message))
                        },
                    )
                }

                is BackupScreenEffect.RevokeGoogleDriveAccess -> {
                    authorizationHandler.revoke(
                        accountName = effect.accountName,
                        onSuccess = {
                            viewModel.onAction(BackupAction.SignOutCompleted)
                        },
                        onFailure = { message ->
                            viewModel.onAction(BackupAction.SignOutFailed(message))
                        },
                    )
                }

                is BackupScreenEffect.CreateExportDocument -> {
                    pendingExportJson = effect.json
                    exportDocumentLauncher.launch(DefaultBackupFileName)
                }

                BackupScreenEffect.OpenImportDocument -> {
                    importDocumentLauncher.launch(BackupImportMimeTypes)
                }
            }
        }
    }

    LaunchedEffect(pendingExportUri, pendingExportJson) {
        val uri = pendingExportUri ?: return@LaunchedEffect
        val json = pendingExportJson ?: return@LaunchedEffect
        runCatching {
            writeTextToUri(context, uri, json, ioDispatcher)
        }.onSuccess {
            viewModel.onAction(BackupAction.ExportSaved)
        }.onFailure { throwable ->
            throwable.throwIfCancellation()
            viewModel.onAction(
                BackupAction.PlatformFailure(throwable.backupMessage("Backup file could not be saved."))
            )
        }
        pendingExportUri = null
        pendingExportJson = null
    }

    LaunchedEffect(pendingImportUri) {
        val uri = pendingImportUri ?: return@LaunchedEffect
        runCatching {
            readTextFromUri(context, uri, ioDispatcher)
        }.onSuccess { json ->
            viewModel.onAction(BackupAction.ImportContentLoaded(json))
        }.onFailure { throwable ->
            throwable.throwIfCancellation()
            viewModel.onAction(
                BackupAction.PlatformFailure(throwable.backupMessage("Backup file could not be read."))
            )
        }
        pendingImportUri = null
    }

    BackupScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

private suspend fun writeTextToUri(
    context: Context,
    uri: Uri,
    text: String,
    ioDispatcher: CoroutineDispatcher,
) {
    withContext(ioDispatcher) {
        val outputStream = context.contentResolver.openOutputStream(uri)
            ?: error("Selected backup file could not be opened.")
        outputStream.use { stream -> stream.write(text.toByteArray(Charsets.UTF_8)) }
    }
}

private suspend fun readTextFromUri(
    context: Context,
    uri: Uri,
    ioDispatcher: CoroutineDispatcher,
): String {
    return withContext(ioDispatcher) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: error("Selected backup file could not be opened.")
        inputStream.bufferedReader(Charsets.UTF_8).use { reader -> reader.readText() }
    }
}

private fun Throwable.backupMessage(fallback: String): String {
    return message?.takeIf(String::isNotBlank) ?: fallback
}

private const val BackupMimeType = "application/json"
private const val DefaultBackupFileName = "moviemate-backup.json"
private const val PreviewUnavailableMessage = "Backup is only available in the running app."

private val BackupImportMimeTypes = arrayOf(
    "application/json",
    "text/json",
    "text/plain",
    "*/*",
)
