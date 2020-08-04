// Project variables
def projectName = "microservices-demo"
def projectKubectlVersion = "v1.18.6"
def projectKubeCtlDir = "kubectl_binary"
def projectKubeConfigDir = ".kube"

// Kubernetes Config Variables
def appNameSpace = "app-microservices-demo"
def kubeConfigDir = "/root/.kube"

// Infress Vars
def ingressName = "frontend-external"
def ingressHost = "frontend-external.apps.meflab.xyz"

def app


pipeline {
    agent any

    stages {
        stage('GitCheckout') {
            steps {
                // Cloning project microservices-demo
                //checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'jx-pipeline-git-github-github', url: 'https://github.com/jx-grmef-test/microservices-demo.git']]])

                checkout scm

                sh 'pwd && ls -l && ls -l release '
            }
        }
        stage('Build adservice image') {
            steps {
                echo '############################
                      # Building adservice image #
                      ############################'
                dir("src/adservice") {
                    sh "pwd"
                    //            //script {
                    //            //    app = docker.build("microservices-demo/image/adservice")
                    //            //}
                    //        }
                    //    }
                }
            }
        }
    }
}