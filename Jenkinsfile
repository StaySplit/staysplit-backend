pipeline {
    agent any
    tools {
        jdk 'jdk17'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm // 동적으로 현재 PR 브랜치 checkout
            }
        }
        stage('Build') {
            steps {
                sh './gradlew build' // 또는 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
    }
    post {
        failure {
            // 빌드 실패 시 GitHub에 상태 보고
            githubNotify context: 'jenkins/pr-check', status: 'FAILURE'
            githubNotify context: 'CI/Jenkins', status: 'FAILURE'
        }
        success {
            githubNotify context: 'jenkins/pr-check', status: 'SUCCESS'
            githubNotify context: 'CI/Jenkins', status: 'SUCCESS'
        }
    }
}
