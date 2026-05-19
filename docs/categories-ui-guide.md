# Categories UI Guide

Diese Datei beschreibt den aktuellen Stand des Kategorie-Screens und wie er mit den gemeinsamen Mockdaten zusammenarbeitet.

## Aktueller Stand

Der Kategorie-Screen zeigt alle Kategorien aus `MovieCategoryTypes` als Grid. Die Anzahl der Filme pro Kategorie wird aus derselben Sample-Filmliste berechnet, die auch der Homescreen nutzt.

Aktuell vorhanden:

- Titel `Categories`
- Suchbutton in der Topbar
- einheitliches Suchfeld über `SearchField`
- Kategorie-Grid mit zwei Spalten
- Movie-Zähler pro Kategorie
- Loading-, Empty- und Error-State über gemeinsame UI-Bausteine
- Kategorie-Klick navigiert zum Homescreen mit aktivem Kategorie-Filter

## Wichtige Dateien

### `CategoriesScreen.kt`

Enthält Route und sichtbare Oberfläche des Kategorie-Screens.

- `CategoriesRoute(...)` sammelt `CategoriesViewModel.state` mit `collectAsStateWithLifecycle()`.
- `CategoriesScreen(...)` zeigt Header, Suche, State-Inhalte und Grid.
- `onCategorySelected` ist der vorbereitete Navigationspunkt zum Homescreen.

### `CategoriesViewModel.kt`

Ein AndroidX-`ViewModel` für Mockdaten. Es hält den Screen-State als `StateFlow<CategoriesUiState>` und filtert Kategorien lokal nach Suchtext.

Die Filmzahlen werden nicht fest gepflegt, sondern aus `sourceMovies` berechnet. Dadurch bleiben Home und Kategorien konsistent.

### `CategoriesPreviewData.kt`

Erzeugt `CategoryUiModel`-Listen für Runtime-Mockdaten und Compose Previews.

Wichtig:

```kotlin
previewCategories(movies: List<Movie> = sampleMovies())
```

Die Funktion zählt Filme nach `movie.category.type` und nutzt dieselbe zentrale Kategorie-Liste wie der Home-Filter.

### `CategoryUiModel.kt`

UI-Modell für eine Kategorie-Karte.

Es enthält:

- `type`: Domain-Kategorie für Navigation und Filter
- `label`: sichtbarer UI-Text
- `movieCountText`: vorbereiteter Text für die Filmanzahl

## Was später bleiben soll

- `CategoriesScreen`
- `CategoriesUiState`
- `CategoriesUiAction`
- `CategoryUiModel`
- `previewCategories(movies)` als Logik-Vorlage für Kategorie-Zähler
- Kategorie-Klick mit `CategoryType`

## Was später ersetzt werden kann

- lokale Mockdatenquelle in `CategoriesViewModel`
- Sample-Daten aus `sampleMovies()`
- lokale Suche, falls die Suche später im Repository oder UseCase passieren soll

## Spätere Backend-Anbindung

Ohne eigenen Kategorie-UseCase kann der Kategorie-Screen später zuerst alle Filme über `GetMoviesUseCase` laden und daraus die Zähler berechnen:

```text
GetMoviesUseCase()
  -> List<Movie>
  -> group by CategoryType
  -> List<CategoryUiModel>
```

Falls Kategorien später serverseitig eigene Daten haben, kann dafür ein eigener UseCase ergänzt werden. Die UI muss dann nur noch `CategoriesUiState` erhalten.

## Nächste mögliche Schritte ohne Backend

- Tests für Kategorie-Zähler ergänzen
- Kategorie-Karten mit echten Icons oder Bildern erweitern
- Empty-State für Suchergebnisse optisch unterscheiden
