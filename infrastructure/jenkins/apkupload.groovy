pipeline {
    agent any

    environment {
        AWS_ACCESS_KEY_ID = credentials('aws_secret_key_id')
        AWS_SECRET_ACCESS_KEY = credentials('aws_secret_access_key')
    }

    stages {
        stage('Clean') {
            steps {
                sh '''
                    mkdir -p build
                    rm -rf build/*
                '''
            }
        }
        stage('Load') {
            steps {
                copyArtifacts projectName: 'it.bz.beacon.admin.android/admin.android.test-apk', target: 'build', flatten: true, selector: lastSuccessful(), fingerprintArtifacts: true
            }
        }
        stage('Upload') {
            steps {
                s3Upload(bucket: 'it.bz.beacon.webapp-test', path: 'attic/', acl: 'PublicRead', file: "beacon-admin-debug-build${BUILD_NUMBER}.apk")
            }
        }
    }
}
