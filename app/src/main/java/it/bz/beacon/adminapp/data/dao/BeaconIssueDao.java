package it.bz.beacon.adminapp.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import it.bz.beacon.adminapp.data.entity.BeaconIssue;

@Dao
public abstract class BeaconIssueDao implements BaseDao<BeaconIssue> {

    @Query("DELETE FROM BeaconIssue")
    abstract void deleteAll();
}
