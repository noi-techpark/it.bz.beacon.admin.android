package it.bz.beacon.adminapp.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import it.bz.beacon.adminapp.data.entity.BeaconImage;

@Dao
public abstract class BeaconImageDao implements BaseDao<BeaconImage> {

    @Query("DELETE FROM BeaconImage")
    abstract void deleteAll();
}
