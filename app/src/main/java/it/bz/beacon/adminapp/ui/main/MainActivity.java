// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.ui.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.event.DataUpdateEvent;
import it.bz.beacon.adminapp.data.repository.GroupRepository;
import it.bz.beacon.adminapp.data.repository.InfoRepository;
import it.bz.beacon.adminapp.eventbus.LocationEvent;
import it.bz.beacon.adminapp.eventbus.PubSub;
import it.bz.beacon.adminapp.eventbus.RadiusFilterEvent;
import it.bz.beacon.adminapp.eventbus.StatusFilterEvent;
import it.bz.beacon.adminapp.ui.BaseActivity;
import it.bz.beacon.adminapp.ui.about.AboutFragment;
import it.bz.beacon.adminapp.ui.issue.IssuesFragment;
import it.bz.beacon.adminapp.ui.issue.map.IssuesMapFragment;
import it.bz.beacon.adminapp.ui.main.beacon.BeaconTabsFragment;
import it.bz.beacon.adminapp.ui.main.beacon.map.MapFragment;
import it.bz.beacon.adminapp.ui.map.LocationDisabledFragment;
import it.bz.beacon.adminapp.ui.map.OnRetryLoadMapListener;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnRetryLoadMapListener {

    public static final String EXTRA_REFRESH_DATA = "EXTRA_REFRESH_DATA";

    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_FUSED = 2;
    private static final int MODE_BEACONS = 0;
    private static final int MODE_ISSUES = 1;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawer;

    @BindView(R.id.navigation_view)
    protected NavigationView navigationView;

    protected boolean isMapShowing = false;
    protected boolean isFilterActive = false;
    private int filterIndex = 0;
    private Storage storage;
    private String[] statusFilterItems;
    private String[] statusFilterValues;
    private String[] radiusFilterItems;
    private int[] radiusFilterValues;
    private int currentMode = MODE_BEACONS;

    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        storage = AdminApplication.getStorage();
        setSupportActionBar(toolbar);
        setupNavigationDrawer();

        if(getIntent() != null) {
            if (getIntent().getBooleanExtra(MainActivity.EXTRA_REFRESH_DATA, false)) {

                GroupRepository groupRepository = new GroupRepository(this);
                groupRepository.refreshGroups(new DataUpdateEvent() {
                    @Override
                    public void onSuccess() {
                        InfoRepository infoRepository = new InfoRepository(MainActivity.this);
                        infoRepository.refreshInfos(new DataUpdateEvent() {
                            @Override
                            public void onSuccess() {
                                Log.i(AdminApplication.LOG_TAG, "Infos refreshed!");
                            }

                            @Override
                            public void onError() {
                                Log.e(AdminApplication.LOG_TAG, "Error refreshing infos");

                            }

                            @Override
                            public void onAuthenticationFailed() {
                                Log.e(AdminApplication.LOG_TAG, "Authentication failed");
                                // ignore
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        Log.e(AdminApplication.LOG_TAG, "Error refreshing groups");
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        Log.e(AdminApplication.LOG_TAG, "Authentication failed");
                    }
                });
            }
        }

        statusFilterItems = new String[]{getString(R.string.status_all),
                getString(R.string.status_ok),
                getString(R.string.status_configuration_pending),
                getString(R.string.status_battery_low),
                getString(R.string.status_issue),
                getString(R.string.status_unknown_status),
                getString(R.string.status_not_accessible),
                getString(R.string.status_installed),
                getString(R.string.status_not_installed)};
        statusFilterValues = new String[]{Beacon.STATUS_ALL,
                Beacon.STATUS_OK,
                Beacon.STATUS_CONFIGURATION_PENDING,
                Beacon.STATUS_BATTERY_LOW,
                Beacon.STATUS_ISSUE,
                Beacon.STATUS_UNKNOWN_STATUS,
                Beacon.STATUS_NOT_ACCESSIBLE,
                Beacon.STATUS_INSTALLED,
                Beacon.STATUS_NOT_INSTALLED};
        radiusFilterItems = new String[]{getString(R.string.status_all),
                "1 km",
                "5 km",
                "10 km"};
        radiusFilterValues = new int[]{0,
                1000,
                5000,
                10000};
        navigationView.getMenu().getItem(0).setChecked(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        switchFragment(getString(R.string.beacons), BeaconTabsFragment.newInstance(""));
        getMyLocation();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        TextView txtUsername = navigationView.getHeaderView(0).findViewById(R.id.navigation_username);
        txtUsername.setText(storage.getLoginUserName());

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            TextView txtVersion = navigationView.getRootView().findViewById(R.id.txt_version);
            if (txtVersion != null) {
                txtVersion.setText(String.format(Locale.getDefault(), getString(R.string.version), version));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Button btnLogout = navigationView.getHeaderView(0).findViewById(R.id.navigation_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AdminApplication.logout(MainActivity.this);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_map:
                initMapFragment();
                break;
            case R.id.menu_list:
                isMapShowing = false;
                if (currentMode == MODE_BEACONS) {
                    switchFragment(getString(R.string.beacons), BeaconTabsFragment.newInstance(statusFilterValues[filterIndex]));
                } else {
                    switchFragment(getString(R.string.issues), IssuesFragment.newInstance(currentLocation));
                }
                break;
            case R.id.menu_filter:
                if ((currentMode == MODE_ISSUES) && (currentLocation == null)) {
                    showLocationNotAvailableDialog();
                } else {
                    showFilterDialog();
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        if (currentMode == MODE_BEACONS) {
            builder.setTitle(getString(R.string.filter_beacons));
            builder.setSingleChoiceItems(statusFilterItems, filterIndex, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    filterIndex = item;
                    isFilterActive = (item != 0);
                }
            });
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    PubSub.getInstance().post(new StatusFilterEvent(statusFilterValues[filterIndex]));
                    invalidateOptionsMenu();
                }
            });
        }
        if (currentMode == MODE_ISSUES) {
            builder.setTitle(getString(R.string.filter_issues));
            builder.setSingleChoiceItems(radiusFilterItems, filterIndex, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    filterIndex = item;
                    isFilterActive = (item != 0);
                }
            });
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    PubSub.getInstance().post(new RadiusFilterEvent(radiusFilterValues[filterIndex]));
                    invalidateOptionsMenu();
                }
            });
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showLocationNotAvailableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setTitle(getString(R.string.filter_issues));
        builder.setMessage(getString(R.string.location_not_available));
        builder.setPositiveButton(getString(R.string.ok), null);
        builder.show();
    }

    private void getMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                        .setTitle(getString(R.string.location_permission_title))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST_FUSED);
                            }
                        })
                        .create();
                dialog.show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_FUSED);
            }
        } else {
            getFusedLocation();
        }
    }

    private void getFusedLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                PubSub.getInstance().post(new LocationEvent(currentLocation));
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem filterItem = menu.findItem(R.id.menu_filter);

        if (filterItem != null) {
            if (isFilterActive) {
                filterItem.setIcon(R.drawable.ic_menu_filter);
            } else {
                filterItem.setIcon(R.drawable.ic_menu_filter_outline);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        navigationView.setCheckedItem(id);

        isFilterActive = false;
        filterIndex = 0;
        isMapShowing = false;

        switch (id) {
            case R.id.navigation_beacons:
                currentMode = MODE_BEACONS;
                switchFragment(getString(R.string.beacons), BeaconTabsFragment.newInstance(statusFilterValues[filterIndex]));
                break;
            case R.id.navigation_issues:
                currentMode = MODE_ISSUES;
                switchFragment(getString(R.string.issues), IssuesFragment.newInstance(currentLocation));
                break;
            case R.id.navigation_about:
                switchFragment(getString(R.string.about), AboutFragment.newInstance());
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initMapFragment() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                        .setTitle(R.string.location_permission_title)
                        .setMessage(R.string.location_permission_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        } else {
            showMapFragment();
        }
    }

    private void showMapFragment() {
        if (getSupportFragmentManager() != null) {
            isMapShowing = true;
            if (currentMode == MODE_BEACONS) {
                MapFragment fragment = new MapFragment();
                switchFragment(getString(R.string.beacons), fragment);
            }
            if (currentMode == MODE_ISSUES) {
                IssuesMapFragment fragment = new IssuesMapFragment();
                switchFragment(getString(R.string.issues), fragment);
            }
            invalidateOptionsMenu();
        }
    }

    private void showLocationDisabledFragment() {
        if (getSupportFragmentManager() != null) {
            isMapShowing = false;
            LocationDisabledFragment fragment = LocationDisabledFragment.newInstance(this);
            switchFragment(getString(R.string.beacons), fragment);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        showMapFragment();
                    } else {
                        showLocationDisabledFragment();
                    }
                } else {
                    showLocationDisabledFragment();
                }
                break;
            case LOCATION_PERMISSION_REQUEST_FUSED:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        getFusedLocation();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void onRetry() {
        initMapFragment();
    }

    private void switchFragment(final String title, final Fragment fragment) {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        if (getSupportFragmentManager() != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerView, fragment, title)
                    .runOnCommit(new Runnable() {
                        @Override
                        public void run() {
                            PubSub.getInstance().post(new StatusFilterEvent(statusFilterValues[filterIndex]));
                        }
                    })
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PubSub.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PubSub.getInstance().unregister(this);
    }
}
