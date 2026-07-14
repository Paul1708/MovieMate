package de.fh.erfurt.moviemate.presentation.app

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import de.fh.erfurt.moviemate.presentation.movies.add.AddMovieRoute
import de.fh.erfurt.moviemate.presentation.movies.backup.BackupRoute
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoriesRoute
import de.fh.erfurt.moviemate.presentation.movies.detail.MovieDetailRoute
import de.fh.erfurt.moviemate.presentation.movies.home.HomeRoute
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors

@Composable
fun MovieMateApp(
    appContainer: AppContainer? = null,
    dispatchers: MovieMateDispatchers = MovieMateDispatcherDefaults.Default
) {
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
            modifier = Modifier.padding(paddingValues),
            enterTransition = { movieMateEnterTransition() },
            exitTransition = { movieMateExitTransition() },
            popEnterTransition = { movieMateEnterTransition() },
            popExitTransition = { movieMateExitTransition() },
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

                HomeRoute(
                    appContainer = appContainer,
                    ioDispatcher = dispatchers.io,
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
                CategoriesRoute(
                    appContainer = appContainer,
                    ioDispatcher = dispatchers.io,
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

                MovieDetailRoute(
                    appContainer = appContainer,
                    ioDispatcher = dispatchers.io,
                    movieId = movieId,
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }

            composable(MovieMateDestination.Add) {
                AddMovieRoute(
                    appContainer = appContainer,
                    ioDispatcher = dispatchers.io,
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

private fun movieMateEnterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = NavigationFadeInDurationMillis,
            delayMillis = NavigationFadeOutDurationMillis,
        )
    )
}

private fun movieMateExitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(durationMillis = NavigationFadeOutDurationMillis)
    )
}

private const val NavigationFadeOutDurationMillis = 90
private const val NavigationFadeInDurationMillis = 140
