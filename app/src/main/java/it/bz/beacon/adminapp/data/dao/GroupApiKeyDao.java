// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import it.bz.beacon.adminapp.data.entity.GroupApiKey;

@Dao
public abstract class GroupApiKeyDao implements BaseDao<GroupApiKey> {

    @Transaction
    @Query("SELECT * FROM GroupApiKey ORDER BY groupId ASC")
    public abstract LiveData<List<GroupApiKey>>
    getAll();

    @Query("SELECT * FROM GroupApiKey WHERE groupId = :groupId")
    public abstract GroupApiKey getByGroupId(Long groupId);

    @Transaction
    @Query("DELETE FROM GroupApiKey")
    public abstract void deleteAll();
}
