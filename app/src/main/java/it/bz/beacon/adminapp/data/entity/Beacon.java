package it.bz.beacon.adminapp.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Beacon {

    @NonNull
    @PrimaryKey
    private long id;
    private String title;
    private String description;
    private double battery;
    private boolean warning;

    public Beacon() {
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBattery() {
        return battery;
    }

    public void setBattery(double battery) {
        this.battery = battery;
    }

    public boolean hasWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }
}
