package de.fh.erfurt.moviemate.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes

/**
 * Gemeinsames Suchfeld für Listen-Screens.
 *
 * Home, Kategorien und spätere Screens behalten dadurch denselben Stil und
 * müssen nur Platzhaltertext und Suchwert übergeben.
 */
@Composable
fun SearchField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(MovieMateShapes.controlRadius),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MovieMateColors.CardBackground,
            unfocusedContainerColor = MovieMateColors.CardBackground,
            focusedTextColor = MovieMateColors.PrimaryText,
            unfocusedTextColor = MovieMateColors.PrimaryText,
            focusedPlaceholderColor = MovieMateColors.MutedText,
            unfocusedPlaceholderColor = MovieMateColors.MutedText,
            cursorColor = MovieMateColors.Accent,
            focusedIndicatorColor = MovieMateColors.Accent,
            unfocusedIndicatorColor = MovieMateColors.CardBorder
        ),
        modifier = modifier.fillMaxWidth()
    )
}
