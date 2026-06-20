package de.fh.erfurt.moviemate.presentation.app.startup

import de.fh.erfurt.moviemate.app.di.AppContainer

sealed interface MovieMateStartupState {
    data class Loading(
        val step: MovieMateStartupStep = MovieMateStartupStep.PrepareApp
    ) : MovieMateStartupState

    data class Ready(
        val appContainer: AppContainer
    ) : MovieMateStartupState

    data class Error(
        val message: String
    ) : MovieMateStartupState
}

enum class MovieMateStartupStep(
    val message: String
) {
    PrepareApp("App vorbereiten..."),
    OpenDatabase("Datenbank öffnen..."),
    CheckCategories("Kategorien prüfen..."),
    LoadMovies("Filme vorbereiten..."),
    LoadStartView("Startansicht laden...")
}
