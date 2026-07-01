package de.fh.erfurt.moviemate.presentation.app.startup

import android.content.Context
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.platform.AndroidAppContainerFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class MovieMateStartupRunner(
    private val applicationContext: Context,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun run(
        onStepChanged: (MovieMateStartupStep) -> Unit
    ): AppContainer {
        onStepChanged(MovieMateStartupStep.PrepareApp)

        onStepChanged(MovieMateStartupStep.OpenDatabase)
        val appContainer = withContext(ioDispatcher) {
            AndroidAppContainerFactory(applicationContext).create()
        }

        onStepChanged(MovieMateStartupStep.CheckCategories)
        withContext(ioDispatcher) {
            appContainer.getCategoriesUseCase()
        }

        onStepChanged(MovieMateStartupStep.LoadMovies)
        withContext(ioDispatcher) {
            appContainer.getMoviesUseCase()
        }

        onStepChanged(MovieMateStartupStep.LoadStartView)
        return appContainer
    }
}
