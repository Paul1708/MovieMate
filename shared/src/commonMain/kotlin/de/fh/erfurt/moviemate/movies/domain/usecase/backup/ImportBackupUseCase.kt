package de.fh.erfurt.moviemate.movies.domain.usecase.backup

import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository

class ImportBackupUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(jsonString: String) {
        require(jsonString.isNotBlank()) { "Backup data string must not be empty" }
        movieRepository.importBackup(jsonString)
    }
}