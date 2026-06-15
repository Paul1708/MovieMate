package de.fh.erfurt.moviemate.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoriesScreen
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoriesUiAction

/**
 * Preview-Route für Kategorien mit lokalem UI-State.
 */
@Composable
fun CategoriesPreviewRoute(
    modifier: Modifier = Modifier,
    onCategorySelected: (String) -> Unit = {}
) {
    val stateHolder = remember { CategoriesPreviewStateHolder() }

    CategoriesScreen(
        state = stateHolder.state,
        onAction = { action ->
            stateHolder.onAction(action)
            if (action is CategoriesUiAction.CategoryClicked) {
                onCategorySelected(action.categoryId)
            }
        },
        modifier = modifier
    )
}
