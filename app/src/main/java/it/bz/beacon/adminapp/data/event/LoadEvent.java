package it.bz.beacon.adminapp.data.event;

import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.entity.IssueWithBeacon;

public interface LoadEvent {
    void onSuccessBeacon(BeaconMinimal beaconMinimal);

    void onSuccessIssue(IssueWithBeacon issueWithBeacon);
}
