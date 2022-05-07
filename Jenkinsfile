// Copyright 2022. Happy coding ! :)
// Author: Serhii Butryk 

pipeline {
    // Build on any available node
    agent any 

    stages {
        // Building the app stage
        stage('Build') {
            steps {
                echo 'Started with building Notes' 
                sh "./Notes/gradlew assemble"
                echo 'Finished with building Notes' 
            }
        }
        
    }

}