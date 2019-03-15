package it.bz.beacon.adminapp.data;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Random;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import it.bz.beacon.adminapp.data.dao.BeaconDao;
import it.bz.beacon.adminapp.data.dao.BeaconImageDao;
import it.bz.beacon.adminapp.data.dao.BeaconIssueDao;
import it.bz.beacon.adminapp.data.dao.IssueWithBeaconDao;
import it.bz.beacon.adminapp.data.dao.PendingSecureConfigDao;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconImage;
import it.bz.beacon.adminapp.data.entity.BeaconIssue;
import it.bz.beacon.adminapp.data.entity.PendingSecureConfig;

@Database(
        entities = {
                Beacon.class,
                BeaconImage.class,
                BeaconIssue.class,
                PendingSecureConfig.class
        },
        version = 4, exportSchema = true)

public abstract class BeaconDatabase extends RoomDatabase {

    private static BeaconDatabase INSTANCE;
    public static String DB_NAME = "beacon_db";

    public abstract BeaconDao beaconDao();

    public abstract BeaconImageDao beaconImageDao();

    public abstract BeaconIssueDao beaconIssueDao();

    public abstract IssueWithBeaconDao issueWithBeaconDao();

    public abstract PendingSecureConfigDao pendingSecureConfigDao();

    public static BeaconDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BeaconDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BeaconDatabase.class, DB_NAME)
                            .addCallback(roomDatabaseCallback)
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .addMigrations(MIGRATION_3_4)
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

////                @Override
//                public void onCreate(@NonNull SupportSQLiteDatabase db) {
//                    super.onCreate(db);
//                    new PopulateDbTask(INSTANCE).execute();
//                }
            };

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Beacon "
                    + " ADD COLUMN pendingConfiguration TEXT");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `PendingSecureConfig` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `config` TEXT)");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS `BeaconIssue`");
            database.execSQL("CREATE TABLE IF NOT EXISTS `BeaconIssue` (`id` INTEGER NOT NULL, `beaconId` INTEGER NOT NULL, `problem` TEXT, `problemDescription` TEXT, `reporter` TEXT, `reportDate` INTEGER, `resolved` INTEGER NOT NULL, `resolveDate` INTEGER, `solution` TEXT, `solutionDescription` TEXT)");
            database.execSQL("CREATE INDEX `index_BeaconIssue_beaconId` ON `BeaconIssue` (`beaconId`)");
        }
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
                beacon.setManufacturerId("fJ" + (10 + random.nextInt(80)) + "le" + (10 + random.nextInt(80)));
                switch (random.nextInt(5)) {
                    case 1:
                        beacon.setStatus(Beacon.STATUS_BATTERY_LOW);
                        break;
                    case 2:
                        beacon.setStatus(Beacon.STATUS_CONFIGURATION_PENDING);
                        break;
                    default:
                        beacon.setStatus(Beacon.STATUS_OK);
                }
                beacon.setTxPower(1 + random.nextInt(6));
                beacon.setInterval(100 * (1 + random.nextInt(9)));
                beacon.setMajor(100 + random.nextInt(50));
                beacon.setMinor(random.nextInt(1000));
                beacon.setLat(46.56f + (random.nextInt(10000) / 50000.0f));
                beacon.setLng(10.62f + (random.nextInt(14000) / 10000.0f));
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
