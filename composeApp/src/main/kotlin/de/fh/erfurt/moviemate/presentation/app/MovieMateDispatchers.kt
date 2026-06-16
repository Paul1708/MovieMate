package de.fh.erfurt.moviemate.presentation.app

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

data class MovieMateDispatchers(
    val io: CoroutineDispatcher
)

object MovieMateDispatcherDefaults {
    @Suppress("InjectDispatcher")
    val Default = MovieMateDispatchers(
        io = Dispatchers.IO
    )
}
