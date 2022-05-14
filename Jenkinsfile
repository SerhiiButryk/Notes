// Copyright 2022. Happy coding ! :)
// Author: Serhii Butryk 

pipeline {
    // Build on any available node
    agent any 

    stages {
        
        // Building app stage
        stage('Build Android App') {
            steps {
                script {
                    // Go to directory
                    dir("${env.WORKSPACE}/tools") {
                        // Execute script
                        // sh "./build_app.sh"
                    }
                }
            } // end step
        } // end stage 

        // Running tests
        stage('Running tests') {
            steps {
                script {
                    // Go to directory
                    dir("${env.WORKSPACE}/tools") {
                        // Execute script
                        sh "./run_tests.sh"
                    }
                }        

            } // end step
        } // end stage

        // At clean up stage we do some operations which are needed at the end.
        // For example, we needed to kill emulator, because if tests are failed
        // it wan't be stopped.
        stage('Clean up') {
            steps {
                script {
                    // Go to directory
                    dir("${env.WORKSPACE}/tools") {
                        // Execute script
                        sh "./jenkins_clean_up.sh"
                    }
                }        

            } // end step
        } // end stage
        
        // Archive artifacts stage
        stage('Archive artifacts') {
            steps {
                script {

                    // Archive app artifacts    
                    archiveArtifacts([
                        artifacts: 'Notes-App/**/*.*', 
                        fingerprint: true, 
                        followSymlinks: false, 
                        onlyIfSuccessful: true        
                    ]) 
                    
                }        

            } // end step
        } // end stage

    } // end stages
} // end pipeline