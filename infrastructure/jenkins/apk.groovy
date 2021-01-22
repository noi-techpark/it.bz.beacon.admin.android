pipeline {
    agent {
        node {
            label 'mac'
        }
    }
    options {
        ansiColor('xterm')
    }
    environment {
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')

        GOOGLE_MAPS_API_KEY = credentials('beacon-admin-android-google-maps-api-key')
        BEACON_SUEDTIROL_API_URL = "https://api.beacon.testingmachine.eu"
        KONTAKT_IO_API_KEY = credentials('beacon-admin-android-kontakt-io-api-key')
        API_TRUSTED_USER = credentials('beacon-admin-android-testapi-trusted-user')
        API_TRUSTED_PASSWORD = credentials('beacon-admin-android-testapi-trusted-password')
        GOOGLE_SERVICES_JSON_FILE = credentials('beacon-admin-android-google-services-json')
        KEYSTORE_FILE = credentials('beacon-admin-android-keystore-file')
        KEYSTORE_PASSWORD = credentials('beacon-admin-android-keystore-password')
        KEYSTORE_ALIAS = credentials('beacon-admin-android-keystore-alias')
        KEYSTORE_ALIAS_PASSWORD = credentials('beacon-admin-android-keystore-alias-password')
        SUPPLY_JSON_KEY = credentials('beacon-admin-android-fastlane-google-play-api-key')
        APP_NAME_FULL = "Beacon Administration (Testing)"
        APP_NAME = "BeaconsTest"
    }
    stages {
        stage('Configure') {
            steps {
                sh '''
                    cp config/google_maps_api.xml app/src/release/res/values/google_maps_api.xml
                    cp config/google_maps_api.xml app/src/debug/res/values/google_maps_api.xml
                    sed -i "" "s%GOOGLE_MAPS_API_KEY%${GOOGLE_MAPS_API_KEY}%" app/src/release/res/values/google_maps_api.xml
                    sed -i "" "s%GOOGLE_MAPS_API_KEY%${GOOGLE_MAPS_API_KEY}%" app/src/debug/res/values/google_maps_api.xml

                    cp config/beacon_suedtirol_api.xml app/src/release/res/values/beacon_suedtirol_api.xml
                    cp config/beacon_suedtirol_api.xml app/src/debug/res/values/beacon_suedtirol_api.xml
                    sed -i "" "s%BEACON_SUEDTIROL_API_URL%${BEACON_SUEDTIROL_API_URL}%" app/src/release/res/values/beacon_suedtirol_api.xml
                    sed -i "" "s%BEACON_SUEDTIROL_API_URL%${BEACON_SUEDTIROL_API_URL}%" app/src/debug/res/values/beacon_suedtirol_api.xml
                    sed -i "" "s%KONTAKT_IO_API_KEY%${KONTAKT_IO_API_KEY}%" app/src/release/res/values/beacon_suedtirol_api.xml
                    sed -i "" "s%KONTAKT_IO_API_KEY%${KONTAKT_IO_API_KEY}%" app/src/debug/res/values/beacon_suedtirol_api.xml
                    sed -i "" "s%API_TRUSTED_USER%${API_TRUSTED_USER}%" app/src/release/res/values/beacon_suedtirol_api.xml
                    sed -i "" "s%API_TRUSTED_USER%${API_TRUSTED_USER}%" app/src/debug/res/values/beacon_suedtirol_api.xml
                    sed -i "" "s%API_TRUSTED_PASSWORD%${API_TRUSTED_PASSWORD}%" app/src/release/res/values/beacon_suedtirol_api.xml
                    sed -i "" "s%API_TRUSTED_PASSWORD%${API_TRUSTED_PASSWORD}%" app/src/debug/res/values/beacon_suedtirol_api.xml

                    cat "${GOOGLE_SERVICES_JSON_FILE}" > app/google-services.json
                    sed -i "" "s%it.bz.beacon.admin%it.bz.beacon.admin.debug%" app/google-services.json
                    sed -i "" "s%it.bz.beacon.admin%it.bz.beacon.admin.debug%" fastlane/Appfile
                    sed -i "" "s%it.bz.beacon.admin%it.bz.beacon.admin.debug%" app/build.gradle
                    cat "${KEYSTORE_FILE}" > keystore.jks
                    cat "${KEYSTORE_FILE}" > app/keystore.jks

                    sed -i "" 's%name="beacons">Beacons%name="beacons">'${APP_NAME}'%' app/src/main/res/values/strings.xml
                    sed -i "" "s%Beacon Administration%${APP_NAME_FULL}%" app/src/main/res/values/strings.xml
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
        stage('Build') {
            steps {
                sh 'bundle exec fastlane apk'
            }
        }
        stage('Archive') {
            steps {
                sh 'mv app/build/outputs/apk/debug/app-debug.apk beacon-admin-debug-build${BUILD_NUMBER}.apk'
                archiveArtifacts artifacts: "beacon-admin-debug-build${BUILD_NUMBER}.apk", onlyIfSuccessful: true
            }
        }
        stage('Upload') {
            steps {
                s3Upload(bucket: 'it.bz.beacon.webapp-test', path: 'attic/', acl: 'PublicRead', file: "beacon-admin-debug-build${BUILD_NUMBER}.apk")
            }
        }
    }
    post {
        always {
            sh '''
                rm -rf keystore.jks
                rm -rf app/keystore.jks  
                rm -rf app/src/release/res/values/google_maps_api.xml
                rm -rf app/src/debug/res/values/google_maps_api.xml
                rm -rf app/src/release/res/values/beacon_suedtirol_api.xml
                rm -rf app/src/debug/res/values/beacon_suedtirol_api.xml
                rm -rf app/google-services.json
                rm -rf app/src/debug/google-services.json
                rm -rf beacon-admin-debug-build${BUILD_NUMBER}.apk
            '''
        }
    }
}
