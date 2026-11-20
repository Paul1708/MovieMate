# MovieMate for iOS

The iOS app is a native SwiftUI client for the shared Kotlin Multiplatform domain and
database layer. Xcode builds and embeds the static `Shared.framework` through the existing
Gradle wrapper. The project targets iOS 17 or newer.

## Local configuration

Copy `Configuration/config.local.xcconfig.example` to `Configuration/config.local.xcconfig`
(ignored by Git) next to `Config.xcconfig`, then fill in your local values:

```xcconfig
TEAM_ID=YOUR_APPLE_DEVELOPMENT_TEAM_ID
GOOGLE_CLIENT_ID=YOUR_IOS_OAUTH_CLIENT_ID.apps.googleusercontent.com
GOOGLE_REVERSED_CLIENT_ID=com.googleusercontent.apps.YOUR_IOS_OAUTH_CLIENT_ID
```

Create an **iOS** OAuth client for bundle identifier `de.fh.erfurt.moviemate` in the same
Google Cloud project used by Android, enable the Google Drive API, and make the
`drive.appdata` scope available on the OAuth consent screen. Never commit the real client
values or a downloaded `GoogleService-Info.plist`.

The shared build also requires a repository-local `local.properties` file. Start by
copying `local.properties.example`, then set the OMDb key and the Android SDK installed
on the Mac:

```properties
OMDB_API_KEY=your_api_key
sdk.dir=/Users/your-user/Library/Android/sdk
```

Even an Xcode build needs this Android SDK entry because the build script invokes the
Gradle/Kotlin Multiplatform project before creating `Shared.framework`.

## Build on macOS

1. Open `iosApp.xcodeproj` in Xcode 16.2 or newer.
2. Verify that `$HOME/Library/Android/sdk` exists (install the Android SDK with Android
   Studio first if it does not).
3. Wait for Swift Package Manager to resolve `GoogleSignIn` 9.0.0.
4. Select an Apple development team if it is not set in the local xcconfig.
5. Run the `iosApp` scheme on an iOS 17+ simulator or signed device.

Google Sign-In needs an Apple-signed build when credentials are persisted in Keychain.
The Windows Gradle build can validate shared JVM code, but the Kotlin/Native framework and
SwiftUI app must be compiled and smoke-tested on macOS/Xcode.
