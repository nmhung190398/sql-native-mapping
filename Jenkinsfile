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
        bat "mvnw clean"
    }

    stage('deploy') {
        bat "mvnw deploy -Pprod -DskipTests"
    }
}
