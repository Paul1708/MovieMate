package de.fh.erfurt.moviemate.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MovieMateColors.CardBackground,
            unfocusedContainerColor = MovieMateColors.CardBackground
        ),
        modifier = modifier.fillMaxWidth()
    )
}
