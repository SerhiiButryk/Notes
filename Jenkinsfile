// Copyright 2022. Happy coding ! :)
// Author: Serhii Butryk 

pipeline {
    // Build on any available node
    agent any 

    stages {
        // Building the app stage
        stage('Build') {
            steps {
                sh "cd Notes" 
                sh "./gradlew tasks"
                sh "./gradlew assemble"
            }
        }
        
    }

}
