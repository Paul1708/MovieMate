package de.fh.erfurt.moviemate.presentation.categories

import de.fh.erfurt.moviemate.domain.model.CategoryType
import de.fh.erfurt.moviemate.presentation.home.displayName

// Beispiel-Kategorien für Preview und Entwicklung ohne Backend.
fun previewCategories(): List<CategoryUiModel> {
    return listOf(
        CategoryType.DRAMA,
        CategoryType.ACTION,
        CategoryType.FANTASY,
        CategoryType.ROMANCE,
        CategoryType.COMEDY,
        CategoryType.HORROR,
        CategoryType.FAVORITE,
        CategoryType.OTHER
    ).mapIndexed { index, type ->
        CategoryUiModel(
            type = type,
            label = type.displayName(),
            movieCountText = "${index + 1} Movies"
        )
    }
}

// Vollständiger Beispiel-State für den Kategorie-Screen.
fun previewCategoriesUiState(): CategoriesUiState {
    return CategoriesUiState(
        categories = previewCategories()
    )
}
