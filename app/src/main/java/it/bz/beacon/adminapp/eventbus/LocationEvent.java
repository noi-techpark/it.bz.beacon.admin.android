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
