package it.bz.beacon.adminapp.eventbus;

public class RadiusFilterEvent {
    private int radius;

    public RadiusFilterEvent(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }
}
