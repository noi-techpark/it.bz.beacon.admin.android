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
import it.bz.beacon.adminapp.swagger.client.model.UserRoleGroup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User
 */

public class User {
  @SerializedName("admin")
  private Boolean admin = null;

  @SerializedName("email")
  private String email = null;

  @SerializedName("groups")
  private List<UserRoleGroup> groups = null;

  @SerializedName("id")
  private Long id = null;

  @SerializedName("name")
  private String name = null;

  @SerializedName("surname")
  private String surname = null;

  @SerializedName("username")
  private String username = null;

  public User admin(Boolean admin) {
    this.admin = admin;
    return this;
  }

   /**
   * Get admin
   * @return admin
  **/
  @ApiModelProperty(value = "")
  public Boolean isAdmin() {
    return admin;
  }

  public void setAdmin(Boolean admin) {
    this.admin = admin;
  }

  public User email(String email) {
    this.email = email;
    return this;
  }

   /**
   * Get email
   * @return email
  **/
  @ApiModelProperty(value = "")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public User groups(List<UserRoleGroup> groups) {
    this.groups = groups;
    return this;
  }

  public User addGroupsItem(UserRoleGroup groupsItem) {
    if (this.groups == null) {
      this.groups = new ArrayList<UserRoleGroup>();
    }
    this.groups.add(groupsItem);
    return this;
  }

   /**
   * Get groups
   * @return groups
  **/
  @ApiModelProperty(value = "")
  public List<UserRoleGroup> getGroups() {
    return groups;
  }

  public void setGroups(List<UserRoleGroup> groups) {
    this.groups = groups;
  }

  public User id(Long id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @ApiModelProperty(value = "")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User name(String name) {
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

  public User surname(String surname) {
    this.surname = surname;
    return this;
  }

   /**
   * Get surname
   * @return surname
  **/
  @ApiModelProperty(value = "")
  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public User username(String username) {
    this.username = username;
    return this;
  }

   /**
   * Get username
   * @return username
  **/
  @ApiModelProperty(value = "")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(this.admin, user.admin) &&
        Objects.equals(this.email, user.email) &&
        Objects.equals(this.groups, user.groups) &&
        Objects.equals(this.id, user.id) &&
        Objects.equals(this.name, user.name) &&
        Objects.equals(this.surname, user.surname) &&
        Objects.equals(this.username, user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(admin, email, groups, id, name, surname, username);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class User {\n");
    
    sb.append("    admin: ").append(toIndentedString(admin)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    groups: ").append(toIndentedString(groups)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    surname: ").append(toIndentedString(surname)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
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

