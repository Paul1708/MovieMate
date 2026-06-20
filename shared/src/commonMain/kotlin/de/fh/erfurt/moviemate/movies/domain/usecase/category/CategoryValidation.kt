package de.fh.erfurt.moviemate.movies.domain.usecase.category

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft

internal fun validateCategoryName(name: String) {
    require(name.isNotBlank()) { "Category name cannot be empty" }
    require(name.length <= 50) { "Category name must not exceed 50 characters" }
}

internal fun validateCategoryIconKey(iconKey: String?) {
    require(iconKey == null || iconKey.length <= 50) { "Category icon key must not exceed 50 characters" }
}

internal fun validateCategory(category: Category) {
    require(category.id.isNotBlank()) { "Category ID cannot be empty" }
    validateCategoryName(category.name)
    validateCategoryIconKey(category.iconKey)
}

internal fun validateCategoryDraft(draft: CategoryDraft) {
    validateCategoryName(draft.name)
    validateCategoryIconKey(draft.iconKey)
}
