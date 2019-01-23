package it.bz.beacon.adminapp.ui.detail;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.materialrangebar.RangeBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.viewmodel.BeaconViewModel;
import it.bz.beacon.adminapp.ui.BaseActivity;
import it.bz.beacon.adminapp.ui.main.map.LocationDisabledFragment;
import it.bz.beacon.adminapp.ui.main.map.OnRetryLoadMapListener;
import it.bz.beacon.adminapp.util.DateFormatter;

public class DetailActivity extends BaseActivity implements OnMapReadyCallback, OnRetryLoadMapListener {

    public static final String EXTRA_BEACON_ID = "EXTRA_BEACON_ID";
    public static final String EXTRA_BEACON_NAME = "EXTRA_BEACON_NAME";
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @BindView(R.id.progress)
    protected ConstraintLayout progress;

    @BindView(R.id.progressText)
    protected TextView txtProgress;

    @BindView(R.id.content_detail)
    protected FrameLayout content;

    @BindView(R.id.config_tablayout)
    protected TabLayout tabLayoutConfig;

    @BindView(R.id.info_tablayout)
    protected TabLayout tabLayoutLocation;

    @BindView(R.id.info_content)
    protected LinearLayout contentInfo;

    @BindView(R.id.nameContainer)
    protected TextInputLayout containerName;

    @BindView(R.id.info_name)
    protected TextInputEditText editName;

    @BindView(R.id.ibeacon_content)
    protected ConstraintLayout contentIBeacon;

    @BindView(R.id.eddystone_content)
    protected LinearLayout contentEddystone;

    @BindView(R.id.map_container)
    protected View contentMap;

    @BindView(R.id.gps_content)
    protected LinearLayout contentGPS;

    @BindView(R.id.description_content)
    protected ConstraintLayout contentDescription;

    @BindView(R.id.description)
    protected TextInputEditText editDescription;

    @BindView(R.id.details_last_seen)
    protected TextView txtLastSeen;

    @BindView(R.id.details_status)
    protected ImageView imgStatus;

    @BindView(R.id.info_battery)
    protected TextView txtBattery;

    @BindView(R.id.info_battery_icon)
    protected ImageView imgBattery;

    @BindView(R.id.info_status)
    protected TextView txtStatus;

    @BindView(R.id.info_status_icon)
    protected ImageView imgInfoStatus;

    @BindView(R.id.info_rangebar)
    protected RangeBar rbSignalStrength;

    @BindView(R.id.info_interval)
    protected TextInputEditText editInterval;

    @BindView(R.id.info_telemetry)
    protected SwitchCompat switchTelemetry;

    @BindView(R.id.ibeacon_switch)
    protected SwitchCompat switchIBeacon;

    @BindView(R.id.ibeacon_uuid)
    protected TextInputEditText editUuid;

    @BindView(R.id.ibeacon_major)
    protected TextInputEditText editMajor;

    @BindView(R.id.ibeacon_minor)
    protected TextInputEditText editMinor;

    @BindView(R.id.eddystone_namespace)
    protected TextInputEditText editNamespace;

    @BindView(R.id.eddystone_instanceid)
    protected TextInputEditText editInstanceId;

    @BindView(R.id.eddystone_url)
    protected TextInputEditText editUrl;

    @BindView(R.id.eddystone_switch_eid)
    protected SwitchCompat switchEid;

    @BindView(R.id.eddystone_switch_etlm)
    protected SwitchCompat switchEtlm;

    @BindView(R.id.eddystone_switch_tlm)
    protected SwitchCompat switchTlm;

    @BindView(R.id.eddystone_switch_uid)
    protected SwitchCompat switchUid;

    @BindView(R.id.eddystone_switch_url)
    protected SwitchCompat switchUrl;

    @BindView(R.id.gps_latitude)
    protected TextInputEditText editLatitude;

    @BindView(R.id.gps_longitude)
    protected TextInputEditText editLongitude;

    @BindView(R.id.toggle_outdoor)
    protected Button btnOutdoor;

    @BindView(R.id.toggle_indoor)
    protected Button btnIndoor;

    @BindView(R.id.fab)
    protected FloatingActionButton fabAddIssue;

    private long beaconId;
    private String beaconName;
    private Beacon beacon;
    protected GoogleMap googleMap;
    protected boolean mapShowing = false;
    protected boolean isEditing = false;

    private BeaconViewModel beaconViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            beaconName = getIntent().getStringExtra(EXTRA_BEACON_NAME);
            beaconId = getIntent().getLongExtra(EXTRA_BEACON_ID, -1L);
        }
        configureTabListeners();
        isEditing = false;

        beaconViewModel = ViewModelProviders.of(this).get(BeaconViewModel.class);

        initMapFragment();

        loadBeacon();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpToolbar();
        setContentEnabled(isEditing);
    }

    private void setUpToolbar() {
        if (isEditing) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel);
            }
            toolbar.setTitle(getString(R.string.details_edit));
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            }
            toolbar.setTitle(beaconName);
        }
    }

    private void showCloseWarning() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.close_warning)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isEditing = false;
                        loadBeacon();
                        setContentEnabled(isEditing);
                        invalidateOptionsMenu();
                        setUpToolbar();
                    }
                }).create();
        dialog.show();
    }

    private void setContentEnabled(boolean enabled) {
        setViewTreeEnabled(contentInfo, enabled);
        setViewTreeEnabled(contentIBeacon, enabled);
        setViewTreeEnabled(contentEddystone, enabled);
        setViewTreeEnabled(contentGPS, enabled);
        setViewTreeEnabled(contentDescription, enabled);

        containerName.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void configureTabListeners() {
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

    private void setViewTreeEnabled(ViewGroup viewGroup, boolean enabled) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if ((child instanceof SwitchCompat) ||
                    (child instanceof RangeBar) ||
                    (child instanceof TextInputEditText) ||
                    (child instanceof Button))
                child.setEnabled(enabled);
            else {
                if (child instanceof ViewGroup) {
                    setViewTreeEnabled((ViewGroup) child, enabled);
                }
            }
        }
    }

    private void loadBeacon() {
        showProgress(getString(R.string.loading));

        beaconViewModel.getById(beaconId).observe(this, new Observer<Beacon>() {
            @Override
            public void onChanged(@Nullable Beacon changedBeacon) {
                beacon = changedBeacon;
                showData();
            }
        });
    }

    private void saveData() {
        if (beacon != null) {
            beacon.setName(editName.getText().toString());
            beacon.setTxPower(rbSignalStrength.getRightIndex());
            beacon.setInterval(Integer.valueOf(editInterval.getText().toString()));
            beacon.setTelemetry(switchTelemetry.isChecked());
            beacon.setIBeacon(switchIBeacon.isChecked());
            beacon.setUuid(editUuid.getText().toString());
            beacon.setMajor(Integer.valueOf(editMajor.getText().toString()));
            beacon.setMinor(Integer.valueOf(editMinor.getText().toString()));
            beacon.setEddystoneEid(switchEid.isChecked());
            beacon.setEddystoneEtlm(switchEtlm.isChecked());
            beacon.setEddystoneTlm(switchTlm.isChecked());
            beacon.setEddystoneUid(switchUid.isChecked());
            beacon.setEddystoneUrl(switchUrl.isChecked());
            beacon.setNamespace(editNamespace.getText().toString());
            beacon.setInstanceId(editInstanceId.getText().toString());
            beacon.setUrl(editUrl.getText().toString());
            beacon.setLat(Float.parseFloat(editLatitude.getText().toString().replace(',', '.')));
            beacon.setLng(Float.parseFloat(editLongitude.getText().toString().replace(',', '.')));
            // TODO: set LocationType

            beacon.setDescription(editDescription.getText().toString());

            beaconViewModel.insert(beacon, new InsertEvent() {
                @Override
                public void onSuccess(long id) {
                    Toast.makeText(DetailActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showData() {
        if (beacon != null) {
            setTitle(beacon.getName());
            txtLastSeen.setText(getString(R.string.details_last_seen, DateFormatter.dateToDateString(new Date(beacon.getLastSeen() * 1000))));

            txtBattery.setText(getString(R.string.percent, beacon.getBatteryLevel()));

            editName.setText(beacon.getName());
            if (beacon.getBatteryLevel() < 34) {
                imgBattery.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_battery_alert));
            } else {
                if (beacon.getBatteryLevel() < 66) {
                    imgBattery.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_battery_50));
                } else {
                    imgBattery.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_battery_full));
                }
            }
            if (beacon.getStatus().equals(Beacon.STATUS_OK)) {
                ImageViewCompat.setImageTintList(imgStatus, ColorStateList.valueOf(getColor(R.color.status_ok)));
                ImageViewCompat.setImageTintList(imgInfoStatus, ColorStateList.valueOf(getColor(R.color.status_ok)));
                txtStatus.setText(getString(R.string.status_ok));
            }
            if (beacon.getStatus().equals(Beacon.STATUS_BATTERY_LOW)) {
                ImageViewCompat.setImageTintList(imgStatus, ColorStateList.valueOf(getColor(R.color.status_warning)));
                ImageViewCompat.setImageTintList(imgInfoStatus, ColorStateList.valueOf(getColor(R.color.status_warning)));
                txtStatus.setText(getString(R.string.status_battery_low));
            }
            if (beacon.getStatus().equals(Beacon.STATUS_CONFIGURATION_PENDING)) {
                ImageViewCompat.setImageTintList(imgStatus, ColorStateList.valueOf(getColor(R.color.status_pending)));
                ImageViewCompat.setImageTintList(imgInfoStatus, ColorStateList.valueOf(getColor(R.color.status_pending)));
                txtStatus.setText(getString(R.string.status_configuration_pending));
            }

            rbSignalStrength.setRangePinsByIndices(0, beacon.getTxPower());
            editInterval.setText(String.valueOf(beacon.getInterval()));
            switchTelemetry.setChecked(beacon.getTelemetry());

            switchIBeacon.setChecked(beacon.isIBeacon());
            editUuid.setText(beacon.getUuid());
            editMajor.setText(String.valueOf(beacon.getMajor()));
            editMinor.setText(String.valueOf(beacon.getMinor()));

            switchEid.setChecked(beacon.isEddystoneEid());
            switchEtlm.setChecked(beacon.isEddystoneEtlm());
            switchTlm.setChecked(beacon.isEddystoneTlm());
            switchUid.setChecked(beacon.isEddystoneUid());
            switchUrl.setChecked(beacon.isEddystoneUrl());

            editNamespace.setText(beacon.getNamespace());
            editInstanceId.setText(beacon.getInstanceId());
            editUrl.setText(beacon.getUrl());

            if ((googleMap != null) && (beacon.getLat() != null) && (beacon.getLng() != null)) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(beacon.getLat(), beacon.getLng()), getResources().getInteger(R.integer.default_map_zoom_level)));
            }

            editLatitude.setText(String.format(Locale.getDefault(), "%.6f", beacon.getLat()));
            editLongitude.setText(String.format(Locale.getDefault(), "%.6f", beacon.getLng()));

            updateLocationButtons(beacon.getLocationType());
            btnOutdoor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    beacon.setLocationType(Beacon.LOCATION_OUTDOOR);
                    updateLocationButtons(Beacon.LOCATION_OUTDOOR);
                }
            });

            btnIndoor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    beacon.setLocationType(Beacon.LOCATION_INDOOR);
                    updateLocationButtons(Beacon.LOCATION_INDOOR);
                }
            });
            editDescription.setText(beacon.getDescription());
        }
        fabAddIssue.show();
        content.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void updateLocationButtons(String location) {
        if (location == null) {
            deactivateToggleButton(btnIndoor);
            deactivateToggleButton(btnOutdoor);
        } else {
            if (location.equals(Beacon.LOCATION_OUTDOOR)) {
                activateToggleButton(btnOutdoor);
                deactivateToggleButton(btnIndoor);
            } else {
                activateToggleButton(btnIndoor);
                deactivateToggleButton(btnOutdoor);
            }
        }
    }

    private void activateToggleButton(Button button) {
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.background_toggle_on));
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
        button.setTextColor(ContextCompat.getColor(this, R.color.secondaryText));
    }

    private void deactivateToggleButton(Button button) {
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.background_toggle_off));
        button.setCompoundDrawables(null, null, null, null);
        button.setTextColor(ContextCompat.getColor(this, R.color.divider));
    }

    private void showProgress(String text) {
        fabAddIssue.hide();
        txtProgress.setText(text);
        content.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
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
        if ((beacon != null) && (beacon.getLat() != null) && (beacon.getLng() != null)) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(beacon.getLat(), beacon.getLng()), getResources().getInteger(R.integer.default_map_zoom_level)));
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.474431, 11.3239), getResources().getInteger(R.integer.default_map_zoom_level)));
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem editItem = menu.findItem(R.id.menu_edit);
        MenuItem saveItem = menu.findItem(R.id.menu_save);

        if (editItem != null) {
            editItem.setVisible(!isEditing);
        }
        if (saveItem != null) {
            saveItem.setVisible(isEditing);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if (isEditing) {
                    showCloseWarning();
                } else {
                    finish();
                }
                break;
            case R.id.menu_edit:
                isEditing = true;
                setContentEnabled(isEditing);
                invalidateOptionsMenu();
                setUpToolbar();
                break;
            case R.id.menu_save:
                saveData();
                isEditing = false;
                setContentEnabled(isEditing);
                invalidateOptionsMenu();
                setUpToolbar();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}