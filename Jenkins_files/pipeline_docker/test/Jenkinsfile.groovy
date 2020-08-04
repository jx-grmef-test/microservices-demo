// Project variables

def projectName = "microservices-demo"

// Kubernetes Config Variables
def appNameSpace = "app-microservices-demo"

// Infress Vars
def ingressName = "frontend-external"
def ingressHost = "frontend-external.apps.meflab.xyz"

// Nexus Container Registry Service
def nexusRegistry = "http://nexus-docker.apps.meflab.xyz/repository/microservices-demo"
def nexusPassword = "nexus-password"

def app
def imageName
def dockerIimages


// Project variables



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
        //#############
        //# adservice #
        //#############
        stage('Build adservice image') {
            when { equals expected: true, actual: "Build adservice image" }
            steps {
                dir("src/adservice") {
                    script {
                        //imageName = "adservice"
                        // .trim removes leading and trailing whitespace from the string
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
            when { equals expected: true, actual: "Test and Push adservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "#################################\n" +
                    ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                    ("#################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost:9555 || exit 1'
                        sh "find / -type f -iname ${imageName}"
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

        //###############
        //# cartservice #
        //###############
        stage('Build cartservice image') {
            when { equals expected: true, actual: "Build cartservice image" }
            steps {
                dir("src/cartservice") {
                    script {
                        //imageName = "cartservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName   = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "####################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("####################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push cartservice image to Nexus') {
            when { equals expected: true, actual: "Test and Push cartservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "###################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("###################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost:9555 || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "###################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("###################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("${nexusRegistry}", "${nexusPassword}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        //###################
        //# checkoutservice #
        //###################
        stage('Build checkoutservice image') {
            when { equals expected: true, actual: "Build checkoutservice image" }
            steps {
                dir("src/checkoutservice") {
                    script {
                        //imageName = "checkoutservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName   = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "########################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("########################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push checkoutservice image to Nexus') {
            when { equals expected: true, actual: "Test and Push checkoutservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "########################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("########################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost:9555 || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "########################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("########################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("${nexusRegistry}", "${nexusPassword}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        //###################
        //# currencyservice #
        //###################
        stage('Build currencyservice image') {
            when { equals expected: true, actual: "Build currencyservice image" }
            steps {
                dir("src/currencyservice") {
                    script {
                        //imageName = "currencyservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName   = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "########################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("########################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push currencyservice image to Nexus') {
            when { equals expected: true, actual: "Test and Push currencyservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "########################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("########################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost: || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "########################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("########################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("${nexusRegistry}", "${nexusPassword}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        //################
        //# emailservice #
        //################
        stage('Build emailservice image') {
            when { equals expected: true, actual: "Build emailservice image" }
            steps {
                dir("src/emailservice") {
                    script {
                        //imageName = "emailservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName   = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "#####################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("#####################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push emailservice image to Nexus') {
            when { equals expected: true, actual: "Test and Push emailservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "#####################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("#####################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost: || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "#####################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("#####################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("${nexusRegistry}", "${nexusPassword}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        //############
        //# frontend #
        //############
        stage('Build frontend image') {
            when { equals expected: true, actual: "Build frontend image" }
            steps {
                dir("src/frontend") {
                    script {
                        //imageName = "frontend"
                        // .trim removes leading and trailing whitespace from the string
                        imageName   = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "#################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("#################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push frontend image to Nexus') {
            when { equals expected: true, actual: "Test and Push frontend image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "#################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("#################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost: || exit 1'
                        sh "find / -type f -iname ${imageName}"
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
        //# loadgenerator #
        //#################
        stage('Build loadgenerator image') {
            when { equals expected: true, actual: "Build loadgenerator image" }
            steps {
                dir("src/loadgenerator") {
                    script {
                        //imageName = "loadgenerator"
                        // .trim removes leading and trailing whitespace from the string
                        imageName   = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "######################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("######################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push loadgenerator image to Nexus') {
            when { equals expected: true, actual: "Test and Push loadgenerator image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "######################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("######################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost: || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "######################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("######################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("${nexusRegistry}", "${nexusPassword}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        //##################
        //# paymentservice #
        //##################
        stage('Build paymentservice image') {
            when { equals expected: true, actual: "Build paymentservice image" }
            steps {
                dir("src/paymentservice") {
                    script {
                        //imageName = "paymentservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName   = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "#######################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("#######################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push paymentservice image to Nexus') {
            when { equals expected: true, actual: "Test and Push paymentservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "######################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("######################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost: || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "######################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("######################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("${nexusRegistry}", "${nexusPassword}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        //#########################
        //# productcatalogservice #
        //#########################
        stage('Build productcatalogservice image') {
            when { equals expected: true, actual: "Build productcatalogservice image" }
            steps {
                dir("src/productcatalogservice") {
                    script {
                        //imageName = "productcatalogservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName   = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "##############################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("##############################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push productcatalogservice image to Nexus') {
            when { equals expected: true, actual: "Test and Push productcatalogservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "#############################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("#############################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost: || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "#############################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("#############################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("${nexusRegistry}", "${nexusPassword}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        //#########################
        //# recommendationservice #
        //#########################
        stage('Build recommendationservice image') {
            when { equals expected: true, actual: "Build recommendationservice image" }
            steps {
                dir("src/recommendationservice") {
                    script {
                        //imageName = "recommendationservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName   = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "##############################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("##############################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push recommendationservice image to Nexus') {
            when { equals expected: true, actual: "Test and Push recommendationservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "#############################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("#############################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost: || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "#############################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("#############################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("${nexusRegistry}", "${nexusPassword}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        //###################
        //# shippingservice #
        //###################
        stage('Build shippingservice image') {
            when { equals expected: true, actual: "Build shippingservice image" }
            steps {
                dir("src/shippingservice") {
                    script {
                        //imageName = "shippingservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName   = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "########################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("########################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push shippingservice image to Nexus') {
            when { equals expected: true, actual: "Test and Push shippingservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "#######################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("#######################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost: || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "#######################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("#######################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("${nexusRegistry}", "${nexusPassword}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }
        stage('Clean images') {
            when { equals expected: true, actual: "Clean images" }
            steps {
                echo "###########################\n" +
                    ("# Cleaning up Docker Images #\n" as java.lang.CharSequence) +
                    ("###########################" as java.lang.CharSequence)

                echo "### Dangling all Containers, Images, and Volumes"
                sh 'docker system prune -af --volumes'
           }
        }

    }
}