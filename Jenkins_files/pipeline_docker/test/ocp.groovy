// This is a Declarative Jenkinsfile to be used in the homework
// It should map very well to what you learned in class.
// Implement the sections marked with "TBD:"
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Set your project Prefix using your GUID
//def GUID         = "f2d7"

// Set username - same as your OPENTLC username
def gitUser     =  "mfeliz-shadow-soft.com"

// Nexus service url nexusUrl
def nexusSvc    =  "homework-nexus.gpte-hw-cicd.svc.cluster.local:8081"

// Nexus public route https://homework-nexus.apps.shared.na.openshift.opentlc.com

// Nexus Container Registry Service
def nexusRegistry = "homework-nexus-registry.gpte-hw-cicd.svc.cluster.local:5000"

// Nexus Container Registry public route https://homework-nexus-registry.apps.shared.na.openshift.opentlc.com

// The repository group /repository/maven-all-public

// Nexus repositories access
def nexusUser     = "admin"
def nexusPassword = "redhat"

// SonarQube service
def sonarQubSvc   = "homework-sonarqube.gpte-hw-cicd.svc.cluster.local:9000"

// SonarQube public url
def sonarPubUrl   = "http://homework-sonarqube.apps.shared.na.openshift.opentlc.com"

// Image Registry
def imgRegCluster = "image-registry.openshift-image-registry.svc.cluster.local:5000"
def imgRegSvc     = "image-registry.openshift-image-registry.svc:5000"

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

pipeline {
    agent {
        kubernetes {
            label "maven-skopeo-agent"
            cloud "openshift"
            inheritFrom "maven"
            containerTemplate {
                name "jnlp"
                image "${imgRegSvc}/${GUID}-jenkins/jenkins-agent-appdev:latest"
                resourceRequestMemory "2Gi"
                resourceLimitMemory "2Gi"
                resourceRequestCpu "2"
                resourceLimitCpu "2"
            }
        }
    }
    environment {
        // Define global variables
        // Set Maven command to always include Nexus Settings
        // NOTE: Somehow an inline pod template in a declarative pipeline
        //       needs the "scl_enable" before calling maven.
        mvnCmd = "source /usr/local/bin/scl_enable && mvn -s ./nexus_settings.xml"

        // Images and Projects
        imageName   = "${GUID}-tasks"
        devProject  = "${GUID}-tasks-dev"
        prodProject = "${GUID}-tasks-prod"

        // Tags
        devTag      = "0.0-0"
        prodTag     = "0.0"

        // Blue-Green Settings
        destApp     = "tasks-green"
        activeApp   = ""

        //
    }
    stages {
        // Checkout Source Code.
        stage('Checkout Source') {
            steps {
                checkout scm

                dir('openshift-tasks') {
                    script {
                        def pom = readMavenPom file: 'pom.xml'
                        def version = pom.version

                        // Set the tag for the development image: version + build number
                        devTag  = "${version}-" + currentBuild.number
                        // Set the tag for the production image: version
                        prodTag = "${version}"

                        // Patch Source artifactId to include GUID
                        sh "sed -i 's/GUID/${GUID}/g' ./pom.xml"
                    }
                }
            }
        }

        // Build the Tasks Application in the directory 'openshift-tasks'
        stage('Build war') {
            steps {
                dir('openshift-tasks') {
                    echo "Building version ${devTag}"
                    sh "${mvnCmd} clean package -DskipTests=true"
                }
            }
        }

        // Using Maven run the unit tests
        stage('Unit Tests') {
            steps {
                dir('openshift-tasks') {
                    echo "Running Unit Tests"

                    // TBD: Execute Unit Tests
                    sh "${mvnCmd} test"
                    step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
                }
            }
        }
        // Using Maven call SonarQube for Code Analysis
        stage('Code Analysis') {
            steps {
                dir('openshift-tasks') {
                    script {
                        echo "Running Code Analysis"

                        // TBD: Execute Sonarqube Tests
                        //      Your project name should be "${GUID}-${JOB_BASE_NAME}-${devTag}"
                        //      Your project version should be ${devTag}
                        sh "${mvnCmd} sonar:sonar -Dsonar.host.url=${sonarPubUrl}/ -Dsonar.projectName=${GUID}-${JOB_BASE_NAME}-${devTag} -Dsonar.projectVersion=${devTag}"
                    }
                }
            }
        }
        // Publish the built war file to Nexus
        stage('Publish to Nexus') {
            steps {
                dir('openshift-tasks') {
                    echo "Publish to Nexus"

                    // TBD: Publish to Nexus
                    sh "${mvnCmd} deploy -DskipTests=true -DaltDeploymentRepository=nexus::default::http://${nexusSvc}/repository/releases"
                }
            }
        }
        // Build the OpenShift Image in OpenShift and tag it.
        stage('Build and Tag OpenShift Image') {
            steps {
                dir('openshift-tasks') {
                    echo "Building OpenShift container image ${imageName}:${devTag} in project ${devProject}."

                    script {
                        // TBD: Build Image (binary build), tag Image
                        //      Make sure the image name is correct in the tag!

                        openshift.withCluster() {
                            openshift.withProject("${devProject}") {
                                openshift.selector("bc", "tasks").startBuild("--from-file=./target/openshift-tasks.war", "--wait=true")
                                //openshift.startBuild("tasks", "--from-file=${nexusSvc}/repository/releases/${GUID}/org/jboss/quickstarts/eap/tasks/${prodTag}/tasks-${prodTag}.war", "--wait=true")
                                //openshift.selector("bc", "tasks").startBuild( "--from-file=http://${nexusSvc}/repository/releases/org/jboss/quickstarts/eap/${imageName}/${prodTag}/tasks-${prodTag}.war", "--wait=true")
                                //openshift.startBuild("tasks", "--from-file=http://${nexusSvc}/repository/releases/org/jboss/quickstarts/eap/tasks/${version}/tasks-${version}.war", "--wait=true")

                                echo "Building openshift.tag ${imageName}:latest, tasks:${devTag}"

                                openshift.tag("${imageName}:latest", "tasks:${devTag}")

                                echo "Done building openshift.tag"

                            }
                        }
                    }
                }
            }
        }
        // Deploy the built image to the Development Environment.
        stage('Deploy to Dev') {
            steps {
                dir('openshift-tasks') {
                    echo "Deploying container image to Development Project"
                    script {
                        // TBD: Deploy to development Project
                        //      Set Image, Set VERSION
                        //      (Re-) Create ConfigMap
                        //      Make sure the application is running and ready before proceeding

                        openshift.withCluster() {
                            openshift.withProject("${devProject}") {
                                openshift.set("image", "dc/tasks", "tasks=${imgRegCluster}/${devProject}/tasks:${devTag}")

                                openshift.set("env", "dc/tasks", "VERSION='${devTag} (${devProject})'")

                                // Update the Config Map which contains the users for the Tasks application
                                // (just in case the properties files changed in the latest commit)
                                openshift.selector('configmap', 'tasks-config').delete()
                                def configmap = openshift.create('configmap', 'tasks-config', '--from-file=./configuration/application-users.properties', '--from-file=./configuration/application-roles.properties' )

                                // Deploy the development application.
                                openshift.selector("dc", "tasks").rollout().latest();

                                // Wait for application to be deployed
                                def dc = openshift.selector("dc", "tasks").object()
                                def dc_version = dc.status.latestVersion
                                def rc = openshift.selector("rc", "tasks-${dc_version}").object()

                                echo "Waiting for ReplicationController tasks-${dc_version} to be ready"
                                while (rc.spec.replicas != rc.status.readyReplicas) {
                                    sleep 5
                                    rc = openshift.selector("rc", "tasks-${dc_version}").object()
                                }
                            }
                        }
                    }
                }
            }
        }

        // Copy Image to Nexus Container Registry
        stage('Copy Image to Nexus Container Registry') {
            steps {
                echo "Copy image to Nexus Container Registry"

                sh "skopeo copy --src-tls-verify=false --dest-tls-verify=false --src-creds openshift:\$(oc whoami -t) --dest-creds=admin:redhat docker://${imgRegCluster}/${devProject}/tasks:${devTag} docker://${nexusRegistry}/${GUID}/tasks:${devTag}"

                script {
                    echo "Copy image to Nexus Container Registry"

                    // TBD: Copy image to Nexus container registry

                    // TBD: Tag the built image with the production tag.
                    openshift.withCluster() {
                        openshift.withProject("${prodProject}") {
                            openshift.tag("${devProject}/tasks:${devTag}", "${devProject}/tasks:${prodTag}")
                        }
                    }
                }
            }
        }

        // Blue/Green Deployment into Production
        // -------------------------------------
        stage('Blue/Green Production Deployment') {
            steps {
                dir('openshift-tasks') {
                    script {
                        echo "Blue/Green Production Deployment"
                        // TBD: Determine which application is active
                        //      Set Image, Set VERSION
                        //      (Re-)create ConfigMap
                        //      Deploy into the other application
                        //      Make sure the application is running and ready before proceeding
                        openshift.withCluster() {
                            openshift.withProject("${prodProject}") {
                                activeApp = openshift.selector("route", "tasks").object().spec.to.name
                                if (activeApp == "tasks-green") {
                                    destApp = "tasks-blue"
                                }

                                echo "Active Application:      " + activeApp
                                echo "Destination Application: " + destApp


                                // Update the Image on the Production Deployment Config
                                def dc = openshift.selector("dc/${destApp}").object()

                                //
                                // Deploy into the Production application
                                dc.spec.template.spec.containers[0].image="${imgRegSvc}/${devProject}/tasks:${prodTag}"

                                //
                                openshift.apply(dc)
                                //openshift.selector("dc", "${destApp}").rollout().latest();
                                //
                                openshift.set("env", "dc/${destApp}", "VERSION='${prodTag}  (${destApp})'", "--overwrite")
                                // OR use
                                //openshift.set("env", "dc/tasks-blue", "VERSION='${prodTag} (tasks-blue)'")
                                //openshift.set("env", "dc/tasks-green", "VERSION='${prodTag} (tasks-green)'")


                                // Update Config Map in change config files changed in the source
                                //echo "Update Config Map in change config files changed in the source"
                                openshift.selector("configmap", "${destApp}-config").delete("--ignore-not-found=true")
                                def configmap = openshift.create("configmap", "${destApp}-config", "--from-file=./configuration/application-users.properties", "--from-file=./configuration/application-roles.properties")

                                // Deploy the inactive application.
                                openshift.selector("dc", "${destApp}").rollout().latest();

                                // Wait for application to be deployed
                                def dc_prod = openshift.selector("dc", "${destApp}").object()
                                def dc_version = dc_prod.status.latestVersion
                                def rc_prod = openshift.selector("rc", "${destApp}-${dc_version}").object()
                                echo "Waiting for ${destApp} to be ready"
                                while (rc_prod.spec.replicas != rc_prod.status.readyReplicas) {
                                    sleep 5
                                    rc_prod = openshift.selector("rc", "${destApp}-${dc_version}").object()
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Switch over to new Version') {
            steps{
                echo "Switching Production application to ${destApp}."
                script {
                    // TBD: Execute switch
                    //      Hint: sleep 5 seconds after the switch
                    //            for the route to be fully switched over
                    openshift.withCluster() {
                        openshift.withProject("${prodProject}") {
                            def route = openshift.selector("route/tasks").object()
                            route.spec.to.name="${destApp}"
                            openshift.apply(route)
                            sleep 5
                        }
                    }
                }
            }
        }
    }
}
