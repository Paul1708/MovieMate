package de.fh.erfurt.moviemate.presentation.categories

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.MovieMateColors
import de.fh.erfurt.moviemate.presentation.ScreenHeader
import de.fh.erfurt.moviemate.presentation.ScreenHeaderDefaults
import de.fh.erfurt.moviemate.presentation.TopBarIconButton

/**
 * Einstieg für den Kategorie-Screen während der UI-Entwicklung.
 *
 * Diese Route nutzt noch Mockdaten und wird im nächsten Schritt an die
 * App-Navigation angebunden.
 */
@Composable
fun CategoriesRouteMock(
    modifier: Modifier = Modifier
) {
    val viewModel = remember { CategoriesViewModel() }

    CategoriesScreen(
        state = viewModel.state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

/**
 * Sichtbare Oberfläche des Kategorie-Screens.
 *
 * Der Screen zeigt Kategorien als Grid im Stil des Mockups. Er nutzt denselben
 * State/Action-Ansatz wie der Homescreen und ist dadurch später leicht an echte
 * Daten oder Navigation anzuschließen.
 */
@Composable
fun CategoriesScreen(
    state: CategoriesUiState,
    onAction: (CategoriesUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MovieMateColors.ScreenBackground)
            .padding(
                horizontal = ScreenHeaderDefaults.HorizontalPadding,
                vertical = ScreenHeaderDefaults.VerticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeader(
            title = "Kategorien",
            topBar = {
                CategoriesTopBar(
                    onSearchClicked = { onAction(CategoriesUiAction.SearchClicked) }
                )
            }
        )

        if (state.isSearchVisible) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onAction(CategoriesUiAction.SearchChanged(it)) },
                placeholder = { Text("Find category") },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MovieMateColors.CardBackground,
                    unfocusedContainerColor = MovieMateColors.CardBackground
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(ScreenHeaderDefaults.ContentTopSpacing))

        when {
            state.errorMessage != null -> CategoriesErrorState(
                message = state.errorMessage,
                onRetryClicked = { onAction(CategoriesUiAction.RetryClicked) }
            )

            state.isLoading -> CategoriesLoadingState()
            state.categories.isEmpty() -> CategoriesEmptyState()
            else -> CategoriesGrid(
                categories = state.categories,
                onCategoryClicked = { onAction(CategoriesUiAction.CategoryClicked(it.type)) }
            )
        }
    }
}

@Composable
private fun CategoriesTopBar(
    onSearchClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))

        TopBarIconButton(
            text = "⌕",
            contentDescription = "Kategorie suchen",
            onClick = onSearchClicked
        )
    }
}

/**
 * Grid für die Kategorie-Karten.
 *
 * Zwei Spalten entsprechen dem Mockup und funktionieren gut auf kleinen
 * Smartphone-Bildschirmen.
 */
@Composable
private fun CategoriesGrid(
    categories: List<CategoryUiModel>,
    onCategoryClicked: (CategoryUiModel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(categories, key = { it.type.name }) { category ->
            CategoryFolderCard(
                category = category,
                onClick = { onCategoryClicked(category) }
            )
        }
    }
}

/**
 * Einzelne Kategorie-Karte im Folder-Stil.
 *
 * Der Aufbau imitiert die schwarzen Ordner aus dem Mockup, bleibt aber komplett
 * in Compose gezeichnet und braucht keine externen Bilddateien.
 */
@Composable
private fun CategoryFolderCard(
    category: CategoryUiModel,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Kategorie ${category.label}" }
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(width = 82.dp, height = 58.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 14.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(Color.Black)
            )
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        if (category.movieCountText != null) {
            Text(
                text = category.movieCountText,
                style = MaterialTheme.typography.labelSmall,
                color = MovieMateColors.SecondaryText,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun CategoriesLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(
            text = "Loading categories...",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun CategoriesEmptyState() {
    Text(
        text = "No categories found",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp)
    )
}

@Composable
private fun CategoriesErrorState(
    message: String,
    onRetryClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        TextButton(onClick = onRetryClicked) {
            Text("Retry")
        }
    }
}

@Preview
@Composable
private fun CategoriesScreenPreview() {
    CategoriesScreen(
        state = previewCategoriesUiState(),
        onAction = {}
    )
}

@Preview
@Composable
private fun CategoriesScreenEmptyPreview() {
    CategoriesScreen(
        state = previewCategoriesUiState().copy(categories = emptyList()),
        onAction = {}
    )
}
