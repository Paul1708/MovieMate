package de.fh.erfurt.moviemate.presentation.movies.components

import de.fh.erfurt.moviemate.movies.domain.model.movie.FetchedMovieData
import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieSearchResults
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.FetchMovieDataUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MovieLookupState(
    val isLoading: Boolean = false,
    val results: MovieSearchResults? = null,
    val error: String? = null,
)

/** Owns the cancellable lookup shared by the add and edit forms. */
internal class MovieLookup(
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    private val metadata: FetchMovieDataUseCase,
    private val update: (MovieLookupState) -> Unit,
    private val applyDetails: (FetchedMovieData) -> Unit,
) {
    private var job: Job? = null

    fun search(title: String, year: String, mediaType: MediaType) = launchLookup {
        val results = withContext(dispatcher) { metadata.search(title, year, mediaType) }
        val match = results.singleMatch
        when {
            results.matches.isEmpty() -> update(MovieLookupState(error = "No matches found. Try another title or year."))
            match != null -> loadDetails(match.imdbId, mediaType)
            else -> update(MovieLookupState(results = results))
        }
    }

    fun select(imdbId: String, mediaType: MediaType) = launchLookup { loadDetails(imdbId, mediaType) }

    fun clear() {
        job?.cancel()
        update(MovieLookupState())
    }

    private fun launchLookup(block: suspend () -> Unit) {
        job?.cancel()
        update(MovieLookupState(isLoading = true))
        job = scope.launch {
            try {
                block()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val message = when (error) {
                    is IllegalArgumentException, is IllegalStateException -> error.message
                    else -> null
                }
                update(MovieLookupState(error = message ?: "Title lookup is unavailable. Please try again."))
            }
        }
    }

    private suspend fun loadDetails(imdbId: String, mediaType: MediaType) {
        val details = withContext(dispatcher) { metadata.byImdbId(imdbId, mediaType) }
        if (details == null) {
            update(MovieLookupState(error = "No details found for this title."))
        } else {
            applyDetails(details)
            update(MovieLookupState())
        }
    }
}
