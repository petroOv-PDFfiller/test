pipeline {
    stages {
            stage('Create org') {
                steps {
                    script {

                        sh returnStatus: true, script: "git rebase origin/master"
                        sh returnStatus: true, script: "mvn clean test"

                    }
                }
            }
           }
}