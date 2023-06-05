// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.entity;

import androidx.room.Ignore;

public class BeaconMinimal {

    private String id;
    private int batteryLevel;
    private float lat;
    private float lng;
    @Ignore
    private float provisoricLat;
    @Ignore
    private float provisoricLng;
    private int major;
    private int minor;
    private String manufacturerId;
    private String name;
    private String status;
    @Ignore
    private Integer rssi;

    public BeaconMinimal() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public float getProvisoricLat() {
        return provisoricLat;
    }

    public void setProvisoricLat(float provisoricLat) {
        this.provisoricLat = provisoricLat;
    }

    public float getProvisoricLng() {
        return provisoricLng;
    }

    public void setProvisoricLng(float provisoricLng) {
        this.provisoricLng = provisoricLng;
    }
}
