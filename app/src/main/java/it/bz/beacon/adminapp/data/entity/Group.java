// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Group {

    @NonNull
    @PrimaryKey
    private long id;
    private String name;

    public Group() {
    }

    public Group(it.bz.beacon.adminapp.swagger.client.model.Group remoteGroup) {
        setId(remoteGroup.getId());
        setName(remoteGroup.getName());
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
