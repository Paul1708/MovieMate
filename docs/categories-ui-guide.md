# Categories UI Guide

## Aufbau

- `CategoriesRoute.kt` ist der Android-Container und sammelt State und Effects des
  screen-spezifischen AndroidX-ViewModels.
- `CategoriesViewModel.kt` lädt Kategorien und Filme über UseCases, berechnet die
  Filmanzahl, verarbeitet Suche sowie Add-/Delete-Aktionen und hält den gesamten
  renderbaren `CategoriesUiState`.
- `CategoriesScreen.kt` ist stateless und meldet ausschließlich
  `CategoriesUiAction`.

## Datenfluss

```text
CategoriesScreen
  -> CategoriesUiAction
  -> CategoriesViewModel
  -> Category-/Movie-UseCases
  -> CategoryUiModel
  -> CategoriesUiState
  -> CategoriesScreen
```

Ein Kategorie-Klick emittiert einen semantischen Navigationseffekt. Die Route gibt
diesen an `MovieMateApp` weiter. Das Grid verwendet Kategorie-IDs und der Icon-Picker
Icon-Keys als stabile Identitäten.

## Lokaler Dialogzustand

Der Add-Dialog ist dauerhafter Render-State des Screens und deshalb Teil von
`CategoriesUiState`. Validierungsfeedback bleibt ebenfalls im State. Navigation und
andere einmalige Ausgaben werden dagegen als Effects modelliert.
