pipeline {
  agent any
  stages {
    stage('Build') {
      environment {
        SOLUTION_NAME = 'Simplify'
        BUILD_CONFIG = 'Release'
        PROJECT_OUTPUT_FOLDER = 'Release'
      }
      steps {
        sh 'dotnet build $SOLUTION_NAME -c $BUILD_CONFIG -o $PROJECT_OUTPUT_FOLDER'
      }
    }
  }
}