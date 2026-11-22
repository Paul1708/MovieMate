import SwiftUI

struct HomeView: View {
    @EnvironmentObject private var store: MovieMateStore
    let onAddMovie: () -> Void

    @State private var collection: HomeCollection = .all
    @State private var searchQuery = ""
    @State private var isSearchVisible = false
    @State private var isFilterVisible = false
    @State private var selectedCategoryID = ""
    @State private var selectedMediaType: MediaType?
    @State private var sort = MovieSort.updated
    @State private var pendingDelete: MovieItem?

    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            MovieMateBackground()

            VStack(spacing: 16) {
                MovieMateHeader("MovieMate") {
                    HStack(spacing: 9) {
                        MovieMateIconButton(
                            symbol: "magnifyingglass",
                            accessibilityLabel: "Search titles",
                            isActive: isSearchVisible
                        ) {
                            withAnimation(.easeInOut(duration: 0.2)) {
                                isSearchVisible.toggle()
                                if !isSearchVisible { searchQuery = "" }
                            }
                        }
                        MovieMateIconButton(
                            symbol: "line.3.horizontal.decrease",
                            accessibilityLabel: "Filter titles",
                            isActive: !selectedCategoryID.isEmpty || selectedMediaType != nil || sort != .updated
                        ) {
                            isFilterVisible = true
                        }
                    }
                }

                Picker("Collection", selection: $collection) {
                    ForEach(HomeCollection.allCases) { item in
                        Text(item.rawValue).tag(item)
                    }
                }
                .pickerStyle(.segmented)

                if isSearchVisible {
                    MovieMateSearchField(placeholder: "Find a title", text: $searchQuery)
                        .transition(.move(edge: .top).combined(with: .opacity))
                }

                if store.isRefreshing && !store.isInitialLoading {
                    ProgressView()
                        .tint(MovieMateTheme.accentLight)
                        .frame(maxWidth: .infinity)
                }

                if visibleMovies.isEmpty {
                    Spacer(minLength: 20)
                    MovieMateEmptyState(
                        symbol: "film.stack",
                        title: "No titles found",
                        message: emptyMessage
                    )
                    Spacer()
                } else {
                    ScrollView {
                        LazyVStack(spacing: 12) {
                            ForEach(visibleMovies) { movie in
                                NavigationLink(value: MovieMateRoute.movie(movie.id)) {
                                    MovieCardView(movie: movie, category: store.category(id: movie.categoryID))
                                }
                                .buttonStyle(.plain)
                                .contextMenu {
                                    Button(role: .destructive) {
                                        pendingDelete = movie
                                    } label: {
                                        Label("Delete", systemImage: "trash")
                                    }
                                }
                            }
                        }
                        .padding(.bottom, 96)
                    }
                    .refreshable { await store.refresh() }
                }
            }
            .padding(.horizontal, MovieMateTheme.horizontalPadding)
            .padding(.top, 10)

            Button(action: onAddMovie) {
                Image(systemName: "plus")
                    .font(.system(size: 22, weight: .bold))
                    .foregroundStyle(MovieMateTheme.primaryText)
                    .frame(width: 58, height: 58)
                    .background(MovieMateTheme.accent)
                    .clipShape(Circle())
                    .shadow(color: MovieMateTheme.accent.opacity(0.38), radius: 16, y: 8)
            }
            .accessibilityLabel("Add title")
            .padding(.trailing, MovieMateTheme.horizontalPadding)
            .padding(.bottom, 22)
        }
        .sheet(isPresented: $isFilterVisible) {
            HomeFilterSheet(
                categories: store.categories,
                selectedCategoryID: $selectedCategoryID,
                selectedMediaType: $selectedMediaType,
                sort: $sort
            )
            .presentationDetents([.medium, .large])
            .presentationDragIndicator(.visible)
        }
        .alert("Delete title?", isPresented: deleteAlertBinding, presenting: pendingDelete) { movie in
            Button("Cancel", role: .cancel) { pendingDelete = nil }
            Button("Delete", role: .destructive) {
                Task {
                    await store.deleteMovie(id: movie.id)
                    pendingDelete = nil
                }
            }
        } message: { movie in
            Text("Are you sure you want to delete “\(movie.title)”? This cannot be undone.")
        }
    }

    private var visibleMovies: [MovieItem] {
        let normalizedSearch = searchQuery.trimmingCharacters(in: .whitespacesAndNewlines)
        let filtered = store.movies.filter { movie in
            let matchesCollection: Bool
            switch collection {
            case .all: matchesCollection = true
            case .watched: matchesCollection = movie.isWatched
            case .watchlist: matchesCollection = !movie.isWatched
            }
            let matchesCategory = selectedCategoryID.isEmpty || movie.categoryID == selectedCategoryID
            let matchesSearch = normalizedSearch.isEmpty ||
                movie.title.localizedCaseInsensitiveContains(normalizedSearch) ||
                movie.description.localizedCaseInsensitiveContains(normalizedSearch)
            let matchesType = selectedMediaType == nil || movie.mediaType == selectedMediaType
            return matchesCollection && matchesCategory && matchesType && matchesSearch
        }

        return filtered.sorted { lhs, rhs in
            switch sort {
            case .updated:
                return lhs.updatedAtEpochMillis > rhs.updatedAtEpochMillis
            case .title:
                return lhs.title.localizedCaseInsensitiveCompare(rhs.title) == .orderedAscending
            case .releaseYear:
                return (Int(lhs.releaseYear) ?? 0) > (Int(rhs.releaseYear) ?? 0)
            case .rating:
                return (Double(lhs.preferredRating) ?? 0) > (Double(rhs.preferredRating) ?? 0)
            case .runtime:
                return (Int(lhs.runtimeMinutes) ?? 0) > (Int(rhs.runtimeMinutes) ?? 0)
            }
        }
    }

    private var emptyMessage: String {
        if store.movies.isEmpty { return "Add your first movie or series to start your collection." }
        return "Try another search or filter."
    }

    private var deleteAlertBinding: Binding<Bool> {
        Binding(
            get: { pendingDelete != nil },
            set: { if !$0 { pendingDelete = nil } }
        )
    }
}

private struct HomeFilterSheet: View {
    let categories: [CategoryItem]
    @Binding var selectedCategoryID: String
    @Binding var selectedMediaType: MediaType?
    @Binding var sort: MovieSort
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            ZStack {
                MovieMateBackground()
                VStack(alignment: .leading, spacing: 20) {
                    Picker("Type", selection: $selectedMediaType) {
                        Text("All titles").tag(nil as MediaType?)
                        Text("Movies").tag(MediaType.movie as MediaType?)
                        Text("Series").tag(MediaType.series as MediaType?)
                    }
                    .pickerStyle(.segmented)

                    MovieMateSectionTitle("Category")
                    Menu {
                        Button("All categories") { selectedCategoryID = "" }
                        ForEach(categories) { category in
                            Button(category.name) { selectedCategoryID = category.id }
                        }
                    } label: {
                        HStack {
                            Text(categories.first(where: { $0.id == selectedCategoryID })?.name ?? "All categories")
                            Spacer()
                            Image(systemName: "chevron.up.chevron.down")
                        }
                        .movieMateInput()
                    }

                    MovieMateSectionTitle("Sort by")
                    Picker("Sort by", selection: $sort) {
                        ForEach(MovieSort.allCases) { option in
                            Text(option.rawValue).tag(option)
                        }
                    }
                    .pickerStyle(.menu)
                    .tint(MovieMateTheme.accentLight)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .movieMateInput()

                    Button("Clear filters") {
                        selectedCategoryID = ""
                        selectedMediaType = nil
                        sort = .updated
                    }
                    .foregroundStyle(MovieMateTheme.accentLight)
                    Spacer()
                }
                .padding(MovieMateTheme.horizontalPadding)
            }
            .navigationTitle("Filter & sort")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Done") { dismiss() }
                }
            }
            .tint(MovieMateTheme.accentLight)
            .toolbarBackground(MovieMateTheme.backgroundTop, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
        }
        .preferredColorScheme(.dark)
    }
}
