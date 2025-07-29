pipeline {
    agent any
    tools {
        jdk 'jdk17'
    }
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/StaySplit/staysplit-backend.git', branch: 'main'
            }
        }
        stage('Build') {
            when {
                branch 'main'
            }
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
        }
        success {
          githubNotify context: 'jenkins/pr-check', status: 'SUCCESS'
        }
    }
}
