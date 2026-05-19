package de.fh.erfurt.moviemate.presentation.categories

import de.fh.erfurt.moviemate.domain.model.CategoryType

// UI-Modell für eine Kategorie im Kategorie-Screen.
// Es hält den Domain-Typ und bereits vorbereitete Texte für die Anzeige.
data class CategoryUiModel(
    val type: CategoryType,
    val label: String,
    val movieCountText: String? = null
)
