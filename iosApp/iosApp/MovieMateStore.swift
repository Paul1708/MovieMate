import Combine
import Foundation
import Shared

@MainActor
final class MovieMateStore: ObservableObject {
    @Published private(set) var movies: [MovieItem] = []
    @Published private(set) var categories: [CategoryItem] = []
    @Published private(set) var isInitialLoading = true
    @Published private(set) var isRefreshing = false
    @Published var errorMessage: String?

    @Published private(set) var googleAccount: GoogleAccount?
    @Published private(set) var isAuthenticating = false
    @Published private(set) var googleConfigurationMissing = false
    @Published private(set) var syncState: SyncState = .idle

    private let api: IosMovieMateApi
    private let googleAuth: GoogleDriveAuthService
    private var didStart = false
    private var refreshGeneration = 0

    init(api: IosMovieMateApi, googleAuth: GoogleDriveAuthService = GoogleDriveAuthService()) {
        self.api = api
        self.googleAuth = googleAuth
        googleConfigurationMissing = !googleAuth.isConfigured
    }

    func start() async {
        guard !didStart else { return }
        didStart = true
        await refresh(showLoading: false)
        isInitialLoading = false
        await restoreGoogleAccount()
    }

    func refresh(showLoading: Bool = true) async {
        refreshGeneration += 1
        let generation = refreshGeneration
        if showLoading { isRefreshing = true }

        do {
            let snapshot = try await api.loadLibrary()
            guard generation == refreshGeneration else { return }
            movies = snapshot.movies.map(MovieItem.init(record:))
            categories = snapshot.categories.map(CategoryItem.init(record:))
            errorMessage = nil
        } catch {
            guard generation == refreshGeneration else { return }
            errorMessage = Self.message(for: error)
        }
        if generation == refreshGeneration { isRefreshing = false }
    }

    func movie(id: String) -> MovieItem? {
        movies.first(where: { $0.id == id })
    }

    func category(id: String) -> CategoryItem? {
        categories.first(where: { $0.id == id })
    }

    func saveMovie(_ state: MovieEditorState, movieID: String? = nil) async throws {
        let form = IosMovieForm(
            title: state.title,
            description: state.description,
            categoryId: state.categoryID,
            posterUrl: state.posterURL,
            releaseYear: state.releaseYear,
            runtimeMinutes: state.runtimeMinutes,
            userRating: state.userRating,
            imdbRating: state.imdbRating,
            isWatched: state.isWatched,
            comment: state.comment,
            watchProviders: state.watchProviders
        )

        if let movieID {
            _ = try await api.updateMovie(movieId: movieID, form: form)
        } else {
            _ = try await api.addMovie(form: form)
        }
        await refresh(showLoading: false)
        await syncAfterLocalChange()
    }

    func setWatched(movieID: String, isWatched: Bool) async {
        do {
            _ = try await api.setMovieWatched(movieId: movieID, isWatched: isWatched)
            await refresh(showLoading: false)
            await syncAfterLocalChange()
        } catch {
            errorMessage = Self.message(for: error)
        }
    }

    func deleteMovie(id: String) async {
        do {
            try await api.deleteMovie(movieId: id)
            await refresh(showLoading: false)
            await syncAfterLocalChange()
        } catch {
            errorMessage = Self.message(for: error)
        }
    }

    @discardableResult
    func addCategory(name: String, iconKey: String) async throws -> CategoryItem? {
        let saved = try await api.addCategory(name: name, iconKey: iconKey)
        await refresh(showLoading: false)
        await syncAfterLocalChange()
        return category(id: saved.id)
    }

    func deleteCategory(id: String) async {
        do {
            try await api.deleteCategory(categoryId: id)
            await refresh(showLoading: false)
            await syncAfterLocalChange()
        } catch {
            errorMessage = Self.message(for: error)
        }
    }

    func fetchMovieData(title: String) async throws -> FetchedMovieData? {
        guard let record = try await api.fetchMovieData(title: title) else { return nil }
        return FetchedMovieData(record: record)
    }

    func exportBackup() async throws -> String {
        try await api.exportBackup()
    }

    func importBackup(_ json: String) async throws {
        try await api.importBackup(json: json)
        await refresh(showLoading: false)
        await syncAfterLocalChange()
    }

    func signInWithGoogle() async {
        isAuthenticating = true
        errorMessage = nil
        do {
            googleAccount = try await googleAuth.signIn()
            googleConfigurationMissing = false
            await syncNow()
        } catch {
            errorMessage = Self.message(for: error)
        }
        isAuthenticating = false
    }

    func signOutFromGoogle() {
        googleAuth.signOut()
        googleAccount = nil
        syncState = .idle
    }

    func syncNow() async {
        guard googleAccount != nil else { return }
        syncState = .syncing
        do {
            let refreshedAccount = try await googleAuth.refreshCurrentAccount()
            googleAccount = refreshedAccount
            _ = try await api.synchronizeGoogleDrive(
                accessToken: refreshedAccount.accessToken,
                accountId: refreshedAccount.id
            )
            await refresh(showLoading: false)
            syncState = .success(Date())
        } catch {
            let message = Self.message(for: error)
            if (error as NSError).domain == NSURLErrorDomain {
                syncState = .offline(message)
            } else {
                syncState = .failure(message)
            }
        }
    }

    func appBecameActive() async {
        guard didStart else { return }
        if googleAccount != nil {
            await syncNow()
        } else {
            await refresh(showLoading: false)
        }
    }

    @discardableResult
    func handleGoogleRedirect(_ url: URL) -> Bool {
        googleAuth.handle(url: url)
    }

    func clearError() {
        errorMessage = nil
    }

    private func restoreGoogleAccount() async {
        guard googleAuth.isConfigured else {
            googleConfigurationMissing = true
            return
        }
        do {
            googleAccount = try await googleAuth.restorePreviousSignIn()
            if googleAccount != nil { await syncNow() }
        } catch {
            googleAccount = nil
        }
    }

    private func syncAfterLocalChange() async {
        guard googleAccount != nil else { return }
        await syncNow()
    }

    static func message(for error: Error) -> String {
        let message = (error as NSError).localizedDescription
        return message.isEmpty ? "Something went wrong." : message
    }
}
