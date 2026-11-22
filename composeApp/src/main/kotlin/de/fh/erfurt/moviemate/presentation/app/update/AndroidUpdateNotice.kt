package de.fh.erfurt.moviemate.presentation.app.update

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import de.fh.erfurt.moviemate.BuildConfig
import de.fh.erfurt.moviemate.R

@Composable
internal fun AndroidUpdateNotice() {
    var release by remember { mutableStateOf<AndroidUpdate?>(null) }
    var dismissedVersionCode by rememberSaveable { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        release = findAndroidUpdate(BuildConfig.VERSION_CODE)
    }

    val update = release?.takeUnless { it.versionCode == dismissedVersionCode } ?: return
    val context = LocalContext.current
    val dismiss = { dismissedVersionCode = update.versionCode }

    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text(stringResource(R.string.update_title)) },
        text = { Text(stringResource(R.string.update_message, update.versionName)) },
        confirmButton = {
            TextButton(onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(update.downloadUrl)))
                    dismiss()
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(context, R.string.update_open_failed, Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(stringResource(R.string.update_download))
            }
        },
        dismissButton = {
            TextButton(onClick = dismiss) { Text(stringResource(R.string.update_later)) }
        },
    )
}
