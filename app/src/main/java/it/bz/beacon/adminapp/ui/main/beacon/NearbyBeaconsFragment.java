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
import android.util.Log;

import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.SecureProfileListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleSecureProfileListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;
import com.kontakt.sdk.android.common.profile.ISecureProfile;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import it.bz.beacon.adminapp.AdminApplication;
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
        proximityManager.setIBeaconListener(createIBeaconListener());
        proximityManager.setEddystoneListener(createEddystoneListener());
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
                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST);
                            }
                        })
                        .create();
                dialog.show();
            }
            else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        }
        else {
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

    private IBeaconListener createIBeaconListener() {
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                Log.i(AdminApplication.LOG_TAG, "IBeacon discovered: " + ibeacon.toString());
                Log.i(AdminApplication.LOG_TAG, "region discovered: " + region.toString());

                beaconViewModel.getByMajorMinor(ibeacon.getMajor(), ibeacon.getMinor(), new LoadEvent() {
                    @Override
                    public void onSuccess(BeaconMinimal beaconMinimal) {
                        List<BeaconMinimal> newList;
                        if (nearbyBeacons.getValue() == null) {
                            newList = new ArrayList<BeaconMinimal>();
                        }
                        else {
                            newList = nearbyBeacons.getValue();
                        }

                        if (!isBeaconInList(newList, beaconMinimal)) {
                            newList.add(beaconMinimal);
                            nearbyBeacons.setValue(newList);
                        }
                    }
                });
            }

            @Override
            public void onIBeaconLost(IBeaconDevice ibeacon, IBeaconRegion region) {
                Log.e(AdminApplication.LOG_TAG, "IBeacon lost: " + ibeacon.toString());

                beaconViewModel.getByMajorMinor(ibeacon.getMajor(), ibeacon.getMinor(), new LoadEvent() {
                    @Override
                    public void onSuccess(BeaconMinimal beaconMinimal) {
                        List<BeaconMinimal> newList = nearbyBeacons.getValue();

                        if ((newList != null) && (newList.size() > 0)) {
                            for (int i = 0; i < newList.size(); i++) {
                                if (newList.get(i).getId() == beaconMinimal.getId()) {
                                    newList.remove(i);
                                    nearbyBeacons.setValue(newList);
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        };
    }

    private EddystoneListener createEddystoneListener() {
        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                Log.i(AdminApplication.LOG_TAG, "Eddystone discovered: " + eddystone.toString());

                beaconViewModel.getByInstanceId(eddystone.getInstanceId(), new LoadEvent() {
                    @Override
                    public void onSuccess(BeaconMinimal beaconMinimal) {
                        List<BeaconMinimal> newList;
                        if (nearbyBeacons.getValue() == null) {
                            newList = new ArrayList<BeaconMinimal>();
                        }
                        else {
                            newList = nearbyBeacons.getValue();
                        }

                        if (!isBeaconInList(newList, beaconMinimal)) {
                            newList.add(beaconMinimal);
                            nearbyBeacons.setValue(newList);
                        }
                    }
                });
            }

            @Override
            public void onEddystoneLost(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                Log.e(AdminApplication.LOG_TAG, "Eddystone lost: " + eddystone.toString());

                beaconViewModel.getByInstanceId(eddystone.getInstanceId(), new LoadEvent() {
                    @Override
                    public void onSuccess(BeaconMinimal beaconMinimal) {
                        List<BeaconMinimal> newList = nearbyBeacons.getValue();

                        if ((newList != null) && (newList.size() > 0)) {
                            for (int i = 0; i < newList.size(); i++) {
                                if (newList.get(i).getId() == beaconMinimal.getId()) {
                                    newList.remove(i);
                                    nearbyBeacons.setValue(newList);
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        };
    }

    private SecureProfileListener createSecureProfileListener() {
        return new SimpleSecureProfileListener() {
            @Override
            public void onProfileDiscovered(ISecureProfile profile) {
                super.onProfileDiscovered(profile);
            }

            @Override
            public void onProfilesUpdated(List<ISecureProfile> profiles) {
                super.onProfilesUpdated(profiles);
            }

            @Override
            public void onProfileLost(ISecureProfile profile) {
                super.onProfileLost(profile);
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
}
