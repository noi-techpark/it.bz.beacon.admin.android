package it.bz.beacon.adminapp.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import it.bz.beacon.adminapp.data.dao.BeaconDao;
import it.bz.beacon.adminapp.data.dao.BeaconImageDao;
import it.bz.beacon.adminapp.data.dao.BeaconIssueDao;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconImage;
import it.bz.beacon.adminapp.data.entity.BeaconIssue;

@Database(
        entities = {
                Beacon.class,
                BeaconImage.class,
                BeaconIssue.class
        },
        version = 1, exportSchema = false)

public abstract class BeaconDatabase extends RoomDatabase {

    private static BeaconDatabase INSTANCE;

    public abstract BeaconDao beaconDao();
    public abstract BeaconImageDao beaconImageDao();
    public abstract BeaconIssueDao beaconIssueDao();

    public static BeaconDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BeaconDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BeaconDatabase.class, "beacon_db")
                            .addCallback(roomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final BeaconDao mDao;

        PopulateDbAsync(BeaconDatabase db) {
            mDao = db.beaconDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // TODO: create some fake beacons
//            mDao.deleteAll();
//            Word word = new Word("Hello");
//            mDao.insert(word);
//            word = new Word("World");
//            mDao.insert(word);
            return null;
        }
    }
}
