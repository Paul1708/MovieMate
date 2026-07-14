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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.movies.presentation.backup.BackupUiState
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenBackground
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenDefaults
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

@Composable
fun BackupScreen(
    state: BackupUiState,
    onExportClicked: () -> Unit,
    onImportClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    MovieMateScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MovieMateScreenDefaults.HorizontalPadding)
                .padding(top = MovieMateScreenDefaults.VerticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BackupTopBar()

            Spacer(modifier = Modifier.height(MovieMateScreenDefaults.ContentTopSpacing))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = MovieMateSpacing.sectionSpacing),
                verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.itemSpacing)
            ) {
                BackupActionCard(
                    title = "Export",
                    subtitle = "Export your movie list as a backup file.",
                    icon = MovieMateIcons.uploadFile,
                    contentDescription = "Export backup",
                    enabled = !state.isLoading,
                    onClick = onExportClicked
                )

                BackupActionCard(
                    title = "Import",
                    subtitle = "Import a previous backup.",
                    icon = MovieMateIcons.download,
                    contentDescription = "Import backup",
                    enabled = !state.isLoading,
                    onClick = onImportClicked
                )

                BackupStatus(state = state)
            }
        }
    }
}

@Composable
private fun BackupTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Backup & Import",
            style = MaterialTheme.typography.headlineSmall,
            color = MovieMateColors.PrimaryText,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BackupActionCard(
    title: String,
    subtitle: String,
    @DrawableRes icon: Int,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit
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
            color = MovieMateColors.CardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MovieMateSpacing.detailCardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackupActionIcon(icon = icon)

            Spacer(modifier = Modifier.width(MovieMateSpacing.detailMediumSpacing))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MovieMateColors.PrimaryText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MovieMateColors.SecondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = MovieMateSpacing.detailMicroSpacing)
                )
            }

            Spacer(modifier = Modifier.width(MovieMateSpacing.compactSpacing))

            MovieMateIcon(
                icon = MovieMateIcons.chevronRight,
                tint = MovieMateColors.PrimaryText,
                modifier = Modifier.size(MovieMateSpacing.backupChevronIconSize)
            )
        }
    }
}

@Composable
private fun BackupActionIcon(
    @DrawableRes icon: Int,
) {
    Box(
        modifier = Modifier
            .size(MovieMateSpacing.backupActionIconSize)
            .background(MovieMateColors.Accent, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        MovieMateIcon(
            icon = icon,
            tint = MovieMateColors.PrimaryText,
            modifier = Modifier.size(MovieMateSpacing.backupActionGlyphSize)
        )
    }
}

@Composable
private fun BackupStatus(
    state: BackupUiState
) {
    val errorMessage = state.errorMessage

    when {
        state.isLoading -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MovieMateSpacing.compactSpacing),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MovieMateColors.Accent,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(MovieMateSpacing.compactSpacing))
                Text(
                    text = "Working with backup file...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MovieMateColors.SecondaryText
                )
            }
        }

        errorMessage != null -> BackupStatusText(
            message = errorMessage,
            isError = true
        )

        state.isSuccess -> BackupStatusText(
            message = "Backup action completed.",
            isError = false
        )
    }
}

@Composable
private fun BackupStatusText(
    message: String,
    isError: Boolean
) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = if (isError) MovieMateColors.Rating else MovieMateColors.AccentLight,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MovieMateSpacing.compactSpacing)
    )
}

@Preview
@Composable
private fun BackupScreenPreview() {
    BackupScreen(
        state = BackupUiState(),
        onExportClicked = {},
        onImportClicked = {}
    )
}

@Preview
@Composable
private fun BackupScreenLoadingPreview() {
    BackupScreen(
        state = BackupUiState(isLoading = true),
        onExportClicked = {},
        onImportClicked = {}
    )
}

@Preview
@Composable
private fun BackupScreenErrorPreview() {
    BackupScreen(
        state = BackupUiState(errorMessage = "Backup file could not be imported."),
        onExportClicked = {},
        onImportClicked = {}
    )
}

private const val DisabledActionAlpha = 0.58f
