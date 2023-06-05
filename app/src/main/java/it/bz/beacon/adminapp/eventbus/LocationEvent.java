// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.eventbus;

import com.google.android.gms.maps.model.LatLng;

public class LocationEvent {
    private LatLng location;

    public LocationEvent(LatLng location) {
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }
}
