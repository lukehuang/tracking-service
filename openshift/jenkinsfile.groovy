pipeline {
    agent { label 'maven' }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timeout(time: 10, unit: 'MINUTES')
    }

    environment {
        DEFAULT_JVM_OPTS = '-Dhttp.proxyHost=l98fppx1.admin.arbeitslosenkasse.ch -Dhttp.proxyPort=8080 -Dhttps.proxyHost=l98fppx1.admin.arbeitslosenkasse.ch -Dhttps.proxyPort=8080 -Dhttp.nonProxyHosts="*.admin.arbeitslosenkasse.ch|172.27.97.10|172.27.97.11|172.27.97.12|172.30.0.1"'
        JAVA_TOOL_OPTIONS = "$JAVA_TOOL_OPTIONS $DEFAULT_JVM_OPTS"
        SERVER_URL = "https://alvch.jfrog.io/alvch"
        CREDENTIALS = "artifactory-deploy"
        ARTIFACTORY_SERVER = "alv-ch"
        MAVEN_HOME = "/opt/rh/rh-maven35/root/usr/share/xmvn"
        SONAR_LOGIN = credentials('SONAR_TOKEN')
        SONAR_SERVER = "${env.SONAR_HOST_URL}"
        NAMESPACE_NAME = "jobroom-dev"
        DOCKER_BUILD_NAME = "tracking-service-docker"
        PROJECT_NAME = "tracking-service"
        APP_NAME = "tracking-service"
        ARTIFACT_VERSION = "${new Date().format("YYYY-MM-dd")}.${BUILD_NUMBER}"
    }

    stages {

        stage('Initialize') {
            steps {
                sh '''
                  echo "PATH = ${PATH}"
                  echo "MAVEN_HOME = ${MAVEN_HOME}"
                  echo ARTIFACT_VERSION = ${ARTIFACT_VERSION}
              '''
            }
        }

        stage('Artifactory configuration') {
            steps {
                rtServer(
                        id: ARTIFACTORY_SERVER,
                        url: SERVER_URL,
                        credentialsId: CREDENTIALS
                )

                rtMavenDeployer(
                        id: "MAVEN_DEPLOYER",
                        serverId: ARTIFACTORY_SERVER,
                        releaseRepo: "libs-releases-local",
                        snapshotRepo: "libs-snapshots-local"
                )

                rtMavenResolver(
                        id: "MAVEN_RESOLVER",
                        serverId: ARTIFACTORY_SERVER,
                        releaseRepo: "libs-releases-ocp",
                        snapshotRepo: "libs-snapshots"
                )
            }
        }

        stage('Exec Maven') {
            steps {
                rtMavenRun(
                        pom: 'pom.xml',
                        goals: 'package -DskipTests -DskipITs=true',
                        deployerId: "MAVEN_DEPLOYER",
                        resolverId: "MAVEN_RESOLVER"
                )

                rtPublishBuildInfo(
                        serverId: ARTIFACTORY_SERVER
                )

                // copy resources for docker build
                sh '''
                    cp "${WORKSPACE}/target/tracking-service.jar" "${WORKSPACE}/openshift/"
                '''
            }
        }

        stage('Update templates') {
            steps {
                checkout changelog: false,
                        poll: false,
                        scm: [$class                           : 'GitSCM',
                              branches                         : [[name: '*/master']],
                              doGenerateSubmoduleConfigurations: false,
                              extensions                       : [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'ocp-commons']],
                              submoduleCfg                     : [],
                              userRemoteConfigs                : [[credentialsId: 'cicd-alvchbot-at-github',
                                                                   url          : 'https://github.com/alv-ch/ocp-commons.git']]]
                script {
                    openshift.withCluster() {
                        openshift.withProject('jobroom-dev') {
                            def templateNames = [
                                    'microservice-app-build-config-docker-template',
                            ]

                            templateNames.each {
                                openshift.apply(readFile("${WORKSPACE}/ocp-commons/templates/${it}.yml"))
                            }
                        }
                    }
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject("${NAMESPACE_NAME}") {
                            def microserviceAppBuildConfigDockerTemplate = openshift.selector("template", "microservice-app-build-config-docker-template").object()
                            openshift.apply(openshift.process(microserviceAppBuildConfigDockerTemplate, [
                                    "-p", "NAMESPACE=${NAMESPACE_NAME}",
                                    "-p", "MICROSERVICE_PROJECT_NAME=${PROJECT_NAME}",
                                    "-p", "APPLICATION_NAME=${APP_NAME}",
                            ]))

                            def build = openshift.selector('bc', 'tracking-service-docker').startBuild("--from-dir ./openshift/")
                            result = build.logs('-f')

                            if (result.status == 0) {
                                return true
                            }

                            return false
                        }
                    }
                }
            }
        }

        stage('Analyse code & deploy Docker image') {
            parallel {
                stage('Sonar Analysis') {
                    when {
                        branch 'master'
                    }

                    steps {
                        echo 'Running Sonar analysis...'
                        rtMavenRun(
                                pom: 'pom.xml',
                                goals: 'sonar:sonar -Dsonar.projectKey=OnlineFormService -Dsonar.host.url="$SONAR_SERVER" -Dsonar.login=$SONAR_LOGIN',
                                deployerId: "MAVEN_DEPLOYER",
                                resolverId: "MAVEN_RESOLVER"
                        )
                    }
                }

                stage('Deploy Docker Image') {
                    when {
                        branch 'feature/openshift'
                    }

                    steps {
                        script {
                            openshift.withCluster() {
                                openshift.withProject("${NAMESPACE_NAME}") {
                                    def microServiceDeploymentConfig = openshift.selector('dc', "${PROJECT_NAME}")
                                    if (!microServiceDeploymentConfig.exists()) {
                                        openshift.apply(readFile("deployment-config.yml"))

                                    }
                                    timeout(5) {
                                        microServiceDeploymentConfig.rollout().latest()
                                        microServiceDeploymentConfig.rollout().status()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    post('Publish Test Reports') {
        always {
            junit allowEmptyResults: true, testResults: '**/surefire-reports/**/*.xml'
            junit allowEmptyResults: true, testResults: '**/failsafe-reports/**/*.xml'
        }
    }
}
