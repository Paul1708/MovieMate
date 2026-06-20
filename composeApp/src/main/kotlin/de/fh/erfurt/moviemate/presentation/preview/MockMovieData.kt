package de.fh.erfurt.moviemate.presentation.preview

import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie

/**
 * Gemeinsame Sample-Daten für die Presentation-Schicht.
 *
 * Die Filme sind Domain-Objekte, damit Home, Kategorien und spätere Screens
 * dieselbe Struktur nutzen wie später die Backend-Anbindung.
 */
fun sampleMovies(): List<Movie> {
    return listOf(
        Movie(
            id = "movie-1",
            title = "Star Wars",
            posterUrl = "",
            releaseYear = 2019,
            runtimeMinutes = 130,
            userRating = 10,
            imdbRating = 8.6,
            isWatched = true,
            categoryId = CategoryType.ACTION.storageKey,
            description = "A space adventure used as sample data for the homescreen.",
            comment = "Rewatch with the original trilogy.",
            externalIds = mapOf("imdb" to "tt2527338", "tmdb" to "181812"),
            watchProviders = listOf("Disney+", "Apple TV"),
            updatedAtEpochMillis = 5,
            version = 1
        ),
        Movie(
            id = "movie-2",
            title = "Harry Potter",
            posterUrl = "",
            releaseYear = 2001,
            runtimeMinutes = 152,
            userRating = 8,
            imdbRating = 7.6,
            isWatched = false,
            categoryId = CategoryType.FANTASY.storageKey,
            description = "A fantasy movie used as sample data for the homescreen.",
            comment = null,
            externalIds = mapOf("imdb" to "tt0241527"),
            watchProviders = listOf("Max", "Prime Video"),
            updatedAtEpochMillis = 4,
            version = 1
        ),
        Movie(
            id = "movie-3",
            title = "The Matrix",
            posterUrl = "",
            releaseYear = 1999,
            runtimeMinutes = 136,
            userRating = 9,
            imdbRating = 8.7,
            isWatched = true,
            categoryId = CategoryType.SCI_FI.storageKey,
            description = "A science fiction movie used as sample data for the homescreen.",
            comment = null,
            externalIds = mapOf("imdb" to "tt0133093", "tmdb" to "603"),
            watchProviders = listOf("Netflix"),
            updatedAtEpochMillis = 3,
            version = 2
        ),
        Movie(
            id = "movie-4",
            title = "Inception",
            posterUrl = "",
            releaseYear = 2010,
            runtimeMinutes = 148,
            userRating = 9,
            imdbRating = 8.8,
            isWatched = false,
            categoryId = CategoryType.ACTION.storageKey,
            description = "A dream thriller used as sample data for the homescreen.",
            comment = null,
            externalIds = mapOf("imdb" to "tt1375666"),
            watchProviders = null,
            updatedAtEpochMillis = 2,
            version = 1
        ),
        Movie(
            id = "movie-5",
            title = "The Godfather",
            posterUrl = "",
            releaseYear = 1972,
            runtimeMinutes = 175,
            userRating = 9,
            imdbRating = 9.2,
            isWatched = true,
            categoryId = CategoryType.DRAMA.storageKey,
            description = "A drama movie used as sample data for the homescreen.",
            comment = null,
            externalIds = mapOf("imdb" to "tt0068646"),
            watchProviders = listOf("Paramount+"),
            updatedAtEpochMillis = 1,
            version = 1
        )
    )
}
