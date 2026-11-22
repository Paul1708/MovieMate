import Foundation
import Shared

enum MovieMateTab: String, CaseIterable, Identifiable {
    case home
    case categories
    case add
    case backup

    var id: Self { self }

    var title: String {
        switch self {
        case .home: return "Home"
        case .categories: return "Categories"
        case .add: return "Add"
        case .backup: return "Backup"
        }
    }

    var symbol: String {
        switch self {
        case .home: return "house.fill"
        case .categories: return "square.grid.2x2.fill"
        case .add: return "plus.circle.fill"
        case .backup: return "arrow.triangle.2.circlepath.icloud.fill"
        }
    }
}

enum MovieMateRoute: Hashable {
    case movie(String)
    case category(String)
}

enum HomeCollection: String, CaseIterable, Identifiable {
    case all = "All"
    case watched = "Watched"
    case watchlist = "Watchlist"

    var id: Self { self }
}

enum MovieSort: String, CaseIterable, Identifiable {
    case updated = "Last edited"
    case title = "Title"
    case releaseYear = "Release year"
    case rating = "Rating"
    case runtime = "Runtime"

    var id: Self { self }
}

enum MediaType: String, CaseIterable, Identifiable {
    case movie = "MOVIE"
    case series = "SERIES"

    var id: Self { self }
    var title: String { self == .movie ? "Movie" : "Series" }
}

struct MovieItem: Identifiable, Hashable {
    let id: String
    let title: String
    let description: String
    let releaseYear: String
    let categoryID: String
    let posterURL: String
    let runtimeMinutes: String
    let mediaType: MediaType
    let totalSeasons: String
    let isWatched: Bool
    let comment: String
    let userRating: String
    let imdbRating: String
    let watchProviders: [String]
    let updatedAtEpochMillis: Int64

    init(record: IosMovieRecord) {
        id = record.id
        title = record.title
        description = record.description_
        releaseYear = record.releaseYear
        categoryID = record.categoryId
        posterURL = record.posterUrl
        runtimeMinutes = record.runtimeMinutes
        totalSeasons = record.totalSeasons
        mediaType = MediaType(rawValue: record.mediaType) ?? .movie
        isWatched = record.isWatched
        comment = record.comment
        userRating = record.userRating
        imdbRating = record.imdbRating
        watchProviders = record.watchProviders
        updatedAtEpochMillis = record.updatedAtEpochMillis
    }

    var seasonCountLabel: String? {
        guard let count = Int(totalSeasons), count > 0 else { return nil }
        return count == 1 ? "1 season" : "\(count) seasons"
    }

    var preferredRating: String {
        if !userRating.isEmpty { return userRating }
        if !imdbRating.isEmpty { return imdbRating }
        return "—"
    }
}

struct CategoryItem: Identifiable, Hashable {
    let id: String
    let name: String
    let iconKey: String

    init(record: IosCategoryRecord) {
        id = record.id
        name = record.name
        iconKey = record.iconKey.isEmpty ? "other" : record.iconKey
    }
}

struct MovieEditorState: Equatable {
    var title = ""
    var description = ""
    var categoryID = ""
    var posterURL = ""
    var releaseYear = ""
    var runtimeMinutes = ""
    var mediaType: MediaType = .movie
    var totalSeasons = ""
    var userRating = ""
    var imdbRating = ""
    var isWatched = false
    var comment = ""
    var watchProviders: [String] = []
    var watchProviderInput = ""

    init() {}

    init(movie: MovieItem) {
        title = movie.title
        description = movie.description
        categoryID = movie.categoryID
        posterURL = movie.posterURL
        releaseYear = movie.releaseYear
        runtimeMinutes = movie.runtimeMinutes
        mediaType = movie.mediaType
        totalSeasons = movie.totalSeasons
        userRating = movie.userRating
        imdbRating = movie.imdbRating
        isWatched = movie.isWatched
        comment = movie.comment
        watchProviders = movie.watchProviders
    }

    var canSave: Bool {
        !title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }
}

struct MovieSearchResults: Identifiable {
    let id = UUID()
    let matches: [MovieSearchMatch]
    let totalResults: Int

    init(record: IosMovieSearchResults) {
        matches = record.matches.map(MovieSearchMatch.init(record:))
        totalResults = Int(record.totalResults)
    }
}

struct MovieSearchMatch: Identifiable {
    let id: String
    let title: String
    let year: String
    let posterURL: String

    init(record: IosMovieSearchMatch) {
        id = record.imdbId
        title = record.title
        year = record.year
        posterURL = record.posterUrl
    }
}

struct FetchedMovieData {
    let title: String
    let description: String
    let releaseYear: String
    let posterURL: String
    let runtimeMinutes: String
    let imdbRating: String
    let genres: [String]
    let totalSeasons: String

    init(record: IosFetchedMovieRecord) {
        title = record.title
        description = record.description_
        releaseYear = record.releaseYear
        posterURL = record.posterUrl
        runtimeMinutes = record.runtimeMinutes
        totalSeasons = record.totalSeasons
        imdbRating = record.imdbRating
        genres = record.genres
    }
}

struct GoogleAccount: Equatable {
    let id: String
    let displayName: String
    let email: String
    let accessToken: String
}

enum SyncState: Equatable {
    case idle
    case syncing
    case success(Date)
    case offline(String)
    case failure(String)
}

struct CategoryIconOption: Identifiable, Hashable {
    let id: String
    let title: String
    let symbol: String
}

let categoryIconOptions: [CategoryIconOption] = [
    .init(id: "action", title: "Action", symbol: "bolt.fill"),
    .init(id: "adventure", title: "Adventure", symbol: "map.fill"),
    .init(id: "animation", title: "Animation", symbol: "wand.and.stars"),
    .init(id: "biography", title: "Biography", symbol: "person.text.rectangle"),
    .init(id: "comedy", title: "Comedy", symbol: "theatermasks.fill"),
    .init(id: "crime", title: "Crime", symbol: "lock.shield.fill"),
    .init(id: "documentary", title: "Documentary", symbol: "video.fill"),
    .init(id: "drama", title: "Drama", symbol: "person.2.wave.2.fill"),
    .init(id: "family", title: "Family", symbol: "figure.2.and.child.holdinghands"),
    .init(id: "fantasy", title: "Fantasy", symbol: "sparkles"),
    .init(id: "history", title: "History", symbol: "building.columns.fill"),
    .init(id: "horror", title: "Horror", symbol: "moon.haze.fill"),
    .init(id: "kids", title: "Kids", symbol: "teddybear.fill"),
    .init(id: "magic", title: "Magic", symbol: "wand.and.rays"),
    .init(id: "mystery", title: "Mystery", symbol: "questionmark.diamond.fill"),
    .init(id: "nature", title: "Nature", symbol: "leaf.fill"),
    .init(id: "romance", title: "Romance", symbol: "heart.fill"),
    .init(id: "sci_fi", title: "Science Fiction", symbol: "atom"),
    .init(id: "sport", title: "Sport", symbol: "sportscourt.fill"),
    .init(id: "superhero", title: "Superhero", symbol: "shield.lefthalf.filled"),
    .init(id: "thriller", title: "Thriller", symbol: "eye.fill"),
    .init(id: "time_travel", title: "Time Travel", symbol: "clock.arrow.2.circlepath"),
    .init(id: "war", title: "War", symbol: "scope"),
    .init(id: "western", title: "Western", symbol: "sun.horizon.fill"),
    .init(id: "other", title: "Other", symbol: "film.stack.fill")
]

func categorySymbol(for key: String) -> String {
    categoryIconOptions.first(where: { $0.id == key })?.symbol ?? "film.stack.fill"
}
