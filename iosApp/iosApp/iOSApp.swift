import Shared
import SwiftUI

@main
struct iOSApp: App {
    @Environment(\.scenePhase) private var scenePhase
    @StateObject private var store: MovieMateStore

    init() {
        let api = IosAppContainerFactory().createApi()
        _store = StateObject(wrappedValue: MovieMateStore(api: api))
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(store)
                .onOpenURL { url in
                    _ = store.handleGoogleRedirect(url)
                }
        }
        .onChange(of: scenePhase) { _, phase in
            guard phase == .active else { return }
            Task { await store.appBecameActive() }
        }
    }
}
