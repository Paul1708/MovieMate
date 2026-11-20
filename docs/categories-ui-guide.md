# Categories UI Guide

## Structure

- `CategoriesRoute.kt` is the Android container that collects `StateFlow` state and one-shot effects from the screen-scoped AndroidX `CategoriesViewModel`.
- `CategoriesViewModel.kt` loads categories and movies via shared UseCases, computes per-category movie counts, and handles search as well as add and delete actions inside `CategoriesUiState`.
- `CategoriesScreen.kt` is stateless and emits only `CategoriesUiAction` events.
- `CategoryIconCatalog.kt` and `CategoryGenreIcon.kt` map category icon keys to bundled Material Symbols vector assets.

## Data Flow

```text
CategoriesScreen
  -> CategoriesUiAction
  -> CategoriesViewModel
  -> Category & Movie UseCases
  -> CategoryUiModel
  -> CategoriesUiState
  -> CategoriesScreen
```

Selecting a category emits a semantic navigation effect that `CategoriesRoute.kt` forwards to `MovieMateApp`. The category grid uses category IDs—and the icon picker uses icon keys—as stable item keys.

## Local Dialog State

The add-category dialog is durable screen state and lives inside `CategoriesUiState`, along with inline validation feedback. Navigation and other transient actions are modeled as one-shot effects.
