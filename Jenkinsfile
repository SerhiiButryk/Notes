// Copyright 2022. Happy coding ! :)
// Author: Serhii Butryk 

pipeline {
    // Build on any available node
    agent any 

    stages {
        
        // // Building app stage
        // stage('Build Android App') {
        //     steps {
        //         script {
        //             // Go to directory
        //             dir("${env.WORKSPACE}/tools") {
        //                 // Execute script
        //                 sh "./build_app.sh"
        //             }
        //         }
        //     } // end step
        // } // end stage 

        // Running tests
        // To run tests jenkins user should be able to run commands as super user.
        // To configure this edit '/etc/sudoers' file on Linux and add the next line:
        // "jenkins hostname = (root) NOPASSWD: /usr/sbin/", where hostname is yout machine name.
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