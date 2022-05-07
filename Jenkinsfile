// Copyright 2022. Happy coding ! :)
// Author: Serhii Butryk 

pipeline {
    // Build on any available node
    agent any 

    stages {
        // Building the app stage
        stage('Build Android App') {
            
            steps {
                
                script {
                    // Go to Notes directory
                    dir("${env.WORKSPACE}/Notes") {
                        sh "./gradlew assemble"
                    }
                }
                
            } // end step
        } // end stage
    } // end stages
} // end pipeline
