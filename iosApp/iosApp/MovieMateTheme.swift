import SwiftUI

enum MovieMateTheme {
    static let background = Color(hex: 0x060A13)
    static let backgroundTop = Color(hex: 0x121A28)
    static let backgroundGlow = Color(hex: 0x1C2A44)
    static let card = Color(hex: 0x151C29)
    static let elevatedCard = Color(hex: 0x1A2231)
    static let border = Color(hex: 0x283346)
    static let posterPlaceholder = Color(hex: 0x273246)
    static let primaryText = Color(hex: 0xF8FAFF)
    static let secondaryText = Color(hex: 0xB8BECC)
    static let mutedText = Color(hex: 0x788296)
    static let accent = Color(hex: 0x9B5CF6)
    static let accentLight = Color(hex: 0xB780FF)
    static let accentContainer = Color(hex: 0x2A1B47)
    static let rating = Color(hex: 0xFFC928)
    static let destructive = Color(hex: 0xFF6B73)

    static let horizontalPadding: CGFloat = 18
    static let cardRadius: CGFloat = 14
    static let controlRadius: CGFloat = 12
}

extension Color {
    init(hex: UInt32) {
        self.init(
            red: Double((hex >> 16) & 0xFF) / 255,
            green: Double((hex >> 8) & 0xFF) / 255,
            blue: Double(hex & 0xFF) / 255
        )
    }
}

struct MovieMateBackground: View {
    var body: some View {
        LinearGradient(
            colors: [MovieMateTheme.backgroundTop, MovieMateTheme.background],
            startPoint: .top,
            endPoint: .center
        )
        .overlay(alignment: .topTrailing) {
            Circle()
                .fill(MovieMateTheme.backgroundGlow.opacity(0.45))
                .frame(width: 270, height: 270)
                .blur(radius: 85)
                .offset(x: 110, y: -135)
        }
        .ignoresSafeArea()
    }
}

struct MovieMateCardModifier: ViewModifier {
    func body(content: Content) -> some View {
        content
            .background(MovieMateTheme.elevatedCard)
            .clipShape(RoundedRectangle(cornerRadius: MovieMateTheme.cardRadius, style: .continuous))
            .overlay {
                RoundedRectangle(cornerRadius: MovieMateTheme.cardRadius, style: .continuous)
                    .stroke(MovieMateTheme.border, lineWidth: 1)
            }
    }
}

extension View {
    func movieMateCard() -> some View {
        modifier(MovieMateCardModifier())
    }
}
