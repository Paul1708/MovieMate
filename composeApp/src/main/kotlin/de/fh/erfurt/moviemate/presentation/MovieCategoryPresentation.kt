package de.fh.erfurt.moviemate.presentation

import de.fh.erfurt.moviemate.domain.model.CategoryType

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
        CategoryType.DRAMA -> "Drama"
        CategoryType.ACTION -> "Action"
        CategoryType.FANTASY -> "Fantasy"
        CategoryType.ROMANCE -> "Romance"
        CategoryType.COMEDY -> "Comedy"
        CategoryType.HORROR -> "Horror"
        CategoryType.FAVORITE -> "Favorite"
        CategoryType.SCIENCE_FICTION -> "Science Fiction"
        CategoryType.ANIMATION -> "Animation"
        CategoryType.OTHER -> "Other"
    }
}
