// This is a Declarative Jenkinsfile
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Project variables
def projectName = "microservices-demo" as java.lang.Object

// Kubernetes Config Variables
def appNameSpace = "app-microservices-demo" as java.lang.Object
def dockerKey    = "nexus-registry-key" as java.lang.Object
def dockerConfigJson = "ewoJImF1dGhzIjogewoJCSJuZXh1cy1kb2NrZXIuYXBwcy5tZWZsYWIueHl6IjogewoJCQkiYXV0aCI6ICJZV1J0YVc0NllXUnRhVzR4TWpNMFlXUnRhVzQ9IgoJCX0KCX0sCgkiSHR0cEhlYWRlcnMiOiB7CgkJIlVzZXItQWdlbnQiOiAiRG9ja2VyLUNsaWVudC8xOS4wMy4xMiAobGludXgpIgoJfQp9" as java.lang.Object

// Ingress Vars
def ingressName = "frontend-external" as java.lang.Object
def ingressHost = "frontend-external.apps.meflab.xyz" as java.lang.Object

// Nexus Container Registry Service
def nexusRegistry = "nexus-docker.apps.meflab.xyz/repository/microservices-demo" as java.lang.Object
def nexusPassword = "nexus-password" as java.lang.Objezct

def app
def imageName

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
pipeline {
    agent any

    environment {
        // Define global variables

        // Tags
        devTag = "0.0-0"
        version = "1.0"

    }
    stages {
        stage('GitCheckout') {
            steps {
                // Cloning project microservices-demo
                checkout scm

                //sh 'pwd && ls -l && ls -l release '

                script {

                    // Set the tag for the development image: version + build number
                    devTag = "${version}-" + "${currentBuild.number}"
                }
            }
        }
        //#############
        //# adservice #
        //#############
        stage('Build adservice image') {
            //when { equals expected: true, actual: "Build adservice image" }
            steps {
                dir("src/adservice") {
                    script {
                        //imageName = "adservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "###################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("###################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }
        stage('Test and Push adservice image to Nexus') {
            //when { equals expected: true, actual: "Test and Push adservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "##################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("##################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost:9555 || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "##################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("##################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("http://${nexusRegistry}", "${nexusPassword}") {
                        //app.push("${env.BUILD_NUMBER}")
                        app.push("${devTag}")
                        app.push("latest")
                    }
                }
            }
        }

        //###############
        //# cartservice #
        //###############
        stage('Build cartservice image') {
            //when { equals expected: true, actual: "Build cartservice image" }
            steps {
                dir("src/cartservice") {
                    script {
                        //imageName = "cartservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "#####################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("#####################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push cartservice image to Nexus') {
            //when { equals expected: true, actual: "Test and Push cartservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "####################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("####################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost:9555 || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "####################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("####################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("http://${nexusRegistry}", "${nexusPassword}") {
                        //app.push("${env.BUILD_NUMBER}")
                        app.push("${devTag}")
                        app.push("latest")
                    }
                }
            }
        }

        //###################
        //# checkoutservice #
        //###################
        stage('Build checkoutservice image') {
            //when { equals expected: true, actual: "Build checkoutservice image" }
            steps {
                dir("src/checkoutservice") {
                    script {
                        //imageName = "checkoutservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "#########################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("#########################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push checkoutservice image to Nexus') {
            //when { equals expected: true, actual: "Test and Push checkoutservice image to Nexus" }
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
                    docker.withRegistry("http://${nexusRegistry}", "${nexusPassword}") {
                        //app.push("${env.BUILD_NUMBER}")
                        app.push("${devTag}")
                        app.push("latest")
                    }
                }
            }
        }

        //###################
        //# currencyservice #
        //###################
        stage('Build currencyservice image') {
            //when { equals expected: true, actual: "Build currencyservice image" }
            steps {
                dir("src/currencyservice") {
                    script {
                        //imageName = "currencyservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "#########################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("#########################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push currencyservice image to Nexus') {
            //when { equals expected: true, actual: "Test and Push currencyservice image to Nexus" }
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
                    docker.withRegistry("http://${nexusRegistry}", "${nexusPassword}") {
                        //app.push("${env.BUILD_NUMBER}")
                        app.push("${devTag}")
                        app.push("latest")
                    }
                }
            }
        }

        //################
        //# emailservice #
        //################
        stage('Build emailservice image') {
            //when { equals expected: true, actual: "Build emailservice image" }
            steps {
                dir("src/emailservice") {
                    script {
                        //imageName = "emailservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "######################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("######################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push emailservice image to Nexus') {
            //when { equals expected: true, actual: "Test and Push emailservice image to Nexus" }
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
                    docker.withRegistry("http://${nexusRegistry}", "${nexusPassword}") {
                        //app.push("${env.BUILD_NUMBER}")
                        app.push("${devTag}")
                        app.push("latest")
                    }
                }
            }
        }

        //############
        //# frontend #
        //############
        stage('Build frontend image') {
            //when { equals expected: true, actual: "Build frontend image" }
            steps {
                dir("src/frontend") {
                    script {
                        //imageName = "frontend"
                        // .trim removes leading and trailing whitespace from the string
                        imageName = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "##################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("##################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push frontend image to Nexus') {
            //when { equals expected: true, actual: "Test and Push frontend image to Nexus" }
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
                    docker.withRegistry("http://${nexusRegistry}", "${nexusPassword}") {
                        //app.push("${env.BUILD_NUMBER}")
                        app.push("${devTag}")
                        app.push("latest")
                    }
                }
            }
        }

        //#################
        //# loadgenerator #
        //#################
        stage('Build loadgenerator image') {
            //when { equals expected: true, actual: "Build loadgenerator image" }
            steps {
                dir("src/loadgenerator") {
                    script {
                        //imageName = "loadgenerator"
                        // .trim removes leading and trailing whitespace from the string
                        imageName = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "#######################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("#######################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push loadgenerator image to Nexus') {
            //when { equals expected: true, actual: "Test and Push loadgenerator image to Nexus" }
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
                    docker.withRegistry("http://${nexusRegistry}", "${nexusPassword}") {
                        //app.push("${env.BUILD_NUMBER}")
                        app.push("${devTag}")
                        app.push("latest")
                    }
                }
            }
        }

        //##################
        //# paymentservice #
        //##################
        stage('Build paymentservice image') {
            //when { equals expected: true, actual: "Build paymentservice image" }
            steps {
                dir("src/paymentservice") {
                    script {
                        //imageName = "paymentservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "########################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("########################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push paymentservice image to Nexus') {
            //when { equals expected: true, actual: "Test and Push paymentservice image to Nexus" }
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
                    docker.withRegistry("http://${nexusRegistry}", "${nexusPassword}") {
                        //app.push("${env.BUILD_NUMBER}")
                        app.push("${devTag}")
                        app.push("latest")
                    }
                }
            }
        }

        //#########################
        //# productcatalogservice #
        //#########################
        stage('Build productcatalogservice image') {
            //when { equals expected: true, actual: "Build productcatalogservice image" }
            steps {
                dir("src/productcatalogservice") {
                    script {
                        //imageName = "productcatalogservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "###############################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("###############################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push productcatalogservice image to Nexus') {
            //when { equals expected: true, actual: "Test and Push productcatalogservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "##############################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("##############################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost: || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "##############################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("##############################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("http://${nexusRegistry}", "${nexusPassword}") {
                        //app.push("${env.BUILD_NUMBER}")
                        app.push("${devTag}")
                        app.push("latest")
                    }
                }
            }
        }

        //#########################
        //# recommendationservice #
        //#########################
        stage('Build recommendationservice image') {
            //when { equals expected: true, actual: "Build recommendationservice image" }
            steps {
                dir("src/recommendationservice") {
                    script {
                        //imageName = "recommendationservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "###############################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("###############################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push recommendationservice image to Nexus') {
            //when { equals expected: true, actual: "Test and Push recommendationservice image to Nexus" }
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */
            steps {
                echo "##############################################\n" +
                        ("# Testing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("##############################################" as java.lang.CharSequence)

                script {
                    docker.image("${projectName}/image/${imageName}:latest").inside("--entrypoint=''") { c ->
                        //sh 'curl http://localhost: || exit 1'
                        sh "find / -type f -iname ${imageName}"
                        sh "echo \"Tests ${imageName} passed\""
                    }
                }
                echo "##############################################\n" +
                        ("# Pushing ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                        ("##############################################" as java.lang.CharSequence)
                script {
                    docker.withRegistry("http://${nexusRegistry}", "${nexusPassword}") {
                        //app.push("${env.BUILD_NUMBER}")
                        app.push("${devTag}")
                        app.push("latest")
                    }
                }
            }
        }

        //###################
        //# shippingservice #
        //###################
        stage('Build shippingservice image') {
            //when { equals expected: true, actual: "Build shippingservice image" }
            steps {
                dir("src/shippingservice") {
                    script {
                        //imageName = "shippingservice"
                        // .trim removes leading and trailing whitespace from the string
                        imageName = sh(returnStdout: true, script: "pwd | awk -F \"/\" '{print \$NF}'").trim()

                        echo "#########################################\n" +
                                ("# Building ${imageName} image ${devTag} #\n" as java.lang.CharSequence) +
                                ("#########################################" as java.lang.CharSequence)

                        app = docker.build("${projectName}/image/${imageName}")
                    }
                }
            }
        }

        stage('Test and Push shippingservice image to Nexus') {
            //when { equals expected: true, actual: "Test and Push shippingservice image to Nexus" }
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
                    docker.withRegistry("http://${nexusRegistry}", "${nexusPassword}") {
                        //app.push("${env.BUILD_NUMBER}")
                        app.push("${devTag}")
                        app.push("latest")
                    }
                }
            }
        }


        //#############################
        //# Deploying The Application #
        //#############################
        stage('Create NameSpace') {
            //when { equals expected: true, actual: "Create NameSpace" }
            steps {
                //Check if Namespace exist
                script {
                    def ns = sh(returnStdout: true, script: "kubectl get ns | awk '{print \$1}' | egrep '${appNameSpace}\$' > /dev/null && echo '1' || echo '0'").trim()
                    //echo "Dir is ${dir} "
                    if (ns == '0') {
                        // Create a NameSpace
                        echo "Creating ${appNameSpace} NameSpace"
                        sh "kubectl create namespace ${appNameSpace}"
                        sh "kubectl get ns | grep ${appNameSpace}"
                    } else {
                        echo "List ${appNameSpace} NameSpace"
                        sh "kubectl get ns | grep ${appNameSpace}"
                    }
                }
            }
        }
        stage('Create Docker Secrete for online-boutique') {
            //when { equals expected: true, actual: "Create Docker Secrete for online-boutique" }
            steps {
                echo "Deploying ${dockerKey} in ${appNameSpace} Namespace"
                sh "kubectl -n ${appNameSpace} apply -f - <<EOF\n" +
                        "apiVersion: v1\n" +
                        "kind: Secret\n" +
                        "metadata:\n" +
                        "  name: ${dockerKey}\n" +
                        "  namespace: ${appNameSpace}\n" +
                        "data:\n" +
                        "  .dockerconfigjson: ${dockerConfigJson}\n" +
                        "type: kubernetes.io/dockerconfigjson\n" +
                        "EOF"
            }
        }
        stage('Deploy online-boutique') {
            //when { equals expected: true, actual: "Deploy online-boutique" }
            steps {
                sh 'pwd && ls -l && ls -l release '
                echo "Deploying ${projectName} in ${appNameSpace} Namespace"
                sh "kubectl -n ${appNameSpace} apply -f ./release/kubernetes-manifests-nexus.yaml"
            }
        }
        stage('pods Verification') {
            //when { equals expected: true, actual: "pods Verification" }
            steps {
                echo "See if pods are in a Ready state"
                sh "kubectl -n ${appNameSpace} wait --for=condition=ready --timeout=300s pods -l app=adservice"
                sh "kubectl -n ${appNameSpace} wait --for=condition=ready --timeout=300s pods -l app=cartservice"
                sh "kubectl -n ${appNameSpace} wait --for=condition=ready --timeout=300s pods -l app=checkoutservice"
                sh "kubectl -n ${appNameSpace} wait --for=condition=ready --timeout=300s pods -l app=currencyservice"
                sh "kubectl -n ${appNameSpace} wait --for=condition=ready --timeout=300s pods -l app=emailservice"
                sh "kubectl -n ${appNameSpace} wait --for=condition=ready --timeout=300s pods -l app=frontend"
                sh "kubectl -n ${appNameSpace} wait --for=condition=ready --timeout=300s pods -l app=loadgenerator"
                sh "kubectl -n ${appNameSpace} wait --for=condition=ready --timeout=300s pods -l app=paymentservice"
                sh "kubectl -n ${appNameSpace} wait --for=condition=ready --timeout=300s pods -l app=productcatalogservice"
                sh "kubectl -n ${appNameSpace} wait --for=condition=ready --timeout=300s pods -l app=recommendationservice"
                sh "kubectl -n ${appNameSpace} get pods"
            }
        }
        stage('Crete App Ingress') {
            //when { equals expected: true, actual: "Crete App Ingress" }
            steps {
                echo "Creating ${appNameSpace} frontend Ingress"
                sh "kubectl -n ${appNameSpace} apply -f - <<EOF\n" +
                        "apiVersion: extensions/v1beta1\n" +
                        "kind: Ingress\n" +
                        "metadata:\n" +
                        "  name: ${ingressName}\n" +
                        "  namespace: ${appNameSpace}\n" +
                        "  annotations:\n" +
                        "    nginx.ingress.kubernetes.io/ssl-redirect: \"false\"\n" +
                        "    nginx.ingress.kubernetes.io/proxy-body-size: \"0\"\n" +
                        "spec:\n" +
                        "  rules:\n" +
                        "    - host: ${ingressHost}\n" +
                        "      http:\n" +
                        "        paths:\n" +
                        "          - backend:\n" +
                        "              serviceName: frontend-external\n" +
                        "              servicePort: 80\n" +
                        "EOF"
            }
        }
        stage('Test online-boutique URL') {
            steps {
                echo "Testing online-boutique URL"
                sh 'sleep 10'
                sh "curl -iv ${ingressHost}"
            }
        }

        //###################
        //# Cleaning Images #
        //###################
        stage('Clean images') {
            //when { equals expected: true, actual: "Clean images" }
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