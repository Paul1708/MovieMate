package de.fh.erfurt.moviemate.movies.domain.usecase.backup

import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository

class ExportBackupUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(): String {
        return movieRepository.exportBackup()
    }
}