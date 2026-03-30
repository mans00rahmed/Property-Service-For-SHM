pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/mans00rahmed/Property-Service-For-SHM.git'
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvnw.cmd clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'target/site/jacoco/jacoco.xml']],
                        id: 'property-service-coverage',
                        name: 'Property Service Coverage'
                    )
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    bat 'mvnw.cmd sonar:sonar -Dsonar.projectKey=property-service -Dsonar.projectName="Property Service"'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        always {
            echo 'Property Service pipeline finished.'
        }
        success {
            echo 'Property Service built successfully.'
        }
        failure {
            echo 'Property Service pipeline failed.'
        }
    }
}