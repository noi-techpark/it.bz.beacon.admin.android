package it.bz.beacon.adminapp.data.event;

import it.bz.beacon.adminapp.data.entity.BeaconMinimal;

public interface LoadEvent {
    void onSuccess(BeaconMinimal beaconMinimal);
}
