pipeline {
  agent {
    dockerfile true
  }

  options {
    buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '5'))
  }

  triggers {
    cron '@midnight'
  }

  parameters {
    choice(name: 'deployProfile',
      description: 'Choose where the built plugin should be deployed to',
      choices: ['central.snapshots', 'maven.central.release'])
  }

  stages {
    stage('build') {
      steps {
        script {
          def targetBranch = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
          def officialRelease = params.deployProfile == 'maven.central.release';
          def publishingUri = "https://central.sonatype.com/publishing/deployments"
          
          if (officialRelease) {
            sh "git config --global user.name 'ivy-team'"
            sh "git config --global user.email 'info@ivyteam.ch'"
            
            sh "git checkout -b ${targetBranch}"
            sh "git tag -l | xargs git tag -d"
            
            def releasedVersion = version('-DremoveSnapshot=true')
            sh "git add . ;git commit -m 'update versions for release ${releasedVersion}'"
            sh "git tag \"v${releasedVersion}\" -a -m \"Official release ${releasedVersion}\""
          }
          
          withCredentials([string(credentialsId: 'gpg.password.axonivy', variable: 'GPG_PWD'), file(credentialsId: 'gpg.keystore.axonivy', variable: 'GPG_FILE')]) {
            sh "gpg --batch --import ${env.GPG_FILE}"
            def phase = env.BRANCH_NAME == 'master' ? 'deploy' : 'verify'
            def mavenProps = "-Dgpg.passphrase='${env.GPG_PWD}' ";
            if (officialRelease) {
              mavenProps += "-P ${params.deployProfile}"
            }
            maven cmd: "clean ${phase} ${mavenProps}"
            currentBuild.description = "<a href='${publishingUri}'>publishing</a>"
          }
          
          if (officialRelease) {
            def nextVersion=version('-DnextSnapshot=true')
            sh "git add . ;git commit -m 'prepare next dev-cycle ${nextVersion}'"
            
            withEnv(['GIT_SSH_COMMAND=ssh -o StrictHostKeyChecking=no']) {
              sshagent(credentials: ['github-axonivy']) {
                sh "git push origin --tags"
                sh "git push -u origin ${targetBranch}"
              }
              def title = "Prepare for next development cycle (${env.BRANCH_NAME})"
              def message = ":warning: merge this PR only if you published the artifact on [CentralPortal](${publishingUri})"
              withCredentials([file(credentialsId: 'github-ivyteam-token-repo-manager', variable: 'tokenFile')]) {
                sh "gh auth login --with-token < ${tokenFile}"
                sh "gh pr create --title '${title}' --body '${message}' --head ${targetBranch} --base ${env.BRANCH_NAME}"
              }
            }
          }
        }

        recordIssues tools: [eclipse()], qualityGates: [[threshold: 1, type: 'TOTAL']]
        recordIssues tools: [mavenConsole()]
        junit testDataPublishers: [[$class: 'StabilityTestDataPublisher']], testResults: '**/target/surefire-reports/**/*.xml'
        archiveArtifacts '**/target/*.jar'
      }
    }
  }
  
}

def version(def param) {
  sh "mvn org.codehaus.mojo:versions-maven-plugin:2.18.0:set ${param} -DgenerateBackupPoms=false | grep '\\[.*' "
  def evalCmd='mvn help:evaluate -Dexpression=\'project.version\' -q -DforceStdout'
  def current = sh(script: evalCmd, returnStdout: true)
  return current;
}
