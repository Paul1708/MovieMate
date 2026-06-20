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
            // TODO Backend-Umstellung: Prüfen, ob hier nur lokale Kategorien gelesen werden
            // oder ob ein kurzer Remote-Status-/Sync-Check nötig ist. Längere Synchronisationen
            // sollten nach dem App-Start im Hintergrund laufen.
            appContainer.getCategoriesUseCase()
        }

        onStepChanged(MovieMateStartupStep.LoadMovies)
        withContext(ioDispatcher) {
            // TODO Backend-Umstellung: Beim Wechsel von Preview-/Mockdaten auf echte Daten
            // sollte dieser Aufruf dieselbe Quelle vorbereiten, die Home später anzeigt.
            appContainer.getMoviesUseCase()
        }

        onStepChanged(MovieMateStartupStep.LoadStartView)
        // TODO Backend-Umstellung: Wenn Home und Kategorien nicht mehr über Preview-Routen
        // laufen, hier nur noch notwendige Startdaten prüfen und keine komplette Backend-Ladung blockieren.
        return appContainer
    }
}
