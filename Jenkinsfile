// Copyright 2022. Happy coding ! :)
// Author: Serhii Butryk 

pipeline {
    // Build on any available node
    agent any 

    stages {
        // Building the app stage
        stage('Build App') {
            steps {
                script {
                    dir("${env.WORKSPACE}/Notes") {
                        sh "./gradlew assemble"
                    }
                }
            }
        }
        
    }

}
