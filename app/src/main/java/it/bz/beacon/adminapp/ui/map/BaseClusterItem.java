package it.bz.beacon.adminapp.ui.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import it.bz.beacon.adminapp.data.entity.BeaconMinimal;

public abstract class BaseClusterItem implements ClusterItem {

    protected LatLng position;
    protected long id;
    protected String beaconId;
    protected String name;
    protected String status;

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public abstract String getSnippet();
}
