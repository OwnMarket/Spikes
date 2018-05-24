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
        sh 'dotnet build ${env.SOLUTION_NAME} -c ${env.BUILD_CONFIGURATION} -o ${env.PROJECT_OUTPUT_FOLDER}'
      }
    }
  }
}