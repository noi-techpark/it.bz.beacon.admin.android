package it.bz.beacon.adminapp.data.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.data.entity.Info;
import it.bz.beacon.adminapp.data.event.DataUpdateEvent;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.event.LoadInfoEvent;
import it.bz.beacon.adminapp.data.repository.InfoRepository;

public class InfoViewModel extends AndroidViewModel {

    private InfoRepository repository;

    private LiveData<List<Info>> infos;

    public InfoViewModel(Application application) {
        super(application);
        repository = new InfoRepository(application);
        infos = repository.getAll();
    }

    public LiveData<List<Info>> getAll() {
        return infos;
    }

    public LiveData<Info> getByIdLive(String id) {
        return repository.getByIdLive(id);
    }

    public void getById(String id, LoadInfoEvent loadEvent) {
        repository.getById(id, loadEvent);
    }

    public void getRefreshedById(String id, LoadInfoEvent loadEvent) {
        repository.refreshInfo(id, new DataUpdateEvent() {
            @Override
            public void onSuccess() {
                repository.getById(id, loadEvent);
            }

            @Override
            public void onError() {
                loadEvent.onError();
            }

            @Override
            public void onAuthenticationFailed() {
                Log.d(AdminApplication.LOG_TAG, "onAuthenticationFailed: ignore");
            }
        });
    }

    public void insert(Info info, InsertEvent event) {
        repository.insert(info, event);
    }

    public void insert(Info info) {
        repository.insert(info, null);
    }
}
