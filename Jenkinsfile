#!groovy
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
			sh 'bash ./Build/package.sh $SOLUTION_NAME $PROJECTS $PUBLISH_DIR $PROJECT_OUTPUT_FOLDER
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
    }

}