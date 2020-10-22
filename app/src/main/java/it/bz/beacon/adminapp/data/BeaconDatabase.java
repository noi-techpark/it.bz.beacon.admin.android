package it.bz.beacon.adminapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import it.bz.beacon.adminapp.data.dao.BeaconDao;
import it.bz.beacon.adminapp.data.dao.BeaconImageDao;
import it.bz.beacon.adminapp.data.dao.BeaconIssueDao;
import it.bz.beacon.adminapp.data.dao.GroupDao;
import it.bz.beacon.adminapp.data.dao.InfoDao;
import it.bz.beacon.adminapp.data.dao.IssueWithBeaconDao;
import it.bz.beacon.adminapp.data.dao.PendingSecureConfigDao;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconImage;
import it.bz.beacon.adminapp.data.entity.BeaconIssue;
import it.bz.beacon.adminapp.data.entity.Group;
import it.bz.beacon.adminapp.data.entity.Info;
import it.bz.beacon.adminapp.data.entity.PendingSecureConfig;

@Database(
        entities = {
                Beacon.class,
                BeaconImage.class,
                BeaconIssue.class,
                PendingSecureConfig.class,
                Group.class,
                Info.class
        },
        version = 7, exportSchema = true)

public abstract class BeaconDatabase extends RoomDatabase {

    private static BeaconDatabase INSTANCE;
    public static String DB_NAME = "beacon_db";

    public abstract BeaconDao beaconDao();

    public abstract InfoDao infoDao();

    public abstract BeaconImageDao beaconImageDao();

    public abstract BeaconIssueDao beaconIssueDao();

    public abstract IssueWithBeaconDao issueWithBeaconDao();

    public abstract PendingSecureConfigDao pendingSecureConfigDao();

    public abstract GroupDao groupDao();

    public static BeaconDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BeaconDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BeaconDatabase.class, DB_NAME)
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

    public static void removeInstance() {
        INSTANCE = null;
    }

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
}
