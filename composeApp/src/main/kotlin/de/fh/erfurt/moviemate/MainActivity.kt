package de.fh.erfurt.moviemate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.fh.erfurt.moviemate.platform.AndroidAppContainerFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = AndroidAppContainerFactory(applicationContext).create()

        setContent {
            App(appContainer = appContainer)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
