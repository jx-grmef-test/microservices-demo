node {
    environment {
        registry = "jx-grmef-test/microservices-demo"
        registryCredential = 'jx-pipeline-git-github-github'
    }
    agent any

    def app
    stages {
        stage('GitCheckout') {
            // Cloning project microservices-demo
            // checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'jx-pipeline-git-github-github', url: 'https://github.com/jx-grmef-test/microservices-demo.git']]])

            checkout scm

            sh 'pwd && ls -l && ls -l release'
        }
        stage('Initialize') {
            def dockerHome = tool 'Docker-default'
            env.PATH = "${dockerHome}/bin:${env.PATH}"

            sh 'docker -v'

        }
        stage('Build Adservice image') {
            /* This builds the actual image; synonymous to
             * docker build on the command line */

            app = docker.build( registry + "/src/adservice/")
            //steps {
            //    script {
            //        //docker.build registry + ":$BUILD_NUMBER"
            //        docker.build("jx-grmef-test/microservices-demo/src/adservice/")
            //    }
            //}
            //stage('Test image') {
            //    /* We test our image with a simple smoke test:
            //     * Run a curl inside the newly-build Docker image */

            //    app.inside {
            //        sh 'curl http://localhost:9555 || exit 1'
            //        sh 'echo "Tests passed"'
            //    }
            //}
            //stage('Push image') {
            //    /* Finally, we'll push the image with two tags:
            //     * First, the incremental build number from Jenkins
            //     * Second, the 'latest' tag.
            //     * Pushing multiple tags is cheap, as all the layers are reused. */
            //    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
            //        app.push("${env.BUILD_NUMBER}")
            //        app.push("latest")
            //    }
            //}
        }
    }
}