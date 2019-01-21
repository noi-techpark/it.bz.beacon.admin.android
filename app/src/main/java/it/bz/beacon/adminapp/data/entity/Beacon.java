package it.bz.beacon.adminapp.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Entity
public class Beacon {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({STATUS_OK, STATUS_BATTERY_LOW, STATUS_CONFIGURATION_PENDING})
    public @interface FilterStatus {}

    public static final String STATUS_OK = "OK";
    public static final String STATUS_BATTERY_LOW = "BATTERY_LOW";
    public static final String STATUS_CONFIGURATION_PENDING = "CONFIGURATION_PENDING";;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({LOCATION_OUTDOOR, LOCATION_INDOOR})
    public @interface FilterLocation {}

    public static final String LOCATION_OUTDOOR = "OUTDOOR";
    public static final String LOCATION_INDOOR = "INDOOR";

    @NonNull
    @PrimaryKey
    private long id;
    private int batteryLevel;
    private String description;
    private boolean eddystoneEid;
    private boolean eddystoneEtlm;
    private boolean eddystoneTlm;
    private boolean eddystoneUid;
    private boolean eddystoneUrl;
    private boolean iBeacon;
    private String instanceId;
    private int interval;
    private long lastSeen;
    private float lat;
    private float lng;
    private String locationDescription;
    private String locationType;
    private int major;
    private int minor;
    private String manufacturer;
    private String manufacturerId;
    private String name;
    private String namespace;
    private String status;
    private String telemetry;
    private int txPower;
    private String url;
    private String uuid;

    public Beacon() {
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEddystoneEid() {
        return eddystoneEid;
    }

    public void setEddystoneEid(boolean eddystoneEid) {
        this.eddystoneEid = eddystoneEid;
    }

    public boolean isEddystoneEtlm() {
        return eddystoneEtlm;
    }

    public void setEddystoneEtlm(boolean eddystoneEtlm) {
        this.eddystoneEtlm = eddystoneEtlm;
    }

    public boolean isEddystoneTlm() {
        return eddystoneTlm;
    }

    public void setEddystoneTlm(boolean eddystoneTlm) {
        this.eddystoneTlm = eddystoneTlm;
    }

    public boolean isEddystoneUid() {
        return eddystoneUid;
    }

    public void setEddystoneUid(boolean eddystoneUid) {
        this.eddystoneUid = eddystoneUid;
    }

    public boolean isEddystoneUrl() {
        return eddystoneUrl;
    }

    public void setEddystoneUrl(boolean eddystoneUrl) {
        this.eddystoneUrl = eddystoneUrl;
    }

    public boolean isIBeacon() {
        return iBeacon;
    }

    public void setIBeacon(boolean iBeacon) {
        this.iBeacon = iBeacon;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
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

    public String getTelemetry() {
        return telemetry;
    }

    public void setTelemetry(String telemetry) {
        this.telemetry = telemetry;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
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
}
