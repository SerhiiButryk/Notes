import SwiftUI

struct LoginView: View {
    
    private let titleStr = "Sign in"
    private let fieldUsernameStr = "Username"
    private let fieldPasswordStr = "Password"
    private let signInbuttonStr = "Sing in"
    
    @State private var username = ""
    @State private var password = ""
    @State private var authenticated = false
    
    private let dataModel = AppViewModel()
    
    private let onNavLogin: () -> Void
    
    init(onNavLogin: @escaping () -> Void) {
        self.onNavLogin = onNavLogin
    }
    
    var body: some View {
        
        ZStack {
            
            // Can set background at this point
            
            VStack {
                
                Text(titleStr)
                    .font(.largeTitle.bold())
                    .padding()
                
                TextField(fieldUsernameStr, text: $username)
                    .modifier(CommonFieldStyle(backgroundColor: Color.black.opacity(0.05)))
                
                SecureField(fieldPasswordStr, text: $password)
                    .modifier(CommonFieldStyle(backgroundColor: Color.black.opacity(0.05)))
                
                
                Button(signInbuttonStr) {
                    authenticated = dataModel.login(username, password)
                    if (authenticated) {
                        onNavLogin()
                    }
                }
                .foregroundColor(.white)
                .modifier(CommonFieldStyle(backgroundColor: Color.blue))
            }
            
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

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView{}
    }
}
