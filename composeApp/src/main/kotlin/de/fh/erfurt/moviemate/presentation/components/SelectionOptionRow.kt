package de.fh.erfurt.moviemate.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Einheitliche Auswahlzeile für Dialoge mit RadioButton und Text.
 */
@Composable
fun SelectionOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = MovieMateSpacing.dialogRowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MovieMateColors.Accent,
                unselectedColor = MovieMateColors.MutedText
            )
        )
        leadingContent?.invoke()
        Text(
            text = label,
            color = if (selected) MovieMateColors.PrimaryText else MovieMateColors.SecondaryText,
            modifier = Modifier.padding(start = MovieMateSpacing.dialogTextStartPadding)
        )
    }
}
