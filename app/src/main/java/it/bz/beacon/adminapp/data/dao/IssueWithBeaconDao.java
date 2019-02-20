package it.bz.beacon.adminapp.data.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;
import it.bz.beacon.adminapp.data.entity.IssueWithBeacon;

@Dao
public abstract class IssueWithBeaconDao {

    @Query("SELECT * FROM  IssueWithBeacon)// ORDER BY reportDate ASC")
    public abstract LiveData<List<IssueWithBeacon>> getAllIssuesWithBeacon();

}
