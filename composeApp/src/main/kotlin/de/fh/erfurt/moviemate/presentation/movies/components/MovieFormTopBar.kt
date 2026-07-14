package de.fh.erfurt.moviemate.presentation.movies.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenDefaults
import de.fh.erfurt.moviemate.presentation.components.TopBarIconButton
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors

/** Shared top bar for add and edit movie forms. */
@Composable
fun MovieFormTopBar(
    title: String,
    @DrawableRes navigationIcon: Int,
    navigationContentDescription: String,
    onNavigationClicked: () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    actionEnabled: Boolean = true,
    onActionClicked: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(MovieMateScreenDefaults.TopBarHeight),
        contentAlignment = Alignment.Center,
    ) {
        TopBarIconButton(
            icon = navigationIcon,
            contentDescription = navigationContentDescription,
            onClick = onNavigationClicked,
            modifier = Modifier.align(Alignment.CenterStart),
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MovieMateColors.PrimaryText,
            fontWeight = FontWeight.Bold,
        )

        if (actionLabel != null && onActionClicked != null) {
            TextButton(
                onClick = onActionClicked,
                enabled = actionEnabled,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Text(actionLabel)
            }
        }
    }
}
