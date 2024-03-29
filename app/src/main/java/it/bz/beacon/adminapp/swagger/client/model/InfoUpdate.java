// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

/*
 * Beacon Suedtirol API
 * The API for the Beacon Suedtirol project for configuring beacons and accessing beacon data.
 *
 * OpenAPI spec version: 0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package it.bz.beacon.adminapp.swagger.client.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;

/**
 * InfoUpdate
 */

public class InfoUpdate {
  @SerializedName("address")
  private String address = null;

  @SerializedName("cap")
  private String cap = null;

  @SerializedName("floor")
  private String floor = null;

  @SerializedName("latitude")
  private Float latitude = null;

  @SerializedName("location")
  private String location = null;

  @SerializedName("longitude")
  private Float longitude = null;

  @SerializedName("name")
  private String name = null;

  public InfoUpdate address(String address) {
    this.address = address;
    return this;
  }

   /**
   * Get address
   * @return address
  **/
  @ApiModelProperty(value = "")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public InfoUpdate cap(String cap) {
    this.cap = cap;
    return this;
  }

   /**
   * Get cap
   * @return cap
  **/
  @ApiModelProperty(value = "")
  public String getCap() {
    return cap;
  }

  public void setCap(String cap) {
    this.cap = cap;
  }

  public InfoUpdate floor(String floor) {
    this.floor = floor;
    return this;
  }

   /**
   * Get floor
   * @return floor
  **/
  @ApiModelProperty(value = "")
  public String getFloor() {
    return floor;
  }

  public void setFloor(String floor) {
    this.floor = floor;
  }

  public InfoUpdate latitude(Float latitude) {
    this.latitude = latitude;
    return this;
  }

   /**
   * Get latitude
   * minimum: -90.0
   * maximum: 90.0
   * @return latitude
  **/
  @ApiModelProperty(required = true, value = "")
  public Float getLatitude() {
    return latitude;
  }

  public void setLatitude(Float latitude) {
    this.latitude = latitude;
  }

  public InfoUpdate location(String location) {
    this.location = location;
    return this;
  }

   /**
   * Get location
   * @return location
  **/
  @ApiModelProperty(value = "")
  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public InfoUpdate longitude(Float longitude) {
    this.longitude = longitude;
    return this;
  }

   /**
   * Get longitude
   * minimum: -180.0
   * maximum: 180.0
   * @return longitude
  **/
  @ApiModelProperty(required = true, value = "")
  public Float getLongitude() {
    return longitude;
  }

  public void setLongitude(Float longitude) {
    this.longitude = longitude;
  }

  public InfoUpdate name(String name) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InfoUpdate infoUpdate = (InfoUpdate) o;
    return Objects.equals(this.address, infoUpdate.address) &&
        Objects.equals(this.cap, infoUpdate.cap) &&
        Objects.equals(this.floor, infoUpdate.floor) &&
        Objects.equals(this.latitude, infoUpdate.latitude) &&
        Objects.equals(this.location, infoUpdate.location) &&
        Objects.equals(this.longitude, infoUpdate.longitude) &&
        Objects.equals(this.name, infoUpdate.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, cap, floor, latitude, location, longitude, name);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InfoUpdate {\n");
    
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    cap: ").append(toIndentedString(cap)).append("\n");
    sb.append("    floor: ").append(toIndentedString(floor)).append("\n");
    sb.append("    latitude: ").append(toIndentedString(latitude)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
    sb.append("    longitude: ").append(toIndentedString(longitude)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

