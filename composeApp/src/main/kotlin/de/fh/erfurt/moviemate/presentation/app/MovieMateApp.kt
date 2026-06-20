package de.fh.erfurt.moviemate.presentation.app

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
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.presentation.app.navigation.BottomBar
import de.fh.erfurt.moviemate.presentation.app.navigation.BottomNavItem
import de.fh.erfurt.moviemate.presentation.app.navigation.MovieMateDestination
import de.fh.erfurt.moviemate.presentation.app.navigation.selectedBottomNavItem
import de.fh.erfurt.moviemate.presentation.app.navigation.toRoute
import de.fh.erfurt.moviemate.presentation.preview.AddMoviePreviewRoute
import de.fh.erfurt.moviemate.presentation.preview.CategoriesPreviewRoute
import de.fh.erfurt.moviemate.presentation.preview.HomePreviewRoute
import de.fh.erfurt.moviemate.presentation.preview.MovieDetailPreviewRoute
import de.fh.erfurt.moviemate.presentation.movies.backup.BackupRoute
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors

/**
 * App-Gerüst mit Navigation, Bottom Bar und den aktuell angebundenen Screens.
 */
@Composable
fun MovieMateApp(
    appContainer: AppContainer? = null,
    dispatchers: MovieMateDispatchers = MovieMateDispatcherDefaults.Default
) {
    // Der Container wird hier bereits angenommen, auch wenn die aktuellen
    // Routen noch mit Preview-State arbeiten. So kann später pro Route auf
    // echte Shared-ViewModels umgestellt werden, ohne den App-Einstieg erneut
    // umzubauen.
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
                val initialCategoryId = backStackEntry.arguments
                    ?.getString(MovieMateDestination.CategoryArg)

                HomePreviewRoute(
                    initialCategoryId = initialCategoryId,
                    onMovieSelected = { movieId ->
                        navController.navigate(MovieMateDestination.detailRoute(movieId))
                    },
                    onAddClicked = {
                        navController.navigate(MovieMateDestination.Add) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(MovieMateDestination.Categories) {
                CategoriesPreviewRoute(
                    onCategorySelected = { categoryId ->
                        navController.navigate(MovieMateDestination.homeRoute(categoryId)) {
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

                MovieDetailPreviewRoute(
                    movieId = movieId,
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }

            composable(MovieMateDestination.Add) {
                AddMoviePreviewRoute(
                    onCloseClicked = {
                        navController.popBackStack()
                    }
                )
            }

            composable(MovieMateDestination.Backup) {
                BackupRoute(
                    appContainer = appContainer,
                    ioDispatcher = dispatchers.io
                )
            }
        }
    }
}
