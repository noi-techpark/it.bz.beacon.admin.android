// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.entity;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import it.bz.beacon.adminapp.R;

@Entity
public class Beacon {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({STATUS_OK, STATUS_BATTERY_LOW, STATUS_ISSUE, STATUS_CONFIGURATION_PENDING, STATUS_UNKNOWN_STATUS, STATUS_NOT_ACCESSIBLE})
    public @interface FilterStatus {
    }

    public static final String STATUS_ALL = "ALL";
    public static final String STATUS_OK = "OK";
    public static final String STATUS_BATTERY_LOW = "BATTERY_LOW";
    public static final String STATUS_ISSUE = "ISSUE";
    public static final String STATUS_CONFIGURATION_PENDING = "CONFIGURATION_PENDING";
    public static final String STATUS_UNKNOWN_STATUS = "UNKNOWN_STATUS";
    public static final String STATUS_NOT_ACCESSIBLE = "NOT_ACCESSIBLE";
    public static final String STATUS_INSTALLED = "INSTALLED";
    public static final String STATUS_NOT_INSTALLED = "NOT_INSTALLED";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({LOCATION_OUTDOOR, LOCATION_INDOOR})
    public @interface FilterLocation {
    }

    public static final String LOCATION_OUTDOOR = "OUTDOOR";
    public static final String LOCATION_INDOOR = "INDOOR";

    @NonNull
    @PrimaryKey
    private String id;
    private Integer batteryLevel;
    private String description;
    private Boolean eddystoneEid;
    private Boolean eddystoneEtlm;
    private Boolean eddystoneTlm;
    private Boolean eddystoneUid;
    private Boolean eddystoneUrl;
    private Boolean iBeacon;
    private String instanceId;
    private Integer interval;
    private Long lastSeen;
    private Float lat;
    private Float lng;
    private String locationDescription;
    private String locationType;
    private Integer major;
    private Integer minor;
    private String manufacturer;
    private String manufacturerId;
    private String name;
    private String namespace;
    private String status;
    private Boolean telemetry;
    private Integer txPower;
    private String url;
    private String uuid;
    private String pendingConfiguration;
    private Long groupId;

    public Beacon() {
    }

    public Beacon(it.bz.beacon.adminapp.swagger.client.model.Beacon remoteBeacon) {
        this.setId(remoteBeacon.getId());
        this.setBatteryLevel(remoteBeacon.getBatteryLevel());
        this.setDescription(remoteBeacon.getDescription());
        this.setEddystoneEid(remoteBeacon.isEddystoneEid());
        this.setEddystoneEtlm(remoteBeacon.isEddystoneEtlm());
        this.setEddystoneTlm(remoteBeacon.isEddystoneTlm());
        this.setEddystoneUid(remoteBeacon.isEddystoneUid());
        this.setEddystoneUrl(remoteBeacon.isEddystoneUrl());
        this.setIBeacon(remoteBeacon.isIBeacon());
        this.setInstanceId(remoteBeacon.getInstanceId());
        this.setInterval(remoteBeacon.getInterval());
        this.setLastSeen(remoteBeacon.getLastSeen());
        this.setLat(remoteBeacon.getLat());
        this.setLng(remoteBeacon.getLng());
        this.setLocationDescription(remoteBeacon.getLocationDescription());
        if (remoteBeacon.getLocationType() != null) {
            this.setLocationType(remoteBeacon.getLocationType().getValue());
        }
        this.setMajor(remoteBeacon.getMajor());
        this.setMinor(remoteBeacon.getMinor());
        this.setManufacturer(remoteBeacon.getManufacturer().getValue());
        this.setManufacturerId(remoteBeacon.getManufacturerId());
        this.setName(remoteBeacon.getName());
        this.setNamespace(remoteBeacon.getNamespace());
        this.setStatus(remoteBeacon.getStatus().getValue());
        this.setTelemetry(remoteBeacon.isTelemetry());
        this.setTxPower(remoteBeacon.getTxPower());
        this.setUrl(remoteBeacon.getUrl());
        if(remoteBeacon.getUuid() != null) {
            this.setUuid(remoteBeacon.getUuid().toString());
        }
        if (remoteBeacon.getPendingConfiguration() != null) {
            this.setPendingConfiguration((new Gson()).toJson(remoteBeacon.getPendingConfiguration()));
        }
        else {
            this.setPendingConfiguration(null);
        }
        if(remoteBeacon.getGroup() != null) {
            this.setGroupId(remoteBeacon.getGroup().getId());
        }
    }

    public static int getMarkerId(@FilterStatus String status) {
        int drawableId;
        switch (status) {
            case Beacon.STATUS_ISSUE:
            case Beacon.STATUS_BATTERY_LOW:
                drawableId = R.drawable.marker_issue;
                break;
            case Beacon.STATUS_CONFIGURATION_PENDING:
                drawableId = R.drawable.marker_pending;
                break;
            case Beacon.STATUS_UNKNOWN_STATUS:
                drawableId = R.drawable.marker_unknownstatus;
                break;
            case Beacon.STATUS_NOT_INSTALLED:
                drawableId = R.drawable.marker_provisional;
                break;
            case Beacon.STATUS_NOT_ACCESSIBLE:
                drawableId = R.drawable.marker_not_accessible;
                break;
            default:
                drawableId = R.drawable.marker_ok;
                break;
        }
        return drawableId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isEddystoneEid() {
        return eddystoneEid;
    }

    public void setEddystoneEid(Boolean eddystoneEid) {
        this.eddystoneEid = eddystoneEid;
    }

    public Boolean isEddystoneEtlm() {
        return eddystoneEtlm;
    }

    public void setEddystoneEtlm(Boolean eddystoneEtlm) {
        this.eddystoneEtlm = eddystoneEtlm;
    }

    public Boolean isEddystoneTlm() {
        return eddystoneTlm;
    }

    public void setEddystoneTlm(Boolean eddystoneTlm) {
        this.eddystoneTlm = eddystoneTlm;
    }

    public Boolean isEddystoneUid() {
        return eddystoneUid;
    }

    public void setEddystoneUid(Boolean eddystoneUid) {
        this.eddystoneUid = eddystoneUid;
    }

    public Boolean isEddystoneUrl() {
        return eddystoneUrl;
    }

    public void setEddystoneUrl(Boolean eddystoneUrl) {
        this.eddystoneUrl = eddystoneUrl;
    }

    public Boolean isIBeacon() {
        return iBeacon;
    }

    public void setIBeacon(Boolean iBeacon) {
        this.iBeacon = iBeacon;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(@FilterLocation String locationType) {
        this.locationType = locationType;
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

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
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

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(@FilterStatus String status) {
        this.status = status;
    }

    public Boolean getTelemetry() {
        return telemetry;
    }

    public void setTelemetry(Boolean telemetry) {
        this.telemetry = telemetry;
    }

    public Integer getTxPower() {
        return txPower;
    }

    public void setTxPower(Integer txPower) {
        this.txPower = txPower;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPendingConfiguration() {
        return pendingConfiguration;
    }

    public void setPendingConfiguration(String pendingConfiguration) {
        this.pendingConfiguration = pendingConfiguration;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
