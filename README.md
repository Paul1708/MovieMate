# MovieMate

MovieMate ist ein Kotlin-Multiplatform-Projekt zur Verwaltung von Filmen und
Kategorien. Android und iOS verwenden dieselbe Domain- und SQLDelight-Schicht;
angemeldete Geräte gleichen ihre Bibliothek über Google Drive ab.

## Module

- `composeApp`: Android-App und Compose-Presentation-Schicht.
- `shared`: gemeinsame Domain-, UseCase-, Repository- und SQLDelight-Schicht.
- `server`: Ktor-Servermodul.
- `iosApp`: native SwiftUI-App mit derselben Bibliothek-, Kategorie-, Detail-,
  Bearbeitungs-, Backup- und Sync-Funktionalität wie die Android-App.

## Android-UI-Architektur

Jedes größere Feature ist in drei Verantwortungen getrennt:

```text
Route -> ViewModel -> UseCase
  |          |
  |          +-> StateFlow / SharedFlow
  +-> stateless Screen
```

- Routes erzeugen screen-spezifische AndroidX-ViewModels, sammeln State
  lifecycle-aware und führen Navigation oder Android-Plattformaktionen aus.
- ViewModels verarbeiten Actions, koordinieren UseCases und veröffentlichen
  unveränderlichen UI-State sowie einmalige Effects.
- Screens rendern ausschließlich aus ihrem State und melden Nutzeraktionen zurück.

Weitere Details stehen in [`docs/global-ui-guide.md`](docs/global-ui-guide.md).

## OMDb-Konfiguration

Der Abruf externer Filmdaten benötigt einen OMDb-API-Schlüssel. Lege ihn lokal
in der nicht versionierten Datei `local.properties` ab:

```properties
OMDB_API_KEY=your_api_key
```

Alternativ kann der Schlüssel beim Build über die Umgebungsvariable
`OMDB_API_KEY` bereitgestellt werden. Ohne Schlüssel bricht der Build mit einer
entsprechenden Fehlermeldung ab.

## Google-Drive-Synchronisierung

Die Synchronisierung verwendet ausschließlich den privaten Google-Drive-Ordner
`appDataFolder`. Filme, Kategorien, Löschungen und portable Poster werden beim
Anmelden zusammengeführt und danach bei lokalen Änderungen, beim Aktivieren der
App sowie im geöffneten Vordergrund regelmäßig abgeglichen.

Einmalige Einrichtung in der Google Cloud Console:

1. Google Drive API aktivieren und den OAuth-Zustimmungsbildschirm konfigurieren.
2. Einen Android-OAuth-Client für Paket `de.fh.erfurt.moviemate` und den SHA-1-
   Fingerabdruck der verwendeten Signatur anlegen.
3. Im selben Projekt einen iOS-OAuth-Client für Bundle-ID
   `de.fh.erfurt.moviemate` anlegen.
4. Die iOS-Clientwerte lokal gemäß [`iosApp/README.md`](iosApp/README.md)
   hinterlegen. Zugangsdaten bleiben unversioniert.

## Bauen und testen

Windows:

```powershell
.\gradlew.bat :composeApp:assembleDebug
.\gradlew.bat :composeApp:testDebugUnitTest
.\gradlew.bat :shared:allTests
```

macOS/Linux:

```shell
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:testDebugUnitTest
./gradlew :shared:allTests
```

Server starten:

```shell
./gradlew :server:run
```
