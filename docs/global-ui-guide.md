# Android UI Architecture Guide

This guide documents the Jetpack Compose presentation layer in the `composeApp` module.
The native SwiftUI client in `iosApp` implements its own iOS presentation layer while sharing the same Kotlin Multiplatform `shared` module.

## Screen Architecture

Each major feature follows a consistent three-layer Unidirectional Data Flow (UDF) structure:

```text
Route
  -> instantiates a screen-scoped AndroidX ViewModel
  -> collects StateFlow with collectAsStateWithLifecycle()
  -> collects one-shot SharedFlow effects inside LaunchedEffect
  -> performs Android navigation and platform actions

ViewModel
  -> processes UiAction events
  -> invokes shared domain UseCases
  -> updates an immutable UiState
  -> emits one-shot Effects

Screen
  -> renders purely from UiState
  -> delegates user interactions outward via callbacks
```

Business-relevant coroutines run inside `viewModelScope`. Routes do not launch their own coroutine scopes for UseCase invocations. `AppContainer` provides UseCases and shared dependencies, without holding long-lived ViewModel instances.

## State, Actions, and Effects

- `UiState` represents durable, renderable screen state.
- `UiAction` models user interactions and lifecycle triggers.
- `Effect` models one-shot side effects such as navigation, snackbars/toasts, or system file pickers.

Initial loading is dispatched as an action to the ViewModel. ViewModels never perform navigation directly; they emit a semantic navigation intent handled by the Route or `MovieMateApp`.

## Shared UI Components

- `MovieMateScreenBackground.kt`, `MovieMateScreenDefaults.kt`: shared screen containers and layout defaults.
- `SearchField.kt`: reusable search bar across list screens.
- `ScreenStateContent.kt`: standardized Loading, Empty, and Error state composables.
- `TopBarIconButton.kt`: uniform top-bar icon actions with consistent touch targets.
- `DeleteModeControls.kt`, `SelectionOptionRow.kt`: reusable delete-mode controls and dialog option rows.
- `MovieMateColors.kt`, `MovieMateSpacing.kt`, `MovieMateShapes.kt`: design tokens.
- `MovieMateIcons.kt`: bundled drawable icon catalog.

## Navigation

`MovieMateDestination.kt` encapsulates Android route definitions. Leaf composables remain unaware of route strings and emit semantic actions, while `MovieMateApp.kt` coordinates the `NavHost`, bottom navigation, and back stack.

## Lists and Previews

Dynamic lazy lists use stable domain IDs as item keys. Stateless screens provide `@Preview` functions backed by standalone preview fixtures so they render without repositories or ViewModels.

## Testing

ViewModel unit tests verify state transitions and one-shot effects using fake repositories and test dispatchers. Compose previews allow visual inspection of Loading, Empty, Error, and Content states in isolation.
