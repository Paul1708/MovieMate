package de.fh.erfurt.moviemate.presentation.movies.backup

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.movies.presentation.backup.BackupAccountState
import de.fh.erfurt.moviemate.movies.presentation.backup.BackupUiState
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenBackground
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenDefaults
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing
import java.text.DateFormat
import java.util.Date

@Composable
fun BackupScreen(
    state: BackupUiState,
    onAction: (BackupAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MovieMateScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MovieMateScreenDefaults.HorizontalPadding)
                .padding(top = MovieMateScreenDefaults.VerticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BackupTopBar()

            Spacer(modifier = Modifier.height(MovieMateScreenDefaults.ContentTopSpacing))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = MovieMateSpacing.sectionSpacing),
                verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.itemSpacing),
            ) {
                GoogleDriveAccountCard(
                    state = state,
                    onAction = onAction,
                )

                BackupSectionTitle(text = "Manual backup")

                BackupActionCard(
                    title = "Export",
                    subtitle = "Export your movie list and categories as a backup file.",
                    icon = MovieMateIcons.uploadFile,
                    contentDescription = "Export backup",
                    enabled = !state.isBusy,
                    onClick = { onAction(BackupAction.ExportClicked) },
                )

                BackupActionCard(
                    title = "Import",
                    subtitle = "Import a previous MovieMate backup.",
                    icon = MovieMateIcons.download,
                    contentDescription = "Import backup",
                    enabled = !state.isBusy,
                    onClick = { onAction(BackupAction.ImportClicked) },
                )

                ManualBackupStatus(state = state)
            }
        }
    }
}

@Composable
private fun BackupTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Backup & Sync",
            style = MaterialTheme.typography.headlineSmall,
            color = MovieMateColors.PrimaryText,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun GoogleDriveAccountCard(
    state: BackupUiState,
    onAction: (BackupAction) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MovieMateShapes.cardRadius),
        color = MovieMateColors.CardBackgroundElevated,
        border = BorderStroke(
            width = MovieMateSpacing.dividerThickness,
            color = MovieMateColors.CardBorder,
        ),
    ) {
        Column(
            modifier = Modifier.padding(MovieMateSpacing.detailCardPadding),
            verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
        ) {
            GoogleDriveAccountHeader(state = state)

            when (state.accountState) {
                BackupAccountState.SignedOut -> SignedOutContent(
                    errorMessage = state.syncErrorMessage,
                    onSignInClicked = { onAction(BackupAction.SignInClicked) },
                )

                BackupAccountState.SigningIn -> SigningInContent()
                BackupAccountState.SignedIn -> SignedInContent(
                    state = state,
                    onSyncClicked = { onAction(BackupAction.SyncClicked) },
                    onSignOutClicked = { onAction(BackupAction.SignOutClicked) },
                )
            }
        }
    }
}

@Composable
private fun GoogleDriveAccountHeader(state: BackupUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(MovieMateSpacing.backupActionIconSize)
                .background(MovieMateColors.Accent, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            MovieMateIcon(
                icon = MovieMateIcons.backup,
                tint = MovieMateColors.PrimaryText,
                modifier = Modifier.size(MovieMateSpacing.backupActionGlyphSize),
                contentDescription = "Google Drive sync",
            )
        }

        Spacer(modifier = Modifier.width(MovieMateSpacing.detailMediumSpacing))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Google Drive",
                style = MaterialTheme.typography.titleSmall,
                color = MovieMateColors.PrimaryText,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = state.accountStatusText(),
                style = MaterialTheme.typography.bodySmall,
                color = state.accountStatusColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SignedOutContent(
    errorMessage: String?,
    onSignInClicked: () -> Unit,
) {
    Text(
        text = "Connect your Google account to keep movies, categories, and their contents in sync across devices.",
        style = MaterialTheme.typography.bodySmall,
        color = MovieMateColors.SecondaryText,
    )

    if (errorMessage != null) {
        SyncErrorText(message = errorMessage)
    }

    Button(
        onClick = onSignInClicked,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MovieMateColors.Accent,
            contentColor = MovieMateColors.PrimaryText,
        ),
    ) {
        Text("Connect Google Drive")
    }
}

@Composable
private fun SigningInContent() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MovieMateSpacing.compactSpacing),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(SmallProgressSize),
            color = MovieMateColors.AccentLight,
            strokeWidth = SmallProgressStroke,
        )
        Spacer(modifier = Modifier.width(MovieMateSpacing.compactSpacing))
        Text(
            text = "Connecting to Google Drive...",
            style = MaterialTheme.typography.bodySmall,
            color = MovieMateColors.SecondaryText,
        )
    }
}

@Composable
private fun SignedInContent(
    state: BackupUiState,
    onSyncClicked: () -> Unit,
    onSignOutClicked: () -> Unit,
) {
    val accountEmail = state.accountEmail
    val syncErrorMessage = state.syncErrorMessage
    val lastSyncEpochMillis = state.lastSyncEpochMillis
    val accountLabel = state.accountDisplayName
        ?.takeIf(String::isNotBlank)
        ?: accountEmail
        ?.takeIf(String::isNotBlank)
        ?: "Google account"
    Text(
        text = accountLabel,
        style = MaterialTheme.typography.bodyMedium,
        color = MovieMateColors.PrimaryText,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    if (!accountEmail.isNullOrBlank() && accountEmail != accountLabel) {
        Text(
            text = accountEmail,
            style = MaterialTheme.typography.bodySmall,
            color = MovieMateColors.SecondaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

    when {
        state.isSyncing -> InlineProgress(message = "Syncing movies and categories...")
        syncErrorMessage != null -> SyncErrorText(message = syncErrorMessage)
        lastSyncEpochMillis != null -> Text(
            text = "Last sync: ${formatSyncTime(lastSyncEpochMillis)}",
            style = MaterialTheme.typography.bodySmall,
            color = MovieMateColors.SecondaryText,
        )
        else -> Text(
            text = "Ready to sync",
            style = MaterialTheme.typography.bodySmall,
            color = MovieMateColors.SecondaryText,
        )
    }

    Button(
        onClick = onSyncClicked,
        enabled = !state.isSyncing && !state.isSigningOut,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MovieMateColors.Accent,
            contentColor = MovieMateColors.PrimaryText,
        ),
    ) {
        Text(if (state.isSyncing) "Syncing..." else "Sync now")
    }

    Text(
        text = "Disconnecting stops sync, but this library remains linked to its original Google account.",
        style = MaterialTheme.typography.labelSmall,
        color = MovieMateColors.MutedText,
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(
            onClick = onSignOutClicked,
            enabled = !state.isSyncing && !state.isSigningOut,
        ) {
            Text(if (state.isSigningOut) "Disconnecting..." else "Disconnect Google Drive")
        }
    }
}

@Composable
private fun InlineProgress(message: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(
            modifier = Modifier.size(SmallProgressSize),
            color = MovieMateColors.AccentLight,
            strokeWidth = SmallProgressStroke,
        )
        Spacer(modifier = Modifier.width(MovieMateSpacing.compactSpacing))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MovieMateColors.SecondaryText,
        )
    }
}

@Composable
private fun SyncErrorText(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = MovieMateColors.Rating,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun BackupSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MovieMateColors.SecondaryText,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MovieMateSpacing.detailTinySpacing),
    )
}

@Composable
private fun BackupActionCard(
    title: String,
    subtitle: String,
    @DrawableRes icon: Int,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(MovieMateSpacing.backupActionCardHeight)
            .alpha(if (enabled) 1f else DisabledActionAlpha)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        shape = RoundedCornerShape(MovieMateShapes.cardRadius),
        color = MovieMateColors.CardBackgroundElevated,
        border = BorderStroke(
            width = MovieMateSpacing.dividerThickness,
            color = MovieMateColors.CardBorder,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MovieMateSpacing.detailCardPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackupActionIcon(icon = icon)

            Spacer(modifier = Modifier.width(MovieMateSpacing.detailMediumSpacing))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MovieMateColors.PrimaryText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MovieMateColors.SecondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = MovieMateSpacing.detailMicroSpacing),
                )
            }

            Spacer(modifier = Modifier.width(MovieMateSpacing.compactSpacing))

            MovieMateIcon(
                icon = MovieMateIcons.chevronRight,
                tint = MovieMateColors.PrimaryText,
                modifier = Modifier.size(MovieMateSpacing.backupChevronIconSize),
            )
        }
    }
}

@Composable
private fun BackupActionIcon(@DrawableRes icon: Int) {
    Box(
        modifier = Modifier
            .size(MovieMateSpacing.backupActionIconSize)
            .background(MovieMateColors.Accent, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        MovieMateIcon(
            icon = icon,
            tint = MovieMateColors.PrimaryText,
            modifier = Modifier.size(MovieMateSpacing.backupActionGlyphSize),
        )
    }
}

@Composable
private fun ManualBackupStatus(state: BackupUiState) {
    val errorMessage = state.errorMessage

    when {
        state.isLoading -> InlineProgress(message = "Working with backup file...")
        errorMessage != null -> BackupStatusText(message = errorMessage, isError = true)
        state.isSuccess -> BackupStatusText(message = "Backup action completed.", isError = false)
    }
}

@Composable
private fun BackupStatusText(
    message: String,
    isError: Boolean,
) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = if (isError) MovieMateColors.Rating else MovieMateColors.AccentLight,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun formatSyncTime(epochMillis: Long): String {
    val formatter = remember { DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT) }
    return remember(epochMillis, formatter) { formatter.format(Date(epochMillis)) }
}

private fun BackupUiState.accountStatusText(): String {
    return when (accountState) {
        BackupAccountState.SignedOut -> "Not connected"
        BackupAccountState.SigningIn -> "Connecting..."
        BackupAccountState.SignedIn -> if (isSyncing) "Syncing" else "Sync active"
    }
}

private fun BackupUiState.accountStatusColor() = when (accountState) {
    BackupAccountState.SignedOut -> MovieMateColors.SecondaryText
    BackupAccountState.SigningIn -> MovieMateColors.AccentLight
    BackupAccountState.SignedIn -> MovieMateColors.AccentLight
}

@Preview
@Composable
private fun BackupScreenPreview() {
    BackupScreen(
        state = BackupUiState(),
        onAction = {},
    )
}

@Preview
@Composable
private fun BackupScreenSigningInPreview() {
    BackupScreen(
        state = BackupUiState(accountState = BackupAccountState.SigningIn),
        onAction = {},
    )
}

@Preview
@Composable
private fun BackupScreenSignedInPreview() {
    BackupScreen(
        state = BackupUiState(
            accountState = BackupAccountState.SignedIn,
            accountDisplayName = "Ada Lovelace",
            accountEmail = "ada@example.com",
            lastSyncEpochMillis = 1_720_000_000_000,
        ),
        onAction = {},
    )
}

@Preview
@Composable
private fun BackupScreenLoadingPreview() {
    BackupScreen(
        state = BackupUiState(isLoading = true),
        onAction = {},
    )
}

@Preview
@Composable
private fun BackupScreenErrorPreview() {
    BackupScreen(
        state = BackupUiState(syncErrorMessage = "Google Drive authorization failed."),
        onAction = {},
    )
}

private val SmallProgressSize = 18.dp
private val SmallProgressStroke = 2.dp
private const val DisabledActionAlpha = 0.58f
