// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.util;

import it.bz.beacon.adminapp.data.entity.BeaconImage;

public interface OnImagesDownloadedCallback {
    void onSuccess(BeaconImage beaconImage);
    void onFailure(Exception e);
}
