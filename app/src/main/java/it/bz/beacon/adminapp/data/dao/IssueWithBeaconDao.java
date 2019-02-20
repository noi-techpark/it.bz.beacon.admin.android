package it.bz.beacon.adminapp.data.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;
import it.bz.beacon.adminapp.data.entity.IssueWithBeacon;

@Dao
public abstract class IssueWithBeaconDao {

    @Transaction
    @Query("SELECT bi.id, bi.beaconId, bi.problem, bi.problemDescription, bi.reporter, bi.reportDate, bi.resolved, bi.resolveDate, bi.solution, bi.solutionDescription, b.batteryLevel, b.lastSeen, b.name, b.status FROM BeaconIssue AS bi INNER JOIN Beacon AS b ON bi.beaconId = b.id ORDER BY bi.reportDate ASC")
    public abstract LiveData<List<IssueWithBeacon>> getAllIssuesWithBeacon();
}