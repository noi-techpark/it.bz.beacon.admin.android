package it.bz.beacon.adminapp.data.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiException;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.data.dao.BeaconIssueDao;
import it.bz.beacon.adminapp.data.entity.BeaconIssue;
import it.bz.beacon.adminapp.data.event.DataUpdateEvent;
import it.bz.beacon.adminapp.data.event.InsertEvent;

public class BeaconIssueRepository {

    private BeaconIssueDao beaconIssueDao;
    private Storage storage;
    private int synchronizationInterval;

    public BeaconIssueRepository(Context context) {
        BeaconDatabase db = BeaconDatabase.getDatabase(context);
        beaconIssueDao = db.beaconIssueDao();
        storage = AdminApplication.getStorage();
        synchronizationInterval = context.getResources().getInteger(R.integer.synchronization_interval);
    }

    public LiveData<List<BeaconIssue>> getAll(long beaconId) {
        if (shouldSynchronize()) {
            refreshBeaconIssues(beaconId, null);
        }
        return beaconIssueDao.getAll(beaconId);
    }

    public LiveData<BeaconIssue> getById(long id) {
        refreshBeaconIssue(id, null);
        return beaconIssueDao.getById(id);
    }

    private boolean shouldSynchronize() {
        return (storage.getLastSynchronizationIssues() + synchronizationInterval * 60000L < System.currentTimeMillis());
    }

    public void refreshBeaconIssues(long beaconId, final DataUpdateEvent dataUpdateEvent) {
        try {
            AdminApplication.getIssueApi().getListUsingGET2Async(beaconId, false, new ApiCallback<List<io.swagger.client.model.BeaconIssue>>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (dataUpdateEvent != null) {
                        if (statusCode == 403) {
                            dataUpdateEvent.onAuthenticationFailed();
                        }
                        else {
                            dataUpdateEvent.onError();
                        }
                    }
                }

                @Override
                public void onSuccess(List<io.swagger.client.model.BeaconIssue> result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            saveBeaconIssue(result.get(i));
                        }
                        if (dataUpdateEvent != null) {
                            dataUpdateEvent.onSuccess();
                        }
                        storage.setLastSynchronizationIssues(System.currentTimeMillis());
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        }
        catch (ApiException e) {
            if (dataUpdateEvent != null) {
                dataUpdateEvent.onError();
            }
            Log.e(AdminApplication.LOG_TAG, e.getMessage());
        }
    }

    private void saveBeaconIssue(io.swagger.client.model.BeaconIssue remoteBeaconIssue) {
        BeaconIssue beaconIssue;
        beaconIssue = new BeaconIssue();
        beaconIssue.setId(remoteBeaconIssue.getId());
        beaconIssue.setBeaconId(remoteBeaconIssue.getBeacon().getId());
        beaconIssue.setProblem(remoteBeaconIssue.getProblem());
        beaconIssue.setProblemDescription(remoteBeaconIssue.getProblemDescription());
        // TODO: re-generate swagger entities which has date as long
//        beaconIssue.setReportDate(remoteBeaconIssue.getReportDate());
        beaconIssue.setReporter(remoteBeaconIssue.getReporter());
        beaconIssue.setResolved(remoteBeaconIssue.isResolved());
        // TODO: re-generate swagger entities which has date as long
//        beaconIssue.setResolveDate(remoteBeaconIssue.getResolveDate());
        beaconIssue.setSolution(remoteBeaconIssue.getSolution());
        beaconIssue.setSolutionDescription(remoteBeaconIssue.getSolutionDescription());
        beaconIssueDao.insert(beaconIssue);
    }

    public void refreshBeaconIssue(long beaconIssueId, final DataUpdateEvent dataUpdateEvent) {
        try {
            AdminApplication.getIssueApi().getUsingGET1Async(beaconIssueId, new ApiCallback<io.swagger.client.model.BeaconIssue>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (dataUpdateEvent != null) {
                        if (statusCode == 403) {
                            dataUpdateEvent.onAuthenticationFailed();
                        }
                        else {
                            dataUpdateEvent.onError();
                        }
                    }
                }

                @Override
                public void onSuccess(io.swagger.client.model.BeaconIssue remoteBeaconIssue, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (remoteBeaconIssue != null) {
                        saveBeaconIssue(remoteBeaconIssue);
                        if (dataUpdateEvent != null) {
                            dataUpdateEvent.onSuccess();
                        }
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        }
        catch (ApiException e) {
            Log.e(AdminApplication.LOG_TAG, e.getMessage());
            if (dataUpdateEvent != null) {
                dataUpdateEvent.onError();
            }
        }
    }

    public void insert(BeaconIssue beaconIssue, InsertEvent event) {
        new InsertAsyncTask(beaconIssueDao, event).execute(beaconIssue);
    }

    private static class InsertAsyncTask extends AsyncTask<BeaconIssue, Void, Long> {

        private BeaconIssueDao asyncTaskDao;
        private InsertEvent insertEvent;

        InsertAsyncTask(BeaconIssueDao dao, InsertEvent event) {
            asyncTaskDao = dao;
            insertEvent = event;
        }

        @Override
        protected Long doInBackground(final BeaconIssue... params) {
            return asyncTaskDao.insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            if (insertEvent != null) {
                insertEvent.onSuccess(id);
            }
        }
    }

    public void deleteBeaconIssue(BeaconIssue beacon) {
        beaconIssueDao.delete(beacon);
    }
}
