# Homescreen UI Guide

Diese Datei beschreibt den aktuellen Stand der Homescreen-UI und erklärt, welche Teile Runtime-Mockdaten nutzen und welche Teile später bei der Backend-Anbindung bleiben sollen.

## Aktueller Stand

Der Homescreen ist weiterhin eine UI-Implementierung mit Mockdaten. Die Testfilme liegen aber nicht mehr im Home-Paket, sondern zentral in `presentation/mock`. Dadurch nutzen Home und Kategorien dieselbe Filmliste.

Aktuell vorhanden:

- Homescreen mit Titel, Tabs, Suche, Filter, Sortierung, Filmliste und BottomBar
- vertikale Movie Cards im Stil des Mockups
- lokale Suche auf Sample-Filmen
- lokale Tab-Filterung: `All`, `Watched`, `Watch later`
- lokaler Kategorie-Filter
- lokaler Sortierdialog
- gemeinsame Loading-, Empty- und Error-Komponenten
- MovieCard-Klick als Navigation zum Detail-Platzhalter
- Mapper von Domain-`Movie` zu `MovieUiModel`

## Wichtige Dateien

### `HomeScreen.kt`

Enthält Route und sichtbare Oberfläche des Homescreens.

- `HomeRouteMock(...)` erzeugt aktuell ein `HomeViewModel` mit Sample-Daten.
- `HomeScreen(...)` ist die reine Compose-UI.
- `onMovieSelected` ist vorbereitet, damit später der Detail-Screen geöffnet werden kann.
- Die UI kennt keine Repository- oder Backend-Details.

### `HomeViewModel.kt`

Ein einfaches ViewModel-Skelett für Mockdaten. Es nutzt noch kein Lifecycle-ViewModel, ist aber bewusst so benannt, weil es später in diese Richtung migrieren soll.

Er verarbeitet:

- Tab-Wechsel
- Suche
- Filter öffnen und schließen
- Kategorie-Auswahl
- Sortierung
- Clear Filter
- Retry als Platzhalter

Später kann diese Klasse durch ein echtes ViewModel ersetzt werden, das `GetMoviesUseCase` nutzt.

### `HomeUiState.kt` und `HomeUiAction.kt`

`HomeUiState` beschreibt den gesamten Anzeigezustand. `HomeUiAction` beschreibt alle Nutzeraktionen.

Dadurch bleibt `HomeScreen` möglichst dumm: Der Screen zeigt State an und meldet Aktionen nach außen.

### `MovieUiModel.kt`

UI-Modell für eine Filmkarte.

Es enthält zwei Arten von Daten:

- Rohwerte für Logik: `rating`, `categoryType`, `publishingYear`, `runtimeMinutes`
- formatierte Texte für Anzeige: `ratingText`, `categoryText`, `yearText`, `runtimeText`

Dadurch muss die UI nicht aus Texten zurückrechnen, wenn gesucht, gefiltert oder sortiert wird.

### `MovieUiMapper.kt`

Übersetzt Domain-Filme in UI-Filme:

```kotlin
Movie.toUiModel()
```

Diese Mapper-Funktion ist die wichtigste Vorbereitung für echte Daten aus dem Backend.

### `HomePreviewData.kt`

Enthält nur noch Preview-Hilfen für Compose:

- `previewHomeMovies()`
- `previewHomeUiState()`

Die echten Sample-Filme liegen zentral in `presentation/mock/MockMovieData.kt`.

## Mockdaten

Runtime-Mockdaten kommen aus:

```text
presentation/mock/MockMovieData.kt
presentation/mock/MovieMateMockStore.kt
```

Der Homescreen bekommt die Filme über `HomeRouteMock(movies = ...)`. In der App kommt diese Liste aktuell aus `MovieMateMockStore`.

## Was später bleiben soll

Diese Teile sind auch für die echte App sinnvoll:

- `HomeScreen`
- `HomeUiState`
- `HomeUiAction`
- `MovieUiModel`
- `Movie.toUiModel()`
- `HomeTopBar`
- `HomeFilterDialog`
- `HomeSortDialog`
- `MovieCard`
- gemeinsame UI-Bausteine wie `SearchField` und `ScreenStateContent`

## Was später ersetzt werden kann

Diese Teile sind hauptsächlich für Mockbetrieb oder Previews gedacht:

- `HomeRouteMock()` als Runtime-Einstieg
- `HomeViewModel` in der aktuellen Form
- lokale Filterlogik in `HomeViewModel`
- lokale Sortierlogik in `HomeViewModel`
- `sampleMovies()` für Runtime-Daten
- Poster-Platzhalter in `MovieCard`

Für Compose Previews sollten Sample-Daten weiter behalten werden.

## Spätere Backend-Anbindung

Der spätere Datenfluss sollte ungefähr so aussehen:

```text
HomeScreen
  -> HomeUiAction
  -> echtes ViewModel
  -> GetMoviesUseCase
  -> MovieRepository
  -> List<Movie>
  -> Movie.toUiModel()
  -> HomeUiState
```

Die aktuelle lokale Logik lässt sich später durch einen UseCase-Aufruf ersetzen:

```kotlin
getMoviesUseCase(
    searchQuery = state.searchQuery,
    categoryType = state.selectedCategory,
    isWatched = state.selectedTab.toWatchedFilter(),
    sortBy = state.selectedSortCriterion
)
```

## Nächste mögliche Schritte ohne Backend

- Tests für Filterung und Sortierung ergänzen
- echte Icons statt Text-Symbolen vorbereiten
- Posteranzeige vorbereiten
- Detail-Platzhalter später durch echten Detail-Screen ersetzen
