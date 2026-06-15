package de.fh.erfurt.moviemate.presentation.app.navigation

/**
 * Zentrale Routen der Android-App.
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

    fun homeRoute(categoryId: String? = null): String {
        return if (categoryId.isNullOrBlank()) {
            Home
        } else {
            "$Home?$CategoryArg=$categoryId"
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
