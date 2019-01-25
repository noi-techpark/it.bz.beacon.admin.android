package it.bz.beacon.adminapp.event;

public class StatusFilterEvent {
    private String status;

    public StatusFilterEvent(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
