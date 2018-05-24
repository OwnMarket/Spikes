pipeline {
  agent any
  
  environment {
    GIT_REPO = 'https://github.com/Chainium/Spikes.git'
    GIT_BRANCH = 'master'
    SOLUTION_NAME = 'Simplify'
    BUILD_CONFIGURATION = 'Release'
    PROJECT_OUTPUT_FOLDER = 'Release'
    TEST_RESULTS_FOLDER = 
  }
  
  stages {
    stage('Build') {
      steps {
        git(url: ${env.GIT_REPO}, ${branch: env.GIT_BRANCH}, poll: true)
        sh 'dotnet build ${env.SOLUTION_NAME} -c ${env.BUILD_CONFIGURATION} -o ${env.PROJECT_OUTPUT_FOLDER}'
      }
    }
    stage('Unit tests') {
      steps {
        sh 'build/runtests.sh ${env.SOLUTION_NAME} ${env.BUILD_CONFIGURATION} ${env.PROJECT_OUTPUT_FOLDER} "/testresults" *.Tests*'
      }
    }
    stage('Integration tests') {
      steps {
        sh 'echo "Run integration tests"'
      }
    }
  }
}