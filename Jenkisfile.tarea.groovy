pipeline {
      environment {
        doError = '1'
    }
    agent {
        label 'docker'
    }
    stages {
        stage('Source') {
            steps {
                git 'https://github.com/Dvidchv/unir-cicd'
            }
        }
        stage('Build') {
            steps {
                echo 'Building stage!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        
        stage('API tests') {
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        
       stage('E2E tests') {
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }

    }
    post {
    failure {
        mail to: 'dvidchv@gmail.com',
             subject: "Pipeline fallido: ${currentBuild.fullDisplayName}, ID: ${env.BUILD_ID}",
             body: "Ocurrio un error en ${env.BUILD_URL}"
    }
     success {
             echo  "Nombre: ${currentBuild.fullDisplayName}, ID: ${env.BUILD_ID}"
    }
        always {
            echo "Running  ${currentBuild.fullDisplayName}, ${env.BUILD_ID} on ${env.JENKINS_URL}"
            junit 'results/*_result.xml'
            cleanWs()
        }
    }
}
