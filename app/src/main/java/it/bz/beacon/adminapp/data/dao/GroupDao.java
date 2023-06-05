// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import it.bz.beacon.adminapp.data.entity.Group;

@Dao
public abstract class GroupDao implements BaseDao<Group> {

    @Transaction
    @Query("SELECT * FROM `Group` ORDER BY id ASC")
    public abstract LiveData<List<Group>> getAll();
}
