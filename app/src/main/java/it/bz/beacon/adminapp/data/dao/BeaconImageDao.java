package it.bz.beacon.adminapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import it.bz.beacon.adminapp.data.entity.BeaconImage;

@Dao
public abstract class BeaconImageDao implements BaseDao<BeaconImage> {

    @Transaction
    @Query("SELECT * FROM BeaconImage WHERE beaconId = :beaconId ORDER BY id ASC")
    public abstract LiveData<List<BeaconImage>> getAllByBeaconId(String beaconId);

    @Query("SELECT * FROM BeaconImage WHERE id = :id")
    public abstract LiveData<BeaconImage> getById(long id);
}
