// Copyright 2022. Happy coding ! :)
// Author: Serhii Butryk 

pipeline {
    // Build on any available node
    agent any 

    stages {
        // Building the app stage
        stage('Build') {
            steps {
                sh "pwd"
                sh "cd Notes"
                sh "pwd"
                sh "./gradlew tasks"
                sh "./gradlew assemble"
            }
        }
        
    }

}
