pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        git(url: 'https://github.com/Chainium/Spikes.git', branch: 'master', poll: true)
        sh 'dotnet build Simplify -c Release -o Release'
      }
    }
    stage('Unit tests') {
      steps {
        sh 'echo "TODO: run unit tests"'
      }
    }
    stage('Integration tests') {
      steps {
        sh 'echo "Run integration tests"'
      }
    }
  }
}