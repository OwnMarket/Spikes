#!groovy
pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'dotnet publish $SOLUTION_NAME -c $BUILD_CONFIG -o $PROJECT_OUTPUT_FOLDER'
      }
    }
    stage('Unit tests') {
      environment {
        RESULTS_OUTPUT_PATH = 'testresults'
        FULL_RESULTS_PATH ="$WORKSPACE/$RESULTS_OUTPUT_PATH"
      }
      steps {
        
        sh "mkdir $RESULTS_OUTPUT_PATH"
        sh 'bash ./Build/run_tests.sh $SOLUTION_NAME $BUILD_CONFIG $PROJECT_OUTPUT_FOLDER $FULL_RESULTS_PATH *.Tests*'
        xunit testTimeMargin: "3000", 
              thresholdMode: 1, 
              thresholds: [
                failed(failureNewThreshold: '0', failureThreshold: '0', unstableNewThreshold: '0', unstableThreshold: '0'), 
                skipped(failureNewThreshold: '0', failureThreshold: '0', unstableNewThreshold: '0', unstableThreshold: '0')
              ], 
              tools: [
                MSTest(deleteOutputFiles: true, 
                failIfNotNew: true, 
                pattern: "${RESULTS_OUTPUT_PATH}/*.trx", skipNoTestFiles: false, stopProcessingIfError: true)
              ]
      }
    }
    stage('Integration tests') {
      environment {
        RESULTS_OUTPUT_PATH = 'integrationtestresults'
        FULL_RESULTS_PATH ="$WORKSPACE/$RESULTS_OUTPUT_PATH"
      }
      steps {
        
        sh "mkdir $RESULTS_OUTPUT_PATH"
        sh 'bash ./Build/run_tests.sh $SOLUTION_NAME $BUILD_CONFIG $PROJECT_OUTPUT_FOLDER $FULL_RESULTS_PATH *.IntegrationTests*'
        xunit testTimeMargin: "3000", 
              thresholdMode: 1, 
              thresholds: [
                failed(failureNewThreshold: '0', failureThreshold: '0', unstableNewThreshold: '0', unstableThreshold: '0'), 
                skipped(failureNewThreshold: '0', failureThreshold: '0', unstableNewThreshold: '0', unstableThreshold: '0')
              ], 
              tools: [
                MSTest(deleteOutputFiles: true, 
                failIfNotNew: true, 
                pattern: "${RESULTS_OUTPUT_PATH}/*.trx", skipNoTestFiles: false, stopProcessingIfError: true)
              ]
      }
    }
    stage('Publish'){
        environment {
            PROJECTS ='Simplify.App;'
        }
        steps {
            sh "mkdir $PUBLISH_DIR"
            sh 'bash ./Build/package.sh $SOLUTION_NAME $PROJECTS $PUBLISH_DIR $PROJECT_OUTPUT_FOLDER'
        }
    }
  }
  
  
  environment {
    SOLUTION_NAME = 'Simplify'
    BUILD_CONFIG = 'Release'
    PROJECT_OUTPUT_FOLDER = 'Release'
    PUBLISH_DIR = 'publish'
  }
  
  post {
        always {
            archiveArtifacts artifacts: "$PUBLISH_DIR/*.tar.gz,$PUBLISH_DIR/*.zip", fingerprint: true
        }
        success {
            slackSend color: 'good', message: 'Completed: ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)'
        }
        failure {
            mail bcc: '', body: "<b>Example</b><br>\n\<br>Project: ${env.JOB_NAME} <br>Build Number: ${env.BUILD_NUMBER} <br> URL de build: ${env.BUILD_URL}", cc: '', charset: 'UTF-8', from: '', mimeType: 'text/html', replyTo: '', subject: "ERROR CI: Project name -> ${env.JOB_NAME}", to: "foo@foomail.com";
        }
        unstable {
            echo 'This will run only if the run was marked as unstable'
        }
        changed {
            echo 'This will run only if the state of the Pipeline has changed'
            echo 'For example, if the Pipeline was previously failing but is now successful'
        }

    }

}