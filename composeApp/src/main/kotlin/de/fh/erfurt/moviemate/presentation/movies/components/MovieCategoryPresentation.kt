package de.fh.erfurt.moviemate.presentation.movies.components

import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType

// Zentrale Liste aller Kategorien, die ein Film in der UI haben kann.
// Home-Filter und Kategorie-Screen müssen diese Liste gemeinsam nutzen.
val MovieCategoryTypes: List<CategoryType> = CategoryType.entries

/**
 * Wandelt den internen Kategorie-Namen in einen gut lesbaren UI-Text um.
 *
 * Diese Funktion liegt bewusst allgemein in `presentation`, damit alle Screens
 * dieselbe Beschriftung für dieselben Kategorien verwenden.
 */
fun CategoryType.displayName(): String {
    return when (this) {
        CategoryType.ACTION -> "Action"
        CategoryType.ADVENTURE -> "Adventure"
        CategoryType.FANTASY -> "Fantasy"
        CategoryType.ANIMATION -> "Animation"
        CategoryType.BIOGRAPHY -> "Biography"
        CategoryType.COMEDY -> "Comedy"
        CategoryType.CRIME -> "Crime"
        CategoryType.DOCUMENTARY -> "Documentary"
        CategoryType.DRAMA -> "Drama"
        CategoryType.FAMILY -> "Family"
        CategoryType.HISTORY -> "History"
        CategoryType.HORROR -> "Horror"
        CategoryType.MYSTERY -> "Mystery"
        CategoryType.ROMANCE -> "Romance"
        CategoryType.SCI_FI -> "Science Fiction"
        CategoryType.THRILLER -> "Thriller"
        CategoryType.WAR -> "War"
        CategoryType.WESTERN -> "Western"
        CategoryType.UNKNOWN -> "Other"
    }
}
