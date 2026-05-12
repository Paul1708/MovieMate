# Homescreen UI Guide

Diese Datei beschreibt den aktuellen Stand der Homescreen-UI, wie die einzelnen Teile zusammenspielen und welche Bereiche nur für Mockdaten gedacht sind.

## Aktueller Stand

Der Homescreen ist aktuell eine reine UI-Implementierung mit Mockdaten. Er ist so aufgebaut, dass später echte Filme über Shared-UseCases und Repository angebunden werden können, ohne die sichtbare UI stark umzubauen.

Aktuell vorhanden:

- Homescreen mit Titel, Tabs, Suche, Filter, Filmliste und BottomBar
- vertikale Movie Cards wie im Mockup
- lokale Suche auf Mockdaten
- lokale Tab-Filterung: `All`, `Watched`, `watch later`
- lokaler Filter-Dialog für Kategorie und Sortierung
- Loading-, Empty- und Error-State als eigene UI-Bausteine
- Preview-Daten, die bereits über das Domain-Modell `Movie` laufen
- Mapper von `Movie` zu `MovieUiModel`
- einfacher `HomeViewModel`-State-Holder als Platzhalter für ein echtes ViewModel

## Wichtige Dateien

### `HomeScreen.kt`

Enthält die sichtbare Homescreen-Oberfläche.

Wichtige Funktionen:

- `HomeRouteMock()`: Einstieg für die App während der UI-Entwicklung.
- `HomeScreen(...)`: reine UI, bekommt State und sendet Aktionen nach außen.
- mehrere `@Preview`-Funktionen für normale Liste, Empty, Loading und Error.

Die UI kennt keine Backend-Details. Sie arbeitet nur mit:

```kotlin
HomeUiState
HomeUiAction
```

### `HomeViewModel.kt`

Ein einfacher State-Holder für Mockdaten.

Er verarbeitet:

- Tab-Wechsel
- Suche
- Filter öffnen/schließen
- Kategorie-Auswahl
- Sortierung
- Clear Filter
- Retry als Platzhalter

Wichtig: Das ist noch kein echtes Backend-ViewModel. Es ist nur so strukturiert, dass später `GetMoviesUseCase` eingebaut werden kann.

### `HomeUiState.kt`

Beschreibt den gesamten Anzeigezustand des Homescreens:

- Filme
- aktiver Tab
- Suchtext
- Sichtbarkeit des Suchfelds
- Kategorie-Filter
- Sortierung
- Sichtbarkeit des Filterdialogs
- Loading/Error

### `HomeUiAction.kt`

Beschreibt alle Nutzeraktionen im Homescreen.

Beispiele:

- `TabSelected`
- `SearchChanged`
- `FilterClicked`
- `CategorySelected`
- `SortSelected`
- `MovieClicked`

Dadurch muss `HomeScreen` nicht wissen, wie die Aktion verarbeitet wird.

### `MovieUiModel.kt`

UI-Modell für eine Filmkarte.

Dieses Modell enthält bereits formatierte Texte wie:

- `ratingText`
- `categoryText`
- `yearText`
- `runtimeText`

Die UI muss dadurch keine Domain-Formatierung kennen.

### `MovieUiMapper.kt`

Übersetzt Domain-Filme in UI-Filme:

```kotlin
Movie.toUiModel()
```

Das ist die wichtigste Vorbereitung für spätere Backend-Anbindung.

### `HomePreviewData.kt`

Enthält Mockdaten für Entwicklung und Previews.

Wichtig: Die Mockdaten werden zuerst als echte Domain-`Movie`-Objekte angelegt und danach mit `toUiModel()` in UI-Daten übersetzt. Dadurch wird der spätere Datenfluss schon simuliert.

### `HomeFilterDialog.kt`

Ein einfacher Dialog für Kategorie und Sortierung.

Aktuell filtert er nur lokale Mockdaten. Später können dieselben Werte an `GetMoviesUseCase` übergeben werden.

### `HomeStateContent.kt`

Enthält eigenständige UI-Bausteine für:

- `HomeLoadingState`
- `HomeEmptyState`
- `HomeErrorState`

Diese Zustände sind später direkt für Repository- oder Netzwerkzustände nutzbar.

### `Moviecard.kt`

Zeigt eine einzelne Filmkarte in der Liste.

Die Karte bekommt direkt ein `MovieUiModel` und zeigt:

- Poster-Platzhalter
- Titel
- Bewertung
- Kategorie
- Jahr
- Laufzeit

## Was aktuell nur Mock ist

Diese Teile sind nur für die UI-Entwicklung gedacht:

- `HomeRouteMock()`
- `HomeViewModel` in der aktuellen Form
- `previewDomainMovies()`
- `previewHomeMovies()`
- lokale Filterung in `HomeViewModel.filterMovies(...)`
- lokale Sortierung in `sortForHome(...)`
- leere Handler für `MovieClicked`
- Poster-Platzhalter in `MovieCard`

Diese Teile dürfen später ersetzt oder stark vereinfacht werden.

## Was später bleiben soll

Diese Teile sind auch für die echte App sinnvoll:

- `HomeScreen`
- `HomeUiState`
- `HomeUiAction`
- `MovieUiModel`
- `Movie.toUiModel()`
- `HomeTopBar`
- `HomeFilterDialog`
- `HomeLoadingState`
- `HomeEmptyState`
- `HomeErrorState`
- `MovieCard`
- `BottomBar` und `BottomNavItem`

## Spätere Backend-Anbindung

Der spätere Datenfluss sollte ungefähr so aussehen:

```text
HomeScreen
  -> HomeUiAction
  -> echtes HomeViewModel
  -> GetMoviesUseCase
  -> MovieRepository
  -> Local/Remote DataSource
  -> List<Movie>
  -> Movie.toUiModel()
  -> HomeUiState
```

Im echten ViewModel kann die aktuelle Mock-Logik ersetzt werden:

```kotlin
sourceMovies
```

durch:

```kotlin
getMoviesUseCase(
    searchQuery = state.searchQuery,
    categoryType = state.selectedCategory,
    isWatched = state.selectedTab.toWatchedFilter(),
    sortBy = state.selectedSortCriterion
)
```

## Was beim Umstieg weg kann

Wenn echte Daten angebunden sind, können diese Teile entfernt oder nur noch für Previews behalten werden:

- `HomeRouteMock()` als App-Einstieg
- Mockdaten in `HomePreviewData.kt` für Runtime
- lokale Filterlogik im Mock-`HomeViewModel`
- lokale Sortierlogik im Mock-`HomeViewModel`

Für Compose Previews sollten die Mockdaten aber weiter behalten werden.

## Nächste mögliche UI-Schritte

Ohne Backend kann man danach noch machen:

- visuelles Fine-Tuning nach Mockup
- echte Icons statt Text-Symbolen
- Posteranzeige vorbereiten
- BottomBar optisch weiter anpassen
- Detail-Navigation nur als Callback vorbereiten
- Tests für Mapper und Mock-ViewModel ergänzen
