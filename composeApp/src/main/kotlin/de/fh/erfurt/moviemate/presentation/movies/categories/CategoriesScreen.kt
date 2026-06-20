package de.fh.erfurt.moviemate.presentation.movies.categories

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import de.fh.erfurt.moviemate.presentation.components.DeleteBadge
import de.fh.erfurt.moviemate.presentation.components.DeleteConfirmationDialog
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenBackground
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenDefaults
import de.fh.erfurt.moviemate.presentation.components.ScreenEmptyState
import de.fh.erfurt.moviemate.presentation.components.ScreenErrorState
import de.fh.erfurt.moviemate.presentation.components.ScreenLoadingState
import de.fh.erfurt.moviemate.presentation.components.SearchField
import de.fh.erfurt.moviemate.presentation.components.TopBarIconButton
import de.fh.erfurt.moviemate.presentation.components.deleteModeWiggle
import de.fh.erfurt.moviemate.presentation.preview.previewCategoriesUiState
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Category overview screen.
 */
@Composable
fun CategoriesScreen(
    state: CategoriesUiState,
    onAction: (CategoriesUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = state.isDeleteMode && state.pendingDeleteCategoryId == null) {
        onAction(CategoriesUiAction.ExitDeleteMode)
    }

    MovieMateScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MovieMateScreenDefaults.HorizontalPadding)
                .padding(top = MovieMateScreenDefaults.VerticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CategoriesTopBar(
                onSearchClicked = { onAction(CategoriesUiAction.SearchClicked) }
            )

            if (state.isSearchVisible) {
                Spacer(modifier = Modifier.height(MovieMateSpacing.fieldSpacing))
                SearchField(
                    value = state.searchQuery,
                    onValueChange = { onAction(CategoriesUiAction.SearchChanged(it)) },
                    placeholder = "Find a category",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(MovieMateScreenDefaults.ContentTopSpacing))

            when {
                state.errorMessage != null -> ScreenErrorState(
                    message = state.errorMessage,
                    onRetryClicked = { onAction(CategoriesUiAction.RetryClicked) }
                )

                state.isLoading -> ScreenLoadingState(message = "Loading categories...")
                state.categories.isEmpty() -> ScreenEmptyState(message = "No categories found")
                else -> CategoriesGrid(
                    categories = state.categories,
                    isDeleteMode = state.isDeleteMode,
                    onCategoryClicked = {
                        if (state.isDeleteMode) {
                            onAction(CategoriesUiAction.ExitDeleteMode)
                        } else {
                            onAction(CategoriesUiAction.CategoryClicked(it.id))
                        }
                    },
                    onCategoryLongPressed = { onAction(CategoriesUiAction.CategoryLongPressed(it.id)) },
                    onDeleteCategoryClicked = { onAction(CategoriesUiAction.DeleteCategoryClicked(it.id)) }
                )
            }
        }

        if (!state.isDeleteMode) {
            FloatingActionButton(
                onClick = { onAction(CategoriesUiAction.AddClicked) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = MovieMateScreenDefaults.HorizontalPadding,
                        bottom = MovieMateSpacing.detailLargeSpacing
                    )
                    .semantics { contentDescription = "Add category" },
                shape = CircleShape,
                containerColor = MovieMateColors.Accent,
                contentColor = MovieMateColors.PrimaryText
            ) {
                Text(
                    text = MovieMateIcons.add,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (state.isAddDialogVisible) {
        AddCategoryDialog(
            name = state.addCategoryName,
            selectedIconKey = state.selectedIconKey,
            errorMessage = state.addCategoryError,
            onNameChanged = { onAction(CategoriesUiAction.AddNameChanged(it)) },
            onIconSelected = { onAction(CategoriesUiAction.AddIconSelected(it)) },
            onConfirm = { onAction(CategoriesUiAction.AddConfirmed) },
            onDismiss = { onAction(CategoriesUiAction.AddDismissed) }
        )
    }

    val pendingDeleteCategory = state.categories.firstOrNull { category ->
        category.id == state.pendingDeleteCategoryId
    }
    if (pendingDeleteCategory != null) {
        DeleteConfirmationDialog(
            title = "Delete category?",
            message = "Are you sure you want to delete \"${pendingDeleteCategory.name}\"? Movies in this category will not be deleted.",
            onConfirm = { onAction(CategoriesUiAction.ConfirmDeleteCategoryClicked) },
            onDismiss = { onAction(CategoriesUiAction.DeleteCategoryDismissed) }
        )
    }
}

@Composable
private fun CategoriesTopBar(
    onSearchClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.headlineSmall,
                color = MovieMateColors.PrimaryText,
                fontWeight = FontWeight.Bold
            )

            TopBarIconButton(
                text = MovieMateIcons.search,
                contentDescription = "Search categories",
                onClick = onSearchClicked
            )
        }

        Box(
            modifier = Modifier
                .padding(top = MovieMateSpacing.titleSpacing)
                .fillMaxWidth()
                .height(MovieMateSpacing.categoryHeaderIndicatorHeight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MovieMateSpacing.categoryHeaderDividerHeight)
                    .align(Alignment.BottomStart)
                    .background(MovieMateColors.Divider)
            )
            Box(
                modifier = Modifier
                    .width(MovieMateSpacing.categoryHeaderIndicatorWidth)
                    .height(MovieMateSpacing.categoryHeaderIndicatorHeight)
                    .clip(RoundedCornerShape(MovieMateSpacing.categoryHeaderIndicatorHeight))
                    .background(MovieMateColors.AccentLight)
            )
        }
    }
}

@Composable
private fun CategoriesGrid(
    categories: List<CategoryUiModel>,
    isDeleteMode: Boolean,
    onCategoryClicked: (CategoryUiModel) -> Unit,
    onCategoryLongPressed: (CategoryUiModel) -> Unit,
    onDeleteCategoryClicked: (CategoryUiModel) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.categoryGridSpacing),
        horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.categoryGridSpacing),
        contentPadding = PaddingValues(bottom = MovieMateSpacing.navigationContentBottomPadding),
        modifier = Modifier.fillMaxSize()
    ) {
        items(categories, key = { it.id }) { category ->
            CategoryTile(
                category = category,
                isDeleteMode = isDeleteMode,
                onClick = { onCategoryClicked(category) },
                onLongClick = { onCategoryLongPressed(category) },
                onDeleteClick = { onDeleteCategoryClicked(category) }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun CategoryTile(
    category: CategoryUiModel,
    isDeleteMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val shape = RoundedCornerShape(MovieMateShapes.folderRadius)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .deleteModeWiggle(isDeleteMode)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(MovieMateSpacing.categoryTileHeight)
                .clip(shape)
                .background(MovieMateColors.CardBackgroundElevated)
                .border(
                    width = MovieMateSpacing.dividerThickness,
                    color = MovieMateColors.CardBorder,
                    shape = shape
                )
                .semantics { contentDescription = "Category ${category.name}" }
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .padding(MovieMateSpacing.categoryTilePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CategoryGenreIcon(
                iconKey = category.iconKey,
                modifier = Modifier.size(MovieMateSpacing.categoryIconSize)
            )

            Spacer(modifier = Modifier.height(MovieMateSpacing.categoryIconBottomSpacing))

            Text(
                text = category.name,
                style = MaterialTheme.typography.labelMedium,
                color = MovieMateColors.PrimaryText,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = category.movieCountText ?: "0 Movies",
                style = MaterialTheme.typography.labelSmall,
                color = MovieMateColors.SecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isDeleteMode) {
            DeleteBadge(
                contentDescription = "Delete category ${category.name}",
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = MovieMateSpacing.compactSpacing,
                        end = MovieMateSpacing.compactSpacing
                    )
            )
        }
    }
}

@Composable
private fun AddCategoryDialog(
    name: String,
    selectedIconKey: String,
    errorMessage: String?,
    onNameChanged: (String) -> Unit,
    onIconSelected: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MovieMateColors.CardBackground,
        titleContentColor = MovieMateColors.PrimaryText,
        textContentColor = MovieMateColors.SecondaryText,
        title = { Text("Add category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.titleSpacing)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChanged,
                    label = { Text("Name") },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { message ->
                        { Text(message) }
                    },
                    shape = RoundedCornerShape(MovieMateShapes.controlRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MovieMateColors.SoftControl,
                        unfocusedContainerColor = MovieMateColors.SoftControl,
                        focusedBorderColor = MovieMateColors.AccentLight,
                        unfocusedBorderColor = MovieMateColors.SoftControlBorder,
                        cursorColor = MovieMateColors.AccentLight,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Icon",
                    style = MaterialTheme.typography.titleSmall,
                    color = MovieMateColors.PrimaryText,
                    fontWeight = FontWeight.SemiBold
                )
                CategoryIconPicker(
                    selectedIconKey = selectedIconKey,
                    onIconSelected = onIconSelected
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
internal fun CategoryIconPicker(
    selectedIconKey: String?,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing)
    ) {
        items(CategoryIconOptions, key = { it.key }) { option ->
            CategoryIconChoice(
                option = option,
                selected = selectedIconKey.categoryIconOption().key == option.key,
                onClick = { onIconSelected(option.key) }
            )
        }
    }
}

@Composable
private fun CategoryIconChoice(
    option: CategoryIconOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.semantics { contentDescription = "Category icon ${option.label}" },
        shape = RoundedCornerShape(MovieMateShapes.chipRadius),
        color = if (selected) MovieMateColors.SoftControl else MovieMateColors.CardBackgroundElevated,
        border = BorderStroke(
            width = MovieMateSpacing.dividerThickness,
            color = if (selected) MovieMateColors.AccentLight else MovieMateColors.CardBorder
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(MovieMateSpacing.compactSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CategoryGenreIcon(
                iconKey = option.key,
                modifier = Modifier.size(MovieMateSpacing.categoryIconSize)
            )
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
