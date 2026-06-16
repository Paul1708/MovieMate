package de.fh.erfurt.moviemate

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import de.fh.erfurt.moviemate.presentation.app.MovieMateApp
import de.fh.erfurt.moviemate.presentation.app.opening.MovieMateOpeningScreen
import de.fh.erfurt.moviemate.presentation.app.startup.MovieMateStartupRunner
import de.fh.erfurt.moviemate.presentation.app.startup.MovieMateStartupState
import de.fh.erfurt.moviemate.presentation.app.startup.MovieMateStartupStep
import de.fh.erfurt.moviemate.presentation.theme.MovieMateTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MovieMateStartup(applicationContext = applicationContext)
        }
    }
}

@Composable
private fun MovieMateStartup(
    applicationContext: Context
) {
    var startupState by remember { mutableStateOf<MovieMateStartupState>(MovieMateStartupState.Loading()) }
    var retryCount by remember { mutableStateOf(0) }
    var isOpeningVisible by remember { mutableStateOf(true) }
    var openingAlpha by remember { mutableStateOf(1f) }

    LaunchedEffect(applicationContext, retryCount) {
        startupState = MovieMateStartupState.Loading()
        isOpeningVisible = true
        openingAlpha = 1f

        val openingStartedAt = SystemClock.uptimeMillis()
        runCatching {
            MovieMateStartupRunner(
                applicationContext = applicationContext,
                ioDispatcher = Dispatchers.IO
            ).run { step ->
                startupState = MovieMateStartupState.Loading(step)
            }
        }.onSuccess { container ->
            val elapsedMillis = SystemClock.uptimeMillis() - openingStartedAt
            val remainingOpeningMillis = MinimumOpeningDurationMillis - elapsedMillis

            if (remainingOpeningMillis > 0) {
                delay(remainingOpeningMillis)
            }

            startupState = MovieMateStartupState.Ready(container)
        }.onFailure { throwable ->
            startupState = MovieMateStartupState.Error(throwable.startupMessage())
        }
    }

    LaunchedEffect(startupState) {
        if (startupState is MovieMateStartupState.Ready) {
            val fadeStartedAt = withFrameNanos { frameTimeNanos -> frameTimeNanos }
            var progress = 0f

            while (progress < 1f) {
                val frameTimeNanos = withFrameNanos { it }
                progress = ((frameTimeNanos - fadeStartedAt).toFloat() / OpeningFadeOutDurationNanos)
                    .coerceIn(0f, 1f)
                openingAlpha = 1f - progress
            }

            openingAlpha = 0f
            isOpeningVisible = false
        }
    }

    MovieMateTheme {
        val state = startupState
        val readyState = state as? MovieMateStartupState.Ready
        Box(modifier = Modifier.fillMaxSize()) {
            if (readyState != null) {
                MovieMateApp(appContainer = readyState.appContainer)
            }

            if (isOpeningVisible) {
                val errorState = state as? MovieMateStartupState.Error
                val retryAction = if (errorState != null) {
                    { retryCount += 1 }
                } else {
                    null
                }

                MovieMateOpeningScreen(
                    message = state.startupMessage(),
                    errorMessage = errorState?.message,
                    onRetryClicked = retryAction,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(openingAlpha)
                )
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

private fun MovieMateStartupState.startupMessage(): String {
    return when (this) {
        is MovieMateStartupState.Loading -> step.message
        is MovieMateStartupState.Ready -> MovieMateStartupStep.LoadStartView.message
        is MovieMateStartupState.Error -> "Start fehlgeschlagen"
    }
}

private fun Throwable.startupMessage(): String {
    val detail = message?.takeIf { it.isNotBlank() }
    return if (detail == null) {
        "Die App konnte nicht vorbereitet werden."
    } else {
        "Die App konnte nicht vorbereitet werden: $detail"
    }
}

private const val MinimumOpeningDurationMillis = 600L
private const val OpeningFadeOutDurationMillis = 450L
private const val OpeningFadeOutDurationNanos = OpeningFadeOutDurationMillis * 1_000_000L
