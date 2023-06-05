// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.ui.main.beacon.map;

import com.google.android.gms.maps.model.LatLng;

import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.ui.map.BaseClusterItem;

public class BeaconClusterItem extends BaseClusterItem {

    private BeaconMinimal beaconMinimal;

    public BeaconClusterItem(BeaconMinimal beaconMinimal) {
        this.beaconMinimal = beaconMinimal;
        this.beaconId = beaconMinimal.getId();
        this.name = beaconMinimal.getName();
        if (beaconMinimal.getLat() == 0 && beaconMinimal.getLng() == 0) {
            this.status = Beacon.STATUS_NOT_INSTALLED;
        }
        else {
            this.status = beaconMinimal.getStatus();
        }

        if (beaconMinimal.getStatus().equals(Beacon.STATUS_NOT_INSTALLED)
                && beaconMinimal.getProvisoricLat() != 0 && beaconMinimal.getProvisoricLng() != 0) {
            this.position = new LatLng(beaconMinimal.getProvisoricLat(), beaconMinimal.getProvisoricLng());
        } else if (beaconMinimal.getLat() != 0 && beaconMinimal.getLng() != 0) {
            this.position = new LatLng(beaconMinimal.getLat(), beaconMinimal.getLng());
        }
    }

    @Override
    public String getSnippet() {
        int major = beaconMinimal.getMajor();
        int minor = beaconMinimal.getMinor();

        return "Major: " + major + System.getProperty("line.separator") + "Minor: " + minor;
    }
}
