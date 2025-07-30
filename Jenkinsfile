pipeline {
    agent any
    tools {
        jdk 'jdk17'
    }
    stages {
        stage('Checkout') {
            when {
                anyOf {
                    branch 'main'
                    changeRequest()
                }
            }
            steps {
                checkout scm // 동적으로 현재 PR 브랜치 checkout
            }
        }
        stage('Prepare Upload Dir') {
            steps {
                bat 'if not exist uploads mkdir uploads'
            }
        }
//        stage('Copy keystore') {
//            steps {
//                bat ' copy /Y keystore.p12 src\\main\\resources\\keystore.p12'
//            }
//        }
        stage('Create Config') {
          steps {
            withCredentials([file(credentialsId: 'APPLICATION_YML_CONTENT', variable: 'APP_YML_FILE')]) {
                bat 'copy %APP_YML_FILE% src\\main\\resources\\application.yml'
                bat 'type src\\main\\resources\\application.yml'
            }
          }
        }
        stage('Build') {
            when {
                anyOf {
                    branch 'main'
                    changeRequest()
                }
            }
            steps {
                bat './gradlew build -x test' // 또는 'mvn clean install'
            }
        }
    }
    post {
        failure {
            script {
                if (env.CHANGE_ID) {
                    githubNotify context: 'jenkins/pr-check', 
                                status: 'FAILURE', 
                                description: 'PR 검증이 실패했습니다. 로그를 확인해주세요.',
                                targetUrl: env.BUILD_URL
                    
                    githubNotify context: 'CI/Jenkins', 
                                status: 'FAILURE', 
                                description: 'Jenkins CI 빌드가 실패했습니다.',
                                targetUrl: env.BUILD_URL
                }
            }
        }
        
        success {
            script {
                if (env.CHANGE_ID) {
                    githubNotify context: 'jenkins/pr-check', 
                                status: 'SUCCESS', 
                                description: 'PR 검증이 성공적으로 완료되었습니다.',
                                targetUrl: env.BUILD_URL
                    
                    githubNotify context: 'CI/Jenkins', 
                                status: 'SUCCESS', 
                                description: 'Jenkins CI 빌드가 성공했습니다.',
                                targetUrl: env.BUILD_URL
                }
            }
        }
        
        always {
            echo "파이프라인이 완료되었습니다."
        }
    }
}
