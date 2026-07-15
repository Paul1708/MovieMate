import Foundation
import GoogleSignIn
import UIKit

@MainActor
final class GoogleDriveAuthService {
    private let driveAppDataScope = "https://www.googleapis.com/auth/drive.appdata"

    var isConfigured: Bool {
        guard let clientID = configuredClientID else { return false }
        return !clientID.contains("NOT_CONFIGURED")
    }

    func restorePreviousSignIn() async throws -> GoogleAccount? {
        guard configureGoogleSignIn(), GIDSignIn.sharedInstance.hasPreviousSignIn() else {
            return nil
        }
        let user = try await GIDSignIn.sharedInstance.restorePreviousSignIn()
        return try await refreshedAccount(from: user)
    }

    func signIn() async throws -> GoogleAccount {
        guard configureGoogleSignIn() else {
            throw GoogleAuthError.missingConfiguration
        }
        guard let presenter = Self.presentingViewController() else {
            throw GoogleAuthError.missingPresenter
        }
        let result = try await GIDSignIn.sharedInstance.signIn(
            withPresenting: presenter,
            hint: nil,
            additionalScopes: [driveAppDataScope]
        )
        let user = try await ensureDriveAppDataScope(
            for: result.user,
            presenting: presenter
        )
        return try await refreshedAccount(from: user)
    }

    func refreshCurrentAccount() async throws -> GoogleAccount {
        guard let user = GIDSignIn.sharedInstance.currentUser else {
            throw GoogleAuthError.notSignedIn
        }
        return try await refreshedAccount(from: user)
    }

    func signOut() {
        GIDSignIn.sharedInstance.signOut()
    }

    func handle(url: URL) -> Bool {
        GIDSignIn.sharedInstance.handle(url)
    }

    private var configuredClientID: String? {
        (Bundle.main.object(forInfoDictionaryKey: "GIDClientID") as? String)?
            .trimmingCharacters(in: .whitespacesAndNewlines)
            .nilIfEmpty
    }

    @discardableResult
    private func configureGoogleSignIn() -> Bool {
        guard let clientID = configuredClientID, !clientID.contains("NOT_CONFIGURED") else {
            return false
        }
        GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID: clientID)
        return true
    }

    private func refreshedAccount(from user: GIDGoogleUser) async throws -> GoogleAccount {
        let refreshedUser = try await user.refreshTokensIfNeeded()
        guard refreshedUser.grantedScopes?.contains(driveAppDataScope) == true else {
            throw GoogleAuthError.missingDriveAppDataScope
        }
        guard let stableAccountID = refreshedUser.userID?.nilIfEmpty else {
            throw GoogleAuthError.missingAccountID
        }
        let profile = refreshedUser.profile
        return GoogleAccount(
            id: stableAccountID,
            displayName: (profile?.name)?.nilIfEmpty ?? profile?.email ?? "Google account",
            email: profile?.email ?? "",
            accessToken: refreshedUser.accessToken.tokenString
        )
    }

    private func ensureDriveAppDataScope(
        for user: GIDGoogleUser,
        presenting presenter: UIViewController
    ) async throws -> GIDGoogleUser {
        if user.grantedScopes?.contains(driveAppDataScope) == true {
            return user
        }

        let result = try await user.addScopes(
            [driveAppDataScope],
            presenting: presenter
        )
        guard result.user.grantedScopes?.contains(driveAppDataScope) == true else {
            throw GoogleAuthError.missingDriveAppDataScope
        }
        return result.user
    }

    private static func presentingViewController() -> UIViewController? {
        let scenes = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .filter { $0.activationState == .foregroundActive }
        guard let root = scenes
            .flatMap(\.windows)
            .first(where: \.isKeyWindow)?
            .rootViewController else {
            return nil
        }

        var presenter = root
        while let presented = presenter.presentedViewController {
            presenter = presented
        }
        return presenter
    }
}

enum GoogleAuthError: LocalizedError {
    case missingConfiguration
    case missingPresenter
    case missingAccountID
    case missingDriveAppDataScope
    case notSignedIn

    var errorDescription: String? {
        switch self {
        case .missingConfiguration:
            return "Google Sign-In is not configured. Add the client values to config.local.xcconfig."
        case .missingPresenter:
            return "Google Sign-In cannot be presented right now."
        case .missingAccountID:
            return "Google did not return a stable account identifier."
        case .missingDriveAppDataScope:
            return "Google Drive app-data access was not granted. Disconnect MovieMate from Google and sign in again."
        case .notSignedIn:
            return "No Google account is signed in."
        }
    }
}

private extension String {
    var nilIfEmpty: String? { isEmpty ? nil : self }
}
