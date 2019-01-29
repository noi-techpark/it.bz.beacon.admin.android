package it.bz.beacon.adminapp.data.event;

public interface DataUpdateEvent {
    void onSuccess();
    void onError();
    void onAuthenticationFailed();
}
