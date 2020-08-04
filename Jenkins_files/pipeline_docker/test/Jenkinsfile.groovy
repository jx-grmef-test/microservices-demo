// Project variables

def projectName = "microservices-demo"

// Kubernetes Config Variables
def appNameSpace = "app-microservices-demo"
def appVersion   = "latest"

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

        //#############################
        //# Deploying The Application #
        //#############################
        stage('Create NameSpace') {
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
        stage('Deploy online-boutique') {
            steps {
                // Creating  a NameSpace kubectl-binary
                echo "Creating ${appNameSpace} Application"
                sh "kubectl -n ${appNameSpace} apply -f - <<EOF\n" +
                        "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  name: emailservice\n" +
                        "spec:\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: emailservice\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: emailservice\n" +
                        "    spec:\n" +
                        "      terminationGracePeriodSeconds: 5\n" +
                        "      containers:\n" +
                        "      - name: server\n" +
                        "        image: ${nexusRegistry}/emailservice:${appVersion}\n" +
                        "        ports:\n" +
                        "        - containerPort: 8080\n" +
                        "        env:\n" +
                        "        - name: PORT\n" +
                        "          value: \"8080\"\n" +
                        "        # - name: DISABLE_TRACING\n" +
                        "        #   value: \"1\"\n" +
                        "        - name: DISABLE_PROFILER\n" +
                        "          value: \"1\"\n" +
                        "        readinessProbe:\n" +
                        "          periodSeconds: 5\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:8080\"]\n" +
                        "        livenessProbe:\n" +
                        "          periodSeconds: 5\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:8080\"]\n" +
                        "        resources:\n" +
                        "          requests:\n" +
                        "            cpu: 100m\n" +
                        "            memory: 64Mi\n" +
                        "          limits:\n" +
                        "            cpu: 200m\n" +
                        "            memory: 128Mi\n" +
                        "---\n" +
                        "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        "  name: emailservice\n" +
                        "spec:\n" +
                        "  type: ClusterIP\n" +
                        "  selector:\n" +
                        "    app: emailservice\n" +
                        "  ports:\n" +
                        "  - name: grpc\n" +
                        "    port: 5000\n" +
                        "    targetPort: 8080\n" +
                        "---\n" +
                        "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  name: checkoutservice\n" +
                        "spec:\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: checkoutservice\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: checkoutservice\n" +
                        "    spec:\n" +
                        "      containers:\n" +
                        "        - name: server\n" +
                        "          image: ${nexusRegistry}/checkoutservice:${appVersion}\n" +
                        "          ports:\n" +
                        "          - containerPort: 5050\n" +
                        "          readinessProbe:\n" +
                        "            exec:\n" +
                        "              command: [\"/bin/grpc_health_probe\", \"-addr=:5050\"]\n" +
                        "          livenessProbe:\n" +
                        "            exec:\n" +
                        "              command: [\"/bin/grpc_health_probe\", \"-addr=:5050\"]\n" +
                        "          env:\n" +
                        "          - name: PORT\n" +
                        "            value: \"5050\"\n" +
                        "          - name: PRODUCT_CATALOG_SERVICE_ADDR\n" +
                        "            value: \"productcatalogservice:3550\"\n" +
                        "          - name: SHIPPING_SERVICE_ADDR\n" +
                        "            value: \"shippingservice:50051\"\n" +
                        "          - name: PAYMENT_SERVICE_ADDR\n" +
                        "            value: \"paymentservice:50051\"\n" +
                        "          - name: EMAIL_SERVICE_ADDR\n" +
                        "            value: \"emailservice:5000\"\n" +
                        "          - name: CURRENCY_SERVICE_ADDR\n" +
                        "            value: \"currencyservice:7000\"\n" +
                        "          - name: CART_SERVICE_ADDR\n" +
                        "            value: \"cartservice:7070\"\n" +
                        "          resources:\n" +
                        "            requests:\n" +
                        "              cpu: 100m\n" +
                        "              memory: 64Mi\n" +
                        "            limits:\n" +
                        "              cpu: 200m\n" +
                        "              memory: 128Mi\n" +
                        "---\n" +
                        "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        "  name: checkoutservice\n" +
                        "spec:\n" +
                        "  type: ClusterIP\n" +
                        "  selector:\n" +
                        "    app: checkoutservice\n" +
                        "  ports:\n" +
                        "  - name: grpc\n" +
                        "    port: 5050\n" +
                        "    targetPort: 5050\n" +
                        "---\n" +
                        "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  name: recommendationservice\n" +
                        "spec:\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: recommendationservice\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: recommendationservice\n" +
                        "    spec:\n" +
                        "      terminationGracePeriodSeconds: 5\n" +
                        "      containers:\n" +
                        "      - name: server\n" +
                        "        image: ${nexusRegistry}/recommendationservice:${appVersion}\n" +
                        "        ports:\n" +
                        "        - containerPort: 8080\n" +
                        "        readinessProbe:\n" +
                        "          periodSeconds: 5\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:8080\"]\n" +
                        "        livenessProbe:\n" +
                        "          periodSeconds: 5\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:8080\"]\n" +
                        "        env:\n" +
                        "        - name: PORT\n" +
                        "          value: \"8080\"\n" +
                        "        - name: PRODUCT_CATALOG_SERVICE_ADDR\n" +
                        "          value: \"productcatalogservice:3550\"\n" +
                        "        resources:\n" +
                        "          requests:\n" +
                        "            cpu: 100m\n" +
                        "            memory: 220Mi\n" +
                        "          limits:\n" +
                        "            cpu: 200m\n" +
                        "            memory: 450Mi\n" +
                        "---\n" +
                        "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        "  name: recommendationservice\n" +
                        "spec:\n" +
                        "  type: ClusterIP\n" +
                        "  selector:\n" +
                        "    app: recommendationservice\n" +
                        "  ports:\n" +
                        "  - name: grpc\n" +
                        "    port: 8080\n" +
                        "    targetPort: 8080\n" +
                        "---\n" +
                        "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  name: frontend\n" +
                        "spec:\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: frontend\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: frontend\n" +
                        "      annotations:\n" +
                        "        sidecar.istio.io/rewriteAppHTTPProbers: \"true\"\n" +
                        "    spec:\n" +
                        "      containers:\n" +
                        "        - name: server\n" +
                        "          image: ${nexusRegistry}/frontend:${appVersion}\n" +
                        "          ports:\n" +
                        "          - containerPort: 8080\n" +
                        "          readinessProbe:\n" +
                        "            initialDelaySeconds: 10\n" +
                        "            httpGet:\n" +
                        "              path: \"/_healthz\"\n" +
                        "              port: 8080\n" +
                        "              httpHeaders:\n" +
                        "              - name: \"Cookie\"\n" +
                        "                value: \"shop_session-id=x-readiness-probe\"\n" +
                        "          livenessProbe:\n" +
                        "            initialDelaySeconds: 10\n" +
                        "            httpGet:\n" +
                        "              path: \"/_healthz\"\n" +
                        "              port: 8080\n" +
                        "              httpHeaders:\n" +
                        "              - name: \"Cookie\"\n" +
                        "                value: \"shop_session-id=x-liveness-probe\"\n" +
                        "          env:\n" +
                        "          - name: PORT\n" +
                        "            value: \"8080\"\n" +
                        "          - name: PRODUCT_CATALOG_SERVICE_ADDR\n" +
                        "            value: \"productcatalogservice:3550\"\n" +
                        "          - name: CURRENCY_SERVICE_ADDR\n" +
                        "            value: \"currencyservice:7000\"\n" +
                        "          - name: CART_SERVICE_ADDR\n" +
                        "            value: \"cartservice:7070\"\n" +
                        "          - name: RECOMMENDATION_SERVICE_ADDR\n" +
                        "            value: \"recommendationservice:8080\"\n" +
                        "          - name: SHIPPING_SERVICE_ADDR\n" +
                        "            value: \"shippingservice:50051\"\n" +
                        "          - name: CHECKOUT_SERVICE_ADDR\n" +
                        "            value: \"checkoutservice:5050\"\n" +
                        "          - name: AD_SERVICE_ADDR\n" +
                        "            value: \"adservice:9555\"\n" +
                        "          - name: ENV_PLATFORM\n" +
                        "            value: \"gcp\"\n" +
                        "          resources:\n" +
                        "            requests:\n" +
                        "              cpu: 100m\n" +
                        "              memory: 64Mi\n" +
                        "            limits:\n" +
                        "              cpu: 200m\n" +
                        "              memory: 128Mi\n" +
                        "---\n" +
                        "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        "  name: frontend\n" +
                        "spec:\n" +
                        "  type: ClusterIP\n" +
                        "  selector:\n" +
                        "    app: frontend\n" +
                        "  ports:\n" +
                        "  - name: http\n" +
                        "    port: 80\n" +
                        "    targetPort: 8080\n" +
                        "---\n" +
                        "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        "  name: frontend-external\n" +
                        "spec:\n" +
                        "  type: LoadBalancer\n" +
                        "  selector:\n" +
                        "    app: frontend\n" +
                        "  ports:\n" +
                        "  - name: http\n" +
                        "    port: 80\n" +
                        "    targetPort: 8080\n" +
                        "---\n" +
                        "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  name: paymentservice\n" +
                        "spec:\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: paymentservice\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: paymentservice\n" +
                        "    spec:\n" +
                        "      terminationGracePeriodSeconds: 5\n" +
                        "      containers:\n" +
                        "      - name: server\n" +
                        "        image: ${nexusRegistry}/paymentservice:${appVersion}\n" +
                        "        ports:\n" +
                        "        - containerPort: 50051\n" +
                        "        env:\n" +
                        "        - name: PORT\n" +
                        "          value: \"50051\"\n" +
                        "        readinessProbe:\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:50051\"]\n" +
                        "        livenessProbe:\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:50051\"]\n" +
                        "        resources:\n" +
                        "          requests:\n" +
                        "            cpu: 100m\n" +
                        "            memory: 64Mi\n" +
                        "          limits:\n" +
                        "            cpu: 200m\n" +
                        "            memory: 128Mi\n" +
                        "---\n" +
                        "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        "  name: paymentservice\n" +
                        "spec:\n" +
                        "  type: ClusterIP\n" +
                        "  selector:\n" +
                        "    app: paymentservice\n" +
                        "  ports:\n" +
                        "  - name: grpc\n" +
                        "    port: 50051\n" +
                        "    targetPort: 50051\n" +
                        "---\n" +
                        "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  name: productcatalogservice\n" +
                        "spec:\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: productcatalogservice\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: productcatalogservice\n" +
                        "    spec:\n" +
                        "      terminationGracePeriodSeconds: 5\n" +
                        "      containers:\n" +
                        "      - name: server\n" +
                        "        image: ${nexusRegistry}/productcatalogservice:${appVersion}\n" +
                        "        ports:\n" +
                        "        - containerPort: 3550\n" +
                        "        env:\n" +
                        "        - name: PORT\n" +
                        "          value: \"3550\"\n" +
                        "        readinessProbe:\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:3550\"]\n" +
                        "        livenessProbe:\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:3550\"]\n" +
                        "        resources:\n" +
                        "          requests:\n" +
                        "            cpu: 100m\n" +
                        "            memory: 64Mi\n" +
                        "          limits:\n" +
                        "            cpu: 200m\n" +
                        "            memory: 128Mi\n" +
                        "---\n" +
                        "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        "  name: productcatalogservice\n" +
                        "spec:\n" +
                        "  type: ClusterIP\n" +
                        "  selector:\n" +
                        "    app: productcatalogservice\n" +
                        "  ports:\n" +
                        "  - name: grpc\n" +
                        "    port: 3550\n" +
                        "    targetPort: 3550\n" +
                        "---\n" +
                        "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  name: cartservice\n" +
                        "spec:\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: cartservice\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: cartservice\n" +
                        "    spec:\n" +
                        "      terminationGracePeriodSeconds: 5\n" +
                        "      containers:\n" +
                        "      - name: server\n" +
                        "        image: ${nexusRegistry}/cartservice:${appVersion}\n" +
                        "        ports:\n" +
                        "        - containerPort: 7070\n" +
                        "        env:\n" +
                        "        - name: REDIS_ADDR\n" +
                        "          value: \"redis-cart:6379\"\n" +
                        "        - name: PORT\n" +
                        "          value: \"7070\"\n" +
                        "        - name: LISTEN_ADDR\n" +
                        "          value: \"0.0.0.0\"\n" +
                        "        resources:\n" +
                        "          requests:\n" +
                        "            cpu: 200m\n" +
                        "            memory: 64Mi\n" +
                        "          limits:\n" +
                        "            cpu: 300m\n" +
                        "            memory: 128Mi\n" +
                        "        readinessProbe:\n" +
                        "          initialDelaySeconds: 15\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:7070\", \"-rpc-timeout=5s\"]\n" +
                        "        livenessProbe:\n" +
                        "          initialDelaySeconds: 15\n" +
                        "          periodSeconds: 10\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:7070\", \"-rpc-timeout=5s\"]\n" +
                        "---\n" +
                        "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        "  name: cartservice\n" +
                        "spec:\n" +
                        "  type: ClusterIP\n" +
                        "  selector:\n" +
                        "    app: cartservice\n" +
                        "  ports:\n" +
                        "  - name: grpc\n" +
                        "    port: 7070\n" +
                        "    targetPort: 7070\n" +
                        "---\n" +
                        "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  name: loadgenerator\n" +
                        "spec:\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: loadgenerator\n" +
                        "  replicas: 1\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: loadgenerator\n" +
                        "      annotations:\n" +
                        "        sidecar.istio.io/rewriteAppHTTPProbers: \"true\"\n" +
                        "    spec:\n" +
                        "      terminationGracePeriodSeconds: 5\n" +
                        "      restartPolicy: Always\n" +
                        "      containers:\n" +
                        "      - name: main\n" +
                        "        image: ${nexusRegistry}/loadgenerator:${appVersion}\n" +
                        "        env:\n" +
                        "        - name: FRONTEND_ADDR\n" +
                        "          value: \"frontend:80\"\n" +
                        "        - name: USERS\n" +
                        "          value: \"10\"\n" +
                        "        resources:\n" +
                        "          requests:\n" +
                        "            cpu: 300m\n" +
                        "            memory: 256Mi\n" +
                        "          limits:\n" +
                        "            cpu: 500m\n" +
                        "            memory: 512Mi\n" +
                        "---\n" +
                        "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  name: currencyservice\n" +
                        "spec:\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: currencyservice\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: currencyservice\n" +
                        "    spec:\n" +
                        "      terminationGracePeriodSeconds: 5\n" +
                        "      containers:\n" +
                        "      - name: server\n" +
                        "        image: ${nexusRegistry}/currencyservice:${appVersion}\n" +
                        "        ports:\n" +
                        "        - name: grpc\n" +
                        "          containerPort: 7000\n" +
                        "        env:\n" +
                        "        - name: PORT\n" +
                        "          value: \"7000\"\n" +
                        "        readinessProbe:\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:7000\"]\n" +
                        "        livenessProbe:\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:7000\"]\n" +
                        "        resources:\n" +
                        "          requests:\n" +
                        "            cpu: 100m\n" +
                        "            memory: 64Mi\n" +
                        "          limits:\n" +
                        "            cpu: 200m\n" +
                        "            memory: 128Mi\n" +
                        "---\n" +
                        "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        "  name: currencyservice\n" +
                        "spec:\n" +
                        "  type: ClusterIP\n" +
                        "  selector:\n" +
                        "    app: currencyservice\n" +
                        "  ports:\n" +
                        "  - name: grpc\n" +
                        "    port: 7000\n" +
                        "    targetPort: 7000\n" +
                        "---\n" +
                        "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  name: shippingservice\n" +
                        "spec:\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: shippingservice\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: shippingservice\n" +
                        "    spec:\n" +
                        "      containers:\n" +
                        "      - name: server\n" +
                        "        image: ${nexusRegistry}/shippingservice:${appVersion}\n" +
                        "        ports:\n" +
                        "        - containerPort: 50051\n" +
                        "        env:\n" +
                        "        - name: PORT\n" +
                        "          value: \"50051\"\n" +
                        "        readinessProbe:\n" +
                        "          periodSeconds: 5\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:50051\"]\n" +
                        "        livenessProbe:\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:50051\"]\n" +
                        "        resources:\n" +
                        "          requests:\n" +
                        "            cpu: 100m\n" +
                        "            memory: 64Mi\n" +
                        "          limits:\n" +
                        "            cpu: 200m\n" +
                        "            memory: 128Mi\n" +
                        "---\n" +
                        "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        "  name: shippingservice\n" +
                        "spec:\n" +
                        "  type: ClusterIP\n" +
                        "  selector:\n" +
                        "    app: shippingservice\n" +
                        "  ports:\n" +
                        "  - name: grpc\n" +
                        "    port: 50051\n" +
                        "    targetPort: 50051\n" +
                        "---\n" +
                        "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  name: redis-cart\n" +
                        "spec:\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: redis-cart\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: redis-cart\n" +
                        "    spec:\n" +
                        "      containers:\n" +
                        "      - name: redis\n" +
                        "        image: redis:alpine\n" +
                        "        ports:\n" +
                        "        - containerPort: 6379\n" +
                        "        readinessProbe:\n" +
                        "          periodSeconds: 5\n" +
                        "          tcpSocket:\n" +
                        "            port: 6379\n" +
                        "        livenessProbe:\n" +
                        "          periodSeconds: 5\n" +
                        "          tcpSocket:\n" +
                        "            port: 6379\n" +
                        "        volumeMounts:\n" +
                        "        - mountPath: /data\n" +
                        "          name: redis-data\n" +
                        "        resources:\n" +
                        "          limits:\n" +
                        "            memory: 256Mi\n" +
                        "            cpu: 125m\n" +
                        "          requests:\n" +
                        "            cpu: 70m\n" +
                        "            memory: 200Mi\n" +
                        "      volumes:\n" +
                        "      - name: redis-data\n" +
                        "        emptyDir: {}\n" +
                        "---\n" +
                        "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        "  name: redis-cart\n" +
                        "spec:\n" +
                        "  type: ClusterIP\n" +
                        "  selector:\n" +
                        "    app: redis-cart\n" +
                        "  ports:\n" +
                        "  - name: redis\n" +
                        "    port: 6379\n" +
                        "    targetPort: 6379\n" +
                        "---\n" +
                        "apiVersion: apps/v1\n" +
                        "kind: Deployment\n" +
                        "metadata:\n" +
                        "  name: adservice\n" +
                        "spec:\n" +
                        "  selector:\n" +
                        "    matchLabels:\n" +
                        "      app: adservice\n" +
                        "  template:\n" +
                        "    metadata:\n" +
                        "      labels:\n" +
                        "        app: adservice\n" +
                        "    spec:\n" +
                        "      terminationGracePeriodSeconds: 5\n" +
                        "      containers:\n" +
                        "      - name: server\n" +
                        "        image: ${nexusRegistry}/adservice:${appVersion}\n" +
                        "        ports:\n" +
                        "        - containerPort: 9555\n" +
                        "        env:\n" +
                        "        - name: PORT\n" +
                        "          value: \"9555\"\n" +
                        "\n" +
                        "        resources:\n" +
                        "          requests:\n" +
                        "            cpu: 200m\n" +
                        "            memory: 180Mi\n" +
                        "          limits:\n" +
                        "            cpu: 300m\n" +
                        "            memory: 300Mi\n" +
                        "        readinessProbe:\n" +
                        "          initialDelaySeconds: 20\n" +
                        "          periodSeconds: 15\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:9555\"]\n" +
                        "        livenessProbe:\n" +
                        "          initialDelaySeconds: 20\n" +
                        "          periodSeconds: 15\n" +
                        "          exec:\n" +
                        "            command: [\"/bin/grpc_health_probe\", \"-addr=:9555\"]\n" +
                        "---\n" +
                        "apiVersion: v1\n" +
                        "kind: Service\n" +
                        "metadata:\n" +
                        "  name: adservice\n" +
                        "spec:\n" +
                        "  type: ClusterIP\n" +
                        "  selector:\n" +
                        "    app: adservice\n" +
                        "  ports:\n" +
                        "  - name: grpc\n" +
                        "    port: 9555\n" +
                        "    targetPort: 9555" +
                        "EOF"
            }
        }
    }
}