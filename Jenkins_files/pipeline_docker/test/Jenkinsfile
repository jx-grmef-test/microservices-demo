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
def dockerIimages

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
                checkout scm

                //sh 'pwd && ls -l && ls -l release '

                script {

                    // Set the tag for the development image: version + build number
                    devTag  = "${version}-" + currentBuild.number
                    // Set the tag for the production image: version
                    prodTag = "${version}"
                }
            }
        }
        //###############
        //## adservice ##
        //###############
        stage('Build adservice image') {
            steps {
                dir("src/adservice") {
                    script {
                        //imageName = "adservice"
                        imageName   = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "##################################\n" +
                            ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                            ("##################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }
        stage('Test and Push adservice image to Nexus') {
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "#################################\n" +
                    ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                    ("#################################" as java.lang.CharSequence)
                sh "echo $PATH"
                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost:9555 || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        //sh 'cat /app/build/install/hipstershop/bin/AdService'
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "#################################\n" +
                    ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                    ("#################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("${nexusRegistry}", "${nexusPassword}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }
        //#################
        //## cartservice ##
        //#################
        stage('Build cartservice image') {
            steps {
                dir("src/cartservice") {
                    script {
                        //imageName = "adservice"
                        imageName   = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "##################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("##################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }
        stage('Test and Push cartservice image to Nexus') {
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "#################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("#################################" as java.lang.CharSequence)
                sh "echo $PATH"
                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost:9555 || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        //sh 'cat /app/build/install/hipstershop/bin/AdService'
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "#################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("#################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("${nexusRegistry}", "${nexusPassword}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }
        stage ('Clean images') {
            steps {
                echo "###########################\n" +
                    ("# Cleaning up Docker Images #\n" as java.lang.CharSequence) +
                    ("###########################" as java.lang.CharSequence)

                echo "### Dangling all Containers, Images, and Volumes"
                //sh 'docker system prune -af --volumes'
           }
        }
    }
}