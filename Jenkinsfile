pipeline {
	agent {
        kubernetes {
            label 'centos7-6gb'
        }
    }
    
	parameters {
        choice choices: ['ci', 'integration', 'milestone', 'release'],
            description: 'Sets the build type. CI builds are used for testing reasons only; integration builds are periodic builds; milestones are considered more-or-less stable, while releases are Eclipse.org releases with a corresponding release review.',
            name: 'BUILD_TYPE'
        string defaultValue: '',
            description: 'If set, the tycho-versions plugin is used to update the versions of the Maven plugins before deployment for graduated plugins',
            name: 'RELEASE_VERSION'
        string defaultValue: '',
            description: 'If set, the tycho-versions plugin is used to update the versions of the incubating Maven plugins before deployment for graduated plugins',
            name: 'INCUBATION_VERSION'
	}
	options {
		buildDiscarder(logRotator(numToKeepStr: '5'))
        timeout(120 /*minutes*/) 
	}
	environment {
	   SIGN_BUILD_PARAMETER = params.BUILD_TYPE == 'ci' ? '' : ' -Dsign.build=true '
	}
	
	tools {
        maven 'apache-maven-latest'
        jdk 'oracle-jdk8-latest' //AdoptOpenJDK does not work because of JavaFX dependencies
    }

	stages {
		stage('Maven Bootstrap') { 
			steps {
				sh "mvn -B -f releng/org.eclipse.viatra.parent.core/pom.xml -DBUILD_TYPE=$BUILD_TYPE $SIGN_BUILD_PARAMETER -Dmaven.repo.local=$WORKSPACE/.repository clean install"
			}
		}
		stage('Full build') { 
			steps {
                xvnc {
                    sh "mvn -B -f releng/org.eclipse.viatra.parent.all/pom.xml -DBUILD_TYPE=$BUILD_TYPE $SIGN_BUILD_PARAMETER -Dmaven.repo.local=$WORKSPACE/.repository -Dmaven.test.failure.ignore=true -Dviatra.download.area=/home/data/httpd/download.eclipse.org/viatra -DrunUITests=true -Dlicense.aggregate=false clean install"
                }
			}
		}
		stage('download.eclipse.org') {
		  when {expression { return params.BUILD_TYPE != "ci" }}
		  steps {
		      sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
		          sh '''
                        $PROJECT=$WORKSPACE/releng/org.eclipse.viatra.update
                        $WORK_DIR=$PROJECT/work
                        
                        VERSION=$(<$PROJECT/target/classes/version.qualified)
                        mkdir $WORK_DIR
                        
                        # Upload repository to download.eclipse.org
                        scp -o BatchMode=yes -r $PROJECT/target/repository/* genie.projectname@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/viatra/$BUILD_TYPE/$VERSION
                        
                        # Download composite artifact contents
                        scp -o BatchMode=yes genie.projectname@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/viatra/$BUILD_TYPE/composite* $WORK_DIR 
                        
                        # Add new repository to composite
                        /shared/common/apache-ant-1.9.6/bin/ant -f /shared/modeling/tools/promotion/manage-composite.xml add -Dchild.repository='../$VERSION'
                        
                        # Upload modified composite descriptor
                        scp -o BatchMode=yes $WORK_DIR/composite* genie.projectname@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/viatra/$BUILD_TYPE
		          '''
		      }
		  }
		}
        stage('Maven Deploy') {
            when {branch "master"} 
			steps {
                script {
                    if (params.RELEASE_VERSION) {
                        sh "mvn -B -f releng/org.eclipse.viatra.parent.all/pom.xml -Dmaven.repo.local=$WORKSPACE/.repository -DnewVersion=$RELEASE_VERSION org.eclipse.tycho:tycho-versions-plugin:set-version"
                        sh "mvn -B -f releng/org.eclipse.viatra.parent.core/pom.xml -DBUILD_TYPE=$BUILD_TYPE -Dmaven.repo.local=$WORKSPACE/.repository clean install"
                    }
                    if (params.INCUBATION_VERSION) {
                        sh "mvn -B -f releng/org.eclipse.viatra.parent.incubation.maven/pom.xml -Dmaven.repo.local=$WORKSPACE/.repository -DnewVersion=$INCUBATION_VERSION org.eclipse.tycho:tycho-versions-plugin:set-version"
                        sh "mvn -B -f releng/org.eclipse.viatra.parent.incubation.maven/pom.xml -DBUILD_TYPE=$BUILD_TYPE -Dmaven.repo.local=$WORKSPACE/.repository clean install"
                    }
                }
                
				sh "mvn -B -f releng/org.eclipse.viatra.parent.core/pom.xml -DBUILD_TYPE=$BUILD_TYPE -Dmaven.repo.local=$WORKSPACE/.repository deploy --fail-never"
                sh "mvn -B -f releng/org.eclipse.viatra.parent.incubation.maven/pom.xml -DBUILD_TYPE=$BUILD_TYPE -Dmaven.repo.local=$WORKSPACE/.repository deploy --fail-never"
			}
		}
	}

	post {
		always {
			archiveArtifacts artifacts: 'releng/org.eclipse.viatra.update/target/repository/**'
            archiveArtifacts artifacts: 'releng/org.eclipse.viatra.update/target/org.eclipse.viatra.update-*.zip'
            archiveArtifacts artifacts: 'query/tests/org.eclipse.viatra.query.rcptt/org.eclipse.viatra.query.rcptt.tests/target/results/**'
            archiveArtifacts artifacts: 'releng/org.eclipse.viatra.docs/target/reference/api/**'
            javadoc javadocDir: 'releng/org.eclipse.viatra.docs/target/reference/api', keepAll: false
			junit testResults: '**/tests/**/target/surefire-reports/*.xml'
		}
    }
}
