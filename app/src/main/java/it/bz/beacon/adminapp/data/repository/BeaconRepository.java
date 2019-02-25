package it.bz.beacon.adminapp.data.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiException;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.data.dao.BeaconDao;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.event.DataUpdateEvent;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.event.LoadEvent;

public class BeaconRepository {

    private BeaconDao beaconDao;
    private LiveData<List<BeaconMinimal>> beacons;
    private Storage storage;
    private int synchronizationInterval;

    public BeaconRepository(Context context) {
        BeaconDatabase db = BeaconDatabase.getDatabase(context);
        beaconDao = db.beaconDao();
        beacons = beaconDao.getAll();
        storage = AdminApplication.getStorage();
        synchronizationInterval = context.getResources().getInteger(R.integer.synchronization_interval);
    }

    public LiveData<List<BeaconMinimal>> getAll() {
        if (shouldSynchronize()) {
            refreshBeacons(null);
        }
        return beacons;
    }

    public LiveData<Beacon> getById(long id) {
        refreshBeacon(id, null);
        return beaconDao.getById(id);
    }

    public void getByInstanceId(String instanceId, LoadEvent loadEvent) {
        new LoadByInstanceIdTask(beaconDao, loadEvent).execute(instanceId);
    }

    private boolean shouldSynchronize() {
        return (storage.getLastSynchronizationBeacons() + synchronizationInterval * 60000L < System.currentTimeMillis());
    }

    public void refreshBeacons(final DataUpdateEvent dataUpdateEvent) {
        try {
            AdminApplication.getBeaconApi().getListUsingGETAsync(new ApiCallback<List<io.swagger.client.model.Beacon>>() {
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
                public void onSuccess(List<io.swagger.client.model.Beacon> result, int statusCode, Map<String, List<String>> responseHeaders) {

                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            saveBeacon(result.get(i));
                        }
                        if (dataUpdateEvent != null) {
                            dataUpdateEvent.onSuccess();
                        }
                        storage.setLastSynchronizationBeacons(System.currentTimeMillis());
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

    private void saveBeacon(io.swagger.client.model.Beacon remoteBeacon) {
        Beacon beacon;
        beacon = new Beacon();
        beacon.setId(remoteBeacon.getId());
        beacon.setBatteryLevel(remoteBeacon.getBatteryLevel());
        beacon.setDescription(remoteBeacon.getDescription());
        beacon.setEddystoneEid(remoteBeacon.isEddystoneEid());
        beacon.setEddystoneEtlm(remoteBeacon.isEddystoneEtlm());
        beacon.setEddystoneTlm(remoteBeacon.isEddystoneTlm());
        beacon.setEddystoneUid(remoteBeacon.isEddystoneUid());
        beacon.setEddystoneUrl(remoteBeacon.isEddystoneUrl());
        beacon.setIBeacon(remoteBeacon.isIBeacon());
        beacon.setInstanceId(remoteBeacon.getInstanceId());
        beacon.setInterval(remoteBeacon.getInterval());
        beacon.setLastSeen(remoteBeacon.getLastSeen());
        beacon.setLat(remoteBeacon.getLat());
        beacon.setLng(remoteBeacon.getLng());
        beacon.setLocationDescription(remoteBeacon.getLocationDescription());
        if (remoteBeacon.getLocationType() != null) {
            beacon.setLocationType(remoteBeacon.getLocationType().getValue());
        }
        beacon.setMajor(remoteBeacon.getMajor());
        beacon.setMinor(remoteBeacon.getMinor());
        beacon.setManufacturer(remoteBeacon.getManufacturer().getValue());
        beacon.setManufacturerId(remoteBeacon.getManufacturerId());
        beacon.setName(remoteBeacon.getName());
        beacon.setNamespace(remoteBeacon.getNamespace());
        beacon.setStatus(remoteBeacon.getStatus().getValue());
        beacon.setTelemetry(remoteBeacon.isTelemetry());
        beacon.setTxPower(remoteBeacon.getTxPower());
        beacon.setUrl(remoteBeacon.getUrl());
        beacon.setUuid(remoteBeacon.getUuid().toString());
        if (remoteBeacon.getPendingConfiguration() != null) {
            beacon.setPendingConfiguration((new Gson()).toJson(remoteBeacon.getPendingConfiguration()));
        }
        else {
            beacon.setPendingConfiguration(null);
        }
        insert(beacon, null);
    }

    public void refreshBeacon(long beaconId, final DataUpdateEvent dataUpdateEvent) {
        try {
            AdminApplication.getBeaconApi().getUsingGETAsync(beaconId, new ApiCallback<io.swagger.client.model.Beacon>() {
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
                public void onSuccess(io.swagger.client.model.Beacon remoteBeacon, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (remoteBeacon != null) {
                        saveBeacon(remoteBeacon);
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

    public void insert(Beacon beacon, InsertEvent event) {
        new InsertAsyncTask(beaconDao, event).execute(beacon);
    }

    private static class InsertAsyncTask extends AsyncTask<Beacon, Void, Long> {

        private BeaconDao asyncTaskDao;
        private InsertEvent insertEvent;

        InsertAsyncTask(BeaconDao dao, InsertEvent event) {
            asyncTaskDao = dao;
            insertEvent = event;
        }

        @Override
        protected Long doInBackground(final Beacon... params) {
            return asyncTaskDao.insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            if (insertEvent != null) {
                insertEvent.onSuccess(id);
            }
            Log.d(AdminApplication.LOG_TAG, "inserted: " + id);
        }
    }

    private static class LoadByInstanceIdTask extends AsyncTask<String, Void, BeaconMinimal> {

        private BeaconDao asyncTaskDao;
        private LoadEvent loadEvent;

        LoadByInstanceIdTask(BeaconDao dao, LoadEvent event) {
            asyncTaskDao = dao;
            loadEvent = event;
        }

        @Override
        protected BeaconMinimal doInBackground(String... ids) {
            return asyncTaskDao.getByInstanceId(ids[0]);
        }

        @Override
        protected void onPostExecute(BeaconMinimal beaconMinimal) {
            if ((loadEvent != null) && (beaconMinimal != null)) {
                loadEvent.onSuccessBeacon(beaconMinimal);
            }
        }
    }

    public void deleteBeacon(Beacon beacon) {
        beaconDao.delete(beacon);
    }
}
