package it.bz.beacon.adminapp.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Random;

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
                }

                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    // TODO: remove this in production
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final BeaconDao beaconDao;

        PopulateDbAsync(BeaconDatabase db) {
            beaconDao = db.beaconDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            Random random = new Random();

            for (int i = 0; i < 1000; i++) {
                Beacon beacon = new Beacon();
                beacon.setId(i);
                beacon.setName("Beacon " + i);
                beacon.setBatteryLevel(random.nextInt(100));
                beacon.setManufacturerId("fJ" + (10 + random.nextInt(80)) + "le"+ (10 + random.nextInt(80)));
                beacon.setStatus("ok");
                beacon.setMajor(100 + random.nextInt(50));
                beacon.setMinor(random.nextInt(1000));
                beaconDao.insert(beacon);
            }
            return null;
        }
    }
}
