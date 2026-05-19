package de.fh.erfurt.moviemate.presentation

import de.fh.erfurt.moviemate.domain.model.CategoryType

/**
 * Zentrale Routen der Android-App.
 *
 * Route-Strings bleiben hier gekapselt, damit Screens und Leaf-Composables nur
 * semantische Events ausgeben und keine Backstack-Details kennen muessen.
 */
object MovieMateDestination {
    const val Home = "home"
    const val Categories = "categories"
    const val Add = "add"
    const val Backup = "backup"

    const val CategoryArg = "category"
    const val MovieIdArg = "movieId"

    const val HomeRoute = "$Home?$CategoryArg={$CategoryArg}"
    const val DetailRoute = "detail/{$MovieIdArg}"

    fun homeRoute(category: CategoryType? = null): String {
        return if (category == null) {
            Home
        } else {
            "$Home?$CategoryArg=${category.name}"
        }
    }

    fun detailRoute(movieId: String): String = "detail/$movieId"
}

fun BottomNavItem.toRoute(): String {
    return when (this) {
        BottomNavItem.Home -> MovieMateDestination.homeRoute()
        BottomNavItem.Categories -> MovieMateDestination.Categories
        BottomNavItem.Add -> MovieMateDestination.Add
        BottomNavItem.Backup -> MovieMateDestination.Backup
    }
}

fun String?.selectedBottomNavItem(): BottomNavItem {
    return when {
        this == null -> BottomNavItem.Home
        startsWith(MovieMateDestination.Categories) -> BottomNavItem.Categories
        startsWith(MovieMateDestination.Add) -> BottomNavItem.Add
        startsWith(MovieMateDestination.Backup) -> BottomNavItem.Backup
        else -> BottomNavItem.Home
    }
}

fun String?.toCategoryTypeOrNull(): CategoryType? {
    if (this == null) return null

    return CategoryType.entries.firstOrNull { it.name == this }
}
