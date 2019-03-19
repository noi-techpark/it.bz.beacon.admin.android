package it.bz.beacon.adminapp.data.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.bz.beacon.adminapp.data.entity.BeaconImage;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.repository.BeaconImageRepository;

public class BeaconImageViewModel extends AndroidViewModel {

    private BeaconImageRepository repository;

    public BeaconImageViewModel(Application application) {
        super(application);
        repository = new BeaconImageRepository(application);
    }

    public LiveData<List<BeaconImage>> getAllByBeaconId(String beaconId) {
        return repository.getAllByBeaconId(beaconId);
    }

    public void deleteBeaconImage(BeaconImage beaconImage) {
        repository.deleteBeaconImage(beaconImage);
    }

    public LiveData<BeaconImage> getById(long id) {
        return repository.getById(id);
    }

    public void insert(BeaconImage beaconImage, InsertEvent event) {
        repository.insert(beaconImage, event);
    }

    public void insert(BeaconImage beaconImage) {
        repository.insert(beaconImage, null);
    }
}
