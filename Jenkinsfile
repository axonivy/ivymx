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

  stages {
    stage('build') {
      steps {
        script {
          withCredentials([string(credentialsId: 'gpg.password.axonivy', variable: 'GPG_PWD'), file(credentialsId: 'gpg.keystore.axonivy', variable: 'GPG_FILE')]) {
            sh "gpg --batch --import ${env.GPG_FILE}"
            def phase = env.BRANCH_NAME == 'master' ? 'deploy' : 'verify'
            maven cmd: "clean ${phase} -Dgpg.passphrase='${env.GPG_PWD}' -Dskip.gpg=false " 
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
