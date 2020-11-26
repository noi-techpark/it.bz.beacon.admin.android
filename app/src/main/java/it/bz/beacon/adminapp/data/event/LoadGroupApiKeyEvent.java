package it.bz.beacon.adminapp.data.event;

import it.bz.beacon.adminapp.data.entity.GroupApiKey;

public interface LoadGroupApiKeyEvent {
    void onSuccess(GroupApiKey groupApiKey);

    void onError();
}
