import Foundation
import SwiftUI
import UIKit

struct MovieMateHeader<Trailing: View>: View {
    let title: String
    let trailing: Trailing

    init(_ title: String, @ViewBuilder trailing: () -> Trailing) {
        self.title = title
        self.trailing = trailing()
    }

    var body: some View {
        VStack(spacing: 12) {
            HStack(spacing: 12) {
                Text(title)
                    .font(.title2.bold())
                    .foregroundStyle(MovieMateTheme.primaryText)
                Spacer()
                trailing
            }

            ZStack(alignment: .leading) {
                Rectangle()
                    .fill(MovieMateTheme.border)
                    .frame(height: 1)
                Capsule()
                    .fill(MovieMateTheme.accentLight)
                    .frame(width: 54, height: 3)
            }
        }
    }
}

extension MovieMateHeader where Trailing == EmptyView {
    init(_ title: String) {
        self.init(title) { EmptyView() }
    }
}

struct MovieMateIconButton: View {
    let symbol: String
    let accessibilityLabel: String
    var isActive = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: symbol)
                .font(.system(size: 16, weight: .bold))
                .foregroundStyle(isActive ? MovieMateTheme.primaryText : MovieMateTheme.secondaryText)
                .frame(width: 40, height: 40)
                .background(isActive ? MovieMateTheme.accent : MovieMateTheme.elevatedCard)
                .clipShape(Circle())
                .overlay {
                    Circle().stroke(MovieMateTheme.border, lineWidth: isActive ? 0 : 1)
                }
        }
        .buttonStyle(.plain)
        .accessibilityLabel(accessibilityLabel)
    }
}

struct MovieMateSearchField: View {
    let placeholder: String
    @Binding var text: String

    var body: some View {
        HStack(spacing: 10) {
            Image(systemName: "magnifyingglass")
                .foregroundStyle(MovieMateTheme.mutedText)
            TextField(placeholder, text: $text)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .foregroundStyle(MovieMateTheme.primaryText)
            if !text.isEmpty {
                Button {
                    text = ""
                } label: {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundStyle(MovieMateTheme.mutedText)
                }
                .buttonStyle(.plain)
                .accessibilityLabel("Clear search")
            }
        }
        .padding(.horizontal, 14)
        .frame(height: 46)
        .background(MovieMateTheme.card)
        .clipShape(RoundedRectangle(cornerRadius: MovieMateTheme.controlRadius, style: .continuous))
        .overlay {
            RoundedRectangle(cornerRadius: MovieMateTheme.controlRadius, style: .continuous)
                .stroke(MovieMateTheme.border, lineWidth: 1)
        }
    }
}

struct MovieMateEmptyState: View {
    let symbol: String
    let title: String
    let message: String

    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: symbol)
                .font(.system(size: 38, weight: .semibold))
                .foregroundStyle(MovieMateTheme.accentLight)
            Text(title)
                .font(.headline)
                .foregroundStyle(MovieMateTheme.primaryText)
            Text(message)
                .font(.subheadline)
                .foregroundStyle(MovieMateTheme.secondaryText)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 52)
    }
}

struct MoviePosterView: View {
    let source: String
    var cornerRadius: CGFloat = 10

    var body: some View {
        GeometryReader { proxy in
            Group {
                if let image = decodedImage {
                    Image(uiImage: image)
                        .resizable()
                        .interpolation(.medium)
                        .scaledToFill()
                } else if let url = remoteURL {
                    AsyncImage(url: url) { phase in
                        switch phase {
                        case .success(let image):
                            image
                                .resizable()
                                .interpolation(.medium)
                                .scaledToFill()
                        case .failure:
                            placeholder
                        case .empty:
                            ZStack {
                                placeholder
                                ProgressView().tint(MovieMateTheme.accentLight)
                            }
                        @unknown default:
                            placeholder
                        }
                    }
                } else {
                    placeholder
                }
            }
            .frame(width: proxy.size.width, height: proxy.size.height)
            .clipped()
        }
        .background(MovieMateTheme.posterPlaceholder)
        .clipShape(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
    }

    private var decodedImage: UIImage? {
        if let cached = MoviePosterImageCache.shared.image(for: source) {
            return cached
        }

        let image: UIImage?
        if source.hasPrefix("data:image"), let comma = source.firstIndex(of: ",") {
            let encoded = String(source[source.index(after: comma)...])
            image = Data(base64Encoded: encoded, options: .ignoreUnknownCharacters)
                .flatMap(UIImage.init(data:))
        } else if source.hasPrefix("file://"), let url = URL(string: source),
                  let data = try? Data(contentsOf: url) {
            image = UIImage(data: data)
        } else {
            image = nil
        }

        if let image {
            MoviePosterImageCache.shared.insert(image, for: source)
        }
        return image
    }

    private var remoteURL: URL? {
        guard source.hasPrefix("https://") || source.hasPrefix("http://") else { return nil }
        return URL(string: source)
    }

    private var placeholder: some View {
        ZStack {
            MovieMateTheme.posterPlaceholder
            Image(systemName: "film.stack.fill")
                .font(.system(size: 30, weight: .semibold))
                .foregroundStyle(MovieMateTheme.mutedText)
        }
    }
}

private final class MoviePosterImageCache {
    static let shared = MoviePosterImageCache()

    private let cache = NSCache<NSString, UIImage>()

    private init() {
        cache.countLimit = 80
    }

    func image(for source: String) -> UIImage? {
        cache.object(forKey: source as NSString)
    }

    func insert(_ image: UIImage, for source: String) {
        cache.setObject(image, forKey: source as NSString)
    }
}

struct MovieCardView: View {
    let movie: MovieItem
    let category: CategoryItem?

    var body: some View {
        HStack(spacing: 12) {
            MoviePosterView(source: movie.posterURL)
                .frame(width: 96, height: 142)
                .overlay {
                    RoundedRectangle(cornerRadius: 10, style: .continuous)
                        .stroke(MovieMateTheme.border, lineWidth: 1)
                }
                .padding(.leading, 8)
                .padding(.vertical, 8)

            VStack(alignment: .leading, spacing: 8) {
                HStack(alignment: .top, spacing: 8) {
                    Text(movie.title)
                        .font(.headline)
                        .foregroundStyle(MovieMateTheme.primaryText)
                        .lineLimit(2)
                    Spacer(minLength: 4)
                    if movie.isWatched {
                        Image(systemName: "checkmark.circle.fill")
                            .foregroundStyle(MovieMateTheme.accentLight)
                            .accessibilityLabel("Watched")
                    }
                }

                if let category {
                    Label(category.name, systemImage: categorySymbol(for: category.iconKey))
                        .font(.caption.weight(.semibold))
                        .foregroundStyle(MovieMateTheme.accentLight)
                        .lineLimit(1)
                }

                Text(movie.description)
                    .font(.caption)
                    .foregroundStyle(MovieMateTheme.secondaryText)
                    .lineLimit(2)

                Spacer(minLength: 0)

                HStack(spacing: 13) {
                    if !movie.releaseYear.isEmpty {
                        Label(movie.releaseYear, systemImage: "calendar")
                    }
                    if !movie.runtimeMinutes.isEmpty {
                        Label("\(movie.runtimeMinutes) min", systemImage: "clock")
                    }
                    Spacer(minLength: 0)
                    Label(movie.preferredRating, systemImage: "star.fill")
                        .foregroundStyle(MovieMateTheme.rating)
                }
                .font(.caption2.weight(.semibold))
                .foregroundStyle(MovieMateTheme.secondaryText)
            }
            .frame(maxWidth: .infinity, minHeight: 142, alignment: .leading)
            .padding(.vertical, 9)
            .padding(.trailing, 12)
        }
        .frame(maxWidth: .infinity, minHeight: 158, alignment: .leading)
        .movieMateCard()
    }
}

struct CategoryTileView: View {
    let category: CategoryItem
    let movieCount: Int

    var body: some View {
        VStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(MovieMateTheme.accentContainer)
                    .frame(width: 54, height: 54)
                Image(systemName: categorySymbol(for: category.iconKey))
                    .font(.system(size: 23, weight: .semibold))
                    .foregroundStyle(MovieMateTheme.accentLight)
            }
            Text(category.name)
                .font(.headline)
                .foregroundStyle(MovieMateTheme.primaryText)
                .lineLimit(1)
            Text("\(movieCount) \(movieCount == 1 ? "movie" : "movies")")
                .font(.caption)
                .foregroundStyle(MovieMateTheme.secondaryText)
        }
        .frame(maxWidth: .infinity, minHeight: 150)
        .padding(12)
        .movieMateCard()
    }
}

struct MovieMatePrimaryButton: View {
    let title: String
    var symbol: String?
    var isLoading = false
    var isEnabled = true
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 9) {
                if isLoading {
                    ProgressView().tint(MovieMateTheme.primaryText)
                } else if let symbol {
                    Image(systemName: symbol)
                }
                Text(title).fontWeight(.bold)
            }
            .foregroundStyle(MovieMateTheme.primaryText)
            .frame(maxWidth: .infinity)
            .frame(height: 50)
            .background(isEnabled ? MovieMateTheme.accent : MovieMateTheme.border)
            .clipShape(RoundedRectangle(cornerRadius: MovieMateTheme.controlRadius, style: .continuous))
        }
        .buttonStyle(.plain)
        .disabled(!isEnabled || isLoading)
    }
}

struct MovieMateSectionTitle: View {
    let title: String

    init(_ title: String) {
        self.title = title
    }

    var body: some View {
        Text(title.uppercased())
            .font(.caption.weight(.bold))
            .tracking(0.8)
            .foregroundStyle(MovieMateTheme.secondaryText)
            .frame(maxWidth: .infinity, alignment: .leading)
    }
}

struct MovieMateInputModifier: ViewModifier {
    func body(content: Content) -> some View {
        content
            .foregroundStyle(MovieMateTheme.primaryText)
            .padding(.horizontal, 13)
            .frame(minHeight: 46)
            .background(MovieMateTheme.card)
            .clipShape(RoundedRectangle(cornerRadius: MovieMateTheme.controlRadius, style: .continuous))
            .overlay {
                RoundedRectangle(cornerRadius: MovieMateTheme.controlRadius, style: .continuous)
                    .stroke(MovieMateTheme.border, lineWidth: 1)
            }
    }
}

extension View {
    func movieMateInput() -> some View {
        modifier(MovieMateInputModifier())
    }
}
