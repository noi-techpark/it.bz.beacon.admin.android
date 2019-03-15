package it.bz.beacon.adminapp.data.repository;

import androidx.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.Map;

import io.swagger.client.ApiCallback;
import io.swagger.client.ApiException;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.dao.BeaconImageDao;
import it.bz.beacon.adminapp.data.entity.BeaconImage;
import it.bz.beacon.adminapp.data.event.DataUpdateEvent;
import it.bz.beacon.adminapp.data.event.InsertEvent;

public class BeaconImageRepository {
    private BeaconImageDao beaconImageDao;

    public BeaconImageRepository(Context context) {
        BeaconDatabase db = BeaconDatabase.getDatabase(context);
        beaconImageDao = db.beaconImageDao();
    }

    public LiveData<List<BeaconImage>> getAllByBeaconId(String beaconId) {
        refreshBeaconImages(beaconId, null);
        return beaconImageDao.getAllByBeaconId(beaconId);
    }

    public LiveData<BeaconImage> getById(long id) {
        return beaconImageDao.getById(id);
    }

    public void refreshBeaconImages(final String beaconId, final DataUpdateEvent dataUpdateEvent) {
        try {
            AdminApplication.getImageApi().getListUsingGET1Async(beaconId, new ApiCallback<List<io.swagger.client.model.BeaconImage>>() {
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
                public void onSuccess(List<io.swagger.client.model.BeaconImage> result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (result != null) {
                        BeaconImage beaconImage;
                        io.swagger.client.model.BeaconImage remoteBeaconImage;
                        for (int i = 0; i < result.size(); i++) {
                            remoteBeaconImage = result.get(i);
                            beaconImage = new BeaconImage();
                            beaconImage.setId(remoteBeaconImage.getId());
                            beaconImage.setBeaconId(beaconId);
                            beaconImage.setFileName(remoteBeaconImage.getFileName());
                            beaconImageDao.insert(beaconImage);
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

                }
            });
        } catch (ApiException e) {
            if (dataUpdateEvent != null) {
                dataUpdateEvent.onError();
            }
            Log.e(AdminApplication.LOG_TAG, e.getMessage());
        }
    }

    public void insert(BeaconImage beaconImage, InsertEvent event) {
        new BeaconImageRepository.InsertAsyncTask(beaconImageDao, event).execute(beaconImage);
    }

    private static class InsertAsyncTask extends AsyncTask<BeaconImage, Void, Long> {

        private BeaconImageDao asyncTaskDao;
        private InsertEvent insertEvent;

        InsertAsyncTask(BeaconImageDao dao, InsertEvent event) {
            asyncTaskDao = dao;
            insertEvent = event;
        }

        @Override
        protected Long doInBackground(final BeaconImage... params) {
            return asyncTaskDao.insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            if (insertEvent != null) {
                insertEvent.onSuccess(id);
            }
            else {
                insertEvent.onFailure();
            }
        }
    }

    public void deleteBeaconImage(BeaconImage beaconImage) {
        beaconImageDao.delete(beaconImage);
    }
}
