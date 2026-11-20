# Homescreen UI Guide

## Structure

- `HomeRoute.kt` is the Android container. It creates the screen-scoped AndroidX `HomeViewModel`, collects its `StateFlow` lifecycle-aware, and handles navigation effects.
- `HomeViewModel.kt` loads movies and categories via shared UseCases and handles search, tabs (`All`, `Watched`, `Watchlist`), category filtering, sorting, and deletion.
- `HomeScreen.kt` is the stateless Jetpack Compose screen. It renders `HomeUiState` and dispatches `HomeUiAction` events.
- `HomeTopBar.kt` and `HomeFilterDialog.kt` provide top-bar controls and category/sort selection dialogs.
- `MovieUiMappers.kt` explicitly maps domain models to presentation-ready UI models.

## Data Flow

```text
HomeScreen
  -> HomeUiAction
  -> HomeViewModel
  -> UseCases
  -> Domain Models
  -> UI Mapping
  -> HomeUiState
  -> HomeScreen
```

Navigation to movie details or the add-movie flow is emitted as a one-shot `HomeEffect` and executed in `HomeRoute.kt`. The movie list uses each movie's domain ID as a stable `LazyColumn` key.

## Testability

`HomeScreen` renders in `@Preview` functions without a ViewModel. `HomeViewModelTest.kt` tests state transitions, filtering, sorting, and navigation effects independently.
