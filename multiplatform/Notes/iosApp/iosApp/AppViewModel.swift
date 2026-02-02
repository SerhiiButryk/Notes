import OSLog

class AppViewModel {
    
    private let logTag = "AppViewModel"
    private let logger: Logger
    
    init() {
        var bundleId = ""
        
        if let bundleIdentifier = Bundle.main.bundleIdentifier {
            bundleId = bundleIdentifier
        }
        
        self.logger = Logger(subsystem: bundleId, category: logTag)
    }
    
    func login(_ username: String, _ password: String) -> Bool {
        logger.info("login: done")
        return true
    }
    
}
