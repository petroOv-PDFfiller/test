pipeline {
    agent any
    tools {
            maven "maven"
            jdk "JDK"
    }
    stages {
        stage('Build project') {
            steps {
                script {
                    sh "git config user.name 'Jenkins New'"
                    sh "git config user.email jenkins@example.com"
                    if(env.CHANGE_TARGET){
                        status = sh returnStatus: true, script: "git rebase origin/${CHANGE_TARGET}"
                        if(status != 0){
                            error 'Rebase failed'
                        }
                    }
                    configFileProvider([configFile(fileId: 'SetiingsMaven', variable: 'SETTINGS')]) {
                        status = sh returnStatus: true, script: "mvn -s $SETTINGS clean test-compile"
                        if(status != 0){
                            error 'Compile failed'
                        }
                    }
                }
            }
        }
    }
    post {
        unsuccessful {
            slackSend channel: '#ddd-at', color: '#e01f1f', iconEmoji: '', message: "Branch failed ${env.JOB_NAME}:(<${env.BUILD_URL}|Open>)", tokenCredentialId: 'jenkins-slack', username: ''
        }
    }
}