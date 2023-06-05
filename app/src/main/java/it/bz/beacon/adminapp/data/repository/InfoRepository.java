// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.data.dao.InfoDao;
import it.bz.beacon.adminapp.data.entity.Info;
import it.bz.beacon.adminapp.data.event.DataUpdateEvent;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.event.LoadInfoEvent;
import it.bz.beacon.adminapp.swagger.client.ApiCallback;
import it.bz.beacon.adminapp.swagger.client.ApiException;

public class InfoRepository {

    private InfoDao infoDao;
    private LiveData<List<Info>> infos;
    private Storage storage;
    private int synchronizationInterval;

    public InfoRepository(Context context) {
        BeaconDatabase db = BeaconDatabase.getDatabase(context);
        infoDao = db.infoDao();
        infos = infoDao.getAll();
        storage = AdminApplication.getStorage();
        synchronizationInterval = context.getResources().getInteger(R.integer.synchronization_interval);
    }

    public LiveData<List<Info>> getAll() {
        if (shouldSynchronize()) {
            refreshInfos(null);
        }
        return infos;
    }

    public LiveData<Info> getByIdLive(String id) {
        refreshInfo(id, null);
        return infoDao.getByIdLive(id);
    }

    public void getById(String id, LoadInfoEvent loadEvent) {
        LoadByIdTask task = new LoadByIdTask(infoDao, loadEvent);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
    }

    private boolean shouldSynchronize() {
        return (storage.getLastSynchronizationInfos() + synchronizationInterval * 60000L < System.currentTimeMillis());
    }

    public void refreshInfos(@Nullable final DataUpdateEvent dataUpdateEvent) {
        try {
            AdminApplication.getInfoControllerApi().getListUsingGET3Async(storage.getLastSynchronizationInfos(), new ApiCallback<List<it.bz.beacon.adminapp.swagger.client.model.Info>>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (dataUpdateEvent != null) {
                        if ((statusCode == 403) || (statusCode == 401)) {
                            dataUpdateEvent.onAuthenticationFailed();
                        } else {
                            dataUpdateEvent.onError();
                        }
                    }
                }

                @Override
                public void onSuccess(List<it.bz.beacon.adminapp.swagger.client.model.Info> result, int statusCode, Map<String, List<String>> responseHeaders) {

                    if (result != null) {
                        saveInfos(result);
                        if (dataUpdateEvent != null) {
                            dataUpdateEvent.onSuccess();
                        }
                        storage.setLastSynchronizationInfos(System.currentTimeMillis());
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

    private void saveInfo(it.bz.beacon.adminapp.swagger.client.model.Info remoteInfo) {
        if (remoteInfo.getUuid() == null) {
            // indicates that something went wrong when the server tried to get infos from Kontakt.io
            return;
        }
        Info info = new Info(remoteInfo);
        insert(info, null);
    }

    private void saveInfos(List<it.bz.beacon.adminapp.swagger.client.model.Info> remoteInfos) {
        ArrayList<Info> infos = new ArrayList<>();
        for (it.bz.beacon.adminapp.swagger.client.model.Info remoteInfo : remoteInfos) {
            if (remoteInfo.getUuid() != null) {
                infos.add(new Info(remoteInfo));
            }
        }
        insertMultiple(infos);
    }

    public void refreshInfo(String infoId, final DataUpdateEvent dataUpdateEvent) {
        try {
            AdminApplication.getInfoControllerApi().getUsingGET2Async(infoId, new ApiCallback<it.bz.beacon.adminapp.swagger.client.model.Info>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (dataUpdateEvent != null) {
                        if ((statusCode == 403) || (statusCode == 401)) {
                            dataUpdateEvent.onAuthenticationFailed();
                        } else {
                            dataUpdateEvent.onError();
                        }
                    }
                }

                @Override
                public void onSuccess(it.bz.beacon.adminapp.swagger.client.model.Info remoteInfo, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (remoteInfo != null) {
                        saveInfo(remoteInfo);
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
            Log.e(AdminApplication.LOG_TAG, e.getMessage());
            if (dataUpdateEvent != null) {
                dataUpdateEvent.onError();
            }
        }
    }

    public void insert(Info info, InsertEvent event) {
        new InsertAsyncTask(infoDao, event).execute(info);
    }

    public void insertMultiple(ArrayList<Info> infos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                infoDao.insertMultiple(infos);
            }
        }).start();
    }

    private static class InsertAsyncTask extends AsyncTask<Info, Void, Long> {

        private InfoDao asyncTaskDao;
        private InsertEvent insertEvent;

        InsertAsyncTask(InfoDao dao, InsertEvent event) {
            asyncTaskDao = dao;
            insertEvent = event;
        }

        @Override
        protected Long doInBackground(final Info... params) {
            return asyncTaskDao.insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            if (insertEvent != null) {
                insertEvent.onSuccess(id);
            }
        }
    }

    private static class LoadByIdTask extends AsyncTask<String, Void, Info> {

        private InfoDao asyncTaskDao;
        private LoadInfoEvent loadEvent;

        LoadByIdTask(InfoDao dao, LoadInfoEvent event) {
            asyncTaskDao = dao;
            loadEvent = event;
        }

        @Override
        protected Info doInBackground(String... ids) {
            return asyncTaskDao.getById(ids[0]);
        }

        @Override
        protected void onPostExecute(Info info) {
            if ((loadEvent != null) && (info != null)) {
                loadEvent.onSuccess(info);
            } else {
                loadEvent.onError();
            }
        }
    }
}
