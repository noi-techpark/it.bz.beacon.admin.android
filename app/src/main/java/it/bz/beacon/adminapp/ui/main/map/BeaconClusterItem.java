package it.bz.beacon.adminapp.ui.main.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;

public class BeaconClusterItem implements ClusterItem {

    private LatLng position;
    private BeaconMinimal beaconMinimal;

    public BeaconClusterItem(BeaconMinimal beaconMinimal) {
        this.beaconMinimal = beaconMinimal;
        if (beaconMinimal.getLat() != 0 && beaconMinimal.getLng() != 0) {
            this.position = new LatLng(beaconMinimal.getLat(), beaconMinimal.getLng());
        }
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return beaconMinimal.getName();
    }

    @Override
    public String getSnippet() {
        int major = beaconMinimal.getMajor();
        int minor = beaconMinimal.getMinor();

        String manufacturerId = beaconMinimal.getManufacturerId();
        return manufacturerId + System.getProperty("line.separator") + "Major: " + major + System.getProperty("line.separator") + "Minor: " + minor;
    }

    public BeaconMinimal getBeaconMinimal() {
        return beaconMinimal;
    }
}
