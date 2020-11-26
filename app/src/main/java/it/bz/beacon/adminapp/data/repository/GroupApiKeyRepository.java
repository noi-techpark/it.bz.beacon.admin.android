package it.bz.beacon.adminapp.data.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.data.dao.GroupApiKeyDao;
import it.bz.beacon.adminapp.data.entity.GroupApiKey;
import it.bz.beacon.adminapp.data.event.DataUpdateEvent;
import it.bz.beacon.adminapp.data.event.LoadGroupApiKeyEvent;
import it.bz.beacon.adminapp.swagger.client.ApiCallback;
import it.bz.beacon.adminapp.swagger.client.ApiException;
import it.bz.beacon.adminapp.swagger.client.model.User;

public class GroupApiKeyRepository {

    private GroupApiKeyDao groupApiKeyDao;
    private LiveData<List<GroupApiKey>> groupApiKeys;
    private Storage storage;
    private int synchronizationInterval;

    public GroupApiKeyRepository(Context context) {
        BeaconDatabase db = BeaconDatabase.getDatabase(context);
        groupApiKeyDao = db.groupApiKeyDao();
        groupApiKeys = groupApiKeyDao.getAll();
        storage = AdminApplication.getStorage();
        synchronizationInterval = context.getResources().getInteger(R.integer.synchronization_interval);
    }

    public LiveData<List<GroupApiKey>> getAll() {
        if (shouldSynchronize()) {
            refreshGroupApiKeys(null);
        }
        return groupApiKeys;
    }

    private boolean shouldSynchronize() {
        return (storage.getLastSynchronizationGroupApiKeys() + synchronizationInterval * 60000L < System.currentTimeMillis());
    }

    public void refreshGroupApiKeys(final DataUpdateEvent dataUpdateEvent) {
        try {

            AdminApplication.getUserControllerApi().getListUsingGET7Async(new ApiCallback<List<User>>() {
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
                public void onSuccess(List<User> result, int statusCode, Map<String, List<String>> responseHeaders) {
                    Optional<User> loggedUser = result.stream().filter(user -> user.getUsername().equals(storage.getLoginUserName())).findFirst();
                    if (loggedUser.isPresent()) {

                        try {
                            AdminApplication.getUserControllerApi().getApiKeyUsingGET1Async(
                                    loggedUser.get().getId(),
                                    new ApiCallback<List<it.bz.beacon.adminapp.swagger.client.model.GroupApiKey>>() {
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
                                        public void onSuccess(List<it.bz.beacon.adminapp.swagger.client.model.GroupApiKey> result, int statusCode, Map<String, List<String>> responseHeaders) {

                                            if (result != null) {
                                                saveGroupApiKeys(result);
                                                if (dataUpdateEvent != null) {
                                                    dataUpdateEvent.onSuccess();
                                                }
                                                storage.setLastSynchronizationGroupApiKeys(System.currentTimeMillis());
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
                            dataUpdateEvent.onError();
                        }
                    } else {
                        dataUpdateEvent.onError();
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

    private void saveGroupApiKeys(List<it.bz.beacon.adminapp.swagger.client.model.GroupApiKey> remoteGroupApiKeys) {
        ArrayList<GroupApiKey> groupApiKeys = new ArrayList<>();
        for (it.bz.beacon.adminapp.swagger.client.model.GroupApiKey remoteGroupApiKey : remoteGroupApiKeys) {
            groupApiKeys.add(new GroupApiKey(remoteGroupApiKey));
        }
        insertMultiple(groupApiKeys);
    }

    public void insertMultiple(ArrayList<GroupApiKey> groupApiKeys) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                groupApiKeyDao.deleteAll();
                groupApiKeyDao.insertMultiple(groupApiKeys);
            }
        }).start();
    }

    public void getByGroupId(Long groupId, LoadGroupApiKeyEvent loadEvent) {
        LoadByGroupIdTask task = new LoadByGroupIdTask(groupApiKeyDao, loadEvent);
        if (shouldSynchronize()) {
            refreshGroupApiKeys(new DataUpdateEvent() {
                @Override
                public void onSuccess() {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupId);
                }

                @Override
                public void onError() {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupId);
                }

                @Override
                public void onAuthenticationFailed() {
                    Log.d(AdminApplication.LOG_TAG, "onAuthenticationFailed: ignore");
                }
            });
        } else {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupId);
        }
    }

    private static class LoadByGroupIdTask extends AsyncTask<Long, Void, GroupApiKey> {

        private GroupApiKeyDao asyncTaskDao;
        private GroupApiKeyDao repository;
        private LoadGroupApiKeyEvent loadEvent;

        LoadByGroupIdTask(GroupApiKeyDao dao, LoadGroupApiKeyEvent event) {
            asyncTaskDao = dao;
            loadEvent = event;
        }

        @Override
        protected GroupApiKey doInBackground(Long... ids) {
            return asyncTaskDao.getByGroupId(ids[0]);
        }

        @Override
        protected void onPostExecute(GroupApiKey groupApiKey) {
            if ((loadEvent != null) && (groupApiKey != null)) {
                loadEvent.onSuccess(groupApiKey);
            } else {
                loadEvent.onError();
            }
        }
    }
}
