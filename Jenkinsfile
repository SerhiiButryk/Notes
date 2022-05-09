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
                    dir("${env.WORKSPACE}/tools") {
                        // Execute build script
                        sh "./build_app.sh"
                    }
                }
            } // end step
        } // end stage 
        
        stage('Archive artifacts') {
            steps {

                def distDir = "${env.WORKSPACE}/dist"    

                archiveArtifacts
                    artifacts: 'dist', 
                    fingerprint: true, 
                    followSymlinks: false, 
                    onlyIfSuccessful: true

            }
        } // end stage

    } // end stages
} // end pipeline