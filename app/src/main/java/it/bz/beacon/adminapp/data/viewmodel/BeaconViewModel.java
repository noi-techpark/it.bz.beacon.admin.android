package it.bz.beacon.adminapp.data.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.event.LoadBeaconEvent;
import it.bz.beacon.adminapp.data.event.LoadBeaconMinimalEvent;
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

    public LiveData<Beacon> getByIdLive(long id) {
        return repository.getByIdLive(id);
    }

    public void getById(long id, LoadBeaconEvent loadEvent) {
        repository.getById(id, loadEvent);
    }

    public void getByInstanceId(String instanceId, LoadBeaconMinimalEvent loadEvent) {
        repository.getByInstanceId(instanceId, loadEvent);
    }

    public void insert(Beacon beacon, InsertEvent event) {
        repository.insert(beacon, event);
    }

    public void insert(Beacon beacon) {
        repository.insert(beacon, null);
    }
}
