// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.entity;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Entity
public class Info {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({STATUS_PLANNED, STATUS_INSTALLED})
    public @interface FilterStatus {
    }

    public static final String STATUS_PLANNED = "PLANNED";
    public static final String STATUS_INSTALLED = "INSTALLED";

    @NonNull
    @PrimaryKey
    private String id;
    private String name;
    private String address;
    private String floor;
    private String cap;
    private String location;
    private Double latitude;
    private Double longitude;
    private Integer batteryLevel;
    private Long trustedUpdatedAt;
    private String uuid;
    private String namespace;
    private String instanceId;
    private Integer major;
    private Integer minor;
    private Integer txPower;
    private String status;
    private Boolean online;

    public Info() {
    }

    public Info(it.bz.beacon.adminapp.swagger.client.model.Info remoteInfo) {
        this.setId(remoteInfo.getId());
        this.setName(remoteInfo.getName());
        this.setAddress(remoteInfo.getAddress());
        this.setFloor(remoteInfo.getFloor());
        this.setCap(remoteInfo.getCap());
        this.setLocation(remoteInfo.getLocation());
        this.setLatitude(remoteInfo.getLatitude());
        this.setLongitude(remoteInfo.getLongitude());
        this.setBatteryLevel(remoteInfo.getBatteryLevel());
        this.setTrustedUpdatedAt(remoteInfo.getTrustedUpdatedAt());
        if (remoteInfo.getUuid() != null) {
            this.setUuid(remoteInfo.getUuid().toString());
        }
        this.setNamespace(remoteInfo.getNamespace());
        this.setInstanceId(remoteInfo.getInstanceId());
        this.setMajor(remoteInfo.getMajor());
        this.setMinor(remoteInfo.getMinor());
        this.setTxPower(remoteInfo.getTxPower());
        if (remoteInfo.getStatus() != null) {
            this.setStatus(remoteInfo.getStatus().getValue());
        }
        this.setOnline(remoteInfo.isOnline());
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Long getTrustedUpdatedAt() {
        return trustedUpdatedAt;
    }

    public void setTrustedUpdatedAt(Long trustedUpdatedAt) {
        this.trustedUpdatedAt = trustedUpdatedAt;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public Integer getTxPower() {
        return txPower;
    }

    public void setTxPower(Integer txPower) {
        this.txPower = txPower;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(@FilterStatus String status) {
        this.status = status;
    }

    public Boolean isOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }
}
