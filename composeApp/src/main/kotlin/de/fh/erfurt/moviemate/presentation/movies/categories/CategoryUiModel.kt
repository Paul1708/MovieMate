package de.fh.erfurt.moviemate.presentation.movies.categories

// UI-Modell für Custom Categories. Movies speichern die Kategorie über `id`.
data class CategoryUiModel(
    val id: String,
    val name: String,
    val iconKey: String? = DefaultCategoryIconKey,
    val movieCountText: String? = null,
)
