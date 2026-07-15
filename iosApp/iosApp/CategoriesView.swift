import SwiftUI

struct CategoriesView: View {
    @EnvironmentObject private var store: MovieMateStore

    @State private var searchQuery = ""
    @State private var isSearchVisible = false
    @State private var isAddingCategory = false
    @State private var pendingDelete: CategoryItem?

    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            MovieMateBackground()

            VStack(spacing: 16) {
                MovieMateHeader("Categories") {
                    MovieMateIconButton(
                        symbol: "magnifyingglass",
                        accessibilityLabel: "Search categories",
                        isActive: isSearchVisible
                    ) {
                        withAnimation(.easeInOut(duration: 0.2)) {
                            isSearchVisible.toggle()
                            if !isSearchVisible { searchQuery = "" }
                        }
                    }
                }

                if isSearchVisible {
                    MovieMateSearchField(placeholder: "Find a category", text: $searchQuery)
                        .transition(.move(edge: .top).combined(with: .opacity))
                }

                if visibleCategories.isEmpty {
                    Spacer(minLength: 20)
                    MovieMateEmptyState(
                        symbol: "square.grid.2x2",
                        title: "No categories found",
                        message: store.categories.isEmpty
                            ? "Create a category to organize your collection."
                            : "Try another search."
                    )
                    Spacer()
                } else {
                    ScrollView {
                        LazyVGrid(
                            columns: [GridItem(.flexible(), spacing: 12), GridItem(.flexible(), spacing: 12)],
                            spacing: 12
                        ) {
                            ForEach(visibleCategories) { category in
                                NavigationLink(value: MovieMateRoute.category(category.id)) {
                                    CategoryTileView(
                                        category: category,
                                        movieCount: store.movies.filter { $0.categoryID == category.id }.count
                                    )
                                }
                                .buttonStyle(.plain)
                                .contextMenu {
                                    Button(role: .destructive) {
                                        pendingDelete = category
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

            Button {
                isAddingCategory = true
            } label: {
                Image(systemName: "plus")
                    .font(.system(size: 22, weight: .bold))
                    .foregroundStyle(MovieMateTheme.primaryText)
                    .frame(width: 58, height: 58)
                    .background(MovieMateTheme.accent)
                    .clipShape(Circle())
                    .shadow(color: MovieMateTheme.accent.opacity(0.38), radius: 16, y: 8)
            }
            .accessibilityLabel("Add category")
            .padding(.trailing, MovieMateTheme.horizontalPadding)
            .padding(.bottom, 22)
        }
        .sheet(isPresented: $isAddingCategory) {
            AddCategorySheet(onSave: store.addCategory) { _ in }
                .presentationDetents([.medium, .large])
                .presentationDragIndicator(.visible)
        }
        .alert("Delete category?", isPresented: deleteAlertBinding, presenting: pendingDelete) { category in
            Button("Cancel", role: .cancel) { pendingDelete = nil }
            Button("Delete", role: .destructive) {
                Task {
                    await store.deleteCategory(id: category.id)
                    pendingDelete = nil
                }
            }
        } message: { category in
            Text("Delete “\(category.name)”? Movies in this category will remain in your library.")
        }
    }

    private var visibleCategories: [CategoryItem] {
        let normalized = searchQuery.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !normalized.isEmpty else { return store.categories }
        return store.categories.filter { $0.name.localizedCaseInsensitiveContains(normalized) }
    }

    private var deleteAlertBinding: Binding<Bool> {
        Binding(
            get: { pendingDelete != nil },
            set: { if !$0 { pendingDelete = nil } }
        )
    }
}

struct CategoryMoviesView: View {
    @EnvironmentObject private var store: MovieMateStore
    let categoryID: String

    var body: some View {
        ZStack {
            MovieMateBackground()
            ScrollView {
                LazyVStack(spacing: 12) {
                    if movies.isEmpty {
                        MovieMateEmptyState(
                            symbol: "film",
                            title: "No movies yet",
                            message: "Movies assigned to this category will appear here."
                        )
                    } else {
                        ForEach(movies) { movie in
                            NavigationLink(value: MovieMateRoute.movie(movie.id)) {
                                MovieCardView(movie: movie, category: store.category(id: movie.categoryID))
                            }
                            .buttonStyle(.plain)
                        }
                    }
                }
                .padding(MovieMateTheme.horizontalPadding)
            }
        }
        .navigationTitle(store.category(id: categoryID)?.name ?? "Category")
        .navigationBarTitleDisplayMode(.inline)
        .toolbarBackground(MovieMateTheme.backgroundTop, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
    }

    private var movies: [MovieItem] {
        store.movies
            .filter { $0.categoryID == categoryID }
            .sorted { $0.title.localizedCaseInsensitiveCompare($1.title) == .orderedAscending }
    }
}
