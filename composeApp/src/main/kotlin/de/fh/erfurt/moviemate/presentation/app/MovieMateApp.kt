package de.fh.erfurt.moviemate.presentation.app

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.movies.presentation.backup.BackupAccountState
import de.fh.erfurt.moviemate.presentation.app.navigation.BottomBar
import de.fh.erfurt.moviemate.presentation.app.navigation.BottomNavItem
import de.fh.erfurt.moviemate.presentation.app.navigation.MovieMateDestination
import de.fh.erfurt.moviemate.presentation.app.navigation.selectedBottomNavItem
import de.fh.erfurt.moviemate.presentation.app.navigation.toRoute
import de.fh.erfurt.moviemate.presentation.movies.add.AddMovieRoute
import de.fh.erfurt.moviemate.presentation.movies.backup.BackupRoute
import de.fh.erfurt.moviemate.presentation.movies.backup.BackupSyncCoordinator
import de.fh.erfurt.moviemate.presentation.movies.backup.GoogleDriveAuthorizationHandler
import de.fh.erfurt.moviemate.presentation.movies.backup.GoogleDriveSyncController
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoriesRoute
import de.fh.erfurt.moviemate.presentation.movies.detail.MovieDetailRoute
import de.fh.erfurt.moviemate.presentation.movies.home.HomeRoute
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MovieMateApp(
    appContainer: AppContainer? = null,
    dispatchers: MovieMateDispatchers = MovieMateDispatcherDefaults.Default
) {
    val applicationContext = LocalContext.current.applicationContext
    val appScope = rememberCoroutineScope()
    val syncCoordinator = remember(appContainer, applicationContext, dispatchers.io) {
        appContainer?.let { container ->
            GoogleDriveSyncController(
                context = applicationContext,
                coroutineScope = appScope,
                ioDispatcher = dispatchers.io,
                syncBackup = { accessToken, accountId ->
                    container.syncWithGoogleDriveUseCase(accessToken, accountId)
                    Unit
                },
            )
        }
    }
    val authorizationHandler = remember(appContainer, applicationContext) {
        appContainer?.let { GoogleDriveAuthorizationHandler(applicationContext) }
    }
    GoogleDriveAppSyncEffects(
        appContainer = appContainer,
        syncCoordinator = syncCoordinator,
        authorizationHandler = authorizationHandler,
    )

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
                    syncCoordinator = syncCoordinator,
                    authorizationHandler = authorizationHandler,
                    ioDispatcher = dispatchers.io,
                )
            }
        }
    }
}

@Composable
private fun GoogleDriveAppSyncEffects(
    appContainer: AppContainer?,
    syncCoordinator: GoogleDriveSyncController?,
    authorizationHandler: GoogleDriveAuthorizationHandler?,
) {
    if (appContainer == null || syncCoordinator == null || authorizationHandler == null) return

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, syncCoordinator, authorizationHandler) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            silentlyAuthorizeGoogleDrive(syncCoordinator, authorizationHandler)
            while (true) {
                delay(ForegroundSyncIntervalMillis)
                silentlyAuthorizeGoogleDrive(syncCoordinator, authorizationHandler)
            }
        }
    }

    DisposableEffect(lifecycleOwner, syncCoordinator, authorizationHandler) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                silentlyAuthorizeGoogleDrive(syncCoordinator, authorizationHandler)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(appContainer, syncCoordinator) {
        appContainer.dataChangeRevision.collectLatest {
            delay(AutoSyncDebounceMillis)
            // Reauthorize first so long-running foreground sessions never reuse an expired token.
            silentlyAuthorizeGoogleDrive(syncCoordinator, authorizationHandler)
        }
    }
}

private fun silentlyAuthorizeGoogleDrive(
    syncCoordinator: BackupSyncCoordinator,
    authorizationHandler: GoogleDriveAuthorizationHandler,
) {
    val currentState = syncCoordinator.state.value
    if (currentState.accountState == BackupAccountState.SigningIn ||
        currentState.isSyncing ||
        currentState.isSigningOut
    ) {
        return
    }

    val storedSession = syncCoordinator.storedSession() ?: return
    syncCoordinator.authorizationStarted()
    authorizationHandler.authorize(
        preferredAccountName = storedSession.accountName,
        forceAccountSelection = false,
        onResolutionRequired = {
            // Silent restoration never opens UI; the user can reconnect from the Backup tab.
            syncCoordinator.authorizationCancelled(preserveStoredSession = true)
        },
        onAuthorized = syncCoordinator::authorizationSucceeded,
        onFailure = { message ->
            syncCoordinator.authorizationFailed(
                message = message,
                preserveStoredSession = true,
            )
        },
    )
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
private const val AutoSyncDebounceMillis = 750L
private const val ForegroundSyncIntervalMillis = 60_000L
