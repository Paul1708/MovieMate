import GoogleSignInSwift
import SwiftUI
import UniformTypeIdentifiers

struct BackupView: View {
    @EnvironmentObject private var store: MovieMateStore

    @State private var isImporting = false
    @State private var isExporting = false
    @State private var exportDocument: MovieMateBackupDocument?
    @State private var isWorkingWithFile = false
    @State private var fileStatus: String?
    @State private var fileStatusIsError = false

    var body: some View {
        ZStack {
            MovieMateBackground()

            VStack(spacing: 16) {
                MovieMateHeader("Backup & sync")

                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        GoogleDriveSyncCard()

                        MovieMateSectionTitle("Manual backup")
                            .padding(.top, 6)

                        backupActionCard(
                            title: "Export",
                            subtitle: "Save your library as a JSON backup file.",
                            symbol: "square.and.arrow.up"
                        ) {
                            Task { await prepareExport() }
                        }

                        backupActionCard(
                            title: "Import",
                            subtitle: "Restore a previously exported MovieMate backup.",
                            symbol: "square.and.arrow.down"
                        ) {
                            isImporting = true
                        }

                        if isWorkingWithFile {
                            HStack(spacing: 9) {
                                ProgressView().tint(MovieMateTheme.accentLight)
                                Text("Working with backup file…")
                            }
                            .font(.caption)
                            .foregroundStyle(MovieMateTheme.secondaryText)
                            .frame(maxWidth: .infinity, alignment: .center)
                            .padding(.top, 5)
                        } else if let fileStatus {
                            Label(
                                fileStatus,
                                systemImage: fileStatusIsError ? "exclamationmark.triangle.fill" : "checkmark.circle.fill"
                            )
                            .font(.caption)
                            .foregroundStyle(fileStatusIsError ? MovieMateTheme.destructive : MovieMateTheme.accentLight)
                        }

                        Text("Cloud sync uses Google Drive’s private app-data folder. Your MovieMate data is not visible among your regular Drive files.")
                            .font(.caption)
                            .foregroundStyle(MovieMateTheme.mutedText)
                            .padding(.top, 4)
                    }
                    .padding(.bottom, 34)
                }
            }
            .padding(.horizontal, MovieMateTheme.horizontalPadding)
            .padding(.top, 10)
        }
        .fileImporter(
            isPresented: $isImporting,
            allowedContentTypes: [.json],
            allowsMultipleSelection: false,
            onCompletion: importBackup
        )
        .fileExporter(
            isPresented: $isExporting,
            document: exportDocument,
            contentType: .json,
            defaultFilename: "moviemate-backup"
        ) { result in
            switch result {
            case .success:
                fileStatusIsError = false
                fileStatus = "Backup exported."
            case .failure(let error):
                fileStatusIsError = true
                fileStatus = MovieMateStore.message(for: error)
            }
            exportDocument = nil
        }
    }

    @ViewBuilder
    private func GoogleDriveSyncCard() -> some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(spacing: 13) {
                ZStack {
                    Circle()
                        .fill(MovieMateTheme.accentContainer)
                        .frame(width: 52, height: 52)
                    Image(systemName: "arrow.triangle.2.circlepath.icloud.fill")
                        .font(.system(size: 23, weight: .bold))
                        .foregroundStyle(MovieMateTheme.accentLight)
                }

                VStack(alignment: .leading, spacing: 3) {
                    Text("Google Drive sync")
                        .font(.headline)
                        .foregroundStyle(MovieMateTheme.primaryText)
                    Text(store.googleAccount == nil ? "Keep every device up to date" : "Synchronization enabled")
                        .font(.caption)
                        .foregroundStyle(MovieMateTheme.secondaryText)
                }
                Spacer()
                syncStatusGlyph
            }

            if let account = store.googleAccount {
                HStack(spacing: 11) {
                    Text(String(account.displayName.prefix(1)).uppercased())
                        .font(.headline.bold())
                        .foregroundStyle(MovieMateTheme.primaryText)
                        .frame(width: 42, height: 42)
                        .background(MovieMateTheme.accent)
                        .clipShape(Circle())
                    VStack(alignment: .leading, spacing: 2) {
                        Text(account.displayName)
                            .font(.subheadline.bold())
                            .foregroundStyle(MovieMateTheme.primaryText)
                            .lineLimit(1)
                        Text(account.email)
                            .font(.caption)
                            .foregroundStyle(MovieMateTheme.secondaryText)
                            .lineLimit(1)
                    }
                    Spacer()
                }

                syncStatusText

                HStack(spacing: 10) {
                    Button {
                        Task { await store.syncNow() }
                    } label: {
                        Label("Sync now", systemImage: "arrow.triangle.2.circlepath")
                            .font(.subheadline.bold())
                            .foregroundStyle(MovieMateTheme.primaryText)
                            .frame(maxWidth: .infinity)
                            .frame(height: 46)
                            .background(MovieMateTheme.accent)
                            .clipShape(RoundedRectangle(cornerRadius: MovieMateTheme.controlRadius))
                    }
                    .buttonStyle(.plain)
                    .disabled(store.syncState == .syncing)

                    Button("Sign out") {
                        store.signOutFromGoogle()
                    }
                    .font(.subheadline.bold())
                    .foregroundStyle(MovieMateTheme.secondaryText)
                    .frame(height: 46)
                    .padding(.horizontal, 10)
                }
            } else {
                Text("Sign in once to merge titles, categories and their contents across your signed-in devices.")
                    .font(.subheadline)
                    .foregroundStyle(MovieMateTheme.secondaryText)

                if store.googleConfigurationMissing {
                    Label("Add the iOS OAuth client values to config.local.xcconfig first.", systemImage: "wrench.and.screwdriver.fill")
                        .font(.caption)
                        .foregroundStyle(MovieMateTheme.rating)
                }

                GoogleSignInButton {
                    Task { await store.signInWithGoogle() }
                }
                .frame(height: 50)
                .disabled(store.googleConfigurationMissing || store.isAuthenticating)
                .opacity(store.googleConfigurationMissing ? 0.55 : 1)
                .overlay {
                    if store.isAuthenticating {
                        RoundedRectangle(cornerRadius: 4)
                            .fill(.black.opacity(0.25))
                            .overlay { ProgressView().tint(.white) }
                    }
                }
            }
        }
        .padding(16)
        .movieMateCard()
    }

    @ViewBuilder
    private var syncStatusGlyph: some View {
        switch store.syncState {
        case .syncing:
            ProgressView().tint(MovieMateTheme.accentLight)
        case .success:
            Image(systemName: "checkmark.circle.fill").foregroundStyle(MovieMateTheme.accentLight)
        case .offline:
            Image(systemName: "wifi.slash").foregroundStyle(MovieMateTheme.rating)
        case .failure:
            Image(systemName: "exclamationmark.circle.fill").foregroundStyle(MovieMateTheme.destructive)
        case .idle:
            Image(systemName: "cloud").foregroundStyle(MovieMateTheme.mutedText)
        }
    }

    @ViewBuilder
    private var syncStatusText: some View {
        switch store.syncState {
        case .idle:
            Text("Ready to synchronize")
                .foregroundStyle(MovieMateTheme.secondaryText)
        case .syncing:
            Text("Synchronizing titles and categories…")
                .foregroundStyle(MovieMateTheme.secondaryText)
        case .success(let date):
            Text("Last synchronized \(date.formatted(date: .omitted, time: .shortened))")
                .foregroundStyle(MovieMateTheme.accentLight)
        case .offline(let message):
            Text(message).foregroundStyle(MovieMateTheme.rating)
        case .failure(let message):
            Text(message).foregroundStyle(MovieMateTheme.destructive)
        }
    }

    private func backupActionCard(
        title: String,
        subtitle: String,
        symbol: String,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            HStack(spacing: 14) {
                Image(systemName: symbol)
                    .font(.system(size: 20, weight: .bold))
                    .foregroundStyle(MovieMateTheme.primaryText)
                    .frame(width: 46, height: 46)
                    .background(MovieMateTheme.accent)
                    .clipShape(Circle())

                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(.headline)
                        .foregroundStyle(MovieMateTheme.primaryText)
                    Text(subtitle)
                        .font(.caption)
                        .foregroundStyle(MovieMateTheme.secondaryText)
                        .multilineTextAlignment(.leading)
                }
                Spacer()
                Image(systemName: "chevron.right")
                    .foregroundStyle(MovieMateTheme.secondaryText)
            }
            .padding(15)
            .frame(maxWidth: .infinity, minHeight: 94)
            .movieMateCard()
        }
        .buttonStyle(.plain)
        .disabled(isWorkingWithFile)
    }

    private func prepareExport() async {
        isWorkingWithFile = true
        fileStatus = nil
        defer { isWorkingWithFile = false }
        do {
            exportDocument = MovieMateBackupDocument(json: try await store.exportBackup())
            isExporting = true
        } catch {
            fileStatusIsError = true
            fileStatus = MovieMateStore.message(for: error)
        }
    }

    private func importBackup(_ result: Result<[URL], Error>) {
        switch result {
        case .failure(let error):
            fileStatusIsError = true
            fileStatus = MovieMateStore.message(for: error)
        case .success(let urls):
            guard let url = urls.first else { return }
            Task { await readAndImport(url) }
        }
    }

    private func readAndImport(_ url: URL) async {
        isWorkingWithFile = true
        fileStatus = nil
        defer { isWorkingWithFile = false }

        let accessed = url.startAccessingSecurityScopedResource()
        defer { if accessed { url.stopAccessingSecurityScopedResource() } }
        do {
            let json = String(decoding: try Data(contentsOf: url), as: UTF8.self)
            try await store.importBackup(json)
            fileStatusIsError = false
            fileStatus = "Backup imported."
        } catch {
            fileStatusIsError = true
            fileStatus = MovieMateStore.message(for: error)
        }
    }
}

struct MovieMateBackupDocument: FileDocument {
    static var readableContentTypes: [UTType] { [.json] }
    var json: String

    init(json: String) {
        self.json = json
    }

    init(configuration: ReadConfiguration) throws {
        guard let data = configuration.file.regularFileContents else {
            throw CocoaError(.fileReadCorruptFile)
        }
        json = String(decoding: data, as: UTF8.self)
    }

    func fileWrapper(configuration: WriteConfiguration) throws -> FileWrapper {
        FileWrapper(regularFileWithContents: Data(json.utf8))
    }
}
