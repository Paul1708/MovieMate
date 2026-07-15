package de.fh.erfurt.moviemate.movies.domain.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * App-wide monotonic signal for successful local data mutations.
 *
 * Presentation layers can observe [revision] and debounce cloud sync work without
 * coupling the repositories to a platform lifecycle or background scheduler.
 */
class DataChangeNotifier {
    private val mutableRevision = MutableStateFlow(0L)

    val revision: StateFlow<Long> = mutableRevision.asStateFlow()

    fun notifyDataChanged() {
        mutableRevision.update { current ->
            if (current == Long.MAX_VALUE) 0L else current + 1L
        }
    }
}
