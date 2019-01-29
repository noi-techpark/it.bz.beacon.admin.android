package it.bz.beacon.adminapp.eventbus;

public class StatusFilterEvent {
    private String status;

    public StatusFilterEvent(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
