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
import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.dao.BeaconDao;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.event.DataUpdateEvent;
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
        refreshBeacons(null);
        return beacons;
    }

    public LiveData<Beacon> getById(long id) {
        return beaconDao.getById(id);
    }

    public void refreshBeacons(final DataUpdateEvent dataUpdateEvent) {
        try {
            AdminApplication.getBeaconApi().getListUsingGETAsync(new ApiCallback<List<io.swagger.client.model.Beacon>>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (dataUpdateEvent != null) {
                        dataUpdateEvent.onError();
                    }
                }

                @Override
                public void onSuccess(List<io.swagger.client.model.Beacon> result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (statusCode == 403) {
                        if (dataUpdateEvent != null) {
                            dataUpdateEvent.onAuthenticationFailed();
                            return;
                        }
                    }
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
                            beacon.setTxPower(remoteBeacon.getTxPower());
                            beacon.setUrl(remoteBeacon.getUrl());
                            beacon.setUuid(remoteBeacon.getUuid().toString());
                            beaconDao.insert(beacon);
                        }
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
                    Log.i(AdminApplication.LOG_TAG, "progress: " + bytesRead);
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
