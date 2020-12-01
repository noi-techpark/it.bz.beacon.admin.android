package it.bz.beacon.adminapp.data.event;

import it.bz.beacon.adminapp.data.entity.Info;

public interface LoadInfoEvent {
    void onSuccess(Info info);

    void onError();
}
