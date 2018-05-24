pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'dotnet build $SOLUTION_NAME -c $BUILD_CONFIG -o $PROJECT_OUTPUT_FOLDER'
      }
    }
  }
  environment {
    SOLUTION_NAME = 'Simplify'
    BUILD_CONFIG = 'Release'
    PROJECT_OUTPUT_FOLDER = 'Release'
  }
}