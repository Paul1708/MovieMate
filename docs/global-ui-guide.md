# Global UI Guide

Diese Datei beschreibt gemeinsame UI-Bausteine, Mockdaten und Navigationsstrukturen der aktuellen Presentation-Schicht.

## Gemeinsame UI-Bausteine

### `ScreenHeader.kt`

Einheitlicher Header für Screens. Er legt Titelhöhe, Topbar-Höhe, Divider und Abstände fest.

Home und Categories nutzen diesen Header bereits. Neue Screens sollten ihn ebenfalls verwenden, damit Titel, Icons und Divider beim Wechsel nicht springen.

### `SearchField.kt`

Einheitliches Suchfeld für Listen-Screens.

Aktuell genutzt von:

- Home
- Categories

Neue Screens sollten dieses Suchfeld nutzen, wenn sie eine einfache Textsuche anbieten.

### `ScreenStateContent.kt`

Gemeinsame Zustände für datengetriebene Screens:

- `ScreenLoadingState`
- `ScreenEmptyState`
- `ScreenErrorState`

Diese Bausteine ersetzen screenspezifische Loading-/Empty-/Error-Komponenten, solange kein spezielles Layout nötig ist.

### `TopBarIconButton.kt`

Einheitliche runde Icon-Schaltfläche für Topbars.

Unterstützt Text-Symbole und die zentralen Canvas-Icons aus `MovieMateIcons.kt`, damit neue Topbar-Aktionen denselben runden Button-Stil behalten.

### `MovieMateIcons.kt`

Zentrale Canvas-Icons für wiederverwendbare App-Aktionen und Statussymbole, zum Beispiel Suche, Filter, Navigation zurück, Entfernen, Backup, Watch-Status und Bild-Platzhalter.

Feature-spezifische Iconsets bleiben in ihren Feature-Paketen: Bottom-Navigation in `app/navigation`, Kategorie-Genres in `movies/categories` und Film-Metadaten in `movies/components`.

### `MovieMateColors.kt`

Zentrale Farbwerte für die aktuelle UI. Neue Komponenten sollten zuerst diese Farben verwenden, bevor eigene Farben ergänzt werden.

## Mockdaten und Sample-Daten

### `MockMovieData.kt`

Enthält `sampleMovies()`.

Diese Funktion liefert Domain-`Movie`-Objekte und simuliert damit schon den späteren Backend-Datenfluss.

## Navigation

### `BottomNavItem.kt`

Beschreibt nur die Einträge der BottomBar:

- Home
- Categories
- Add
- Backup

### `MovieMateDestination.kt`

Kapselt die zentralen Android-Navigationsrouten:

- Home
- Categories
- Detail
- Add
- Backup

Detail ist kein BottomBar-Item, sondern wird über MovieCard-Klicks geöffnet.

### `PlaceholderScreen.kt`

Einheitlicher Platzhalter für noch nicht implementierte Screens.

Aktuell genutzt für:

- Detail
- Add
- Backup

## Namenskonvention

`ViewModel` bedeutet AndroidX-`ViewModel`. Screen-Container sammeln `StateFlow`
mit `collectAsStateWithLifecycle()` und geben State plus Events an stateless
Content-Composables weiter.

## Sprache der UI

Die sichtbaren UI-Texte sind aktuell auf Englisch vereinheitlicht. Kommentare und Dokumentation sind weiterhin auf Deutsch, damit die Projektstruktur für das Team leichter nachvollziehbar bleibt.

## Was später weg kann

Bei echter Backend-Anbindung können Runtime-Sample-Daten ersetzt werden:

- `sampleMovies()` als Runtime-Datenquelle
- lokale Filter- und Sortierlogik in ViewModels
- Platzhalter-Screens für Detail, Add und Backup

Für Compose Previews sollten Sample-Daten weiter behalten werden.
