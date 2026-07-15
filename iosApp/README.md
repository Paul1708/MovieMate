# MovieMate for iOS

The iOS app is a native SwiftUI client for the shared Kotlin Multiplatform domain and
database layer. Xcode builds and embeds the static `Shared.framework` through the existing
Gradle wrapper. The project targets iOS 17 or newer.

## Local configuration

Create `Configuration/config.local.xcconfig` (ignored by Git) next to `Config.xcconfig`:

```xcconfig
TEAM_ID=YOUR_APPLE_DEVELOPMENT_TEAM_ID
GOOGLE_CLIENT_ID=YOUR_IOS_OAUTH_CLIENT_ID.apps.googleusercontent.com
GOOGLE_REVERSED_CLIENT_ID=com.googleusercontent.apps.YOUR_IOS_OAUTH_CLIENT_ID
```

Create an **iOS** OAuth client for bundle identifier `de.fh.erfurt.moviemate` in the same
Google Cloud project used by Android, enable the Google Drive API, and make the
`drive.appdata` scope available on the OAuth consent screen. Never commit the real client
values or a downloaded `GoogleService-Info.plist`.

The shared build also requires `OMDB_API_KEY` in the repository's ignored
`local.properties` file:

```properties
OMDB_API_KEY=your_api_key
```

## Build on macOS

1. Open `iosApp.xcodeproj` in Xcode 16.2 or newer.
2. Wait for Swift Package Manager to resolve `GoogleSignIn` 9.0.0.
3. Select an Apple development team if it is not set in the local xcconfig.
4. Run the `iosApp` scheme on an iOS 17+ simulator or signed device.

Google Sign-In needs an Apple-signed build when credentials are persisted in Keychain.
The Windows Gradle build can validate shared JVM code, but the Kotlin/Native framework and
SwiftUI app must be compiled and smoke-tested on macOS/Xcode.
