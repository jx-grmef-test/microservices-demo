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

pipeline {
    agent {
        dockerfile true
    }

    stages {
        stage('GitCheckout') {
            steps {
                // Cloning project microservices-demo
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'jx-pipeline-git-github-github', url: 'https://github.com/jx-grmef-test/microservices-demo.git']]])
                sh 'pwd && ls -l && ls -l release '
            }
        }
        stage('Build images') {
            steps {
                echo 'Building adservice image'
                script {
                    docker.build registry + ":$BUILD_NUMBER"

                }
            }
        }
    }
}