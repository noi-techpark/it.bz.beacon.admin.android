package it.bz.beacon.adminapp.ui.main.beacon;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import java.util.List;

import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.viewmodel.BeaconViewModel;

public class NearbyBeaconsFragment extends BaseBeaconsFragment {

    private BeaconViewModel beaconViewModel;

    public NearbyBeaconsFragment() {
        // Required empty public constructor
    }

    public static NearbyBeaconsFragment newInstance() {
        NearbyBeaconsFragment fragment = new NearbyBeaconsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beaconViewModel = ViewModelProviders.of(this).get(BeaconViewModel.class);
    }

    @Override
    protected void getBeacons(Observer<List<BeaconMinimal>> observer) {
        // TODO: find nearby beacons (Bluetooth)
        beaconViewModel.getAll().observe(this, observer);
    }
}
