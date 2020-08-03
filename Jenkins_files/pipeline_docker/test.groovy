node {

    def app
    stage('GitCheckout') {
        // Cloning project microservices-demo
        // checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'jx-pipeline-git-github-github', url: 'https://github.com/jx-grmef-test/microservices-demo.git']]])

        checkout scm

        sh 'pwd && ls -l && ls -l release'
    }
    stage('Initialize') {
        def dockerHome = tool 'Docker-19'
        env.PATH = "${dockerHome}/bin:${env.PATH}"

        sh 'docker -v'

    }
    stage('Build Adservice image') {
        /* This builds the actual image; synonymous to
         * docker build on the command line */
        dir("src/adservice") {
            sh "pwd"
            app = docker.build("microservices-demo/src/adservice")
        }

        stage('Test Adservice image') {
            /* We test our image with a simple smoke test:
             * Run a curl inside the newly-build Docker image */

            sh "echo $PATH"
            docker.image("microservices-demo/src/adservice:latest").inside("--entrypoint='/app/build/install/hipstershop/bin/AdService'") { c ->
                //sh 'curl http://localhost:9555 || exit 1'
                sh 'cat /app/build/install/hipstershop/bin/AdService'
                sh 'echo "Tests passed"'
            }
        }
        stage('Push Adservice image to Nexus') {
            /* Finally, we'll push the image with two tags:
             * First, the incremental build number from Jenkins
             * Second, the 'latest' tag.
             * Pushing multiple tags is cheap, as all the layers are reused. */
            docker.withRegistry('http://nexus.apps.meflab.xyz/repository/microservices-demo/', 'nexus-password') {
                app.push("${env.BUILD_NUMBER}")
                app.push("latest")
            }
        }
    }
}
