# Homescreen UI Guide

## Aufbau

- `HomeRoute.kt` ist der Android-Container. Die Route erstellt das AndroidX-
  `HomeViewModel`, sammelt dessen `StateFlow` lifecycle-aware und verarbeitet
  Navigationseffekte.
- `HomeViewModel.kt` lädt Filme und Kategorien über UseCases und verarbeitet Suche,
  Tabs, Kategorie-Filter, Sortierung und Löschen.
- `HomeScreen.kt` ist die stateless Compose-Oberfläche. Sie rendert `HomeUiState`
  und meldet ausschließlich `HomeUiAction`.
- `MovieUiMappers.kt` übersetzt Domain-Daten explizit in renderorientierte UI-Modelle.

## Datenfluss

```text
HomeScreen
  -> HomeUiAction
  -> HomeViewModel
  -> UseCase
  -> Domain-Daten
  -> UI-Mapping
  -> HomeUiState
  -> HomeScreen
```

Navigation zu Detail oder Add wird als `HomeEffect` emittiert und erst in der Route
ausgeführt. Die Filmliste verwendet die Film-ID als stabilen LazyColumn-Key.

## Testbarkeit

`HomeScreen` kann ohne ViewModel in Previews gerendert werden. `HomeViewModelTest`
prüft getrennt davon State-Übergänge und Navigationseffekte.
