package it.bz.beacon.adminapp.ui.detail;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.appyvet.materialrangebar.RangeBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.ui.BaseActivity;
import it.bz.beacon.adminapp.ui.main.map.LocationDisabledFragment;
import it.bz.beacon.adminapp.ui.main.map.OnRetryLoadMapListener;

public class DetailActivity extends BaseActivity implements OnMapReadyCallback, OnRetryLoadMapListener {

    public static final String EXTRA_BEACON_ID = "EXTRA_BEACON_ID";
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @BindView(R.id.config_tablayout)
    protected TabLayout tabLayoutConfig;

    @BindView(R.id.info_tablayout)
    protected TabLayout tabLayoutLocation;

    @BindView(R.id.config_info_content)
    protected View contentInfo;

    @BindView(R.id.config_ibeacon_content)
    protected View contentIBeacon;

    @BindView(R.id.config_eddystone_content)
    protected View contentEddystone;

    @BindView(R.id.map_container)
    protected View contentMap;

    @BindView(R.id.gps_content)
    protected View contentGPS;

    @BindView(R.id.description_content)
    protected View contentDescription;

    @BindView(R.id.rangebar)
    protected RangeBar rangeBar;

    private long beaconId;
    protected GoogleMap googleMap;
    protected boolean mapShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // TODO: Set beacon name as title
        setTitle("Beacon584");

        tabLayoutConfig.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onConfigTabTapped(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onConfigTabTapped(tab.getPosition());
            }
        });

        tabLayoutLocation.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onLocationTabTapped(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onLocationTabTapped(tab.getPosition());
            }
        });
        rangeBar.setRangePinsByIndices(0, 3);

        initMapFragment();
    }

    private void onConfigTabTapped(int position) {
        switch (position) {
            case 0:
                contentInfo.setVisibility(View.VISIBLE);
                contentIBeacon.setVisibility(View.GONE);
                contentEddystone.setVisibility(View.GONE);
                break;
            case 1:
                contentInfo.setVisibility(View.GONE);
                contentIBeacon.setVisibility(View.VISIBLE);
                contentEddystone.setVisibility(View.GONE);
                break;
            case 2:
                contentInfo.setVisibility(View.GONE);
                contentIBeacon.setVisibility(View.GONE);
                contentEddystone.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void onLocationTabTapped(int position) {
        switch (position) {
            case 0:
                contentMap.setVisibility(View.VISIBLE);
                contentGPS.setVisibility(View.GONE);
                contentDescription.setVisibility(View.GONE);
                break;
            case 1:
                contentMap.setVisibility(View.GONE);
                contentGPS.setVisibility(View.VISIBLE);
                contentDescription.setVisibility(View.GONE);
                break;
            case 2:
                contentMap.setVisibility(View.GONE);
                contentGPS.setVisibility(View.GONE);
                contentDescription.setVisibility(View.VISIBLE);
                break;
        }
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
                                ActivityCompat.requestPermissions(DetailActivity.this,
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
        if (!mapShowing && getSupportFragmentManager() != null) {
            mapShowing = true;
            SupportMapFragment fragment = new SupportMapFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.map_container, fragment).commit();
            fragment.getMapAsync(this);
        }
    }

    private void showLocationDisabledFragment() {
        if (getSupportFragmentManager() != null) {
            mapShowing = false;
            LocationDisabledFragment fragment = LocationDisabledFragment.newInstance(this);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.map_container, fragment).commit();
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

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_detail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_edit:
                break;
            case R.id.menu_save:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
