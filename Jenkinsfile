pipeline {
  agent any
  stages {
    stage('listDir') {
      parallel {
        stage('listDir') {
          steps {
            sh 'ls -l /var/jenkins_home/workspace/'
          }
        }

        stage('listFiles') {
          steps {
            sh 'ls -l /var/jenkins_home/workspace/microservices-demo3_test'
          }
        }

      }
    }

  }
}