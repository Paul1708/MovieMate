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
                    title: "Title not found",
                    message: "It may have been removed on another device."
                )
                .padding(MovieMateTheme.horizontalPadding)
            }
        }
        .navigationTitle(isEditing ? "Edit title" : "Title details")
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
        .alert("Delete title?", isPresented: $showDeleteConfirmation) {
            Button("Cancel", role: .cancel) { }
            Button("Delete", role: .destructive) {
                Task {
                    await store.deleteMovie(id: movieID)
                    dismiss()
                }
            }
        } message: {
            Text("This title will be removed on all synchronized devices.")
        }
    }

    private var movie: MovieItem? {
        store.movie(id: movieID)
    }

    private func readContent(movie: MovieItem) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack {
                MoviePosterView(source: movie.posterURL, cornerRadius: MovieMateTheme.cardRadius)
                    .frame(width: 210, height: 315)
                    .overlay {
                        RoundedRectangle(cornerRadius: MovieMateTheme.cardRadius, style: .continuous)
                            .stroke(MovieMateTheme.border, lineWidth: 1)
                    }
                    .shadow(color: .black.opacity(0.35), radius: 18, y: 10)
            }
            .frame(maxWidth: .infinity)
            .padding(.top, 18)
            .padding(.bottom, 26)

            VStack(alignment: .leading, spacing: 18) {
                Text(movie.title)
                    .font(.largeTitle.bold())
                    .foregroundStyle(MovieMateTheme.primaryText)
                    .fixedSize(horizontal: false, vertical: true)

                if let category = store.category(id: movie.categoryID) {
                    Label(category.name, systemImage: categorySymbol(for: category.iconKey))
                        .font(.subheadline.bold())
                        .foregroundStyle(MovieMateTheme.accentLight)
                        .padding(.horizontal, 13)
                        .frame(height: 36)
                        .background(MovieMateTheme.accentContainer)
                        .clipShape(Capsule())
                }

                if movie.mediaType == .series || !movie.releaseYear.isEmpty ||
                    !movie.runtimeMinutes.isEmpty ||
                    !movie.userRating.isEmpty ||
                    !movie.imdbRating.isEmpty {
                    LazyVGrid(
                        columns: [GridItem(.adaptive(minimum: 125), spacing: 10)],
                        spacing: 10
                    ) {
                        if !movie.releaseYear.isEmpty {
                            infoTile(
                                title: "Release year",
                                value: movie.releaseYear,
                                symbol: "calendar",
                                tint: MovieMateTheme.accentLight
                            )
                        }
                        if movie.mediaType == .series {
                            infoTile(
                                title: "Type",
                                value: movie.seasonCountLabel.map { "Series · \($0)" } ?? "Series",
                                symbol: "tv",
                                tint: MovieMateTheme.accentLight
                            )
                        } else if !movie.runtimeMinutes.isEmpty {
                            infoTile(
                                title: "Runtime",
                                value: "\(movie.runtimeMinutes) min",
                                symbol: "clock.fill",
                                tint: MovieMateTheme.accentLight
                            )
                        }
                        if !movie.userRating.isEmpty {
                            infoTile(
                                title: "My rating",
                                value: "\(movie.userRating) / 10",
                                symbol: "star.fill",
                                tint: MovieMateTheme.rating
                            )
                        }
                        if !movie.imdbRating.isEmpty {
                            infoTile(
                                title: "IMDb",
                                value: "\(movie.imdbRating) / 10",
                                symbol: "star.leadinghalf.filled",
                                tint: MovieMateTheme.rating
                            )
                        }
                    }
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

                detailSection(title: "Description", text: movie.description)

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

    private func infoTile(
        title: String,
        value: String,
        symbol: String,
        tint: Color
    ) -> some View {
        VStack(alignment: .leading, spacing: 7) {
            Label(title, systemImage: symbol)
                .font(.caption.weight(.semibold))
                .foregroundStyle(tint)
            Text(value)
                .font(.headline)
                .foregroundStyle(MovieMateTheme.primaryText)
                .lineLimit(1)
        }
        .frame(maxWidth: .infinity, minHeight: 66, alignment: .leading)
        .padding(.horizontal, 13)
        .padding(.vertical, 8)
        .movieMateCard()
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
                isFetching: $isFetching,
                onAddCategory: store.addCategory
            )

            MovieMatePrimaryButton(
                title: "Save changes",
                symbol: "checkmark",
                isLoading: isSaving,
                isEnabled: editor.canSave && !isFetching,
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

    private func save(movie: MovieItem) async {
        guard editor.canSave, !isFetching else { return }
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
