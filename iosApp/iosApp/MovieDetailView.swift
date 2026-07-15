import SwiftUI

struct MovieDetailView: View {
    @EnvironmentObject private var store: MovieMateStore
    @Environment(\.dismiss) private var dismiss
    let movieID: String

    @State private var isEditing = false
    @State private var editor = MovieEditorState()
    @State private var isSaving = false
    @State private var isFetching = false
    @State private var statusMessage: String?
    @State private var showDeleteConfirmation = false

    var body: some View {
        ZStack {
            MovieMateBackground()

            if let movie {
                ScrollView {
                    if isEditing {
                        editContent(movie: movie)
                    } else {
                        readContent(movie: movie)
                    }
                }
                .scrollDismissesKeyboard(.interactively)
            } else {
                MovieMateEmptyState(
                    symbol: "exclamationmark.triangle",
                    title: "Movie not found",
                    message: "It may have been removed on another device."
                )
                .padding(MovieMateTheme.horizontalPadding)
            }
        }
        .navigationTitle(isEditing ? "Edit movie" : "Movie details")
        .navigationBarTitleDisplayMode(.inline)
        .toolbarBackground(MovieMateTheme.backgroundTop, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
        .toolbar {
            if movie != nil {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(isEditing ? "Cancel" : "Edit") {
                        if let movie {
                            editor = MovieEditorState(movie: movie)
                        }
                        isEditing.toggle()
                        statusMessage = nil
                    }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button(role: .destructive) {
                        showDeleteConfirmation = true
                    } label: {
                        Image(systemName: "trash")
                    }
                    .tint(MovieMateTheme.destructive)
                }
            }
        }
        .tint(MovieMateTheme.accentLight)
        .onAppear {
            if let movie { editor = MovieEditorState(movie: movie) }
        }
        .onChange(of: movie) { _, updated in
            if let updated, !isEditing { editor = MovieEditorState(movie: updated) }
        }
        .alert("Delete movie?", isPresented: $showDeleteConfirmation) {
            Button("Cancel", role: .cancel) { }
            Button("Delete", role: .destructive) {
                Task {
                    await store.deleteMovie(id: movieID)
                    dismiss()
                }
            }
        } message: {
            Text("This movie will be removed on all synchronized devices.")
        }
    }

    private var movie: MovieItem? {
        store.movie(id: movieID)
    }

    private func readContent(movie: MovieItem) -> some View {
        VStack(alignment: .leading, spacing: 20) {
            ZStack(alignment: .bottomLeading) {
                MoviePosterView(source: movie.posterURL, cornerRadius: 0)
                    .frame(maxWidth: .infinity)
                    .frame(height: 390)

                LinearGradient(
                    colors: [.clear, MovieMateTheme.background.opacity(0.96)],
                    startPoint: .center,
                    endPoint: .bottom
                )

                VStack(alignment: .leading, spacing: 10) {
                    Text(movie.title)
                        .font(.largeTitle.bold())
                        .foregroundStyle(MovieMateTheme.primaryText)
                    HStack(spacing: 14) {
                        if !movie.releaseYear.isEmpty {
                            Label(movie.releaseYear, systemImage: "calendar")
                        }
                        if !movie.runtimeMinutes.isEmpty {
                            Label("\(movie.runtimeMinutes) min", systemImage: "clock")
                        }
                        Label(movie.preferredRating, systemImage: "star.fill")
                            .foregroundStyle(MovieMateTheme.rating)
                    }
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(MovieMateTheme.secondaryText)
                }
                .padding(MovieMateTheme.horizontalPadding)
            }

            VStack(alignment: .leading, spacing: 18) {
                if let category = store.category(id: movie.categoryID) {
                    Label(category.name, systemImage: categorySymbol(for: category.iconKey))
                        .font(.subheadline.bold())
                        .foregroundStyle(MovieMateTheme.accentLight)
                        .padding(.horizontal, 13)
                        .frame(height: 36)
                        .background(MovieMateTheme.accentContainer)
                        .clipShape(Capsule())
                }

                Button {
                    Task { await store.setWatched(movieID: movie.id, isWatched: !movie.isWatched) }
                } label: {
                    Label(
                        movie.isWatched ? "Watched" : "Add to watched",
                        systemImage: movie.isWatched ? "checkmark.circle.fill" : "circle"
                    )
                    .font(.subheadline.bold())
                    .foregroundStyle(MovieMateTheme.primaryText)
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
                    .background(movie.isWatched ? MovieMateTheme.accentContainer : MovieMateTheme.accent)
                    .clipShape(RoundedRectangle(cornerRadius: MovieMateTheme.controlRadius))
                }
                .buttonStyle(.plain)

                detailSection(title: "About the movie", text: movie.description)

                if !movie.comment.isEmpty {
                    detailSection(title: "My comment", text: movie.comment)
                }

                if !movie.watchProviders.isEmpty {
                    VStack(alignment: .leading, spacing: 10) {
                        MovieMateSectionTitle("Watch providers")
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                ForEach(movie.watchProviders, id: \.self) { provider in
                                    Text(provider)
                                        .font(.caption.bold())
                                        .foregroundStyle(MovieMateTheme.accentLight)
                                        .padding(.horizontal, 12)
                                        .frame(height: 34)
                                        .background(MovieMateTheme.accentContainer)
                                        .clipShape(Capsule())
                                }
                            }
                        }
                    }
                }
            }
            .padding(.horizontal, MovieMateTheme.horizontalPadding)
            .padding(.bottom, 38)
        }
    }

    private func editContent(movie: MovieItem) -> some View {
        VStack(spacing: 20) {
            if let statusMessage {
                Text(statusMessage)
                    .font(.caption)
                    .foregroundStyle(MovieMateTheme.destructive)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }

            MovieFormView(
                editor: $editor,
                categories: store.categories,
                isFetching: isFetching,
                onFetch: fetchMetadata,
                onAddCategory: store.addCategory
            )

            MovieMatePrimaryButton(
                title: "Save changes",
                symbol: "checkmark",
                isLoading: isSaving,
                isEnabled: editor.canSave,
                action: { Task { await save(movie: movie) } }
            )
        }
        .padding(MovieMateTheme.horizontalPadding)
        .padding(.bottom, 38)
    }

    private func detailSection(title: String, text: String) -> some View {
        VStack(alignment: .leading, spacing: 9) {
            MovieMateSectionTitle(title)
            Text(text)
                .font(.body)
                .foregroundStyle(MovieMateTheme.secondaryText)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(15)
                .movieMateCard()
        }
    }

    private func fetchMetadata() async {
        let title = editor.title.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !title.isEmpty else { return }
        isFetching = true
        statusMessage = nil
        defer { isFetching = false }
        do {
            guard let fetched = try await store.fetchMovieData(title: title) else {
                statusMessage = "No movie data found for this title."
                return
            }
            editor.title = fetched.title
            editor.description = fetched.description
            editor.releaseYear = fetched.releaseYear
            editor.posterURL = fetched.posterURL
            editor.runtimeMinutes = fetched.runtimeMinutes
            editor.imdbRating = fetched.imdbRating
        } catch {
            statusMessage = MovieMateStore.message(for: error)
        }
    }

    private func save(movie: MovieItem) async {
        guard editor.canSave else { return }
        isSaving = true
        statusMessage = nil
        defer { isSaving = false }
        do {
            try await store.saveMovie(editor, movieID: movie.id)
            isEditing = false
        } catch {
            statusMessage = MovieMateStore.message(for: error)
        }
    }
}
