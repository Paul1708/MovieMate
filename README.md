# MovieMate

MovieMate ist ein Kotlin-Multiplatform-Projekt zur lokalen Verwaltung von Filmen
und Kategorien. Die produktive Benutzeroberfläche wird aktuell für Android mit
Jetpack Compose umgesetzt.

## Module

- `composeApp`: Android-App und Compose-Presentation-Schicht.
- `shared`: gemeinsame Domain-, UseCase-, Repository- und SQLDelight-Schicht.
- `server`: Ktor-Servermodul.
- `iosApp`: minimaler iOS-Einstieg; derzeit nicht Teil der produktiven UI.

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

## Bauen und testen

Windows:

```powershell
.\gradlew.bat :composeApp:assembleDebug
.\gradlew.bat :composeApp:testDebugUnitTest
```

macOS/Linux:

```shell
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:testDebugUnitTest
```

Server starten:

```shell
./gradlew :server:run
```
