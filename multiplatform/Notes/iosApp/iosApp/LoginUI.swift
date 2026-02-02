import SwiftUI

struct LoginUI: View {
    
    private let titleStr = "Login"
    private let fieldUsernameStr = "Username"
    private let fieldPasswordStr = "Password"
    private let buttonStr = "Sing in"
    
    @State private var username = ""
    @State private var password = ""
    @State private var authenticated = false
    
    private let appViewModel = AppViewModel()
    
    var body: some View {
        
        NavigationView {
            
            ZStack {
                
                // Can set background at this point
                
                VStack {
                    
                    Text(titleStr)
                        .font(.largeTitle)
                        .bold()
                        .padding()
                    
                    TextField(fieldUsernameStr, text: $username)
                        .modifier(CommonFieldStyle(backgroundColor: Color.black.opacity(0.05)))
                    
                    SecureField(fieldPasswordStr, text: $password)
                        .modifier(CommonFieldStyle(backgroundColor: Color.black.opacity(0.05)))
                    
                        
                    Button(buttonStr) {
                        authenticated = appViewModel.login(username, password)
                        
                    }
                    .foregroundColor(.white)
                    .modifier(CommonFieldStyle(backgroundColor: Color.blue))
                    
                    
                    NavigationLink(destination: Text("Some text"), isActive: $authenticated) {
                        EmptyView()
                    }
                }
                
            }.navigationBarHidden(true)
            
        }
        
    }
}

struct CommonFieldStyle : ViewModifier {
    
    let backgroundColor: Color
    
    func body(content: Content) -> some View {
        content.padding()
        .frame(maxWidth: .infinity)
        .background(backgroundColor)
        .cornerRadius(10)
        .padding([.leading, .trailing], 20)
    }
    
}

struct LoginUI_Previews: PreviewProvider {
    static var previews: some View {
        LoginUI()
    }
}
