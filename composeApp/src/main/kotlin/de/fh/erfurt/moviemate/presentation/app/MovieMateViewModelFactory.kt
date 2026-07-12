package de.fh.erfurt.moviemate.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CancellationException

/** Creates screen-scoped Android ViewModels with explicit constructor dependencies. */
fun <VM : ViewModel> movieMateViewModelFactory(
    create: () -> VM,
): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = create() as T
    }
}

internal fun Throwable.throwIfCancellation() {
    if (this is CancellationException) throw this
}
