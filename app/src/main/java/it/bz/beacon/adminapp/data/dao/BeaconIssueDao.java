// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;
import it.bz.beacon.adminapp.data.entity.BeaconIssue;

@Dao
public abstract class BeaconIssueDao implements BaseDao<BeaconIssue> {

    @Transaction
    @Query("SELECT * FROM BeaconIssue ORDER BY reportDate DESC")
    public abstract LiveData<List<BeaconIssue>> getAll();

    @Transaction
    @Query("SELECT * FROM BeaconIssue WHERE beaconId = :beaconId ORDER BY reportDate DESC")
    public abstract LiveData<List<BeaconIssue>> getAllByBeaconId(String beaconId);
}
