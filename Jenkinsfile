pipeline {
    agent any
    tool name: 'default-maven', type: 'maven'
    stages {
            stage('Create org') {
                steps {
                    script {

                        status = sh returnStatus: true, script: "git rebase origin/master"
                        if(status != 0){
                            error 'rebase failed'
                        }
                        status = sh returnStatus: true, script: "mvn clean compile"
                        if(status != 0){
                            error 'Compile failed'
                        }
                    }
                }
            }
           }
}