// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.ui.main.beacon;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.event.LoadBeaconMinimalEvent;
import it.bz.beacon.adminapp.data.viewmodel.BeaconViewModel;
import it.bz.beacon.adminapp.eventbus.PubSub;
import it.bz.beacon.adminapp.eventbus.StatusFilterEvent;
import it.bz.beacon.adminapp.swagger.client.ApiCallback;
import it.bz.beacon.adminapp.swagger.client.ApiException;
import it.bz.beacon.adminapp.swagger.client.api.TrustedBeaconControllerApi;
import it.bz.beacon.adminapp.swagger.client.model.BeaconBatteryLevelUpdate;

public class NearbyBeaconsFragment extends BaseBeaconsFragment {

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private ProximityManager proximityManager;
    private BeaconViewModel beaconViewModel;
    private MutableLiveData<List<BeaconMinimal>> nearbyBeacons;
    private TrustedBeaconControllerApi trustedApi;
    private HashMap<String, BeaconMinimal> nearbyBeaconsMap;

    public NearbyBeaconsFragment() {
        // Required empty public constructor
    }

    public static NearbyBeaconsFragment newInstance(String statusFilter) {
        NearbyBeaconsFragment fragment = new NearbyBeaconsFragment();
        fragment.prepareStatusFilter(statusFilter);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        trustedApi = AdminApplication.getTrustedBeaconControllerApi();
        if (!getString(R.string.trustedApiUser).isEmpty() && !getString(R.string.trustedApiPassword).isEmpty()) {
            trustedApi.getApiClient().setUsername(getString(R.string.trustedApiUser));
            trustedApi.getApiClient().setPassword(getString(R.string.trustedApiPassword));
        }
        beaconViewModel = ViewModelProviders.of(this).get(BeaconViewModel.class);
        KontaktSDK.initialize(getString(R.string.apiKey));
        proximityManager = ProximityManagerFactory.create(getContext());
        proximityManager.setSecureProfileListener(createSecureProfileListener());
        nearbyBeaconsMap = new HashMap<>();
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
        if (!isBluetoothAvailable()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), getActivity().getString(R.string.error_bluetooth_disable), Snackbar.LENGTH_LONG)
                    .setAction(getActivity().getString(R.string.enable), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enableBluetooth();
                        }
                    })
                    .show();
            swipeBeacons.setRefreshing(false);
        } else if (!isLocationAvailable()) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), getActivity().getString(R.string.error_location_disable), Snackbar.LENGTH_LONG)
                    .setAction(getActivity().getString(R.string.enable), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enableLocation();
                        }
                    })
                    .show();
            swipeBeacons.setRefreshing(false);
        } else {
            proximityManager.connect(new OnServiceReadyListener() {
                @Override
                public void onServiceReady() {
                    proximityManager.startScanning();
                }
            });
        }
    }

    public boolean isBluetoothAvailable() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return bluetoothAdapter != null
                && bluetoothAdapter.isEnabled()
                && bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON;
    }

    public boolean isLocationAvailable() {
        int locationMode = Settings.Secure.LOCATION_MODE_OFF;
        try {
            locationMode = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            Log.w(AdminApplication.LOG_TAG, e.getLocalizedMessage());
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    public void enableBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(intentBtEnabled, REQUEST_ENABLE_BT);
        }
    }

    public void enableLocation() {
        if (!isLocationAvailable()) {
            Intent intentLocationEnabled = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            int REQUEST_ENABLE_LOCATION = 1;
            startActivityForResult(intentLocationEnabled, REQUEST_ENABLE_LOCATION);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list, menu);
        MenuItem search = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                setSearchFilter(s);
                return false;
            }
        });
    }

    private boolean isBeaconInList(@NonNull BeaconMinimal beacon) {
        return nearbyBeaconsMap.containsKey(beacon.getId());
    }

    private List<BeaconMinimal> updateBeaconInList(BeaconMinimal freshBeacon) {
        nearbyBeaconsMap.put(freshBeacon.getId(), freshBeacon);
        List<BeaconMinimal> list = new ArrayList<>(nearbyBeaconsMap.values());
        Collections.sort(list, (obj1, obj2) -> {
            if ((obj1 != null) && (obj2 != null) && (obj1.getRssi() != null) && (obj2.getRssi() != null)) {
                return obj2.getRssi() - obj1.getRssi();
            } else {
                return 0;
            }
        });
        return list;
    }

    private List<BeaconMinimal> addBeaconToList(BeaconMinimal newBeacon) {
        nearbyBeaconsMap.put(newBeacon.getId(), newBeacon);
        List<BeaconMinimal> list = new ArrayList<>(nearbyBeaconsMap.values());
        Collections.sort(list, (obj1, obj2) -> {
            if ((obj1 != null) && (obj2 != null) && (obj1.getRssi() != null) && (obj2.getRssi() != null)) {
                return obj2.getRssi() - obj1.getRssi();
            } else {
                return 0;
            }
        });
        return list;
    }

    private List<BeaconMinimal> removeBeaconFromList(BeaconMinimal beacon) {
        nearbyBeaconsMap.remove(beacon.getId());
        return new ArrayList<>(nearbyBeaconsMap.values());
    }

    private SecureProfileListener createSecureProfileListener() {
        return new SimpleSecureProfileListener() {
            @Override
            public void onProfileDiscovered(final ISecureProfile profile) {
                super.onProfileDiscovered(profile);
                updateBatteryStatus(profile);
                updateList(profile);
            }

            @Override
            public void onProfilesUpdated(List<ISecureProfile> profiles) {
                super.onProfilesUpdated(profiles);
                for (ISecureProfile profile : profiles) {
                    updateList(profile);
                }
            }

            @Override
            public void onProfileLost(ISecureProfile profile) {
                super.onProfileLost(profile);
                beaconViewModel.getByInstanceId(profile.getUniqueId(), new LoadBeaconMinimalEvent() {
                    @Override
                    public void onSuccess(BeaconMinimal beaconMinimal) {
                        nearbyBeacons.setValue(removeBeaconFromList(beaconMinimal));
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            private void updateList(final ISecureProfile profile) {
                beaconViewModel.getByInstanceId(profile.getUniqueId(), new LoadBeaconMinimalEvent() {
                    @Override
                    public void onSuccess(BeaconMinimal beaconMinimal) {
                        beaconMinimal.setRssi(profile.getRssi());
                        synchronized (this) {
                            if (isBeaconInList(beaconMinimal)) {
                                nearbyBeacons.setValue(updateBeaconInList(beaconMinimal));
                            } else {
                                nearbyBeacons.setValue(addBeaconToList(beaconMinimal));
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        Log.e(AdminApplication.LOG_TAG, "beaconViewModel.getByInstanceId failed");
                    }
                });
            }

            private void updateBatteryStatus(ISecureProfile profile) {
                try {
                    if (profile.getBatteryLevel() > 0) {
                        BeaconBatteryLevelUpdate update = new BeaconBatteryLevelUpdate();
                        update.setBatteryLevel(profile.getBatteryLevel());
                        String manufacturerId = profile.getUniqueId();
                        trustedApi.updateUsingPATCH2Async(update, manufacturerId, new ApiCallback<it.bz.beacon.adminapp.swagger.client.model.Beacon>() {
                            @Override
                            public void onFailure(ApiException e, int i, Map<String, List<String>> map) {
                                Log.e(AdminApplication.LOG_TAG, "updateBatteryStatus failed: " + e.getLocalizedMessage());
                            }

                            @Override
                            public void onSuccess(it.bz.beacon.adminapp.swagger.client.model.Beacon beacon, int i, Map<String, List<String>> map) {
                            }

                            @Override
                            public void onUploadProgress(long l, long l1, boolean b) {

                            }

                            @Override
                            public void onDownloadProgress(long l, long l1, boolean b) {

                            }
                        }).execute();
                    }
                } catch (Exception e) {
                    Log.e(AdminApplication.LOG_TAG, e.getLocalizedMessage());
                }
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
        proximityManager.disconnect();
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
        super.onRefresh();
        startScanningIfLocationPermissionGranted();
    }
}
