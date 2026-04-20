import SwiftUI
import OSLog

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

func createLogger(withTag tag: String) -> Logger {
    var subsystem = ""
    if let bundleIdentifier = Bundle.main.bundleIdentifier {
        subsystem = bundleIdentifier
    }
    return Logger(subsystem: subsystem, category: tag)
}
