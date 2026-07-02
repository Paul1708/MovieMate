package de.fh.erfurt.moviemate.presentation.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Bottom navigation for Home, Categories, Add, and Backup.
 */
@Composable
fun BottomBar(
    selectedItem: BottomNavItem = BottomNavItem.Home,
    onItemSelected: (BottomNavItem) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(MovieMateSpacing.bottomBarHeight)
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(MovieMateSpacing.bottomBarHeight),
            containerColor = MovieMateColors.ScreenBackground,
            tonalElevation = MovieMateSpacing.noElevation
        ) {
            BottomNavItem.entries.forEach { item ->
                val iconColor = if (selectedItem == item) {
                    MovieMateColors.AccentLight
                } else {
                    MovieMateColors.SecondaryText
                }

                NavigationBarItem(
                    selected = selectedItem == item,
                    onClick = { onItemSelected(item) },
                    icon = {
                        BottomNavIcon(
                            icon = item.icon,
                            color = iconColor,
                            modifier = Modifier.size(MovieMateSpacing.bottomBarIconSize)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.offset(y = (-2).dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MovieMateColors.AccentLight,
                        selectedTextColor = MovieMateColors.AccentLight,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = MovieMateColors.SecondaryText,
                        unselectedTextColor = MovieMateColors.SecondaryText
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(MovieMateSpacing.dividerThickness)
                .background(MovieMateColors.CardBorder)
        )
    }
}
