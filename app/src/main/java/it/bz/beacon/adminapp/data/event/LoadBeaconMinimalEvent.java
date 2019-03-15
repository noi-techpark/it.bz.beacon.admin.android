package it.bz.beacon.adminapp.data.event;

import it.bz.beacon.adminapp.data.entity.BeaconMinimal;

public interface LoadBeaconMinimalEvent {
    void onSuccess(BeaconMinimal beaconMinimal);

    void onError();
}
