// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.data.dao.GroupDao;
import it.bz.beacon.adminapp.data.entity.Group;
import it.bz.beacon.adminapp.data.event.DataUpdateEvent;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.swagger.client.ApiCallback;
import it.bz.beacon.adminapp.swagger.client.ApiException;

public class GroupRepository {

    private GroupDao groupDao;
    private LiveData<List<Group>> groups;
    private Storage storage;
    private int synchronizationInterval;

    public GroupRepository(Context context) {
        BeaconDatabase db = BeaconDatabase.getDatabase(context);
        groupDao = db.groupDao();
        groups = groupDao.getAll();
        storage = AdminApplication.getStorage();
        synchronizationInterval = context.getResources().getInteger(R.integer.synchronization_interval);
    }

    public LiveData<List<Group>> getAll() {
        if (shouldSynchronize()) {
            refreshGroups(null);
        }
        return groups;
    }

    private boolean shouldSynchronize() {
        return (storage.getLastSynchronizationGroups() + synchronizationInterval * 60000L < System.currentTimeMillis());
    }

    public void refreshGroups(final DataUpdateEvent dataUpdateEvent) {
        try {
            AdminApplication.getGroupControllerApi().getListUsingGET1Async(new ApiCallback<List<it.bz.beacon.adminapp.swagger.client.model.Group>>() {
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
                public void onSuccess(List<it.bz.beacon.adminapp.swagger.client.model.Group> result, int statusCode, Map<String, List<String>> responseHeaders) {

                    if (result != null) {
                        saveGroups(result);
                        if (dataUpdateEvent != null) {
                            dataUpdateEvent.onSuccess();
                        }
                        storage.setLastSynchronizationGroups(System.currentTimeMillis());
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

    private void saveGroup(it.bz.beacon.adminapp.swagger.client.model.Group remoteGroup) {
        Group group = new Group(remoteGroup);
        insert(group, null);
    }

    private void saveGroups(List<it.bz.beacon.adminapp.swagger.client.model.Group> remoteGroups) {
        ArrayList<it.bz.beacon.adminapp.data.entity.Group> groups = new ArrayList<>();
        for (it.bz.beacon.adminapp.swagger.client.model.Group remoteGroup : remoteGroups) {
            groups.add(new it.bz.beacon.adminapp.data.entity.Group(remoteGroup));
        }
        insertMultiple(groups);
    }

    public void insert(Group group, InsertEvent event) {
        new InsertAsyncTask(groupDao, event).execute(group);
    }

    public void insertMultiple(ArrayList<Group> groups) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                groupDao.insertMultiple(groups);
            }
        }).start();
    }

    private static class InsertAsyncTask extends AsyncTask<Group, Void, Long> {

        private GroupDao asyncTaskDao;
        private InsertEvent insertEvent;

        InsertAsyncTask(GroupDao dao, InsertEvent event) {
            asyncTaskDao = dao;
            insertEvent = event;
        }

        @Override
        protected Long doInBackground(final Group... params) {
            return asyncTaskDao.insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            if (insertEvent != null) {
                insertEvent.onSuccess(id);
            }
        }
    }

    public void deleteGroup(Group group) {
        groupDao.delete(group);
    }
}
