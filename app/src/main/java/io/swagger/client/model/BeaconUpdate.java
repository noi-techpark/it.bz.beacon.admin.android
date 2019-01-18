/*
 * Beacon Südtirol API
 * An API to manage beacons of the Beacon Südtirol project.
 *
 * OpenAPI spec version: 1.0-beta
 * Contact: web@raiffeisen.net
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import java.util.UUID;

/**
 * BeaconUpdate
 */

public class BeaconUpdate {
  @SerializedName("description")
  private String description = null;

  @SerializedName("eddystone")
  private Boolean eddystone = null;

  @SerializedName("eddystoneTlm")
  private Boolean eddystoneTlm = null;

  @SerializedName("eddystoneUrl")
  private Boolean eddystoneUrl = null;

  @SerializedName("iBeacon")
  private Boolean iBeacon = null;

  @SerializedName("instanceId")
  private String instanceId = null;

  @SerializedName("interval")
  private Integer interval = null;

  @SerializedName("lat")
  private Float lat = null;

  @SerializedName("lng")
  private Float lng = null;

  @SerializedName("locationDescription")
  private String locationDescription = null;

  /**
   * Gets or Sets locationType
   */
  @JsonAdapter(LocationTypeEnum.Adapter.class)
  public enum LocationTypeEnum {
    OUTDOOR("OUTDOOR"),
    
    INDOOR("INDOOR");

    private String value;

    LocationTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static LocationTypeEnum fromValue(String text) {
      for (LocationTypeEnum b : LocationTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<LocationTypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final LocationTypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public LocationTypeEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return LocationTypeEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("locationType")
  private LocationTypeEnum locationType = null;

  @SerializedName("major")
  private Integer major = null;

  @SerializedName("minor")
  private Integer minor = null;

  @SerializedName("name")
  private String name = null;

  @SerializedName("namespace")
  private String namespace = null;

  @SerializedName("txPower")
  private Integer txPower = null;

  @SerializedName("url")
  private String url = null;

  @SerializedName("uuid")
  private UUID uuid = null;

  public BeaconUpdate description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  @ApiModelProperty(value = "")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BeaconUpdate eddystone(Boolean eddystone) {
    this.eddystone = eddystone;
    return this;
  }

   /**
   * Get eddystone
   * @return eddystone
  **/
  @ApiModelProperty(value = "")
  public Boolean isEddystone() {
    return eddystone;
  }

  public void setEddystone(Boolean eddystone) {
    this.eddystone = eddystone;
  }

  public BeaconUpdate eddystoneTlm(Boolean eddystoneTlm) {
    this.eddystoneTlm = eddystoneTlm;
    return this;
  }

   /**
   * Get eddystoneTlm
   * @return eddystoneTlm
  **/
  @ApiModelProperty(value = "")
  public Boolean isEddystoneTlm() {
    return eddystoneTlm;
  }

  public void setEddystoneTlm(Boolean eddystoneTlm) {
    this.eddystoneTlm = eddystoneTlm;
  }

  public BeaconUpdate eddystoneUrl(Boolean eddystoneUrl) {
    this.eddystoneUrl = eddystoneUrl;
    return this;
  }

   /**
   * Get eddystoneUrl
   * @return eddystoneUrl
  **/
  @ApiModelProperty(value = "")
  public Boolean isEddystoneUrl() {
    return eddystoneUrl;
  }

  public void setEddystoneUrl(Boolean eddystoneUrl) {
    this.eddystoneUrl = eddystoneUrl;
  }

  public BeaconUpdate iBeacon(Boolean iBeacon) {
    this.iBeacon = iBeacon;
    return this;
  }

   /**
   * Get iBeacon
   * @return iBeacon
  **/
  @ApiModelProperty(value = "")
  public Boolean isIBeacon() {
    return iBeacon;
  }

  public void setIBeacon(Boolean iBeacon) {
    this.iBeacon = iBeacon;
  }

  public BeaconUpdate instanceId(String instanceId) {
    this.instanceId = instanceId;
    return this;
  }

   /**
   * Get instanceId
   * @return instanceId
  **/
  @ApiModelProperty(value = "")
  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public BeaconUpdate interval(Integer interval) {
    this.interval = interval;
    return this;
  }

   /**
   * Get interval
   * minimum: 100
   * maximum: 10240
   * @return interval
  **/
  @ApiModelProperty(value = "")
  public Integer getInterval() {
    return interval;
  }

  public void setInterval(Integer interval) {
    this.interval = interval;
  }

  public BeaconUpdate lat(Float lat) {
    this.lat = lat;
    return this;
  }

   /**
   * Get lat
   * @return lat
  **/
  @ApiModelProperty(value = "")
  public Float getLat() {
    return lat;
  }

  public void setLat(Float lat) {
    this.lat = lat;
  }

  public BeaconUpdate lng(Float lng) {
    this.lng = lng;
    return this;
  }

   /**
   * Get lng
   * @return lng
  **/
  @ApiModelProperty(value = "")
  public Float getLng() {
    return lng;
  }

  public void setLng(Float lng) {
    this.lng = lng;
  }

  public BeaconUpdate locationDescription(String locationDescription) {
    this.locationDescription = locationDescription;
    return this;
  }

   /**
   * Get locationDescription
   * @return locationDescription
  **/
  @ApiModelProperty(value = "")
  public String getLocationDescription() {
    return locationDescription;
  }

  public void setLocationDescription(String locationDescription) {
    this.locationDescription = locationDescription;
  }

  public BeaconUpdate locationType(LocationTypeEnum locationType) {
    this.locationType = locationType;
    return this;
  }

   /**
   * Get locationType
   * @return locationType
  **/
  @ApiModelProperty(value = "")
  public LocationTypeEnum getLocationType() {
    return locationType;
  }

  public void setLocationType(LocationTypeEnum locationType) {
    this.locationType = locationType;
  }

  public BeaconUpdate major(Integer major) {
    this.major = major;
    return this;
  }

   /**
   * Get major
   * minimum: 0
   * maximum: 65535
   * @return major
  **/
  @ApiModelProperty(value = "")
  public Integer getMajor() {
    return major;
  }

  public void setMajor(Integer major) {
    this.major = major;
  }

  public BeaconUpdate minor(Integer minor) {
    this.minor = minor;
    return this;
  }

   /**
   * Get minor
   * minimum: 0
   * maximum: 65535
   * @return minor
  **/
  @ApiModelProperty(value = "")
  public Integer getMinor() {
    return minor;
  }

  public void setMinor(Integer minor) {
    this.minor = minor;
  }

  public BeaconUpdate name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @ApiModelProperty(value = "")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BeaconUpdate namespace(String namespace) {
    this.namespace = namespace;
    return this;
  }

   /**
   * Get namespace
   * @return namespace
  **/
  @ApiModelProperty(value = "")
  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public BeaconUpdate txPower(Integer txPower) {
    this.txPower = txPower;
    return this;
  }

   /**
   * Get txPower
   * minimum: 1
   * maximum: 7
   * @return txPower
  **/
  @ApiModelProperty(value = "")
  public Integer getTxPower() {
    return txPower;
  }

  public void setTxPower(Integer txPower) {
    this.txPower = txPower;
  }

  public BeaconUpdate url(String url) {
    this.url = url;
    return this;
  }

   /**
   * Get url
   * @return url
  **/
  @ApiModelProperty(value = "")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public BeaconUpdate uuid(UUID uuid) {
    this.uuid = uuid;
    return this;
  }

   /**
   * Get uuid
   * @return uuid
  **/
  @ApiModelProperty(value = "")
  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BeaconUpdate beaconUpdate = (BeaconUpdate) o;
    return Objects.equals(this.description, beaconUpdate.description) &&
        Objects.equals(this.eddystone, beaconUpdate.eddystone) &&
        Objects.equals(this.eddystoneTlm, beaconUpdate.eddystoneTlm) &&
        Objects.equals(this.eddystoneUrl, beaconUpdate.eddystoneUrl) &&
        Objects.equals(this.iBeacon, beaconUpdate.iBeacon) &&
        Objects.equals(this.instanceId, beaconUpdate.instanceId) &&
        Objects.equals(this.interval, beaconUpdate.interval) &&
        Objects.equals(this.lat, beaconUpdate.lat) &&
        Objects.equals(this.lng, beaconUpdate.lng) &&
        Objects.equals(this.locationDescription, beaconUpdate.locationDescription) &&
        Objects.equals(this.locationType, beaconUpdate.locationType) &&
        Objects.equals(this.major, beaconUpdate.major) &&
        Objects.equals(this.minor, beaconUpdate.minor) &&
        Objects.equals(this.name, beaconUpdate.name) &&
        Objects.equals(this.namespace, beaconUpdate.namespace) &&
        Objects.equals(this.txPower, beaconUpdate.txPower) &&
        Objects.equals(this.url, beaconUpdate.url) &&
        Objects.equals(this.uuid, beaconUpdate.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, eddystone, eddystoneTlm, eddystoneUrl, iBeacon, instanceId, interval, lat, lng, locationDescription, locationType, major, minor, name, namespace, txPower, url, uuid);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BeaconUpdate {\n");
    
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    eddystone: ").append(toIndentedString(eddystone)).append("\n");
    sb.append("    eddystoneTlm: ").append(toIndentedString(eddystoneTlm)).append("\n");
    sb.append("    eddystoneUrl: ").append(toIndentedString(eddystoneUrl)).append("\n");
    sb.append("    iBeacon: ").append(toIndentedString(iBeacon)).append("\n");
    sb.append("    instanceId: ").append(toIndentedString(instanceId)).append("\n");
    sb.append("    interval: ").append(toIndentedString(interval)).append("\n");
    sb.append("    lat: ").append(toIndentedString(lat)).append("\n");
    sb.append("    lng: ").append(toIndentedString(lng)).append("\n");
    sb.append("    locationDescription: ").append(toIndentedString(locationDescription)).append("\n");
    sb.append("    locationType: ").append(toIndentedString(locationType)).append("\n");
    sb.append("    major: ").append(toIndentedString(major)).append("\n");
    sb.append("    minor: ").append(toIndentedString(minor)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    namespace: ").append(toIndentedString(namespace)).append("\n");
    sb.append("    txPower: ").append(toIndentedString(txPower)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

