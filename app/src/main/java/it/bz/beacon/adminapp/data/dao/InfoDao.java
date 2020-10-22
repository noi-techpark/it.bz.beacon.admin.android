package it.bz.beacon.adminapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import it.bz.beacon.adminapp.data.entity.Info;

@Dao
public abstract class InfoDao implements BaseDao<Info> {

    @Transaction
    @Query("SELECT * FROM `Info` ORDER BY name ASC")
    public abstract LiveData<List<Info>> getAll();

    @Query("SELECT * FROM `Info` WHERE id = :id")
    public abstract LiveData<Info> getByIdLive(String id);

    @Query("SELECT * FROM `Info` WHERE id = :id")
    public abstract Info getById(String id);
}
