import OSLog

class AppViewModel {
    
    private let logger = createLogger(withTag: "AppViewModel")
    
    func login(_ username: String, _ password: String) -> Bool {
        logger.info("login: called")
        return true
    }
    
}
