# Beacon Südtirol Administration App

This project contains the Android app for administrators, to control and configure beacons.

## Table of contents

- [Getting started](#getting-started)
- [Running tests](#running-tests)
- [Deployment](#deployment)
- [Information](#information)

## Getting started

These instructions will get you a copy of the project up and running
on your local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

* IDE (Android Studio recommended)
* Android SDK 28 (Android 9.0)
* Google Maps API-Key
* Kontakt.io API-Key
* keystore.keystore (for security reasons this file is not public accessable, ask us to get it)

### Source code

Get a copy of the repository:

```bash
git clone https://github.com/raiffeisennet/beacon-suedtirol-administration-android.git
```

### Configure the project

* Copy config/google_maps_api.xml to app/src/debug/res/values/google_maps_api.xml and fill required values
* Copy config/google_maps_api.xml to app/src/release/res/values/google_maps_api.xml and fill required values
* Copy config/beacon_suedtirol_api.xml to app/src/debug/res/values/beacon_suedtirol_api.xml and fill required values
* Copy config/beacon_suedtirol_api.xml to app/src/release/res/values/beacon_suedtirol_api.xml and fill required values
* If you own credentials for trusted api, fill them in too
* Add keystore.keystore

## Running tests

The unit tests can be executed with the following command:

```bash

```

## Deployment

ToDo: A detailed description about how the application must be deployed.


## Information

### Support

For support, please contact [info@opendatahub.bz.it](mailto:info@opendatahub.bz.it).

### Contributing

If you'd like to contribute, please follow the following instructions:

- Fork the repository.

- Checkout a topic branch from the `development` branch.

- Make sure the tests are passing.

- Create a pull request against the `development` branch.

### Documentation

More documentation can be found at [https://opendatahub.readthedocs.io/en/latest/index.html](https://opendatahub.readthedocs.io/en/latest/index.html).

### License

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE 3.0 or later license.
See the LICENSE.md file for more information.
