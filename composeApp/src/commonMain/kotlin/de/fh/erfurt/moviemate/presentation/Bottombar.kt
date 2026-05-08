package de.fh.erfurt.moviemate.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomBar() {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Text("🏠") },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Text("▦") },
            label = { Text("Categories") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Text("+") },
            label = { Text("Add") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Text("⚙") },
            label = { Text("Settings") }
        )
    }
}