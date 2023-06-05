// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.ArrayList;
import java.util.List;

import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;

@Dao
public abstract class BeaconDao implements BaseDao<Beacon> {

    @Transaction
    @Query("SELECT id, batteryLevel, lat, lng, major, minor, manufacturerId, name, status FROM Beacon ORDER BY name ASC")
    public abstract LiveData<List<BeaconMinimal>> getAll();

    @Query("SELECT * FROM Beacon WHERE id = :id")
    public abstract LiveData<Beacon> getByIdLive(String id);

    @Query("SELECT * FROM Beacon WHERE id = :id")
    public abstract Beacon getById(String id);

    @Query("SELECT id, batteryLevel, lat, lng, major, minor, manufacturerId, name, status FROM Beacon WHERE manufacturerId = :instanceId")
    public abstract BeaconMinimal getByInstanceId(String instanceId);
}
