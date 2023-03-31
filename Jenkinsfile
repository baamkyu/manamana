pipeline {
    agent any
    stages {
        stage("Set Variable") {
            steps {
                script {
                    IMAGE_NAME = "sh80165/crawling"
                    IMAGE_STORAGE = "https://registry.hub.docker.com"
                    IMAGE_STORAGE_CREDENTIAL = "docker-auth"
                    APPLICATION_YML_PATH = "/var/jenkins_home/workspace"
                    CONTAINER_NAME = "crawling"
                    PROJECT_DIR = "crawling/"
                    DOCKER_FILE_PATH = "crawling/"
                }
            }
        }
        stage("Copy user-env"){
            steps{
                dir("${APPLICATION_YML_PATH}"){
                    sh "pwd"
                    sh "cp -r -f crawling-env ${PROJECT_DIR}"
                }
            }
        }

        stage("Build Container Image") {
            steps {
                dir("${DOCKER_FILE_PATH}"){
                    script {
                        image = docker.build("${IMAGE_NAME}")
                    }   
                }
                
            }
        }

        stage("Push Container Image") {
            steps {
                script {

                    docker.withRegistry("", "${IMAGE_STORAGE_CREDENTIAL}") {
                        image.push("latest")
                    }
                }
            }
        }

        stage("Server Run") {
            steps {
                script {

                    // //컨테이너 확인 후 정지
                    sh "docker ps -f name=${CONTAINER_NAME} -q | xargs --no-run-if-empty docker container stop"

                    // //컨테이너 삭제
                    sh "docker container ls -a -f name=${CONTAINER_NAME} -q | xargs -r docker container rm"

                    //기존 이미지 삭제
                    sh "docker images sh80165/crawling -q | xargs -r docker rmi -f"

                    //컨테이너 확인
                    sh "docker ps -a"

                    // 최신 이미지 PULL
                    sh "docker pull ${IMAGE_NAME}:latest"

                    // 이미지 확인
                    sh "docker images"

                    // 최신 이미지 RUN
                    sh "docker run -d --name ${CONTAINER_NAME} ${IMAGE_NAME}:latest"

                    // 컨테이너 확인 - 로그 확인용
                    sh "docker ps -a"
                }   
            }
        }
    }
}
