# Global UI Guide

Diese Datei beschreibt die aktuelle Android-Presentation-Schicht von MovieMate.
Die iOS-Oberfläche ist nicht Bestandteil dieser UI-Implementierung.

## Screen-Architektur

Jedes größere Feature folgt demselben Aufbau:

```text
Route
  -> erstellt ein screen-spezifisches AndroidX-ViewModel
  -> sammelt StateFlow mit collectAsStateWithLifecycle()
  -> sammelt einmalige SharedFlow-Effects in LaunchedEffect
  -> führt Android-Navigation und Plattformaktionen aus

ViewModel
  -> verarbeitet UiAction/Event
  -> ruft UseCases auf
  -> aktualisiert unveränderlichen UiState
  -> emittiert einmalige Effects

Screen
  -> rendert ausschließlich über UiState
  -> meldet Benutzeraktionen nach außen
```

Businessrelevante Coroutines laufen in `viewModelScope`. Routes verwenden keine
eigenen Coroutine-Scopes für UseCase-Aufrufe. Der `AppContainer` stellt UseCases
bereit, besitzt aber keine langlebigen ViewModel-Instanzen.

## State, Actions und Effects

- `UiState` enthält dauerhaften, renderbaren Screen-Zustand.
- `UiAction` beschreibt Benutzer- und Systemereignisse.
- `Effect` beschreibt einmalige Ausgaben wie Navigation oder Dateiauswahl.

Initiales Laden wird als Action an das ViewModel gesendet. Navigation wird nicht im
ViewModel ausgeführt; das ViewModel emittiert nur eine semantische Absicht, die von
der Route bzw. `MovieMateApp` umgesetzt wird.

## Gemeinsame UI-Bausteine

- `ScreenHeader.kt`: einheitliche Header-Struktur.
- `SearchField.kt`: gemeinsames Suchfeld.
- `ScreenStateContent.kt`: Loading-, Empty- und Error-Zustände.
- `TopBarIconButton.kt`: einheitliche Topbar-Aktionen.
- `MovieMateColors.kt`, `MovieMateSpacing.kt`, `MovieMateShapes.kt`: Design-Tokens.
- `MovieMateIcons.kt`: wiederverwendbare App-Icons.

## Navigation

`MovieMateDestination.kt` kapselt die Android-Routen. Leaf-Composables kennen keine
Route-Strings. Sie melden semantische Actions, während `MovieMateApp.kt` den
NavController und den Backstack koordiniert.

## Listen und Previews

Dynamische Lazy-Listen verwenden stabile fachliche IDs als Keys. Stateless Screens
besitzen Previews mit eigenständigen Preview-Daten und benötigen dafür weder
Repositories noch ViewModels.

## Tests

ViewModel-Tests prüfen State-Übergänge und einmalige Effects. Reine Screen-Previews
bleiben zusätzlich erhalten, damit Loading-, Error-, Empty- und Content-Zustände
isoliert geprüft werden können.
