package it.bz.beacon.adminapp.data.repository;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.dao.PendingSecureConfigDao;
import it.bz.beacon.adminapp.data.entity.PendingSecureConfig;
import it.bz.beacon.adminapp.data.event.InsertEvent;

public class PendingSecureConfigRepository {

    private PendingSecureConfigDao pendingSecureConfigDao;

    public PendingSecureConfigRepository(Context context) {
        BeaconDatabase db = BeaconDatabase.getDatabase(context);
        pendingSecureConfigDao = db.pendingSecureConfigDao();
    }

    public List<PendingSecureConfig> getAll() {
        return pendingSecureConfigDao.getAll();
    }

    public List<PendingSecureConfig> getListByApiKey(String apiKey) {
        return pendingSecureConfigDao.getListByApiKey(apiKey);
    }

    public List<String> getAllDistinctApiKey() {
        return pendingSecureConfigDao.getAllDistinctApiKey();
    }

    public void insert(PendingSecureConfig pendingSecureConfig, InsertEvent event) {
        new InsertAsyncTask(pendingSecureConfigDao, event).execute(pendingSecureConfig);
    }

    private static class InsertAsyncTask extends AsyncTask<PendingSecureConfig, Void, Long> {

        private PendingSecureConfigDao asyncTaskDao;
        private InsertEvent insertEvent;

        InsertAsyncTask(PendingSecureConfigDao dao, InsertEvent event) {
            asyncTaskDao = dao;
            insertEvent = event;
        }

        @Override
        protected Long doInBackground(final PendingSecureConfig... params) {
            return asyncTaskDao.insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            if (insertEvent != null) {
                insertEvent.onSuccess(id);
            }
        }
    }

    public void deletePendingSecureConfig(PendingSecureConfig pendingSecureConfig) {
        pendingSecureConfigDao.delete(pendingSecureConfig);
    }
}
