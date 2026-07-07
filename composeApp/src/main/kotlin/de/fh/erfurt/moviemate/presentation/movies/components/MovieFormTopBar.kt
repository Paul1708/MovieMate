package de.fh.erfurt.moviemate.presentation.movies.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIconType

/**
 * Shared top bar for add/edit movie forms.
 *
 * The parent screen owns horizontal and top padding, which keeps add and edit
 * forms aligned to the same content width.
 */
@Composable
fun MovieFormTopBar(
    title: String,
    navigationIcon: String,
    navigationContentDescription: String,
    onNavigationClicked: () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    actionEnabled: Boolean = true,
    onActionClicked: (() -> Unit)? = null
) {
    MovieFormTopBarContent(
        title = title,
        navigationButton = {
            TopBarIconButton(
                text = navigationIcon,
                contentDescription = navigationContentDescription,
                onClick = onNavigationClicked,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        },
        modifier = modifier,
        actionLabel = actionLabel,
        actionEnabled = actionEnabled,
        onActionClicked = onActionClicked
    )
}

@Composable
fun MovieFormTopBar(
    title: String,
    navigationIcon: MovieMateIconType,
    navigationContentDescription: String,
    onNavigationClicked: () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    actionEnabled: Boolean = true,
    onActionClicked: (() -> Unit)? = null
) {
    MovieFormTopBarContent(
        title = title,
        navigationButton = {
            TopBarIconButton(
                icon = navigationIcon,
                contentDescription = navigationContentDescription,
                onClick = onNavigationClicked,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        },
        modifier = modifier,
        actionLabel = actionLabel,
        actionEnabled = actionEnabled,
        onActionClicked = onActionClicked
    )
}

@Composable
private fun MovieFormTopBarContent(
    title: String,
    navigationButton: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    actionEnabled: Boolean = true,
    onActionClicked: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(MovieMateScreenDefaults.TopBarHeight),
        contentAlignment = Alignment.Center
    ) {
        navigationButton()

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MovieMateColors.PrimaryText,
            fontWeight = FontWeight.Bold
        )

        if (actionLabel != null && onActionClicked != null) {
            TextButton(
                onClick = onActionClicked,
                enabled = actionEnabled,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(actionLabel)
            }
        }
    }
}
