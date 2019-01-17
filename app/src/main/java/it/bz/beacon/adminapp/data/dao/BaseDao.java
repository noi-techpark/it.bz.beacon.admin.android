package it.bz.beacon.adminapp.data.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

import java.util.ArrayList;

public interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(T object);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultiple(ArrayList<T> objects);

    @Update
    void update(T object);

    @Delete
    void delete(T object);
}
