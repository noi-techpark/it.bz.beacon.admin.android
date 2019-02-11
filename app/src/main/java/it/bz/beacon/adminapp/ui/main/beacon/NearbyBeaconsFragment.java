package it.bz.beacon.adminapp.ui.main.beacon;

import android.Manifest;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.SecureProfileListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleSecureProfileListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.ISecureProfile;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.event.LoadEvent;
import it.bz.beacon.adminapp.data.viewmodel.BeaconViewModel;
import it.bz.beacon.adminapp.eventbus.PubSub;
import it.bz.beacon.adminapp.eventbus.StatusFilterEvent;

public class NearbyBeaconsFragment extends BaseBeaconsFragment {

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private ProximityManager proximityManager;
    private BeaconViewModel beaconViewModel;
    private MutableLiveData<List<BeaconMinimal>> nearbyBeacons;

    public NearbyBeaconsFragment() {
        // Required empty public constructor
    }

    public static NearbyBeaconsFragment newInstance() {
        NearbyBeaconsFragment fragment = new NearbyBeaconsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        beaconViewModel = ViewModelProviders.of(this).get(BeaconViewModel.class);
        KontaktSDK.initialize(getString(R.string.apiKey));
        proximityManager = ProximityManagerFactory.create(getContext());
        proximityManager.setSecureProfileListener(createSecureProfileListener());
        super.onCreate(savedInstanceState);
    }

    private void startScanningIfLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                        .setTitle(getString(R.string.location_permission_title))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST);
                            }
                        })
                        .create();
                dialog.show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        } else {
            startScanning();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanning();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void getBeacons(Observer<List<BeaconMinimal>> observer) {
        nearbyBeacons = new MutableLiveData<>();
        nearbyBeacons.observe(this, observer);
    }

    @Subscribe
    public void onStatusFilterChanged(StatusFilterEvent event) {
        setStatusFilter(event.getStatus());
    }

    private void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }
        });
    }

    private boolean isBeaconInList(@NonNull List<BeaconMinimal> list, @NonNull BeaconMinimal beacon) {
        for (BeaconMinimal beaconMinimal : list) {
            if (beaconMinimal.getId() == beacon.getId()) {
                return true;
            }
        }
        return false;
    }

    private List<BeaconMinimal> updateBeaconInList(List<BeaconMinimal> list, BeaconMinimal freshBeacon) {
        for (BeaconMinimal beacon : list) {
            if (freshBeacon.getId() == beacon.getId()) {
                beacon.setManufacturerId(freshBeacon.getManufacturerId());
                beacon.setName(freshBeacon.getName());
                beacon.setMajor(freshBeacon.getMajor());
                beacon.setMinor(freshBeacon.getMinor());
                beacon.setBatteryLevel(freshBeacon.getBatteryLevel());
                beacon.setStatus(freshBeacon.getStatus());
                beacon.setLat(freshBeacon.getLat());
                beacon.setLng(freshBeacon.getLng());
                beacon.setRssi(freshBeacon.getRssi());
            }
        }
        Collections.sort(list, new Comparator<BeaconMinimal>() {
            public int compare(BeaconMinimal obj1, BeaconMinimal obj2) {
                if ((obj1 != null) && (obj2 != null) && (obj1.getRssi() != null) && (obj2.getRssi() != null))
                    return obj2.getRssi() - obj1.getRssi();
                else
                    return 0;
            }
        });
        return list;
    }

    private List<BeaconMinimal> addBeaconToList(List<BeaconMinimal> list, BeaconMinimal newBeacon) {
        list.add(newBeacon);
        Collections.sort(list, new Comparator<BeaconMinimal>() {
            public int compare(BeaconMinimal obj1, BeaconMinimal obj2) {
                if ((obj1 != null) && (obj2 != null) && (obj1.getRssi() != null) && (obj2.getRssi() != null))
                    return obj2.getRssi() - obj1.getRssi();
                else
                    return 0;
            }
        });
        return list;
    }

    private List<BeaconMinimal> removeBeaconFromList(List<BeaconMinimal> list, BeaconMinimal beacon) {
        if ((list != null) && (list.size() > 0)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() == beacon.getId()) {
                    list.remove(i);
                    break;
                }
            }
        }
        return list;
    }

    private SecureProfileListener createSecureProfileListener() {
        return new SimpleSecureProfileListener() {
            @Override
            public void onProfileDiscovered(final ISecureProfile profile) {
                updateList(profile);
                super.onProfileDiscovered(profile);
            }

            @Override
            public void onProfilesUpdated(List<ISecureProfile> profiles) {
                for (ISecureProfile profile : profiles) {
                    updateList(profile);
                }
                super.onProfilesUpdated(profiles);
            }

            @Override
            public void onProfileLost(ISecureProfile profile) {
                beaconViewModel.getByInstanceId(profile.getUniqueId(), new LoadEvent() {
                    @Override
                    public void onSuccess(BeaconMinimal beaconMinimal) {
                        List<BeaconMinimal> newList;
                        if (nearbyBeacons.getValue() == null) {
                            newList = new ArrayList<>();
                        }
                        else {
                            newList = nearbyBeacons.getValue();
                        }
                        newList = removeBeaconFromList(newList, beaconMinimal);
                        nearbyBeacons.setValue(newList);
                    }
                });
                super.onProfileLost(profile);
            }

            private void updateList(final ISecureProfile profile) {
                beaconViewModel.getByInstanceId(profile.getUniqueId(), new LoadEvent() {
                    @Override
                    public void onSuccess(BeaconMinimal beaconMinimal) {
                        beaconMinimal.setRssi(profile.getRssi());
                        List<BeaconMinimal> newList;
                        if (nearbyBeacons.getValue() == null) {
                            newList = new ArrayList<>();
                        }
                        else {
                            newList = nearbyBeacons.getValue();
                        }

                        if (!isBeaconInList(newList, beaconMinimal)) {
                            newList = addBeaconToList(newList, beaconMinimal);
                            nearbyBeacons.setValue(newList);
                        }
                        else {
                            updateBeaconInList(newList, beaconMinimal);
                            nearbyBeacons.setValue(newList);
                        }
                    }
                });
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        startScanningIfLocationPermissionGranted();
        PubSub.getInstance().register(this);
    }

    @Override
    public void onPause() {
        proximityManager.stopScanning();
        PubSub.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    public void onDetach() {
        proximityManager.disconnect();
        proximityManager = null;
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        swipeBeacons.setRefreshing(false);
    }
}
