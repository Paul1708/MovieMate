import SwiftUI

struct ContentView: View {
    @EnvironmentObject private var store: MovieMateStore
    @Environment(\.scenePhase) private var scenePhase
    @State private var selectedTab = MovieMateTab.home

    var body: some View {
        ZStack {
            TabView(selection: $selectedTab) {
                NavigationStack {
                    HomeView { selectedTab = .add }
                        .navigationDestination(for: MovieMateRoute.self, destination: destination)
                }
                .tag(MovieMateTab.home)
                .tabItem {
                    Label(MovieMateTab.home.title, systemImage: MovieMateTab.home.symbol)
                }

                NavigationStack {
                    CategoriesView()
                        .navigationDestination(for: MovieMateRoute.self, destination: destination)
                }
                .tag(MovieMateTab.categories)
                .tabItem {
                    Label(MovieMateTab.categories.title, systemImage: MovieMateTab.categories.symbol)
                }

                AddMovieView { selectedTab = .home }
                    .tag(MovieMateTab.add)
                    .tabItem {
                        Label(MovieMateTab.add.title, systemImage: MovieMateTab.add.symbol)
                    }

                BackupView()
                    .tag(MovieMateTab.backup)
                    .tabItem {
                        Label(MovieMateTab.backup.title, systemImage: MovieMateTab.backup.symbol)
                    }
            }
            .tint(MovieMateTheme.accentLight)
            .toolbarBackground(MovieMateTheme.backgroundTop, for: .tabBar)
            .toolbarBackground(.visible, for: .tabBar)

            if store.isInitialLoading {
                launchOverlay
                    .transition(.opacity)
            }
        }
        .preferredColorScheme(.dark)
        .task { await store.start() }
        .task(id: scenePhase) {
            guard scenePhase == .active else { return }
            while !Task.isCancelled {
                do {
                    try await ContinuousClock().sleep(for: .seconds(60))
                } catch {
                    return
                }
                guard !Task.isCancelled else { return }
                if store.googleAccount != nil {
                    await store.syncNow()
                }
            }
        }
        .alert(
            "MovieMate",
            isPresented: Binding(
                get: { store.errorMessage != nil },
                set: { if !$0 { store.clearError() } }
            )
        ) {
            Button("OK") { store.clearError() }
        } message: {
            Text(store.errorMessage ?? "")
        }
    }

    @ViewBuilder
    private func destination(_ route: MovieMateRoute) -> some View {
        switch route {
        case .movie(let id):
            MovieDetailView(movieID: id)
        case .category(let id):
            CategoryMoviesView(categoryID: id)
        }
    }

    private var launchOverlay: some View {
        ZStack {
            MovieMateBackground()
            VStack(spacing: 16) {
                ZStack {
                    Circle()
                        .fill(MovieMateTheme.accentContainer)
                        .frame(width: 82, height: 82)
                    Image(systemName: "film.stack.fill")
                        .font(.system(size: 36, weight: .bold))
                        .foregroundStyle(MovieMateTheme.accentLight)
                }
                Text("MovieMate")
                    .font(.largeTitle.bold())
                    .foregroundStyle(MovieMateTheme.primaryText)
                ProgressView()
                    .tint(MovieMateTheme.accentLight)
            }
        }
        .ignoresSafeArea()
    }
}
