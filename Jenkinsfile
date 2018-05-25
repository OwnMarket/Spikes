pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'dotnet build $SOLUTION_NAME -c $BUILD_CONFIG -o $PROJECT_OUTPUT_FOLDER'
      }
    }
    stage('Unit tests') {
      environment {
        RESULTS_OUTPUT_PATH = '/testresults'
      }
      steps {
        sh 'bash ./Build/run_tests.sh $SOLUTION_NAME $BUILD_CONFIG $PROJECT_OUTPUT_FOLDER $RESULTS_OUTPUT_PATH *.Tests*'
      }
    }
  }
  environment {
    SOLUTION_NAME = 'Simplify'
    BUILD_CONFIG = 'Release'
    PROJECT_OUTPUT_FOLDER = 'Release'
  }
}