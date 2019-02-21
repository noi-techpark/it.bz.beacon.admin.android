package it.bz.beacon.adminapp.ui.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.squareup.otto.Subscribe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.eventbus.LogoutEvent;
import it.bz.beacon.adminapp.eventbus.PubSub;
import it.bz.beacon.adminapp.eventbus.StatusFilterEvent;
import it.bz.beacon.adminapp.ui.BaseActivity;
import it.bz.beacon.adminapp.ui.about.AboutFragment;
import it.bz.beacon.adminapp.ui.issue.IssuesFragment;
import it.bz.beacon.adminapp.ui.login.LoginActivity;
import it.bz.beacon.adminapp.ui.main.beacon.BeaconTabsFragment;
import it.bz.beacon.adminapp.ui.main.map.LocationDisabledFragment;
import it.bz.beacon.adminapp.ui.main.map.MapFragment;
import it.bz.beacon.adminapp.ui.main.map.OnRetryLoadMapListener;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnRetryLoadMapListener {

    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private static final int MODE_BEACONS = 0;
    private static final int MODE_ISSUES = 1;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawer;

    @BindView(R.id.navigation_view)
    protected NavigationView navigationView;

    protected boolean isMapShowing = false;
    protected boolean isFilterActive = false;
    protected int filterIndex = 0;
    private Storage storage;
    private String[] filterItems;
    private String[] filterValues;
    private int currentMode = MODE_BEACONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        storage = AdminApplication.getStorage();
        setSupportActionBar(toolbar);
        setupNavigationDrawer();

        filterItems = new String[]{getString(R.string.status_all),
                getString(R.string.status_ok),
                getString(R.string.status_configuration_pending),
                getString(R.string.status_battery_low),
                getString(R.string.status_issue),
                getString(R.string.status_no_signal)};
        filterValues = new String[]{Beacon.STATUS_ALL,
                Beacon.STATUS_OK,
                Beacon.STATUS_CONFIGURATION_PENDING,
                Beacon.STATUS_BATTERY_LOW,
                Beacon.STATUS_ISSUE,
                Beacon.STATUS_NO_SIGNAL};
        navigationView.getMenu().getItem(0).setChecked(true);

        switchFragment(getString(R.string.beacons), BeaconTabsFragment.newInstance());
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

        Button btnLogout = navigationView.getHeaderView(0).findViewById(R.id.navigation_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logout();
                }
            });
        }
    }

    private void logout() {
        storage.clearStorage();
        deleteDatabase(BeaconDatabase.DB_NAME);
        AdminApplication.setBearerToken("");
        openLogin();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void openLogin() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
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
                    switchFragment(getString(R.string.beacons), BeaconTabsFragment.newInstance());
                }
                else {
                    switchFragment(getString(R.string.issues), IssuesFragment.newInstance());
                }
                break;
            case R.id.menu_filter:
                showFilterDialog();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setTitle(getString(R.string.filter_dialog_title));
        builder.setSingleChoiceItems(filterItems, filterIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                filterIndex = item;
                isFilterActive = (item != 0);
            }
        });
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                PubSub.getInstance().post(new StatusFilterEvent(filterValues[filterIndex]));
                invalidateOptionsMenu();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem filterItem = menu.findItem(R.id.menu_filter);

        if (filterItem != null) {
            if (isFilterActive) {
                filterItem.setIcon(R.drawable.ic_menu_filter);
            }
            else {
                filterItem.setIcon(R.drawable.ic_menu_filter_outline);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        navigationView.setCheckedItem(id);

        switch (id) {
            case R.id.navigation_beacons:
                currentMode = MODE_BEACONS;
                switchFragment(getString(R.string.beacons), BeaconTabsFragment.newInstance());
                isMapShowing = false;
                break;
            case R.id.navigation_issues:
                currentMode = MODE_ISSUES;
                switchFragment(getString(R.string.issues), IssuesFragment.newInstance());
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
            }
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        }
        else {
            showMapFragment();
        }
    }

    private void showMapFragment() {
        if (!isMapShowing && getSupportFragmentManager() != null) {
            isMapShowing = true;
            MapFragment fragment = new MapFragment();
            switchFragment(getString(R.string.beacons), fragment);
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
                    }
                    else {
                        showLocationDisabledFragment();
                    }
                }
                else {
                    showLocationDisabledFragment();
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
                            PubSub.getInstance().post(new StatusFilterEvent(filterValues[filterIndex]));
                        }
                    })
                    .commit();
        }
    }

    @Subscribe
    public void onLogoutRequested(LogoutEvent event) {
        logout();
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
