pipeline {
triggers {
        pullRequest {
            admin('petroOv-PDFfiller')
            triggerPhrase('OK to test')
            useGitHubHooks()
            displayBuildErrorsOnDownstreamBuilds()
            whiteListTargetBranches(['master'])
            allowMembersOfWhitelistedOrgsAsAdmin()
            extensions {
                commitStatus {
                    context('deploy to staging site')
                    triggeredStatus('starting deployment to staging site...')
                    startedStatus('deploying to staging site...')
                    addTestResults(true)
                    statusUrl('http://mystatussite.com/prs')
                    completedStatus('SUCCESS', 'All is well')
                    completedStatus('FAILURE', 'Something went wrong. Investigate!')
                    completedStatus('PENDING', 'still in progress...')
                    completedStatus('ERROR', 'Something went really wrong. Investigate!')
                }
            }
        }
    }
    agent {
        any
    }
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