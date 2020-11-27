package it.bz.beacon.adminapp.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.bz.beacon.adminapp.data.entity.PendingSecureConfig;

@Dao
public abstract class PendingSecureConfigDao implements BaseDao<PendingSecureConfig> {

    @Query("SELECT * FROM PendingSecureConfig ORDER BY id ASC")
    public abstract List<PendingSecureConfig> getAll();

    @Query("SELECT DISTINCT apiKey FROM PendingSecureConfig")
    public abstract List<String> getAllDistinctApiKey();

    @Query("SELECT * FROM PendingSecureConfig WHERE apiKey = :apiKey")
    public abstract List<PendingSecureConfig> getListByApiKey(String apiKey);
}
