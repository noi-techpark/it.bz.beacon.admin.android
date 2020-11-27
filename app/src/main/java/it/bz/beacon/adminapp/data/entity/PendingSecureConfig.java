package it.bz.beacon.adminapp.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PendingSecureConfig {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String config;
    private String apiKey;
    private String beaconId;

    public PendingSecureConfig() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }
}
