package it.bz.beacon.adminapp.ui.detail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.appyvet.materialrangebar.RangeBar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.SecureProfileListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleSecureProfileListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.ISecureProfile;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconImage;
import it.bz.beacon.adminapp.data.entity.Group;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.event.LoadBeaconEvent;
import it.bz.beacon.adminapp.data.viewmodel.BeaconImageViewModel;
import it.bz.beacon.adminapp.data.viewmodel.BeaconViewModel;
import it.bz.beacon.adminapp.data.viewmodel.GroupViewModel;
import it.bz.beacon.adminapp.swagger.client.ApiCallback;
import it.bz.beacon.adminapp.swagger.client.ApiException;
import it.bz.beacon.adminapp.swagger.client.api.TrustedBeaconControllerApi;
import it.bz.beacon.adminapp.swagger.client.model.BaseMessage;
import it.bz.beacon.adminapp.swagger.client.model.BeaconBatteryLevelUpdate;
import it.bz.beacon.adminapp.swagger.client.model.BeaconUpdate;
import it.bz.beacon.adminapp.swagger.client.model.PendingConfiguration;
import it.bz.beacon.adminapp.ui.BaseDetailActivity;
import it.bz.beacon.adminapp.ui.adapter.GalleryAdapter;
import it.bz.beacon.adminapp.ui.adapter.GroupAdapter;
import it.bz.beacon.adminapp.ui.issue.NewIssueActivity;
import it.bz.beacon.adminapp.util.BitmapTools;
import it.bz.beacon.adminapp.util.DateFormatter;
import it.bz.beacon.adminapp.util.OnImagesDownloadedCallback;
import it.bz.beacon.beaconsuedtirolsdk.NearbyBeaconManager;

public class DetailActivity extends BaseDetailActivity implements OnMapReadyCallback, IPickResult,
        GalleryAdapter.OnImageDeleteListener, GoogleMap.OnMapClickListener, TextWatcher {

    public static final String EXTRA_BEACON_ID = "EXTRA_BEACON_ID";
    public static final String EXTRA_BEACON_NAME = "EXTRA_BEACON_NAME";
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @BindView(R.id.scrollview)
    protected NestedScrollView scrollView;

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

    @BindView(R.id.details_info_pending)
    protected LinearLayout pendingInfo;

    @BindView(R.id.nameContainer)
    protected TextInputLayout containerName;

    @BindView(R.id.groupContainer)
    protected TextInputLayout containerGroup;

    @BindView(R.id.uuidContainer)
    protected TextInputLayout containerUuid;

    @BindView(R.id.intervalContainer)
    protected TextInputLayout containerInterval;

    @BindView(R.id.majorContainer)
    protected TextInputLayout containerMajor;

    @BindView(R.id.minorContainer)
    protected TextInputLayout containerMinor;

    @BindView(R.id.latitudeContainer)
    protected TextInputLayout containerLatitude;

    @BindView(R.id.longitudeContainer)
    protected TextInputLayout containerLongitude;

    @BindView(R.id.info_name)
    protected TextInputEditText editName;

    @BindView(R.id.group)
    protected AutoCompleteTextView group;

    @BindView(R.id.ibeacon_content)
    protected ConstraintLayout contentIBeacon;

    @BindView(R.id.eddystone_content)
    protected LinearLayout contentEddystone;

    @BindView(R.id.map_view)
    protected MapView mapView;

    @BindView(R.id.map_layout)
    protected RelativeLayout contentMap;

    @BindView(R.id.gps_content)
    protected LinearLayout contentGPS;

    @BindView(R.id.description_content)
    protected ConstraintLayout contentDescription;

    @BindView(R.id.description_floor_container)
    protected TextInputLayout floorContainer;

    @BindView(R.id.description)
    protected TextInputEditText editDescription;

    @BindView(R.id.description_floor)
    protected TextInputEditText editFloor;

    @BindView(R.id.details_last_seen)
    protected TextView txtLastSeen;

    @BindView(R.id.details_status)
    protected ImageView imgStatus;

    @BindView(R.id.info_battery)
    protected TextView txtBattery;

    @BindView(R.id.info_temperature)
    protected TextView txtTemperature;

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

    @BindView(R.id.address_name)
    protected TextView txtAddressName;

    @BindView(R.id.address_address)
    protected TextView txtAddress;

    @BindView(R.id.address_latitude)
    protected TextView txtAddressLatitude;

    @BindView(R.id.address_longitude)
    protected TextView txtAddressLongitude;

    @BindView(R.id.details_location_subtitle)
    protected TextView txtLocation;

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

    @BindView(R.id.details_images)
    protected RecyclerView images;

    @BindView(R.id.details_images_empty)
    protected TextView txtImagesEmpty;

    @BindView(R.id.details_images_add)
    protected Button btnAddImage;

    @BindView(R.id.gps_current_position)
    protected Button btnCurrentPosition;

    @BindView(R.id.gps_provisional_position)
    protected Button btnProvisionalPosition;

    @BindView(R.id.show_pending_config)
    protected Button btnShowPendingConfig;

    private String beaconId;
    private String beaconName;
    private Beacon beacon;
    private it.bz.beacon.beaconsuedtirolsdk.data.entity.Beacon beaconInfo;
    private boolean isBatteryWarningShowing = false;

    protected GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation = null;

    private GalleryAdapter galleryAdapter;

    private BeaconViewModel beaconViewModel;
    private BeaconImageViewModel beaconImageViewModel;
    private GroupViewModel groupViewModel;

    private ProximityManager proximityManager;

    private boolean loadingImages = false;

    private TrustedBeaconControllerApi trustedApi;

    private GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            beaconName = getIntent().getStringExtra(EXTRA_BEACON_NAME);
            beaconId = getIntent().getStringExtra(EXTRA_BEACON_ID);
        }
        configureTabListeners();
        isEditing = false;

        trustedApi = AdminApplication.getTrustedBeaconControllerApi();
        if (!getString(R.string.trustedApiUser).isEmpty() && !getString(R.string.trustedApiPassword).isEmpty()) {
            trustedApi.getApiClient().setUsername(getString(R.string.trustedApiUser));
            trustedApi.getApiClient().setPassword(getString(R.string.trustedApiPassword));
        }

        beaconViewModel = ViewModelProviders.of(this).get(BeaconViewModel.class);
        beaconImageViewModel = ViewModelProviders.of(this).get(BeaconImageViewModel.class);
        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel.class);

        mapView.onCreate(savedInstanceState);

        images.setHasFixedSize(true);
        images.setItemAnimator(new DefaultItemAnimator());
        images.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        galleryAdapter = new GalleryAdapter(this, this);
        images.setAdapter(galleryAdapter);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(DetailActivity.this);

        groupAdapter = new GroupAdapter(this);
        group.setAdapter(groupAdapter);


        initializeKontakt();
    }

    private void initializeKontakt() {
        KontaktSDK.initialize(getString(R.string.apiKey));
        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setSecureProfileListener(createSecureProfileListener());
    }

    private void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }
        });
    }

    private SecureProfileListener createSecureProfileListener() {
        return new SimpleSecureProfileListener() {
            @Override
            public void onProfileDiscovered(final ISecureProfile profile) {
                updateBeaconNearby(profile);
                updateBatteryStatus(profile);
                super.onProfileDiscovered(profile);
            }

            @Override
            public void onProfilesUpdated(List<ISecureProfile> profiles) {
                super.onProfilesUpdated(profiles);
                for (ISecureProfile profile : profiles) {
                    updateBeaconNearby(profile);
                }
            }

            @Override
            public void onProfileLost(ISecureProfile profile) {
                super.onProfileLost(profile);
                if ((beacon != null) && (beacon.getManufacturerId().equals(profile.getUniqueId()))) {
                    btnShowPendingConfig.setEnabled(false);
                }
            }

            private void updateBeaconNearby(ISecureProfile profile) {
                if ((beacon != null) && (beacon.getManufacturerId().equals(profile.getUniqueId()))) {
                    if (beacon.getStatus().equals(Beacon.STATUS_CONFIGURATION_PENDING)) {
                        btnShowPendingConfig.setEnabled(true);
                    }
                    // sometimes all values are 0: need to check timestamp to see if values are set, since temperature could be 0
                    if ((profile.getTelemetry() != null) && (profile.getTelemetry().getTimestamp() > 0)) {
                        txtTemperature.setText(getString(R.string.degree, profile.getTelemetry().getTemperature()));
                    }
                }
            }

            private void updateBatteryStatus(ISecureProfile profile) {
                if ((beacon != null) && (beacon.getManufacturerId().equals(profile.getUniqueId()))) {
                    try {
                        if (profile.getBatteryLevel() > 0) {
                            BeaconBatteryLevelUpdate update = new BeaconBatteryLevelUpdate();
                            update.setBatteryLevel(profile.getBatteryLevel());
                            String[] nameParts = profile.getName().split("#");
                            trustedApi.updateUsingPATCH2Async(update, nameParts[1], new ApiCallback<it.bz.beacon.adminapp.swagger.client.model.Beacon>() {
                                @Override
                                public void onFailure(ApiException e, int i, Map<String, List<String>> map) {

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
                        //
                    }
                }
            }
        };
    }

    private class ImageCounter {
        private int counter = 0;
        private int goal;

        public ImageCounter(int goal) {
            this.goal = goal;
        }

        void count() {
            counter++;
            if (counter >= goal) {
                loadingImages = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocating();
        if (!isEditing) {
            loadBeacon();
            loadInfo(beaconId);
        }
        startScanningIfLocationPermissionGranted();
        mapView.onResume();
        setUpToolbar(beaconName);
        setContentEnabled(isEditing);

        beaconImageViewModel.getAllByBeaconId(beaconId).observe(this, new Observer<List<BeaconImage>>() {
            @Override
            public void onChanged(@Nullable List<BeaconImage> beaconImages) {
                if (!loadingImages) {
                    loadingImages = true;

                    images.setVisibility(View.GONE);
                    txtImagesEmpty.setVisibility(View.VISIBLE);
                    galleryAdapter.clear();

                    if (beaconImages != null && beaconImages.size() > 0) {
                        final ImageCounter counter = new ImageCounter(beaconImages.size());
                        for (BeaconImage beaconImage : beaconImages) {
                            BitmapTools.downloadImage(DetailActivity.this, beaconImage, new OnImagesDownloadedCallback() {
                                @Override
                                public void onSuccess(BeaconImage beaconImage) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            galleryAdapter.addBeaconImage(beaconImage);
                                            images.setVisibility(View.VISIBLE);
                                            txtImagesEmpty.setVisibility(View.GONE);
                                        }
                                    });
                                    counter.count();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    counter.count();
                                }
                            });
                        }
                    } else {
                        loadingImages = false;
                    }
                }
            }
        });
    }

    protected void setContentEnabled(boolean enabled) {
        setViewTreeEnabled(contentInfo, enabled);
        setViewTreeClickable(containerGroup, enabled);
        setViewTreeEnabled(contentIBeacon, enabled);
        setViewTreeEnabled(contentEddystone, enabled);
        setViewTreeEnabled(contentGPS, enabled);
        setViewTreeEnabled(contentDescription, enabled);

        containerName.setVisibility(enabled ? View.VISIBLE : View.GONE);
        btnAddImage.setVisibility(enabled ? View.VISIBLE : View.GONE);
        if (isEditing) {
            fabAddIssue.hide();
            rbSignalStrength.setPinColor(ContextCompat.getColor(this, R.color.primary));
            rbSignalStrength.setConnectingLineColor(ContextCompat.getColor(this, R.color.primary));
            rbSignalStrength.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
                @Override
                public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                    showBatteryWarning();
                }

                @Override
                public void onTouchStarted(RangeBar rangeBar) {

                }

                @Override
                public void onTouchEnded(RangeBar rangeBar) {

                }
            });
            editInterval.addTextChangedListener(this);
            switchTelemetry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    showBatteryWarning();
                }
            });
            switchUid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    showBatteryWarning();
                }
            });
            btnCurrentPosition.setVisibility(View.VISIBLE);
            btnProvisionalPosition.setVisibility(View.VISIBLE);
            if (beacon.getStatus().equals(Beacon.STATUS_CONFIGURATION_PENDING)) {
                pendingInfo.setVisibility(View.VISIBLE);
            }
            btnShowPendingConfig.setVisibility(View.GONE);
        } else {
            fabAddIssue.show();
            rbSignalStrength.setPinColor(ContextCompat.getColor(this, R.color.primary50));
            rbSignalStrength.setConnectingLineColor(ContextCompat.getColor(this, R.color.primary50));
            rbSignalStrength.setOnRangeBarChangeListener(null);
            editInterval.removeTextChangedListener(this);
            switchTelemetry.setOnCheckedChangeListener(null);
            switchUid.setOnCheckedChangeListener(null);
            btnCurrentPosition.setVisibility(View.GONE);
            btnProvisionalPosition.setVisibility(View.GONE);
            if (map != null) {
                map.setOnMapClickListener(null);
            }
            pendingInfo.setVisibility(View.GONE);
        }
    }

    @Override
    protected void quitEditMode() {
        isEditing = false;
        setContentEnabled(isEditing);
        showData();
        invalidateOptionsMenu();
        clearValidationErrors();
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
                    (child instanceof TextInputEditText) ||
                    (child instanceof AutoCompleteTextView) ||
                    (child instanceof Button) || (child instanceof RangeBar)) {
                child.setEnabled(enabled);
            } else {
                if (child instanceof ViewGroup) {
                    setViewTreeEnabled((ViewGroup) child, enabled);
                }
            }
        }
    }

    private void setViewTreeClickable(ViewGroup viewGroup, boolean enabled) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setClickable(enabled);
            if (child instanceof ViewGroup) {
                setViewTreeClickable((ViewGroup) child, enabled);
            }
        }
    }

    private void loadBeacon() {
        showProgress(getString(R.string.loading));
        groupViewModel.getAll().observe(DetailActivity.this, new Observer<List<Group>>() {
            @Override
            public void onChanged(@Nullable List<Group> groups) {
                groupAdapter.setGroups(groups);
                if(!isEditing && beacon != null) {
                    group.setText(groupAdapter.getNameById(beacon.getGroupId()));
                }
            }
        });

        beaconViewModel.getById(beaconId, new LoadBeaconEvent() {
            @Override
            public void onSuccess(Beacon loadedBeacon) {
                beacon = loadedBeacon;
                showData();
                mapView.getMapAsync(DetailActivity.this);
                makeMapScrollable();
            }

            @Override
            public void onError() {
                showToast(getString(R.string.general_error), Toast.LENGTH_LONG);
            }
        });
    }

    private void loadInfo(String beaconId) {
        NearbyBeaconManager.getInstance().getBeacon(beaconId, new it.bz.beacon.beaconsuedtirolsdk.data.event.LoadBeaconEvent() {
            @Override
            public void onSuccess(it.bz.beacon.beaconsuedtirolsdk.data.entity.Beacon beaconInfo) {
                txtAddressName.setText(beaconInfo.getName());
                String address = "";
                if (!TextUtils.isEmpty(beaconInfo.getAddress())) {
                    address = beaconInfo.getAddress();
                }
                if (!TextUtils.isEmpty(address) || !TextUtils.isEmpty(beaconInfo.getCap()) || !TextUtils.isEmpty(beaconInfo.getLocation())) {
                    address = address.concat(", ");
                }
                if (!TextUtils.isEmpty(beaconInfo.getCap())) {
                    address = address.concat(beaconInfo.getCap()).concat(" ");
                }
                if (!TextUtils.isEmpty(beaconInfo.getLocation())) {
                    address = address.concat(beaconInfo.getLocation());
                }
                txtAddress.setText(address);
                txtAddressLatitude.setText(String.format(Locale.getDefault(), "%.6f", beaconInfo.getLatitude()));
                txtAddressLongitude.setText(String.format(Locale.getDefault(), "%.6f", beaconInfo.getLongitude()));
                DetailActivity.this.beaconInfo = beaconInfo;
                if ((map != null) && (beacon != null) && (beacon.getLat() == 0) && (beacon.getLng() == 0)) {
                    LatLng latlng = new LatLng(beaconInfo.getLatitude(), beaconInfo.getLongitude());
                    setMarker(latlng);
                }
            }

            @Override
            public void onError() {
                Log.e(AdminApplication.LOG_TAG, "error");
            }
        });
    }

    @Override
    protected void showData() {
        if (beacon != null) {
            setUpToolbar(beacon.getName());
            txtLastSeen.setText(getString(R.string.details_last_seen, DateFormatter.dateToDateString(new Date(beacon.getLastSeen()))));

            txtBattery.setText(getString(R.string.percent, beacon.getBatteryLevel()));

            editName.setText(beacon.getName());
            if (beacon.getBatteryLevel() != null) {
                if (beacon.getBatteryLevel() < getResources().getInteger(R.integer.battery_alert_level)) {
                    imgBattery.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_battery_alert));
                } else {
                    if (beacon.getBatteryLevel() < getResources().getInteger(R.integer.battery_half_level)) {
                        imgBattery.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_battery_50));
                    } else {
                        imgBattery.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_battery_full));
                    }
                }
            }
            if (beacon.getStatus().equals(Beacon.STATUS_OK)) {
                ImageViewCompat.setImageTintList(imgStatus, ColorStateList.valueOf(getColor(R.color.status_ok)));
                ImageViewCompat.setImageTintList(imgInfoStatus, ColorStateList.valueOf(getColor(R.color.status_ok)));
                txtStatus.setText(getString(R.string.status_ok));
            }
            if ((beacon.getStatus().equals(Beacon.STATUS_BATTERY_LOW)) || (beacon.getStatus().equals(Beacon.STATUS_ISSUE))) {
                ImageViewCompat.setImageTintList(imgStatus, ColorStateList.valueOf(getColor(R.color.status_warning)));
                ImageViewCompat.setImageTintList(imgInfoStatus, ColorStateList.valueOf(getColor(R.color.status_warning)));
            }
            if (beacon.getStatus().equals(Beacon.STATUS_BATTERY_LOW)) {
                txtStatus.setText(getString(R.string.status_battery_low));
            }
            if (beacon.getStatus().equals(Beacon.STATUS_ISSUE)) {
                txtStatus.setText(getString(R.string.status_issue));
            }
            if (beacon.getStatus().equals(Beacon.STATUS_NO_SIGNAL)) {
                ImageViewCompat.setImageTintList(imgStatus, ColorStateList.valueOf(getColor(R.color.status_error)));
                ImageViewCompat.setImageTintList(imgInfoStatus, ColorStateList.valueOf(getColor(R.color.status_error)));
                txtStatus.setText(getString(R.string.status_no_signal));
            }
            if (beacon.getStatus().equals(Beacon.STATUS_CONFIGURATION_PENDING)) {
                ImageViewCompat.setImageTintList(imgStatus, ColorStateList.valueOf(getColor(R.color.status_pending)));
                ImageViewCompat.setImageTintList(imgInfoStatus, ColorStateList.valueOf(getColor(R.color.status_pending)));
                txtStatus.setText(getString(R.string.status_configuration_pending));
                btnShowPendingConfig.setEnabled(false);
                btnShowPendingConfig.setVisibility(View.VISIBLE);
            } else {
                btnShowPendingConfig.setVisibility(View.GONE);
            }

            rbSignalStrength.setRangePinsByIndices(0, beacon.getTxPower() - 1);
            editInterval.setText(String.valueOf(beacon.getInterval()));
            switchTelemetry.setChecked(beacon.getTelemetry() != null && beacon.getTelemetry());

            switchIBeacon.setChecked(beacon.isIBeacon() != null && beacon.isIBeacon());
            editUuid.setText(beacon.getUuid());
            editMajor.setText(String.valueOf(beacon.getMajor()));
            editMinor.setText(String.valueOf(beacon.getMinor()));

            switchEid.setChecked(beacon.isEddystoneEid() != null && beacon.isEddystoneEid());
            switchEtlm.setChecked(beacon.isEddystoneEtlm() != null && beacon.isEddystoneEtlm());
            switchTlm.setChecked(beacon.isEddystoneTlm() != null && beacon.isEddystoneTlm());
            switchUid.setChecked(beacon.isEddystoneUid() != null && beacon.isEddystoneUid());
            switchUrl.setChecked(beacon.isEddystoneUrl() != null && beacon.isEddystoneUrl());

            editNamespace.setText(beacon.getNamespace());
            editInstanceId.setText(beacon.getInstanceId());
            editUrl.setText(beacon.getUrl());

            if ((map != null) && (beacon.getLat() != 0) && (beacon.getLng() != 0)) {
                LatLng latlng = new LatLng(beacon.getLat(), beacon.getLng());
                setMarker(latlng);
            }

            setLatLngEditFields(beacon.getLat(), beacon.getLng());

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
            editFloor.setText(beacon.getLocationDescription());

            group.setText(groupAdapter.getNameById(beacon.getGroupId()));
        }
        fabAddIssue.show();
        content.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void showPendingData() {
        if ((beacon != null) && (!TextUtils.isEmpty(beacon.getPendingConfiguration()))) {
            PendingConfiguration pendingConfiguration = (new Gson()).fromJson(beacon.getPendingConfiguration(), PendingConfiguration.class);

            rbSignalStrength.setRangePinsByIndices(0, pendingConfiguration.getTxPower() - 1);
            editInterval.setText(String.valueOf(pendingConfiguration.getInterval()));
            switchTelemetry.setChecked(pendingConfiguration.isTelemetry() != null && pendingConfiguration.isTelemetry());

            switchIBeacon.setChecked(pendingConfiguration.isIBeacon() != null && pendingConfiguration.isIBeacon());
            editUuid.setText(pendingConfiguration.getUuid().toString());
            editMajor.setText(String.valueOf(pendingConfiguration.getMajor()));
            editMinor.setText(String.valueOf(pendingConfiguration.getMinor()));

            switchEid.setChecked(pendingConfiguration.isEddystoneEid() != null && pendingConfiguration.isEddystoneEid());
            switchEtlm.setChecked(pendingConfiguration.isEddystoneEtlm() != null && pendingConfiguration.isEddystoneEtlm());
            switchTlm.setChecked(pendingConfiguration.isEddystoneTlm() != null && pendingConfiguration.isEddystoneTlm());
            switchUid.setChecked(pendingConfiguration.isEddystoneUid() != null && pendingConfiguration.isEddystoneUid());
            switchUrl.setChecked(pendingConfiguration.isEddystoneUrl() != null && pendingConfiguration.isEddystoneUrl());

            editNamespace.setText(pendingConfiguration.getNamespace());
            editInstanceId.setText(pendingConfiguration.getInstanceId());
            editUrl.setText(pendingConfiguration.getUrl());
        }
    }

    private void resetPendingData() {
        if (beacon != null) {
            rbSignalStrength.setRangePinsByIndices(0, beacon.getTxPower() - 1);
            editInterval.setText(String.valueOf(beacon.getInterval()));
            switchTelemetry.setChecked(beacon.getTelemetry() != null && beacon.getTelemetry());

            switchIBeacon.setChecked(beacon.isIBeacon() != null && beacon.isIBeacon());
            editUuid.setText(beacon.getUuid());
            editMajor.setText(String.valueOf(beacon.getMajor()));
            editMinor.setText(String.valueOf(beacon.getMinor()));

            switchEid.setChecked(beacon.isEddystoneEid() != null && beacon.isEddystoneEid());
            switchEtlm.setChecked(beacon.isEddystoneEtlm() != null && beacon.isEddystoneEtlm());
            switchTlm.setChecked(beacon.isEddystoneTlm() != null && beacon.isEddystoneTlm());
            switchUid.setChecked(beacon.isEddystoneUid() != null && beacon.isEddystoneUid());
            switchUrl.setChecked(beacon.isEddystoneUrl() != null && beacon.isEddystoneUrl());

            editNamespace.setText(beacon.getNamespace());
            editInstanceId.setText(beacon.getInstanceId());
            editUrl.setText(beacon.getUrl());
        }
    }

    private void updateLocationButtons(String location) {
        if (location == null) {
            deactivateToggleButton(btnIndoor);
            deactivateToggleButton(btnOutdoor);
            txtLocation.setVisibility(View.GONE);
        } else {
            txtLocation.setVisibility(View.VISIBLE);
            if (location.equals(Beacon.LOCATION_OUTDOOR)) {
                activateToggleButton(btnOutdoor);
                deactivateToggleButton(btnIndoor);
                floorContainer.setVisibility(View.GONE);
                txtLocation.setText(getString(R.string.outdoor));
            } else {
                activateToggleButton(btnIndoor);
                deactivateToggleButton(btnOutdoor);
                floorContainer.setVisibility(View.VISIBLE);
                txtLocation.setText(getString(R.string.indoor));
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        stopLocating();
        startLocating();

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success) {
                Log.e(AdminApplication.LOG_TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(AdminApplication.LOG_TAG, "Can't find style. Error: ", e);
        }

        if ((beacon != null) && (beacon.getLat() != 0) && (beacon.getLng() != 0)) {
            LatLng latlng = new LatLng(beacon.getLat(), beacon.getLng());
            setMarker(latlng);
        } else {
            if ((beaconInfo != null) && (beaconInfo.getLatitude() != 0) && (beaconInfo.getLongitude() != 0)) {
                LatLng latlng = new LatLng(beaconInfo.getLatitude(), beaconInfo.getLongitude());
                setMarker(latlng);
            } else {
                goToMyLocation();
            }
        }

        if (isEditing) {
            map.setOnMapClickListener(this);
        }
    }

    private void setMarker(LatLng latLng) {
        String status = beacon.getStatus();
        if (beacon.getLat() == 0 && beacon.getLng() == 0) {
            status = Beacon.STATUS_NOT_INSTALLED;
        }
        map.clear();
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(Beacon.getMarkerId(status))));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, getZoomLevel()));
    }

    private void moveMarker(LatLng latLng) {
        map.clear();
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(Beacon.getMarkerId(beacon.getStatus()))));
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private LocationRequest locationRequest = new LocationRequest();
    private LocationCallback locationCallback = new MyLocationCallback();

    private void startLocating() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                        .setTitle(getString(R.string.location_permission_title))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
                            }
                        })
                        .create();
                dialog.show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            }
        } else {
            doStartLocating();
        }
    }

    private void stopLocating() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                        .setTitle(getString(R.string.location_permission_title))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
                            }
                        })
                        .create();
                dialog.show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            }
        } else {
            doStopLocating();
        }
    }

    @RequiresPermission(value = Manifest.permission.ACCESS_FINE_LOCATION)
    private void doStartLocating() {
        if (map != null) {
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.setMyLocationEnabled(true);
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @RequiresPermission(value = Manifest.permission.ACCESS_FINE_LOCATION)
    private void doStopLocating() {
        if (map != null) {
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setMyLocationEnabled(false);
        }
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void goToMyLocation() {
        if (currentLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, getZoomLevel()));
        }
    }

    private class MyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
    }

    // workaround to make a map inside a ScrollView scrollable and zoomable
    @SuppressLint("ClickableViewAccessibility")
    private void makeMapScrollable() {
        ImageView transparentImageView = findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }

    private int getZoomLevel() {
        return getResources().getInteger(R.integer.default_map_zoom_level);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocating();
                    startScanning();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_detail;
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
            case R.id.menu_edit:
                showPendingData();
                isEditing = true;
                if (map != null) {
                    map.setOnMapClickListener(this);
                }
                setContentEnabled(isEditing);
                invalidateOptionsMenu();
                setUpToolbar(getString(R.string.details_edit));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean validate() {
        boolean valid = true;
        clearValidationErrors();

        int value;
        if ((editName.getText() == null) || (TextUtils.isEmpty(editName.getText().toString()))) {
            containerName.setError(getString(R.string.mandatory));
            tabLayoutConfig.getTabAt(0).setIcon(R.drawable.ic_error);
            valid = false;
        }
        if ((group.getText() == null) || (TextUtils.isEmpty(group.getText().toString())) ||
                !groupAdapter.contains(group.getText().toString())) {
            containerGroup.setError(getString(R.string.mandatory));
            tabLayoutConfig.getTabAt(0).setIcon(R.drawable.ic_error);
            valid = false;
        }
        if ((editInterval.getText() == null) || (TextUtils.isEmpty(editInterval.getText().toString()))) {
            containerInterval.setError(getString(R.string.mandatory));
            tabLayoutConfig.getTabAt(0).setIcon(R.drawable.ic_error);
            valid = false;
        } else {
            value = Integer.valueOf(editInterval.getText().toString());
            if ((value < 100) || (value > 10240)) {
                containerInterval.setError(getString(R.string.invalid_interval));
                tabLayoutConfig.getTabAt(0).setIcon(R.drawable.ic_error);
                valid = false;
            }
        }
        if ((editUuid.getText() == null) || (TextUtils.isEmpty(editUuid.getText().toString()))) {
            if (switchIBeacon.isChecked()) {
                containerUuid.setError(getString(R.string.mandatory));
                tabLayoutConfig.getTabAt(1).setIcon(R.drawable.ic_error);
                valid = false;
            }
        } else {
            try {
                UUID uuid = UUID.fromString(editUuid.getText().toString());
            } catch (Exception e) {
                containerUuid.setError(getString(R.string.invalid_uuid));
                tabLayoutConfig.getTabAt(1).setIcon(R.drawable.ic_error);
                valid = false;
            }
        }
        if ((editMajor.getText() == null) || (TextUtils.isEmpty(editMajor.getText().toString()))) {
            if (switchIBeacon.isChecked()) {
                containerMajor.setError(getString(R.string.mandatory));
                tabLayoutConfig.getTabAt(1).setIcon(R.drawable.ic_error);
                valid = false;
            }
        } else {
            value = Integer.valueOf(editMajor.getText().toString());
            if ((value < 0) || (value > 65535)) {
                containerMajor.setError(getString(R.string.invalid_major_minor));
                tabLayoutConfig.getTabAt(1).setIcon(R.drawable.ic_error);
                valid = false;
            }
        }
        if ((editMinor.getText() == null) || (TextUtils.isEmpty(editMinor.getText().toString()))) {
            if (switchIBeacon.isChecked()) {
                containerMinor.setError(getString(R.string.mandatory));
                tabLayoutConfig.getTabAt(1).setIcon(R.drawable.ic_error);
                valid = false;
            }
        } else {
            value = Integer.valueOf(editMinor.getText().toString());
            if ((value < 0) || (value > 65535)) {
                containerMinor.setError(getString(R.string.invalid_major_minor));
                tabLayoutConfig.getTabAt(1).setIcon(R.drawable.ic_error);
                valid = false;
            }
        }

//        float latitude = 0f;
//        if ((editLatitude.getText() != null) && (editLatitude.getText().toString().length() > 0)) {
//            latitude = Float.parseFloat(editLatitude.getText().toString().replace(',', '.'));
//            if ((latitude < 46.2f) || (latitude > 47.1f)) {
//                containerLatitude.setError(getString(R.string.invalid_coordinate));
//                tabLayoutLocation.getTabAt(1).setIcon(R.drawable.ic_error);
//                valid = false;
//            }
//        }
//        float longitude = 0f;
//        if ((editLongitude.getText() != null) && (editLongitude.getText().toString().length() > 0)) {
//            longitude = Float.parseFloat(editLongitude.getText().toString().replace(',', '.'));
//            if ((longitude < 10.3f) || (longitude > 12.5f)) {
//                containerLongitude.setError(getString(R.string.invalid_coordinate));
//                tabLayoutLocation.getTabAt(1).setIcon(R.drawable.ic_error);
//                valid = false;
//            }
//        }
//        if ((latitude == 0f) && (longitude > 0f)) {
//            containerLatitude.setError(getString(R.string.invalid_coordinate));
//            tabLayoutLocation.getTabAt(1).setIcon(R.drawable.ic_error);
//            valid = false;
//        }
//        if ((latitude > 0f) && (longitude == 0f)) {
//            containerLongitude.setError(getString(R.string.invalid_coordinate));
//            tabLayoutLocation.getTabAt(1).setIcon(R.drawable.ic_error);
//            valid = false;
//        }
        return valid;
    }

    protected void clearValidationErrors() {
        containerInterval.setError(null);
        containerUuid.setError(null);
        containerMajor.setError(null);
        containerMinor.setError(null);
        containerName.setError(null);
        containerLatitude.setError(null);
        containerLongitude.setError(null);
        tabLayoutLocation.getTabAt(1).setIcon(null);
        tabLayoutConfig.getTabAt(0).setIcon(null);
        tabLayoutConfig.getTabAt(1).setIcon(null);
        tabLayoutConfig.getTabAt(2).setIcon(null);
    }

    protected void save() {
        BeaconUpdate beaconUpdate = new BeaconUpdate();
        beaconUpdate.setName(editName.getText().toString());
        beaconUpdate.setGroup(groupAdapter.getGroupId(group.getText().toString()));
        beaconUpdate.setTxPower(rbSignalStrength.getRightIndex() + 1);
        beaconUpdate.setInterval(Integer.valueOf(editInterval.getText().toString()));
        beaconUpdate.setTelemetry(switchTelemetry.isChecked());
        beaconUpdate.setIBeacon(switchIBeacon.isChecked());
        beaconUpdate.setUuid(UUID.fromString(editUuid.getText().toString()));
        beaconUpdate.setMajor(Integer.valueOf(editMajor.getText().toString()));
        beaconUpdate.setMinor(Integer.valueOf(editMinor.getText().toString()));
        beaconUpdate.setEddystoneEid(switchEid.isChecked());
        beaconUpdate.setEddystoneEtlm(switchEtlm.isChecked());
        beaconUpdate.setEddystoneTlm(switchTlm.isChecked());
        beaconUpdate.setEddystoneUid(switchUid.isChecked());
        beaconUpdate.setEddystoneUrl(switchUrl.isChecked());
        beaconUpdate.setNamespace(editNamespace.getText().toString());
        beaconUpdate.setInstanceId(editInstanceId.getText().toString());
        beaconUpdate.setUrl(editUrl.getText().toString());
        if ((editLatitude.getText() != null) && (!TextUtils.isEmpty(editLatitude.getText().toString()))) {
            beaconUpdate.setLat(Float.parseFloat(editLatitude.getText().toString().replace(',', '.')));
        }
        if ((editLongitude.getText() != null) && (!TextUtils.isEmpty(editLongitude.getText().toString()))) {
            beaconUpdate.setLng(Float.parseFloat(editLongitude.getText().toString().replace(',', '.')));
        }
        beaconUpdate.setDescription(editDescription.getText().toString());
        beaconUpdate.setLocationDescription(editFloor.getText().toString());
        beaconUpdate.setTelemetry(switchTelemetry.isChecked());
        if (beacon.getLocationType().equals(Beacon.LOCATION_INDOOR)) {
            beaconUpdate.setLocationType(BeaconUpdate.LocationTypeEnum.INDOOR);
        } else {
            beaconUpdate.setLocationType(BeaconUpdate.LocationTypeEnum.OUTDOOR);
        }

        SaveTask saveTask = new SaveTask();
        saveTask.execute(beaconUpdate);
    }

    private class SaveTask extends AsyncTask<BeaconUpdate, Void, it.bz.beacon.adminapp.swagger.client.model.Beacon> {

        private ProgressDialog dialog = new ProgressDialog(DetailActivity.this, R.style.AlertDialogCustom);
        private ApiException exception = null;

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getString(R.string.saving));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected it.bz.beacon.adminapp.swagger.client.model.Beacon doInBackground(BeaconUpdate... beaconUpdates) {
            try {
                return AdminApplication.getBeaconApi().updateUsingPATCH(beaconUpdates[0], beaconId);
            } catch (ApiException e) {
                e.printStackTrace();
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(it.bz.beacon.adminapp.swagger.client.model.Beacon result) {
            if (result != null) {
                Beacon updatedBeacon = new Beacon();
                updatedBeacon.setId(result.getId());
                updatedBeacon.setBatteryLevel(result.getBatteryLevel());
                updatedBeacon.setDescription(result.getDescription());
                updatedBeacon.setEddystoneEid(result.isEddystoneEid());
                updatedBeacon.setEddystoneEtlm(result.isEddystoneEtlm());
                updatedBeacon.setEddystoneTlm(result.isEddystoneTlm());
                updatedBeacon.setEddystoneUid(result.isEddystoneUid());
                updatedBeacon.setEddystoneUrl(result.isEddystoneUrl());
                updatedBeacon.setIBeacon(result.isIBeacon());
                updatedBeacon.setInstanceId(result.getInstanceId());
                updatedBeacon.setInterval(result.getInterval());
                updatedBeacon.setLastSeen(result.getLastSeen());
                updatedBeacon.setLat(result.getLat());
                updatedBeacon.setLng(result.getLng());
                updatedBeacon.setLocationDescription(result.getLocationDescription());
                if (result.getLocationType() != null) {
                    updatedBeacon.setLocationType(result.getLocationType().getValue());
                }
                updatedBeacon.setMajor(result.getMajor());
                updatedBeacon.setMinor(result.getMinor());
                updatedBeacon.setManufacturer(result.getManufacturer().getValue());
                updatedBeacon.setManufacturerId(result.getManufacturerId());
                updatedBeacon.setName(result.getName());
                beaconName = result.getName();
                updatedBeacon.setGroupId(result.getGroup() != null ? result.getGroup().getId() : null);
                updatedBeacon.setNamespace(result.getNamespace());
                updatedBeacon.setStatus(result.getStatus().getValue());
                updatedBeacon.setTelemetry(result.isTelemetry());
                updatedBeacon.setTxPower(result.getTxPower());
                updatedBeacon.setUrl(result.getUrl());
                updatedBeacon.setUuid(result.getUuid().toString());
                if (result.getPendingConfiguration() != null) {
                    updatedBeacon.setPendingConfiguration((new Gson()).toJson(result.getPendingConfiguration()));
                } else {
                    updatedBeacon.setPendingConfiguration(null);
                }

                beaconViewModel.insert(updatedBeacon, new InsertEvent() {
                    @Override
                    public void onSuccess(long id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        showToast(getString(R.string.saved), Toast.LENGTH_SHORT);
                        loadBeacon();
                    }

                    @Override
                    public void onFailure() {
                        showToast(getString(R.string.general_error), Toast.LENGTH_LONG);
                    }
                });
                isEditing = false;
                setContentEnabled(isEditing);
                invalidateOptionsMenu();
                setUpToolbar(beaconName);
            } else {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if ((exception != null) && ((exception.getCode() == 401) || (exception.getCode() == 403))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetailActivity.this, R.style.AlertDialogCustom));
                    builder.setMessage(getString(R.string.error_authorization));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            AdminApplication.renewLogin(DetailActivity.this);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Snackbar.make(content, getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    save();
                                }
                            })
                            .show();
                }
            }
        }
    }

    private void showToast(String string, int length) {
        Toast.makeText(this, string, length).show();
    }

    @OnClick(R.id.details_images_add)
    public void addImage(View view) {
        PickImageDialog.build(new PickSetup()
                .setTitle(getString(R.string.choose_source))
                .setCancelText(getString(R.string.cancel))
                .setCancelTextColor(ContextCompat.getColor(this, R.color.primary)))
                .show(this);
    }

    @OnClick(R.id.show_pending_config)
    public void showPendingConfig(View view) {
        Intent intent = new Intent(this, PendingConfigurationActivity.class);
        intent.putExtra(EXTRA_BEACON_ID, beaconId);
        intent.putExtra(EXTRA_BEACON_NAME, beaconName);
        startActivity(intent);
    }

    @OnClick(R.id.fab)
    public void createIssue(View view) {
        Intent intent = new Intent(this, NewIssueActivity.class);
        intent.putExtra(EXTRA_BEACON_ID, beaconId);
        startActivity(intent);
    }

    @OnClick(R.id.reset_pending_config)
    public void resetPendingConfig(View view) {
        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setMessage(R.string.reset_warning)
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        resetPendingData();
                    }
                }).create();
        dialog.show();
    }

    @OnClick(R.id.gps_current_position)
    public void useCurrentPosition(View view) {
        if (currentLocation != null) {
            setLatLngEditFields(currentLocation.latitude, currentLocation.longitude);
            setMarker(currentLocation);
        } else {
            showDialog(getString(R.string.position_not_available));
        }
    }

    @OnClick(R.id.gps_provisional_position)
    public void useProvisionalPosition(View view) {
        if (beaconInfo != null) {
            setLatLngEditFields(beaconInfo.getLatitude(), beaconInfo.getLongitude());
            setMarker(new LatLng(beaconInfo.getLatitude(), beaconInfo.getLongitude()));
        } else {
            showDialog(getString(R.string.position_not_available));
        }
    }

    private void setLatLngEditFields(double latitude, double longitude) {
        editLatitude.setText(String.format(Locale.getDefault(), "%.6f", latitude));
        editLongitude.setText(String.format(Locale.getDefault(), "%.6f", longitude));
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocating();
        proximityManager.stopScanning();
        proximityManager.disconnect();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    public void onPickResult(final PickResult pickResult) {
        if (pickResult.getError() == null) {
            String tempFilename = System.currentTimeMillis() + ".jpg";
            Bitmap bitmap = pickResult.getBitmap();
            Matrix rotationMatrix = new Matrix();
            rotationMatrix.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                    new RectF(0, 0, 1024, 1024), Matrix.ScaleToFit.CENTER);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    rotationMatrix, true);
            String tempUri = BitmapTools.saveToInternalStorage(this, bitmap,
                    getString(R.string.temp_folder), tempFilename);

            final File file = new File(tempUri);

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.uploading));
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();

            try {
                AdminApplication.getImageApi().createUsingPOST2Async(beaconId, file, new ApiCallback<it.bz.beacon.adminapp.swagger.client.model.BeaconImage>() {
                    @Override
                    public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                        dialog.dismiss();
                        if ((statusCode == 403) || (statusCode == 401)) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DetailActivity.this, R.style.AlertDialogCustom));
                                    builder.setMessage(getString(R.string.error_authorization));
                                    builder.setCancelable(false);
                                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            AdminApplication.renewLogin(DetailActivity.this);
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onSuccess(it.bz.beacon.adminapp.swagger.client.model.BeaconImage result, int statusCode, Map<String, List<String>> responseHeaders) {
                        ContextWrapper contextWrapper = new ContextWrapper(DetailActivity.this);
                        File directory = contextWrapper.getDir(getString(R.string.image_folder), Context.MODE_PRIVATE);
                        File newFile = new File(directory, result.getFileName());
                        file.renameTo(newFile);
                        BeaconImage beaconImage = new BeaconImage();
                        beaconImage.setFileName(result.getFileName());
                        beaconImage.setBeaconId(beaconId);
                        beaconImage.setId(result.getId());
                        beaconImageViewModel.insert(beaconImage, new InsertEvent() {
                            @Override
                            public void onSuccess(long id) {
                                dialog.dismiss();
                                showToast(getString(R.string.image_saved), Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onFailure() {
                                dialog.dismiss();
                                showToast(getString(R.string.image_error), Toast.LENGTH_LONG);
                            }
                        });
                    }

                    @Override
                    public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
                        if (!done) {
                            dialog.setProgress((int) (bytesWritten * 100 / contentLength));
                        } else {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
                    }
                });
            } catch (ApiException e) {
                e.printStackTrace();
            }

        } else {
            showToast(pickResult.getError().getMessage(), Toast.LENGTH_LONG);
        }
    }

    private void showBatteryWarning() {
        if ((!AdminApplication.getStorage().getDontShowWarningAgain()) && (!isBatteryWarningShowing)) {
            View checkBoxView = View.inflate(this, R.layout.checkbox, null);
            AppCompatCheckBox checkBox = checkBoxView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AdminApplication.getStorage().setDontShowWarningAgain(isChecked);
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
            builder.setTitle(getString(R.string.warning));
            builder.setMessage(getString(R.string.battery_warning));
            builder.setView(checkBoxView);
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isBatteryWarningShowing = false;
                }
            });
            builder.show();
            isBatteryWarningShowing = true;
        }
    }

    @Override
    public void onDelete(final BeaconImage beaconImage) {
        final ProgressDialog dialog = new ProgressDialog(DetailActivity.this, R.style.AlertDialogCustom);
        dialog.setMessage(getString(R.string.deleting));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        try {
            AdminApplication.getImageApi().deleteUsingDELETE1Async(beaconId, beaconImage.getId(), new ApiCallback<BaseMessage>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onSuccess(BaseMessage result, int statusCode, Map<String, List<String>> responseHeaders) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            beaconImageViewModel.deleteBeaconImage(beaconImage);
                            BitmapTools.deleteFromInternalStorage(DetailActivity.this, getString(R.string.image_folder), beaconImage.getFileName());
                        }
                    }).start();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public boolean onDeleteRequested() {
        return isEditing;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        moveMarker(latLng);
        setLatLngEditFields(latLng.latitude, latLng.longitude);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        showBatteryWarning();
    }

    private void startScanningIfLocationPermissionGranted() {
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
}