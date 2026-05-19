package de.fh.erfurt.moviemate.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.fh.erfurt.moviemate.presentation.categories.CategoriesRoute
import de.fh.erfurt.moviemate.presentation.home.HomeRoute

@Composable
fun MovieMateApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        containerColor = MovieMateColors.ScreenBackground,
        bottomBar = {
            BottomBar(
                selectedItem = currentRoute.selectedBottomNavItem(),
                onItemSelected = { item ->
                    navController.navigate(item.toRoute()) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = item != BottomNavItem.Home
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = MovieMateDestination.HomeRoute,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(
                route = MovieMateDestination.HomeRoute,
                arguments = listOf(
                    navArgument(MovieMateDestination.CategoryArg) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val initialCategory = backStackEntry.arguments
                    ?.getString(MovieMateDestination.CategoryArg)
                    .toCategoryTypeOrNull()

                HomeRoute(
                    initialCategory = initialCategory,
                    onMovieSelected = { movieId ->
                        navController.navigate(MovieMateDestination.detailRoute(movieId))
                    }
                )
            }

            composable(MovieMateDestination.Categories) {
                CategoriesRoute(
                    onCategorySelected = { category ->
                        navController.navigate(MovieMateDestination.homeRoute(category)) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                )
            }

            composable(
                route = MovieMateDestination.DetailRoute,
                arguments = listOf(
                    navArgument(MovieMateDestination.MovieIdArg) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments
                    ?.getString(MovieMateDestination.MovieIdArg)
                    .orEmpty()

                PlaceholderScreen(
                    title = "Details",
                    message = "Detail screen for $movieId"
                )
            }

            composable(MovieMateDestination.Add) {
                PlaceholderScreen(
                    title = "Add",
                    message = "Add screen is prepared."
                )
            }

            composable(MovieMateDestination.Backup) {
                PlaceholderScreen(
                    title = "Backup",
                    message = "Backup screen is prepared."
                )
            }
        }
    }
}
