#!/usr/bin/env groovy

node {
    stage('checkout') {
        checkout scm
    }

    stage('check java') {
        bat "dir"
        bat "java -version"
    }

    stage('clean') {
        bat "mvn clean"
    }

    stage('deploy') {
        bat "mvn deploy -Pprod -DskipTests"
    }
}
