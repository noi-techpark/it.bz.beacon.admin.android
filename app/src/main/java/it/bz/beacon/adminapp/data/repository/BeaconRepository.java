package it.bz.beacon.adminapp.data.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.dao.BeaconDao;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.event.InsertEvent;

public class BeaconRepository {

    private BeaconDao beaconDao;
    private LiveData<List<BeaconMinimal>> beacons;

    public BeaconRepository(Context context) {
        BeaconDatabase db = BeaconDatabase.getDatabase(context);
        beaconDao = db.beaconDao();
        beacons = beaconDao.getAll();
    }

    public LiveData<List<BeaconMinimal>> getAll() {
        refreshBeacons();
        return beacons;
    }

    public LiveData<Beacon> getById(long id) {
        return beaconDao.getById(id);
    }

    public void refreshBeacons() {
    }

    public void insert(Beacon beacon, InsertEvent event) {
        new InsertAsyncTask(beaconDao, event).execute(beacon);
    }

    private static class InsertAsyncTask extends AsyncTask<Beacon, Void, Void> {

        private BeaconDao asyncTaskDao;
        private InsertEvent insertEvent;

        InsertAsyncTask(BeaconDao dao, InsertEvent event) {
            asyncTaskDao = dao;
            insertEvent = event;
        }

        @Override
        protected Void doInBackground(final Beacon... params) {
            long id = asyncTaskDao.insert(params[0]);
            if (insertEvent != null) {
                insertEvent.successful(id);
            }
            return null;
        }
    }

    public void deleteBeacon(Beacon beacon) {
        beaconDao.delete(beacon);
    }
}
