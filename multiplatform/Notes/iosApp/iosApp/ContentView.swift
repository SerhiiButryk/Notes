import SwiftUI

struct ContentView: View {
    
    @State private var path: [String] = []
    
    var body: some View {
        
        let onNavLogin: () -> Void = {
            path.append(NotesPreviewScreen)
        }
        
        NavigationStack(path: $path) {
            
            LoginView(onNavLogin: onNavLogin)
            
            .navigationDestination(for: String.self) { value in
                if value == NotesPreviewScreen {
                    MainAppView()
                }
            }
            
        }
        
    }
    
}
