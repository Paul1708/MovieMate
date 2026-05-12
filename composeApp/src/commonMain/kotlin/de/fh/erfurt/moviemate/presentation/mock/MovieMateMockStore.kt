package de.fh.erfurt.moviemate.presentation.mock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.fh.erfurt.moviemate.domain.model.Movie

/**
 * App-weiter Mock-Speicher für UI-Tests ohne Backend.
 *
 * Home, Kategorien und spätere Screens lesen dadurch dieselbe Filmliste. Der
 * Add-Screen kann später testweise Filme ergänzen, ohne Backend-Code zu ändern.
 */
class MovieMateMockStore(
    initialMovies: List<Movie> = sampleMovies()
) {
    var movies by mutableStateOf(initialMovies)
        private set

    fun addMovie(movie: Movie) {
        movies = movies + movie
    }

    fun updateMovie(movie: Movie) {
        movies = movies.map { currentMovie ->
            if (currentMovie.id == movie.id) movie else currentMovie
        }
    }

    fun deleteMovie(movieId: String) {
        movies = movies.filterNot { movie -> movie.id == movieId }
    }

    fun toggleWatched(movieId: String) {
        movies = movies.map { movie ->
            if (movie.id == movieId) {
                movie.copy(isWatched = !movie.isWatched)
            } else {
                movie
            }
        }
    }
}
