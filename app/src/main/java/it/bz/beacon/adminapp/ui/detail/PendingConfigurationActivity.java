package it.bz.beacon.adminapp.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.kontakt.sdk.android.ble.connection.ErrorCause;
import com.kontakt.sdk.android.ble.connection.KontaktDeviceConnection;
import com.kontakt.sdk.android.ble.connection.KontaktDeviceConnectionFactory;
import com.kontakt.sdk.android.ble.connection.SyncableKontaktDeviceConnection;
import com.kontakt.sdk.android.ble.connection.WriteListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.SecureProfileListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleSecureProfileListener;
import com.kontakt.sdk.android.cloud.KontaktCloud;
import com.kontakt.sdk.android.cloud.KontaktCloudFactory;
import com.kontakt.sdk.android.cloud.response.CloudCallback;
import com.kontakt.sdk.android.cloud.response.CloudError;
import com.kontakt.sdk.android.cloud.response.CloudHeaders;
import com.kontakt.sdk.android.cloud.response.paginated.Configs;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.model.Config;
import com.kontakt.sdk.android.common.profile.ISecureProfile;

import java.util.List;
import java.util.Map;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.swagger.client.ApiCallback;
import io.swagger.client.ApiException;
import io.swagger.client.model.PendingConfiguration;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.viewmodel.BeaconViewModel;
import it.bz.beacon.adminapp.ui.BaseActivity;

import static it.bz.beacon.adminapp.ui.detail.DetailActivity.EXTRA_BEACON_ID;
import static it.bz.beacon.adminapp.ui.detail.DetailActivity.EXTRA_BEACON_NAME;

public class PendingConfigurationActivity extends BaseActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @BindView(R.id.fab_apply_now)
    protected MaterialButton btnApplyNow;

    @BindView(R.id.containerView)
    protected LinearLayout containerView;

    private long beaconId;
    private String beaconName;
    private Beacon beacon;
    private PendingConfiguration pendingConfiguration;

    private BeaconViewModel beaconViewModel;

    private SyncableKontaktDeviceConnection syncableKontaktDeviceConnection;
    private KontaktCloud kontaktCloud;
    private KontaktDeviceConnection kontaktDeviceConnection;
    private ProximityManager proximityManager;
    private ISecureProfile secureProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            beaconName = getIntent().getStringExtra(EXTRA_BEACON_NAME);
            beaconId = getIntent().getLongExtra(EXTRA_BEACON_ID, -1L);
        }

        beaconViewModel = ViewModelProviders.of(this).get(BeaconViewModel.class);

        loadBeacon();
        initializeKontakt();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpToolbar();
    }

    private void initializeKontakt() {
        KontaktSDK.initialize(getString(R.string.apiKey));
        kontaktCloud = KontaktCloudFactory.create();
        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setSecureProfileListener(createSecureProfileListener());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pending_configuration;
    }

    private void loadBeacon() {
        try {
            AdminApplication.getBeaconApi().getUsingGETAsync(beaconId, new ApiCallback<io.swagger.client.model.Beacon>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {

                }

                @Override
                public void onSuccess(io.swagger.client.model.Beacon result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (result != null) {
                        beacon = new Beacon();
                        beacon.setId(result.getId());
                        beacon.setBatteryLevel(result.getBatteryLevel());
                        beacon.setDescription(result.getDescription());
                        beacon.setEddystoneEid(result.isEddystoneEid());
                        beacon.setEddystoneEtlm(result.isEddystoneEtlm());
                        beacon.setEddystoneTlm(result.isEddystoneTlm());
                        beacon.setEddystoneUid(result.isEddystoneUid());
                        beacon.setEddystoneUrl(result.isEddystoneUrl());
                        beacon.setIBeacon(result.isIBeacon());
                        beacon.setInstanceId(result.getInstanceId());
                        beacon.setInterval(result.getInterval());
                        beacon.setLastSeen(result.getLastSeen());
                        beacon.setLat(result.getLat());
                        beacon.setLng(result.getLng());
                        beacon.setLocationDescription(result.getLocationDescription());
                        if (result.getLocationType() != null) {
                            beacon.setLocationType(result.getLocationType().getValue());
                        }
                        beacon.setMajor(result.getMajor());
                        beacon.setMinor(result.getMinor());
                        beacon.setManufacturer(result.getManufacturer().getValue());
                        beacon.setManufacturerId(result.getManufacturerId());
                        beacon.setName(result.getName());
                        beacon.setNamespace(result.getNamespace());
                        beacon.setStatus(result.getStatus().getValue());
                        beacon.setTelemetry(result.isTelemetry());
                        beacon.setTxPower(result.getTxPower());
                        beacon.setUrl(result.getUrl());
                        beacon.setUuid(result.getUuid().toString());
                        pendingConfiguration = result.getPendingConfiguration();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setUpToolbar();
                                showDifferences();
                            }
                        });

                    }
                    else {
                        // TODO: show error and hide progress dialog
//                        showDialog(getString(R.string.no_internet));
//                        if (dialog != null) {
//                            dialog.dismiss();
//                        }
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        }
        catch (ApiException e) {
            e.printStackTrace();
            // TODO: show some progress dialog
        }
    }

    private void showDifferences() {
        LinearLayout section = null;
        section = addTextDifference(String.valueOf(beacon.getTxPower()), String.valueOf(pendingConfiguration.getTxPower()), getString(R.string.details_config_signalstrength), section);
        section = addTextDifference(String.valueOf(beacon.getInterval()), String.valueOf(pendingConfiguration.getInterval()), getString(R.string.details_config_signalinterval), section);
        section = addBooleanDifference(beacon.getTelemetry(), pendingConfiguration.isTelemetry(), getString(R.string.details_info_telemetry), section);
        addSection(section, getString(R.string.details_info));

        section = null;
        section = addBooleanDifference(beacon.isIBeacon(), pendingConfiguration.isIBeacon(), getString(R.string.details_ibeacon), section);
        section = addTextDifference(String.valueOf(beacon.getUuid()), String.valueOf(pendingConfiguration.getUuid()), "UUID", section);
        section = addTextDifference(String.valueOf(beacon.getMajor()), String.valueOf(pendingConfiguration.getMajor()), getString(R.string.details_config_major), section);
        section = addTextDifference(String.valueOf(beacon.getMinor()), String.valueOf(pendingConfiguration.getMinor()), getString(R.string.details_config_minor), section);
        addSection(section, getString(R.string.details_ibeacon));

        section = null;
        section = addBooleanDifference(beacon.isEddystoneUid(), pendingConfiguration.isEddystoneUid(), getString(R.string.details_eddystone_uid), section);
        section = addTextDifference(String.valueOf(beacon.getNamespace()), String.valueOf(pendingConfiguration.getNamespace()), getString(R.string.details_config_namespace), section);
        section = addTextDifference(String.valueOf(beacon.getInstanceId()), String.valueOf(pendingConfiguration.getInstanceId()), getString(R.string.details_config_instanceid), section);
        section = addBooleanDifference(beacon.isEddystoneUrl(), pendingConfiguration.isEddystoneUrl(), getString(R.string.details_eddystone_url), section);
        section = addTextDifference(String.valueOf(beacon.getUrl()), String.valueOf(pendingConfiguration.getUrl()), getString(R.string.details_config_url), section);
        section = addBooleanDifference(beacon.isEddystoneEid(), pendingConfiguration.isEddystoneEid(), getString(R.string.details_eddystone_eid), section);
        section = addBooleanDifference(beacon.isEddystoneEtlm(), pendingConfiguration.isEddystoneEtlm(), getString(R.string.details_eddystone_etlm), section);
        section = addBooleanDifference(beacon.isEddystoneTlm(), pendingConfiguration.isEddystoneTlm(), getString(R.string.details_eddystone_tlm), section);
        addSection(section, getString(R.string.details_eddystone));
    }

    private void setSectionTitle(LinearLayout section, String title) {
        TextView textView = section.findViewById(R.id.section_title);
        textView.setText(title);
    }

    private LinearLayout addTextDifference(String oldValue, String newValue, String title, LinearLayout parent) {
        View differenceText = null;
        if (!oldValue.equalsIgnoreCase(newValue)) {
            Log.d(AdminApplication.LOG_TAG, "found difference for " + title);
            if (parent == null) {
                parent = (LinearLayout) getLayoutInflater().inflate(R.layout.section_pending_config, null);
            }
            differenceText = getLayoutInflater().inflate(R.layout.pending_config_text, null);
            TextView textView = differenceText.findViewById(R.id.pending_config_title);
            textView.setText(title);
            textView = differenceText.findViewById(R.id.pending_config_value_old);
            textView.setText(oldValue);
            textView = differenceText.findViewById(R.id.pending_config_value_new);
            textView.setText(newValue);
            parent.addView(differenceText);
        }
        return parent;
    }

    private LinearLayout addBooleanDifference(boolean oldValue, boolean newValue, String title, LinearLayout parent) {
        View differenceBoolean = null;
        if (oldValue != newValue) {
            Log.d(AdminApplication.LOG_TAG, "found difference for " + title);
            if (parent == null) {
                parent = (LinearLayout) getLayoutInflater().inflate(R.layout.section_pending_config, null);
            }
            differenceBoolean = getLayoutInflater().inflate(R.layout.pending_config_boolean, null);
            TextView textView = differenceBoolean.findViewById(R.id.pending_config_title);
            textView.setText(title);
            AppCompatCheckBox checkBox = differenceBoolean.findViewById(R.id.pending_config_value_old);
            checkBox.setChecked(oldValue);
            checkBox = differenceBoolean.findViewById(R.id.pending_config_value_new);
            checkBox.setChecked(newValue);
            parent.addView(differenceBoolean);
        }
        return parent;
    }

    private void addSection(LinearLayout section, String title) {
        if (section != null) {
            setSectionTitle(section, title);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, (int) getResources().getDimension(R.dimen.app_default_margin), 0, 0);
            containerView.addView(section, layoutParams);
        }
    }

    private SecureProfileListener createSecureProfileListener() {
        return new SimpleSecureProfileListener() {
            @Override
            public void onProfileDiscovered(final ISecureProfile profile) {
                updateBeaconNearby(profile);
                super.onProfileDiscovered(profile);
            }

            @Override
            public void onProfilesUpdated(List<ISecureProfile> profiles) {
                for (ISecureProfile profile : profiles) {
                    updateBeaconNearby(profile);
                }
                super.onProfilesUpdated(profiles);
            }

            @Override
            public void onProfileLost(ISecureProfile profile) {
                if (beacon.getManufacturerId().equals(profile.getUniqueId())) {
                    secureProfile = null;
                    btnApplyNow.setEnabled(false);
                }
                super.onProfileLost(profile);
            }

            private void updateBeaconNearby(ISecureProfile profile) {
                if (beacon.getManufacturerId().equals(profile.getUniqueId())) {
                    secureProfile = profile;
                    if (beacon.getStatus().equals(Beacon.STATUS_CONFIGURATION_PENDING)) {
                        btnApplyNow.setEnabled(true);
                    }
                }
            }
        };
    }

    private void setUpToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        if (beacon != null) {
            toolbar.setTitle(beacon.getName());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendToCloud(Config secureConfig, WriteListener.WriteResponse writeResponse) {
        secureConfig.applySecureResponse(writeResponse.getExtra(), writeResponse.getUnixTimestamp());
        kontaktCloud.devices()
                .applySecureConfigs(secureConfig)
                .execute(new CloudCallback<Configs>() {
                    @Override
                    public void onSuccess(Configs response, CloudHeaders headers) {
                        if (response != null && response.getContent() != null && response.getContent().size() > 0) {

                        }
                        Log.d(AdminApplication.LOG_TAG, "Successfully applied config");
                    }

                    @Override
                    public void onError(CloudError error) {
                        Log.e(AdminApplication.LOG_TAG, "config api error");
                    }
                });
    }

    private void sendToBeacon(final ISecureProfile profile) {
        kontaktCloud.configs().secure().withIds(profile.getUniqueId()).execute(new CloudCallback<Configs>() {
            @Override
            public void onSuccess(Configs response, CloudHeaders headers) {
                if (response != null && response.getContent() != null && response.getContent().size() > 0) {
                    final Config config = response.getContent().get(0);
                    Log.d(AdminApplication.LOG_TAG, "Config for beacon received: " + profile.getUniqueId());

                    kontaktDeviceConnection = KontaktDeviceConnectionFactory.create(PendingConfigurationActivity.this, profile, new KontaktDeviceConnection.ConnectionListener() {
                        @Override
                        public void onConnectionOpened() {
                            Log.d(AdminApplication.LOG_TAG, "Connection opened");
                        }

                        @Override
                        public void onConnected() {
                            kontaktDeviceConnection.applySecureConfig(config.getSecureRequest(), new WriteListener() {
                                @Override
                                public void onWriteSuccess(WriteListener.WriteResponse response) {
                                    sendToCloud(config, response);
                                    disconnect();
                                    Log.d(AdminApplication.LOG_TAG, "Written");
                                }

                                @Override
                                public void onWriteFailure(ErrorCause cause) {
                                    Log.d(AdminApplication.LOG_TAG, "Write failure");
                                    disconnect();
                                }
                            });
                        }

                        @Override
                        public void onErrorOccured(int errorCode) {
                            Log.d(AdminApplication.LOG_TAG, "Connection failure");
                        }

                        @Override
                        public void onDisconnected() {
                            Log.d(AdminApplication.LOG_TAG, "Disconnect");
                        }
                    });
                    kontaktDeviceConnection.connect();
                }
            }

            @Override
            public void onError(CloudError error) {

            }
        });
    }

    private void disconnect() {
        if (kontaktDeviceConnection != null) {
            kontaktDeviceConnection.close();
            kontaktDeviceConnection = null;
        }
    }
}
