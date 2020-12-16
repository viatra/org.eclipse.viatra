pipeline {
	agent any
	parameters {
        choice choices: ['ci', 'integration', 'milestone', 'release'],
            description: 'Sets the build type. CI builds are used for testing reasons only; integration builds are periodic builds; milestones are considered more-or-less stable, while releases are Eclipse.org releases with a corresponding release review.',
            name: 'BUILD_TYPE'
	}
	options {
		buildDiscarder(logRotator(numToKeepStr: '5'))
        timeout(120 /*minutes*/) 
	}
	
	tools {
        maven 'apache-maven-latest'
        jdk 'jdk1.8.0-latest'
    }

	stages {
		stage('Maven Bootstrap') { 
			steps {
				sh "mvn -B -f releng/org.eclipse.viatra.parent.core/pom.xml -DBUILD_TYPE=${BUILD_TYPE} -Dmaven.repo.local=${WORKSPACE}/.repository clean install"
			}
		}
		stage('Full build') { 
			steps {
                xvnc {
                    sh "mvn -B -f releng/org.eclipse.viatra.parent.all/pom.xml -DBUILD_TYPE=${BUILD_TYPE} -Dmaven.repo.local=${WORKSPACE}/.repository -Dmaven.test.failure.ignore=true -Dviatra.download.area=/home/data/httpd/download.eclipse.org/viatra -DrunUITests=true -Dlicense.aggregate=false clean install"
                }
			}
		}
	}

	post {
		always {
			archiveArtifacts artifacts: 'releng/org.eclipse.viatra.update/target/repository/**'
            archiveArtifacts artifacts: 'releng/org.eclipse.viatra.update/target/org.eclipse.viatra.update-*.zip'
            archiveArtifacts artifacts: 'releng/org.eclipse.viatra.docs/target/reference/**'
            archiveArtifacts artifacts: 'query/tests/org.eclipse.viatra.query.rcptt/org.eclipse.viatra.query.rcptt.tests/target/results/**'
			junit testResults: '**/tests/**/target/surefire-reports/*.xml'
		}
}
