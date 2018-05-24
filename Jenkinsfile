pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        git(url: '${env.GIT_REPO}', branch: '${env.GIT_BRANCH}', poll: true)
      }
    }
  }
  environment {
    GIT_REPO = 'https://github.com/Chainium/Spikes.git'
    GIT_BRANCH = 'master'
  }
}