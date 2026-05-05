package de.fh.erfurt.moviemate.domain.usecase

import de.fh.erfurt.moviemate.domain.model.Category
import de.fh.erfurt.moviemate.domain.model.Movie
import de.fh.erfurt.moviemate.domain.repository.MovieRepository
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddMovieUseCase(
    private val repository: MovieRepository
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        title: String,
        posterUri: String,
        publishingYear: Int,
        runtime: Int,
        rating: Int,
        category: Category,
        description: String,
    ) {
        require(title.isNotBlank()) { "The movie title cannot be left blank." }
        require(publishingYear > 1888) { "The publication year is invalid." }
        require(runtime > 0) { "The runtime must be greater than 0 minutes." }
        require(rating in 1..10 ) { "The rating must be between 1 and 10." }
        require(category.id.isNotBlank()) { "The category id cannot be blank." }

        val generatedId = Uuid.random().toString()
        val currentTime = Clock.System.now().toEpochMilliseconds()

        val newMovie = Movie(
            id = generatedId,
            title = title.trim(),
            posterUri = posterUri,
            publishingYear = publishingYear,
            runtime = runtime,
            rating = rating, // Standardwert bei Neuerstellung
            isWatched = false,
            category = category,
            description = description.trim(),
            creationTimeEpochMillis = currentTime,
            updateTimeEpochMillis = currentTime
        )
        repository.addMovie(newMovie)
    }

}