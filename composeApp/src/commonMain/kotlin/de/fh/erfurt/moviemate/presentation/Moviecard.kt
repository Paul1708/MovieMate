package de.fh.erfurt.moviemate.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.domain.model.Category


@Composable
fun MovieCard(
    title: String,
    year: String,
    time: String,
    category:  String,
    isWatched: Boolean,
    modifier: Modifier = Modifier
) {
    Card (
        modifier= modifier
            .width(110.dp)
            .height(160.dp),
        shape= RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF9B0))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween

        ) {
            Box(
                modifier= modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Poster")
            }

            Text(title)
            Text(year)
            Text(time)
            Text(category)

            Text(
                text= if (isWatched) "Watched" else "Not Watched"
            )
        }
    }
}