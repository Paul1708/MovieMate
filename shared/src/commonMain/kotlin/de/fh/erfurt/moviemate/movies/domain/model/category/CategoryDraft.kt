package de.fh.erfurt.moviemate.movies.domain.model.category

data class CategoryDraft(
    val name: String,
    val iconKey: String? = null,
    val updatedAtEpochMillis: Long
)
