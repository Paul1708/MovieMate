import PhotosUI
import SwiftUI
import UIKit

struct MovieFormView: View {
    @Binding var editor: MovieEditorState
    let categories: [CategoryItem]
    let isFetching: Bool
    let onFetch: () async -> Void
    let onAddCategory: (String, String) async throws -> CategoryItem?

    @State private var selectedPoster: PhotosPickerItem?
    @State private var isAddingCategory = false
    @State private var posterError: String?

    var body: some View {
        VStack(spacing: 18) {
            posterPicker

            VStack(spacing: 12) {
                HStack(alignment: .bottom, spacing: 10) {
                    labeledField("Title *") {
                        TextField("Movie name", text: $editor.title)
                            .textInputAutocapitalization(.words)
                            .movieMateInput()
                    }

                    Button {
                        Task { await onFetch() }
                    } label: {
                        Group {
                            if isFetching {
                                ProgressView().tint(MovieMateTheme.primaryText)
                            } else {
                                Label("Fetch", systemImage: "magnifyingglass")
                                    .font(.subheadline.bold())
                            }
                        }
                        .frame(width: 90, height: 46)
                        .foregroundStyle(MovieMateTheme.primaryText)
                        .background(MovieMateTheme.accent)
                        .clipShape(RoundedRectangle(cornerRadius: MovieMateTheme.controlRadius))
                    }
                    .buttonStyle(.plain)
                    .disabled(editor.title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || isFetching)
                    .opacity(editor.title.isEmpty ? 0.55 : 1)
                }

                categorySelector

                HStack(spacing: 12) {
                    labeledField("Year") {
                        TextField("2026", text: $editor.releaseYear)
                            .keyboardType(.numberPad)
                            .movieMateInput()
                    }
                    labeledField("Runtime") {
                        TextField("120 min", text: $editor.runtimeMinutes)
                            .keyboardType(.numberPad)
                            .movieMateInput()
                    }
                }

                HStack(spacing: 12) {
                    labeledField("User rating") {
                        TextField("1–10", text: $editor.userRating)
                            .keyboardType(.numberPad)
                            .movieMateInput()
                    }
                    labeledField("IMDb rating") {
                        TextField("0.0–10.0", text: $editor.imdbRating)
                            .keyboardType(.decimalPad)
                            .movieMateInput()
                    }
                }
            }

            Toggle(isOn: $editor.isWatched) {
                Label("Already watched", systemImage: editor.isWatched ? "checkmark.circle.fill" : "circle")
                    .font(.subheadline.bold())
                    .foregroundStyle(MovieMateTheme.primaryText)
            }
            .tint(MovieMateTheme.accent)
            .padding(14)
            .movieMateCard()

            labeledField("About the movie *") {
                TextField("Add a short description", text: $editor.description, axis: .vertical)
                    .lineLimit(4...8)
                    .movieMateInput()
            }

            labeledField("Comment") {
                TextField("Add a personal note", text: $editor.comment, axis: .vertical)
                    .lineLimit(3...6)
                    .movieMateInput()
            }

            watchProvidersEditor
        }
        .sheet(isPresented: $isAddingCategory) {
            AddCategorySheet(onSave: onAddCategory) { category in
                editor.categoryID = category.id
            }
            .presentationDetents([.medium, .large])
            .presentationDragIndicator(.visible)
        }
        .onChange(of: selectedPoster) { _, item in
            guard let item else { return }
            Task { await importPoster(item) }
        }
    }

    private var posterPicker: some View {
        ZStack(alignment: .bottom) {
            MoviePosterView(source: editor.posterURL, cornerRadius: MovieMateTheme.cardRadius)
                .frame(maxWidth: .infinity)
                .frame(height: 220)

            LinearGradient(
                colors: [.clear, MovieMateTheme.background.opacity(0.9)],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: 95)

            PhotosPicker(selection: $selectedPoster, matching: .images) {
                Label(editor.posterURL.isEmpty ? "Add poster" : "Change poster", systemImage: "photo.fill")
                    .font(.subheadline.bold())
                    .foregroundStyle(MovieMateTheme.primaryText)
                    .padding(.horizontal, 18)
                    .frame(height: 42)
                    .background(MovieMateTheme.accent)
                    .clipShape(Capsule())
                    .padding(.bottom, 18)
            }
            .accessibilityLabel(editor.posterURL.isEmpty ? "Add poster" : "Change poster")
        }
        .overlay {
            RoundedRectangle(cornerRadius: MovieMateTheme.cardRadius)
                .stroke(MovieMateTheme.border, lineWidth: 1)
        }
        .overlay(alignment: .topTrailing) {
            if !editor.posterURL.isEmpty {
                Button {
                    editor.posterURL = ""
                    selectedPoster = nil
                } label: {
                    Image(systemName: "xmark")
                        .font(.caption.bold())
                        .foregroundStyle(MovieMateTheme.primaryText)
                        .frame(width: 32, height: 32)
                        .background(.black.opacity(0.65))
                        .clipShape(Circle())
                        .padding(10)
                }
                .accessibilityLabel("Remove poster")
            }
        }
        .overlay(alignment: .topLeading) {
            if let posterError {
                Text(posterError)
                    .font(.caption)
                    .foregroundStyle(MovieMateTheme.destructive)
                    .padding(12)
            }
        }
    }

    private var categorySelector: some View {
        VStack(alignment: .leading, spacing: 7) {
            MovieMateSectionTitle("Category")
            HStack(spacing: 10) {
                Menu {
                    Button("No category") { editor.categoryID = "" }
                    ForEach(categories) { category in
                        Button {
                            editor.categoryID = category.id
                        } label: {
                            Label(category.name, systemImage: categorySymbol(for: category.iconKey))
                        }
                    }
                } label: {
                    HStack {
                        if let selected = categories.first(where: { $0.id == editor.categoryID }) {
                            Label(selected.name, systemImage: categorySymbol(for: selected.iconKey))
                        } else {
                            Label("No category", systemImage: "film")
                        }
                        Spacer()
                        Image(systemName: "chevron.up.chevron.down")
                            .font(.caption.bold())
                            .foregroundStyle(MovieMateTheme.secondaryText)
                    }
                    .foregroundStyle(MovieMateTheme.primaryText)
                    .movieMateInput()
                }

                Button {
                    isAddingCategory = true
                } label: {
                    Image(systemName: "plus")
                        .font(.headline.bold())
                        .foregroundStyle(MovieMateTheme.primaryText)
                        .frame(width: 46, height: 46)
                        .background(MovieMateTheme.accent)
                        .clipShape(RoundedRectangle(cornerRadius: MovieMateTheme.controlRadius))
                }
                .accessibilityLabel("Add category")
            }
        }
    }

    private var watchProvidersEditor: some View {
        VStack(alignment: .leading, spacing: 10) {
            MovieMateSectionTitle("Watch providers")

            if !editor.watchProviders.isEmpty {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(editor.watchProviders, id: \.self) { provider in
                            HStack(spacing: 6) {
                                Text(provider)
                                Button {
                                    editor.watchProviders.removeAll(where: { $0 == provider })
                                } label: {
                                    Image(systemName: "xmark.circle.fill")
                                }
                                .accessibilityLabel("Remove \(provider)")
                            }
                            .font(.caption.bold())
                            .foregroundStyle(MovieMateTheme.accentLight)
                            .padding(.horizontal, 11)
                            .frame(height: 32)
                            .background(MovieMateTheme.accentContainer)
                            .clipShape(Capsule())
                        }
                    }
                }
            }

            HStack(spacing: 10) {
                TextField("Netflix, cinema, …", text: $editor.watchProviderInput)
                    .submitLabel(.done)
                    .onSubmit(addWatchProvider)
                    .movieMateInput()
                Button(action: addWatchProvider) {
                    Image(systemName: "plus")
                        .font(.headline.bold())
                        .foregroundStyle(MovieMateTheme.primaryText)
                        .frame(width: 46, height: 46)
                        .background(MovieMateTheme.accent)
                        .clipShape(RoundedRectangle(cornerRadius: MovieMateTheme.controlRadius))
                }
                .disabled(editor.watchProviderInput.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                .accessibilityLabel("Add watch provider")
            }
        }
    }

    private func labeledField<Content: View>(
        _ title: String,
        @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: 7) {
            MovieMateSectionTitle(title)
            content()
        }
        .frame(maxWidth: .infinity)
    }

    private func addWatchProvider() {
        let value = editor.watchProviderInput.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !value.isEmpty, !editor.watchProviders.contains(value) else { return }
        editor.watchProviders.append(value)
        editor.watchProviderInput = ""
    }

    private func importPoster(_ item: PhotosPickerItem) async {
        do {
            guard let data = try await item.loadTransferable(type: Data.self),
                  let image = UIImage(data: data),
                  let jpeg = image.movieMatePortableJPEG(maxDimension: 1400, quality: 0.82) else {
                posterError = "The selected image could not be loaded."
                return
            }
            editor.posterURL = "data:image/jpeg;base64,\(jpeg.base64EncodedString())"
            posterError = nil
        } catch {
            posterError = "The selected image could not be loaded."
        }
    }
}

struct AddCategorySheet: View {
    let onSave: (String, String) async throws -> CategoryItem?
    let onCreated: (CategoryItem) -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var name = ""
    @State private var iconKey = "other"
    @State private var isSaving = false
    @State private var errorMessage: String?

    var body: some View {
        NavigationStack {
            ZStack {
                MovieMateBackground()
                ScrollView {
                    VStack(alignment: .leading, spacing: 18) {
                        MovieMateSectionTitle("Name")
                        TextField("Category name", text: $name)
                            .textInputAutocapitalization(.words)
                            .movieMateInput()

                        MovieMateSectionTitle("Icon")
                        LazyVGrid(columns: [GridItem(.adaptive(minimum: 66), spacing: 10)], spacing: 10) {
                            ForEach(categoryIconOptions) { option in
                                Button {
                                    iconKey = option.id
                                } label: {
                                    Image(systemName: option.symbol)
                                        .font(.system(size: 20, weight: .semibold))
                                        .foregroundStyle(iconKey == option.id ? MovieMateTheme.primaryText : MovieMateTheme.secondaryText)
                                        .frame(width: 62, height: 54)
                                        .background(iconKey == option.id ? MovieMateTheme.accent : MovieMateTheme.card)
                                        .clipShape(RoundedRectangle(cornerRadius: 11))
                                        .overlay {
                                            RoundedRectangle(cornerRadius: 11)
                                                .stroke(MovieMateTheme.border, lineWidth: iconKey == option.id ? 0 : 1)
                                        }
                                }
                                .accessibilityLabel(option.title)
                            }
                        }

                        if let errorMessage {
                            Text(errorMessage)
                                .font(.caption)
                                .foregroundStyle(MovieMateTheme.destructive)
                        }
                    }
                    .padding(MovieMateTheme.horizontalPadding)
                }
            }
            .navigationTitle("New category")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(isSaving ? "Saving…" : "Save") {
                        Task { await save() }
                    }
                    .disabled(name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || isSaving)
                }
            }
            .tint(MovieMateTheme.accentLight)
            .toolbarBackground(MovieMateTheme.backgroundTop, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
        }
        .preferredColorScheme(.dark)
    }

    private func save() async {
        isSaving = true
        defer { isSaving = false }
        do {
            if let category = try await onSave(name, iconKey) {
                onCreated(category)
            }
            dismiss()
        } catch {
            errorMessage = MovieMateStore.message(for: error)
        }
    }
}

private extension UIImage {
    func movieMatePortableJPEG(maxDimension: CGFloat, quality: CGFloat) -> Data? {
        let largestSide = max(size.width, size.height)
        guard largestSide > 0 else { return nil }
        let scale = min(1, maxDimension / largestSide)
        let targetSize = CGSize(width: size.width * scale, height: size.height * scale)
        let renderer = UIGraphicsImageRenderer(size: targetSize)
        let rendered = renderer.image { _ in
            draw(in: CGRect(origin: .zero, size: targetSize))
        }

        var currentQuality = quality
        while currentQuality >= 0.42 {
            guard let data = rendered.jpegData(compressionQuality: currentQuality) else {
                return nil
            }
            if data.count <= 3 * 1024 * 1024 {
                return data
            }
            currentQuality -= 0.08
        }
        return nil
    }
}
