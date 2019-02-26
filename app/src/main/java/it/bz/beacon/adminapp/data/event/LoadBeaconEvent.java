package it.bz.beacon.adminapp.data.event;

import it.bz.beacon.adminapp.data.entity.Beacon;

public interface LoadBeaconEvent {
    void onSuccess(Beacon beacon);

    void onError();
}
