import SwiftUI

/**
    View which displays notes to users
 */
struct MainAppView: View {
    
    @State private var path: [String] = []
    
    var body: some View {
        
        List {
            Text("A note one")
            Text("A note two")
            Text("A note one three")
        }
        
    }
    
}

