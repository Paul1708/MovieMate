package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Umschalter für den gesehen-Status, der auch außerhalb des Edit-Modus nutzbar ist.
 */
@Composable
internal fun WatchedControl(
    isWatched: Boolean,
    onWatchedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MovieMateShapes.cardRadius),
        color = MovieMateColors.CardBackground
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = MovieMateSpacing.detailControlHorizontalPadding,
                vertical = MovieMateSpacing.detailControlVerticalPadding
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${MovieMateIcons.watched} Watch status",
                    style = MaterialTheme.typography.labelMedium,
                    color = MovieMateColors.SecondaryText
                )
                Text(
                    text = if (isWatched) "Watched" else "Watch later",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MovieMateColors.PrimaryText,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Switch(
                checked = isWatched,
                onCheckedChange = onWatchedChanged
            )
        }
    }
}

/**
 * Read-only Infokarte für einzelne Detailwerte wie zuletzt bearbeitet.
 */
@Composable
internal fun DetailReadOnlyInfo(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MovieMateShapes.cardRadius),
        color = MovieMateColors.CardBackground
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = MovieMateSpacing.detailControlHorizontalPadding,
                vertical = MovieMateSpacing.detailInfoVerticalPadding
            )
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MovieMateColors.SecondaryText
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MovieMateColors.PrimaryText,
                modifier = Modifier.padding(top = MovieMateSpacing.detailMicroSpacing)
            )
        }
    }
}

/**
 * Einheitliches Textfeld für den Detaileditor
 */
@Composable
internal fun DetailTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        minLines = minLines,
        singleLine = minLines == 1,
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth()
    )
}
