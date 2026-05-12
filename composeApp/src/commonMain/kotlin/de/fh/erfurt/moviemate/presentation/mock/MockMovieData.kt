package de.fh.erfurt.moviemate.presentation.mock

import de.fh.erfurt.moviemate.domain.model.Category
import de.fh.erfurt.moviemate.domain.model.CategoryType
import de.fh.erfurt.moviemate.domain.model.Movie

/**
 * Gemeinsame Testdaten für die Presentation-Schicht.
 *
 * Die Filme sind Domain-Objekte, damit Home, Kategorien und spätere Screens
 * dieselbe Struktur nutzen wie später die Backend-Anbindung.
 */
fun previewDomainMovies(): List<Movie> {
    return listOf(
        Movie(
            id = "movie-1",
            title = "Star Wars",
            posterUri = "",
            publishingYear = 2019,
            runtime = 130,
            rating = 10,
            isWatched = true,
            category = Category(id = "action", type = CategoryType.ACTION),
            description = "A space adventure used as sample data for the homescreen.",
            creationTimeEpochMillis = 5,
            updateTimeEpochMillis = 5
        ),
        Movie(
            id = "movie-2",
            title = "Harry Potter",
            posterUri = "",
            publishingYear = 2001,
            runtime = 152,
            rating = 8,
            isWatched = false,
            category = Category(id = "fantasy", type = CategoryType.FANTASY),
            description = "A fantasy movie used as sample data for the homescreen.",
            creationTimeEpochMillis = 4,
            updateTimeEpochMillis = 4
        ),
        Movie(
            id = "movie-3",
            title = "The Matrix",
            posterUri = "",
            publishingYear = 1999,
            runtime = 136,
            rating = 9,
            isWatched = true,
            category = Category(id = "science_fiction", type = CategoryType.SCIENCE_FICTION),
            description = "A science fiction movie used as sample data for the homescreen.",
            creationTimeEpochMillis = 3,
            updateTimeEpochMillis = 3
        ),
        Movie(
            id = "movie-4",
            title = "Inception",
            posterUri = "",
            publishingYear = 2010,
            runtime = 148,
            rating = 9,
            isWatched = false,
            category = Category(id = "action", type = CategoryType.ACTION),
            description = "A dream thriller used as sample data for the homescreen.",
            creationTimeEpochMillis = 2,
            updateTimeEpochMillis = 2
        ),
        Movie(
            id = "movie-5",
            title = "The Godfather",
            posterUri = "",
            publishingYear = 1972,
            runtime = 175,
            rating = 9,
            isWatched = true,
            category = Category(id = "drama", type = CategoryType.DRAMA),
            description = "A drama movie used as sample data for the homescreen.",
            creationTimeEpochMillis = 1,
            updateTimeEpochMillis = 1
        )
    )
}
