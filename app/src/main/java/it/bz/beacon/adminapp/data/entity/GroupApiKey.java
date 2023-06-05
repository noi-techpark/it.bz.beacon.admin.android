// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GroupApiKey {

    @NonNull
    @PrimaryKey
    private long groupId;
    private String apiKey;

    public GroupApiKey() {
    }

    public GroupApiKey(it.bz.beacon.adminapp.swagger.client.model.GroupApiKey remoteGroupApiKey) {
        setGroupId(remoteGroupApiKey.getGroupId());
        setApiKey(remoteGroupApiKey.getApiKey());
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
