pipeline {
    agent {
        node {
            label 'mac'
        }
    }
    parameters {
        string(name: 'VERSION_NAME', defaultValue: '1.0.0', description: 'App Version Name')
    }
    options {
        ansiColor('xterm')
    }
    environment {
        GOOGLE_MAPS_API_KEY = credentials('beacon-admin-android-google-maps-api-key')
        BEACON_SUEDTIROL_API_URL = "https://api.beacon.bz.it"
        KONTAKT_IO_API_KEY = credentials('beacon-admin-android-kontakt-io-api-key')
        API_TRUSTED_USER = credentials('beacon-admin-android-api-trusted-user')
        API_TRUSTED_PASSWORD = credentials('beacon-trusted-nonencrypted-password')
        GOOGLE_SERVICES_JSON_FILE = credentials('beacon-admin-android-google-services-json')
        KEYSTORE_FILE = credentials('beacon-admin-android-keystore-file')
        KEYSTORE_PASSWORD = credentials('beacon-admin-android-keystore-password')
        KEYSTORE_ALIAS = credentials('beacon-admin-android-keystore-alias')
        KEYSTORE_ALIAS_PASSWORD = credentials('beacon-admin-android-keystore-alias-password')
        SUPPLY_JSON_KEY = credentials('beacon-admin-android-fastlane-google-play-api-key')
        GIT_REPOSITORY = "git@github.com:idm-suedtirol/beacon-suedtirol-administration-android.git"

        MAIL = "info@opendatahub.bz.it"
        GITHUB_URL = "https://github.com/noi-techpark/beacon-suedtirol-administration-android"
        APP_TITLE = "Beacon Südtirol/Alto Adige Administration Android App"
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
                    cat "${KEYSTORE_FILE}" > keystore.jks
                    cat "${KEYSTORE_FILE}" > app/keystore.jks
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
        stage('Build & Deploy') {
            steps {
                sh 'bundle exec fastlane alpha'
            }
        }
        stage('Version Bump') {
            steps {
                sshagent (credentials: ['jenkins_github_ssh_key']) {
                    sh '''
                        git remote set-url origin ${GIT_REPOSITORY}
                        git add app/build.gradle
                        git commit -m "Version increment"
                        git push origin HEAD:master
                    '''
                }
            }
        }
        stage('Send Email') {
            steps {
                script {

                    title = "${APP_TITLE}"

                    body  = "-----------------------------------------------------\n"
                    body += title + "\n"
                    body += "-----------------------------------------------------\n\n"
                    body += "Hi all,\nwe are proud to announce that v${VERSION_NAME} of the\n"
                    body += "Beacon Android Administration App [1] has been\n"
                    body += "released.\n\n"
                    body += "Cheers,\n"
                    body += "The Open Data Hub Team\n\n\n"
                    body += "----\n"
                    body += "[1] ${GITHUB_URL}"

                    mail to: "${MAIL}",
                        subject: title + " - Version ${VERSION_NAME} released",
                        body: body
                }
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
            '''
        }
    }
}
