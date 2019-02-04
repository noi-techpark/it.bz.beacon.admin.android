package it.bz.beacon.adminapp.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

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
    public abstract LiveData<Beacon> getById(long id);

    @Query("SELECT id, batteryLevel, lat, lng, major, minor, manufacturerId, name, status FROM Beacon WHERE major = :major AND minor = :minor")
    public abstract BeaconMinimal getByMajorMinor(int major, int minor);

    @Query("SELECT id, batteryLevel, lat, lng, major, minor, manufacturerId, name, status FROM Beacon WHERE instanceId = :instanceId")
    public abstract BeaconMinimal getByInstanceId(String instanceId);

    @Transaction
    public void replaceAll(ArrayList<Beacon> beacons) {
        deleteAll();
        insertMultiple(beacons);
    }

    @Query("DELETE FROM Beacon")
    abstract void deleteAll();
}
