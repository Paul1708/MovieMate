import SwiftUI

struct AddMovieView: View {
    @EnvironmentObject private var store: MovieMateStore
    let onSaved: () -> Void

    @State private var editor = MovieEditorState()
    @State private var isFetching = false
    @State private var isSaving = false
    @State private var statusMessage: String?
    @State private var statusIsError = false

    var body: some View {
        ZStack {
            MovieMateBackground()

            VStack(spacing: 16) {
                MovieMateHeader("Add movie")

                ScrollView {
                    VStack(spacing: 20) {
                        if let statusMessage {
                            Label(
                                statusMessage,
                                systemImage: statusIsError ? "exclamationmark.triangle.fill" : "checkmark.circle.fill"
                            )
                            .font(.caption)
                            .foregroundStyle(statusIsError ? MovieMateTheme.destructive : MovieMateTheme.accentLight)
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
                            title: "Save movie",
                            symbol: "checkmark",
                            isLoading: isSaving,
                            isEnabled: editor.canSave,
                            action: { Task { await save() } }
                        )
                    }
                    .padding(.bottom, 34)
                }
                .scrollDismissesKeyboard(.interactively)
            }
            .padding(.horizontal, MovieMateTheme.horizontalPadding)
            .padding(.top, 10)
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
                statusIsError = true
                statusMessage = "No movie data found for this title."
                return
            }
            editor.title = fetched.title
            editor.description = fetched.description
            editor.releaseYear = fetched.releaseYear
            editor.posterURL = fetched.posterURL
            editor.runtimeMinutes = fetched.runtimeMinutes
            editor.imdbRating = fetched.imdbRating

            if editor.categoryID.isEmpty,
               let match = store.categories.first(where: { category in
                   fetched.genres.contains(where: { genre in
                       genre.localizedCaseInsensitiveCompare(category.name) == .orderedSame
                   })
               }) {
                editor.categoryID = match.id
            }
            statusIsError = false
            statusMessage = "Movie data loaded."
        } catch {
            statusIsError = true
            statusMessage = MovieMateStore.message(for: error)
        }
    }

    private func save() async {
        guard editor.canSave else { return }
        isSaving = true
        statusMessage = nil
        defer { isSaving = false }

        do {
            try await store.saveMovie(editor)
            editor = MovieEditorState()
            statusIsError = false
            statusMessage = "Movie saved."
            onSaved()
        } catch {
            statusIsError = true
            statusMessage = MovieMateStore.message(for: error)
        }
    }
}
