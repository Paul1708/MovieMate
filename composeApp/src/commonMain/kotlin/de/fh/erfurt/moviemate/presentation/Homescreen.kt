package de.fh.erfurt.moviemate.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

data class UiMovie(
    val title: String,
    val year: String,
    val category: String,
    val time: String,
    val isWatched: Boolean
)

@Composable
fun HomeScreen() {
    val movies = listOf(
        UiMovie("Inception", "2010", "action","169 min", false),
        UiMovie("Interstellar", "2014", "science fiction", "211 min", true),
        UiMovie("Matrix", "1999", "fantasy", "128 min", false),
        UiMovie("Avatar", "2009", "fantasy", "210 min", false),
        UiMovie("Joker", "2019", "action", "132 min", false),
        UiMovie("Titanic", "1997", "romance", "136 min", true)
    )

    Scaffold(
        bottomBar = {
            BottomBar()
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Find your movie") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text("All")
                Text("Watched")
                Text("Not watched")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(movies) { movie ->
                    MovieCard(
                        title = movie.title,
                        year = movie.year,
                        time = movie.time,
                        category = movie.category,
                        isWatched = movie.isWatched
                    )
                }
            }
        }
    }
}