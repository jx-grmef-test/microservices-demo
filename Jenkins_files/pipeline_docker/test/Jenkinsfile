// Project variables

def projectName = "microservices-demo"

// Nexus Container Registry Service
def nexusRegistry = "http://nexus-docker.apps.meflab.xyz/repository/microservices-demo"
def nexusPassword = "nexus-password"

// Infress Vars
def ingressName = "frontend-external"
def ingressHost = "frontend-external.apps.meflab.xyz"

def app
def imageName

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
pipeline {
    agent any

    environment {
        // Define global variables


        // Tags
        devTag      = "0.0-0"
        prodTag     = "0.0"
        version     = "v1"
    }
    stages {
        stage('GitCheckout') {
            steps {
                // Cloning project microservices-demo
                //checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'jx-pipeline-git-github-github', url: 'https://github.com/jx-grmef-test/microservices-demo.git']]])

                checkout scm

                sh 'pwd && ls -l && ls -l release '

                script {

                    // Set the tag for the development image: version + build number
                    devTag  = "${version}-" + currentBuild.number
                    // Set the tag for the production image: version
                    prodTag = "${version}"
                }
            }
        }
        stage('Build adservice image') {
            steps {
                echo "#####################################\n" +
                        ("# Building adservice image ${devTag}#\n" as java.lang.CharSequence) +
                        ("#####################################" as java.lang.CharSequence)
                dir("src/adservice") {
                    sh "pwd | awk -F \"/\" '{print $NF}'"
                    script {

                        imageName   = "adservice"
                        //imageName   = "adservice"
                        app         = docker.build("microservices-demo/image/${imageName}")
                    }
                }
            }
        }
        stage('Test and Push adservice image to Nexus') {
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "####################################\n" +
                        ("# Testing adservice image ${devTag}#\n" as java.lang.CharSequence) +
                        ("####################################" as java.lang.CharSequence)
                sh "echo $PATH"
                script {
                    docker.image("microservices-demo/image/adservice:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost:9555 || exit 1'
                        sh 'find / -type f -name AdService'
                        //sh 'cat /app/build/install/hipstershop/bin/AdService'
                        sh 'echo "Tests passed"'
                    }
                }
                echo "####################################\n" +
                        ("# Pushing adservice image ${devTag}#\n" as java.lang.CharSequence) +
                        ("####################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("${nexusRegistry}", "${nexusPassword}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
                echo "######################################\n" +
                    ("# Cleaning up unused adservice image #\n" as java.lang.CharSequence) +
                    ("######################################" as java.lang.CharSequence)
                    //sh "docker rmi $registry:$BUILD_NUMBER"
                dir("src/adservice") {
                    sh "pwd | awk -F \"/\" '{print $NF}'"
                    script {

                        //imageName   = "adservice"
                        //imageName   = "adservice"
                        sh "docker rmi \$(docker images --filter=reference=\"${nexusRegistry}/${imageName}*\" -q)"
                    }
                }
            }
        }
    }
}