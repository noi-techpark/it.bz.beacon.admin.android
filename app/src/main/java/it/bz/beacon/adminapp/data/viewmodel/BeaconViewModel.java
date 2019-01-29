package it.bz.beacon.adminapp.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.repository.BeaconRepository;

public class BeaconViewModel extends AndroidViewModel {

    private BeaconRepository repository;

    private LiveData<List<BeaconMinimal>> beacons;

    public BeaconViewModel(Application application) {
        super(application);
        repository = new BeaconRepository(application);
        beacons = repository.getAll();
    }

    public LiveData<List<BeaconMinimal>> getAll() {
        return beacons;
    }

    public void deleteBeacon(Beacon beacon) {
        repository.deleteBeacon(beacon);
    }

    public LiveData<Beacon> getById(long id) {
        return repository.getById(id);
    }

    public void insert(Beacon beacon, InsertEvent event) {
        repository.insert(beacon, event);
    }

    public void insert(Beacon beacon) {
        repository.insert(beacon, null);
    }
}
