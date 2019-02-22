package it.bz.beacon.adminapp.ui.issue.map;

import com.google.android.gms.maps.model.LatLng;

import it.bz.beacon.adminapp.data.entity.IssueWithBeacon;
import it.bz.beacon.adminapp.ui.map.BaseClusterItem;

public class IssueClusterItem extends BaseClusterItem {

    private IssueWithBeacon issueWithBeacon;

    public IssueClusterItem(IssueWithBeacon issueWithBeacon) {
        this.issueWithBeacon = issueWithBeacon;
        this.id = issueWithBeacon.getId();
        this.beaconId = issueWithBeacon.getBeaconId();
        this.name = issueWithBeacon.getName();
        this.status = issueWithBeacon.getStatus();
        if (issueWithBeacon.getLat() != 0 && issueWithBeacon.getLng() != 0) {
            this.position = new LatLng(issueWithBeacon.getLat(), issueWithBeacon.getLng());
        }
    }

    @Override
    public String getSnippet() {
        return issueWithBeacon.getProblem();
    }
}
