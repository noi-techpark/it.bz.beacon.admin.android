package it.bz.beacon.adminapp.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconImage;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;

@Dao
public abstract class BeaconImageDao implements BaseDao<BeaconImage> {

    @Transaction
    @Query("SELECT * FROM BeaconImage WHERE beaconId = :beaconId ORDER BY id ASC")
    public abstract LiveData<List<BeaconImage>> getAllByBeaconId(long beaconId);

    @Query("SELECT * FROM BeaconImage WHERE id = :id")
    public abstract LiveData<BeaconImage> getById(long id);

    @Query("DELETE FROM BeaconImage")
    abstract void deleteAll();
}
