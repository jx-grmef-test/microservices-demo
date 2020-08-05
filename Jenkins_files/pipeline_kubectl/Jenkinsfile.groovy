// Project variables
def projectName = "microservices-demo" as java.lang.Object
def projectKubectlVersion = "v1.18.6" as java.lang.Object
def projectKubeCtlDir = "kubectl_binary" as java.lang.Object
def projectKubeConfigDir = ".kube" as java.lang.Object

// Kubernetes Config Variables
def appNameSpace = "app-microservices-demo" as java.lang.Object
def kubeConfigDir = "/root/.kube" as java.lang.Object

// Ingress Vars
def ingressName = "frontend-external" as java.lang.Object
def ingressHost = "frontend-external.apps.meflab.xyz" as java.lang.Object

pipeline {
    agent any

    stages {
        stage('GitCheckout') {
            steps {
                // Cloning project microservices-demo
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'jx-pipeline-git-github-github', url: 'https://github.com/jx-grmef-test/microservices-demo.git']]])
                sh 'pwd && ls -l && ls -l release '
            }
        }
        /* stage('InstallKubeCtl') {
            steps {
                // Relocate kubectl-binary
                sh "cp  /var/jenkins_home/workspace/${projectName}/kubernetes-configs/${projectKubeCtlDir}/${projectKubectlVersion}/kubectl /usr/local/bin/"

                // Fix Permissions
                sh 'chmod +x /usr/local/bin/kubectl'

                sh 'ls -l /usr/local/bin/'

                // Create /root/.kube dir if doesn't exist

                script {
                    def dir = sh(returnStdout: true, script: "test -d ${kubeConfigDir} && echo '1' || echo '0'").trim()
                    //echo "Dir is ${dir} "
                    if (dir == '1') {
                        //Copy config to /root/.kube
                        echo 'Copy config to /root/.kube'
                        sh "cp  /var/jenkins_home/workspace/${projectName}/kubernetes-configs/${projectKubeConfigDir}/config /root/.kube"
                    } else {
                        echo 'Create Dir and Copy config to /root/.kube'
                        // Create Dir
                        sh "mkdir ${kubeConfigDir}"
                        //Copy config to /root/.kube
                        sh "cp  /var/jenkins_home/workspace/${projectName}/kubernetes-configs/${projectKubeConfigDir}/config /root/.kube"
                    }
                }

                // Confirm /root/.kube/config file exist
                echo 'Confirm /root/.kube/config file exist'
                sh "ls -l ${kubeConfigDir}/config"

                // Check Cluster access
                echo 'Check Cluster access'
                sh 'kubectl get ns'
            }
        }*/
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
                echo "Deploying ${projectName} in ${appNameSpace} Namespace"
                sh "kubectl -n ${appNameSpace} apply -f ./release/kubernetes-manifests-nexus.yaml"
            }
        }
        stage('pods Verification') {
            steps {
                // Create a NameSpace kubectl-binary
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
            steps {
                // Creating  a NameSpace kubectl-binary
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
    }
}
