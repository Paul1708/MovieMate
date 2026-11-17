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
                            isFetching: $isFetching,
                            onAddCategory: store.addCategory,
                            suggestsCategory: true
                        )

                        MovieMatePrimaryButton(
                            title: "Save movie",
                            symbol: "checkmark",
                            isLoading: isSaving,
                            isEnabled: editor.canSave && !isFetching,
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

    private func save() async {
        guard editor.canSave, !isFetching else { return }
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
