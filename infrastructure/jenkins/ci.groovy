pipeline {
    agent {
        node {
            label 'mac'
        }
    }
    options {
        ansiColor('xterm')
    }
    stages {
        stage('Configure') {
            steps {
                sh '''
                    cd ..
                    cp config/beacon_suedtirol_api.xml app/src/release/res/values/beacon_suedtirol_api.xml
                    cp config/beacon_suedtirol_api.xml app/src/debug/res/values/beacon_suedtirol_api.xml
                    cp config/google_maps_api.xml app/src/release/res/values/google_maps_api.xml
                    cp config/google_maps_api.xml app/src/debug/res/values/google_maps_api.xml
                    cp config/google-services.json app/google-services.json
                '''
            }
        }
        stage('Dependencies') {
            steps {
                sh '''
                    bundle install --path=vendor/bundle
                    bundle update
                '''
            }
        }
        stage('Test') {
            steps {
                sh 'bundle exec fastlane test'
            }
        }
    }
}
