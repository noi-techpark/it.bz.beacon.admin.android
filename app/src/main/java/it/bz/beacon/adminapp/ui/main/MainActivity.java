package it.bz.beacon.adminapp.ui.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.ui.BaseActivity;
import it.bz.beacon.adminapp.ui.about.AboutFragment;
import it.bz.beacon.adminapp.ui.login.LoginActivity;
import it.bz.beacon.adminapp.ui.main.beacon.BeaconTabsFragment;
import it.bz.beacon.adminapp.ui.main.beacon.IssuesFragment;
import it.bz.beacon.adminapp.ui.main.map.LocationDisabledFragment;
import it.bz.beacon.adminapp.ui.main.map.MapFragment;
import it.bz.beacon.adminapp.ui.main.map.OnRetryLoadMapListener;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, OnRetryLoadMapListener {

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawer;

    @BindView(R.id.navigation_view)
    protected NavigationView navigationView;

    protected GoogleMap googleMap;
    protected boolean isMapShowing = false;
    protected boolean isFilterActive = false;
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        storage = AdminApplication.getStorage();
        setSupportActionBar(toolbar);

        switchFragment(getString(R.string.beacons), BeaconTabsFragment.newInstance());
        setupNavigationDrawer();
        navigationView.getMenu().getItem(0).setChecked(true);

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
        openLogin();
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

    private void openLogin() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
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
                switchFragment(getString(R.string.beacons), BeaconTabsFragment.newInstance());
                break;
            case R.id.menu_filter:
                isFilterActive = !isFilterActive;
                invalidateOptionsMenu();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem filterItem = menu.findItem(R.id.menu_filter);

        if (filterItem != null) {
            if (isFilterActive) {
                // TODO: filter items
                filterItem.setIcon(R.drawable.ic_menu_filter);
            } else {
                // TODO: unfilter items
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
                switchFragment(getString(R.string.beacons), BeaconTabsFragment.newInstance());
                break;
            case R.id.navigation_issues:
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
                new AlertDialog.Builder(this)
                        .setTitle(R.string.location_permission_title)
                        .setMessage(R.string.location_permission_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
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
        if (!isMapShowing && getSupportFragmentManager() != null) {
            isMapShowing = true;
            MapFragment fragment = new MapFragment();
            switchFragment(getString(R.string.beacons), fragment);
            fragment.getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.474431, 11.3239), 18));
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
                    .replace(R.id.containerView, fragment)
                    .commit();
        }
    }
}
