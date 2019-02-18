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
    @Query("SELECT * FROM BeaconIssue WHERE beaconId = :beaconId ORDER BY reportDate ASC")
    public abstract LiveData<List<BeaconIssue>> getAll(long beaconId);

    @Query("DELETE FROM BeaconIssue")
    abstract void deleteAll();

    @Query("SELECT * FROM BeaconIssue WHERE id = :id")
    public abstract LiveData<BeaconIssue> getById(long id);
}
