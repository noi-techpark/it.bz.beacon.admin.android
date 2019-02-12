package it.bz.beacon.adminapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import it.bz.beacon.adminapp.data.entity.BeaconIssue;

@Dao
public abstract class BeaconIssueDao implements BaseDao<BeaconIssue> {

    @Query("DELETE FROM BeaconIssue")
    abstract void deleteAll();
}
