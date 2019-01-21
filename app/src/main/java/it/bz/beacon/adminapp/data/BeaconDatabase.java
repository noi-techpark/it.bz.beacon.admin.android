package it.bz.beacon.adminapp.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Random;
import java.util.UUID;

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
    public static String DB_NAME = "beacon_db";

    public abstract BeaconDao beaconDao();
    public abstract BeaconImageDao beaconImageDao();
    public abstract BeaconIssueDao beaconIssueDao();

    public static BeaconDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BeaconDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BeaconDatabase.class, DB_NAME)
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

//                @Override
//                public void onCreate(@NonNull SupportSQLiteDatabase db) {
//                    super.onCreate(db);
//                    // TODO: remove this in production
//                    new PopulateDbTask(INSTANCE).execute();
//                }
            };

    private static class PopulateDbTask extends AsyncTask<Void, Void, Void> {

        private final BeaconDao beaconDao;

        PopulateDbTask(BeaconDatabase db) {
            beaconDao = db.beaconDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            Random random = new Random();

            for (int i = 0; i < 100; i++) {
                Beacon beacon = new Beacon();
                beacon.setId(i);
                beacon.setUuid(UUID.randomUUID().toString().replace("-", "").toUpperCase());
                beacon.setName("Beacon " + i);
                beacon.setLastSeen(System.currentTimeMillis() - random.nextInt(65000));
                beacon.setBatteryLevel(random.nextInt(100));
                beacon.setManufacturerId("fJ" + (10 + random.nextInt(80)) + "le"+ (10 + random.nextInt(80)));
                switch (random.nextInt(3)) {
                    case 1: beacon.setStatus(Beacon.STATUS_BATTERY_LOW);
                    break;
                    case 2: beacon.setStatus(Beacon.STATUS_CONFIGURATION_PENDING);
                    break;
                    default: beacon.setStatus(Beacon.STATUS_OK);
                }
                beacon.setTxPower(1 + random.nextInt(6));
                beacon.setInterval(100 * (1 + random.nextInt(9)));
                beacon.setMajor(100 + random.nextInt(50));
                beacon.setMinor(random.nextInt(1000));
                beacon.setLat(46.0f + (random.nextInt(10000) / 5000.0f));
                beacon.setLng(11.0f + (random.nextInt(10000) / 5000.0f));
                if (random.nextInt(2) == 1) {
                    beacon.setLocationType(Beacon.LOCATION_OUTDOOR);
                }
                else {
                    beacon.setLocationType(Beacon.LOCATION_INDOOR);
                }
                beaconDao.insert(beacon);
            }
            return null;
        }
    }
}
