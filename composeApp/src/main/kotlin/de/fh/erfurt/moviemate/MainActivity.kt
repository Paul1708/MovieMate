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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import kotlin.time.Duration.Companion.milliseconds

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
    var retryCount by remember { mutableIntStateOf(0) }
    var isOpeningVisible by remember { mutableStateOf(true) }
    var openingAlpha by remember { mutableFloatStateOf(1f) }

    RunStartupEffect(
        applicationContext = applicationContext,
        retryCount = retryCount,
        onStartupStateChanged = { startupState = it },
        onOpeningVisibilityChanged = { isOpeningVisible = it },
        onOpeningAlphaChanged = { openingAlpha = it }
    )

    FadeOpeningOnReadyEffect(
        startupState = startupState,
        onOpeningAlphaChanged = { openingAlpha = it },
        onOpeningHidden = { isOpeningVisible = false }
    )

    MovieMateStartupContent(
        startupState = startupState,
        isOpeningVisible = isOpeningVisible,
        openingAlpha = openingAlpha,
        onRetryClicked = { retryCount += 1 }
    )
}

@Composable
private fun RunStartupEffect(
    applicationContext: Context,
    retryCount: Int,
    onStartupStateChanged: (MovieMateStartupState) -> Unit,
    onOpeningVisibilityChanged: (Boolean) -> Unit,
    onOpeningAlphaChanged: (Float) -> Unit
) {
    LaunchedEffect(applicationContext, retryCount) {
        onStartupStateChanged(MovieMateStartupState.Loading())
        onOpeningVisibilityChanged(true)
        onOpeningAlphaChanged(1f)

        val openingStartedAt = SystemClock.uptimeMillis()
        runCatching {
            MovieMateStartupRunner(
                applicationContext = applicationContext,
                ioDispatcher = Dispatchers.IO
            ).run { step ->
                onStartupStateChanged(MovieMateStartupState.Loading(step))
            }
        }.onSuccess { container ->
            delayRemainingOpeningTime(openingStartedAt)
            onStartupStateChanged(MovieMateStartupState.Ready(container))
        }.onFailure { throwable ->
            onStartupStateChanged(MovieMateStartupState.Error(throwable.startupMessage()))
        }
    }
}

@Composable
private fun FadeOpeningOnReadyEffect(
    startupState: MovieMateStartupState,
    onOpeningAlphaChanged: (Float) -> Unit,
    onOpeningHidden: () -> Unit
) {
    LaunchedEffect(startupState) {
        if (startupState !is MovieMateStartupState.Ready) {
            return@LaunchedEffect
        }

        fadeOpening(onOpeningAlphaChanged)
        onOpeningHidden()
    }
}

@Composable
private fun MovieMateStartupContent(
    startupState: MovieMateStartupState,
    isOpeningVisible: Boolean,
    openingAlpha: Float,
    onRetryClicked: () -> Unit
) {
    MovieMateTheme {
        val readyState = startupState as? MovieMateStartupState.Ready
        Box(modifier = Modifier.fillMaxSize()) {
            if (readyState != null) {
                MovieMateApp(appContainer = readyState.appContainer)
            }

            if (isOpeningVisible) {
                val errorState = startupState as? MovieMateStartupState.Error
                val retryAction = if (errorState != null) {
                    onRetryClicked
                } else {
                    null
                }

                MovieMateOpeningScreen(
                    message = startupState.startupMessage(),
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

private suspend fun delayRemainingOpeningTime(openingStartedAt: Long) {
    val elapsedDuration = (SystemClock.uptimeMillis() - openingStartedAt).milliseconds
    val remainingOpeningDuration = MinimumOpeningDuration - elapsedDuration

    if (remainingOpeningDuration.isPositive()) {
        delay(remainingOpeningDuration)
    }
}

private suspend fun fadeOpening(
    onOpeningAlphaChanged: (Float) -> Unit
) {
    val fadeStartedAt = withFrameNanos { frameTimeNanos -> frameTimeNanos }
    var progress = 0f

    while (progress < 1f) {
        val frameTimeNanos = withFrameNanos { it }
        progress = ((frameTimeNanos - fadeStartedAt).toFloat() / OpeningFadeOutDurationNanos)
            .coerceIn(0f, 1f)
        onOpeningAlphaChanged(1f - progress)
    }

    onOpeningAlphaChanged(0f)
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

private val MinimumOpeningDuration = 600.milliseconds
private const val OpeningFadeOutDurationMillis = 450L
private const val OpeningFadeOutDurationNanos = OpeningFadeOutDurationMillis * 1_000_000L
