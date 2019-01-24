package it.bz.beacon.adminapp.data.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.Map;

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

public class BeaconRepository {

    private BeaconDao beaconDao;
    private LiveData<List<BeaconMinimal>> beacons;
    private Storage storage;
    private int synchronizationInterval = 0;

    public BeaconRepository(Context context) {
        BeaconDatabase db = BeaconDatabase.getDatabase(context);
        beaconDao = db.beaconDao();
        beacons = beaconDao.getAll();
        storage = AdminApplication.getStorage();
        synchronizationInterval = context.getResources().getInteger(R.integer.synchronization_interval);
    }

    public LiveData<List<BeaconMinimal>> getAll() {
        if (shouldSynchronize()) {
            Log.d(AdminApplication.LOG_TAG, "Start synchronization");
            refreshBeacons(null);
        }
        else {
            Log.d(AdminApplication.LOG_TAG, "It's not time to synchronize");
        }
        return beacons;
    }

    public LiveData<Beacon> getById(long id) {
        return beaconDao.getById(id);
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
                        } else {
                            dataUpdateEvent.onError();
                        }
                    }
                }

                @Override
                public void onSuccess(List<io.swagger.client.model.Beacon> result, int statusCode, Map<String, List<String>> responseHeaders) {

                    if (result != null) {
                        Beacon beacon;
                        io.swagger.client.model.Beacon remoteBeacon;
                        for (int i = 0; i < result.size(); i++) {
                            remoteBeacon = result.get(i);
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
                            beaconDao.insert(beacon);
                        }
                        if (dataUpdateEvent != null) {
                            dataUpdateEvent.onSuccess();
                        }
                        storage.setLastSynchronizationBeacons(System.currentTimeMillis());
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
                    if (done) {
                        Log.i(AdminApplication.LOG_TAG, "Bytes written: " + bytesWritten);
                    }
                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
                    if (done) {
                        Log.i(AdminApplication.LOG_TAG, "Bytes read: " + bytesRead);
                    }
                }
            });
        } catch (ApiException e) {
            if (dataUpdateEvent != null) {
                dataUpdateEvent.onError();
            }
            Log.e(AdminApplication.LOG_TAG, e.getMessage());
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
            long id = asyncTaskDao.insert(params[0]);
            return id;
        }

        @Override
        protected void onPostExecute(Long id) {
            if (insertEvent != null) {
                insertEvent.onSuccess(id);
            }
        }
    }

    public void deleteBeacon(Beacon beacon) {
        beaconDao.delete(beacon);
    }
}
