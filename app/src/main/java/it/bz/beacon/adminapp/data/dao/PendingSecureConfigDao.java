package it.bz.beacon.adminapp.data.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import it.bz.beacon.adminapp.data.entity.PendingSecureConfig;

@Dao
public abstract class PendingSecureConfigDao implements BaseDao<PendingSecureConfig> {

    @Query("SELECT * FROM PendingSecureConfig ORDER BY id ASC")
    public abstract List<PendingSecureConfig> getAll();
}
