package de.fh.erfurt.moviemate.presentation.movies.backup

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.movies.presentation.backup.BackupUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun BackupRoute(
    appContainer: AppContainer?,
    modifier: Modifier = Modifier,
    ioDispatcher: CoroutineDispatcher
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(BackupUiState()) }

    val exportDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(BackupMimeType)
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val container = appContainer
        if (container == null) {
            state = BackupUiState(errorMessage = PreviewUnavailableMessage)
            return@rememberLauncherForActivityResult
        }

        scope.launch {
            state = BackupUiState(isLoading = true)
            runCatching {
                val jsonString = withContext(ioDispatcher) {
                    container.exportBackupUseCase()
                }
                writeTextToUri(
                    context = context,
                    uri = uri,
                    text = jsonString,
                    ioDispatcher = ioDispatcher
                )
            }.onSuccess {
                state = BackupUiState(isSuccess = true)
            }.onFailure { throwable ->
                state = BackupUiState(
                    errorMessage = throwable.backupMessage("Backup file could not be saved.")
                )
            }
        }
    }

    val importDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val container = appContainer
        if (container == null) {
            state = BackupUiState(errorMessage = PreviewUnavailableMessage)
            return@rememberLauncherForActivityResult
        }

        scope.launch {
            state = BackupUiState(isLoading = true)
            runCatching {
                val jsonString = readTextFromUri(
                    context = context,
                    uri = uri,
                    ioDispatcher = ioDispatcher
                )
                withContext(ioDispatcher) {
                    container.importBackupUseCase(jsonString)
                }
            }.onSuccess {
                state = BackupUiState(isSuccess = true)
            }.onFailure { throwable ->
                state = BackupUiState(
                    errorMessage = throwable.backupMessage("Backup file could not be imported.")
                )
            }
        }
    }

    BackupScreen(
        state = state,
        onExportClicked = {
            if (appContainer == null) {
                state = BackupUiState(errorMessage = PreviewUnavailableMessage)
            } else {
                exportDocumentLauncher.launch(DefaultBackupFileName)
            }
        },
        onImportClicked = {
            if (appContainer == null) {
                state = BackupUiState(errorMessage = PreviewUnavailableMessage)
            } else {
                importDocumentLauncher.launch(BackupImportMimeTypes)
            }
        },
        modifier = modifier
    )
}

private suspend fun writeTextToUri(
    context: Context,
    uri: Uri,
    text: String,
    ioDispatcher: CoroutineDispatcher
) {
    withContext(ioDispatcher) {
        val outputStream = context.contentResolver.openOutputStream(uri)
            ?: error("Selected backup file could not be opened.")
        outputStream.use { stream ->
            stream.write(text.toByteArray(Charsets.UTF_8))
        }
    }
}

private suspend fun readTextFromUri(
    context: Context,
    uri: Uri,
    ioDispatcher: CoroutineDispatcher
): String {
    return withContext(ioDispatcher) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: error("Selected backup file could not be opened.")
        inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
            reader.readText()
        }
    }
}

private fun Throwable.backupMessage(
    fallback: String
): String {
    return message?.takeIf { it.isNotBlank() } ?: fallback
}

private const val BackupMimeType = "application/json"
private const val DefaultBackupFileName = "moviemate-backup.json"
private const val PreviewUnavailableMessage = "Backup is only available in the running app."

private val BackupImportMimeTypes = arrayOf(
    "application/json",
    "text/json",
    "text/plain",
    "*/*"
)
