// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.ui.detail;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.kontakt.sdk.android.ble.connection.ErrorCause;
import com.kontakt.sdk.android.ble.connection.KontaktDeviceConnection;
import com.kontakt.sdk.android.ble.connection.KontaktDeviceConnectionFactory;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.GroupApiKey;
import it.bz.beacon.adminapp.data.entity.PendingSecureConfig;
import it.bz.beacon.adminapp.data.event.LoadBeaconEvent;
import it.bz.beacon.adminapp.data.event.LoadGroupApiKeyEvent;
import it.bz.beacon.adminapp.data.repository.BeaconRepository;
import it.bz.beacon.adminapp.data.viewmodel.BeaconViewModel;
import it.bz.beacon.adminapp.data.viewmodel.GroupApiKeyViewModel;
import it.bz.beacon.adminapp.data.viewmodel.PendingSecureConfigViewModel;
import it.bz.beacon.adminapp.swagger.client.ApiCallback;
import it.bz.beacon.adminapp.swagger.client.ApiException;
import it.bz.beacon.adminapp.swagger.client.api.TrustedBeaconControllerApi;
import it.bz.beacon.adminapp.swagger.client.model.BeaconBatteryLevelUpdate;
import it.bz.beacon.adminapp.swagger.client.model.PendingConfiguration;
import it.bz.beacon.adminapp.ui.BaseActivity;

import static it.bz.beacon.adminapp.ui.detail.DetailActivity.EXTRA_BEACON_ID;
import static it.bz.beacon.adminapp.ui.detail.DetailActivity.EXTRA_BEACON_NAME;

public class PendingConfigurationActivity extends BaseActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @BindView(R.id.fab_apply_now)
    protected MaterialButton btnApplyNow;

    @BindView(R.id.containerView)
    protected LinearLayout containerView;

    @BindView(R.id.scrollview)
    protected ScrollView scrollView;

    @BindView(R.id.empty)
    protected TextView txtEmpty;

    private String beaconId;
    private String beaconName;
    private Beacon beacon;
    private GroupApiKey groupApiKey;
    private boolean isPendingConfigNull = true;

    private BeaconViewModel beaconViewModel;
    private PendingSecureConfigViewModel pendingSecureConfigViewModel;
    private GroupApiKeyViewModel groupApiKeyViewModel;

    private KontaktCloud kontaktCloud;
    private KontaktDeviceConnection kontaktDeviceConnection;
    private ProximityManager proximityManager;
    private ISecureProfile secureProfile;

    private ProgressDialog dialog;

    private TrustedBeaconControllerApi trustedApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            beaconName = getIntent().getStringExtra(EXTRA_BEACON_NAME);
            beaconId = getIntent().getStringExtra(EXTRA_BEACON_ID);
        }

        trustedApi = AdminApplication.getTrustedBeaconControllerApi();
        if (!getString(R.string.trustedApiUser).isEmpty() && !getString(R.string.trustedApiPassword).isEmpty()) {
            trustedApi.getApiClient().setUsername(getString(R.string.trustedApiUser));
            trustedApi.getApiClient().setPassword(getString(R.string.trustedApiPassword));
        }

        beaconViewModel = ViewModelProviders.of(this).get(BeaconViewModel.class);
        pendingSecureConfigViewModel = ViewModelProviders.of(this).get(PendingSecureConfigViewModel.class);
        groupApiKeyViewModel = ViewModelProviders.of(this).get(GroupApiKeyViewModel.class);

        loadBeacon();
        initializeKontakt();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScanningIfLocationPermissionGranted();
        setUpToolbar();
    }

    @Override
    public void onPause() {
        super.onPause();
        proximityManager.stopScanning();
        proximityManager.disconnect();
    }

    private void initializeKontakt() {
        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setSecureProfileListener(createSecureProfileListener());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pending_configuration;
    }

    private void loadBeacon() {
        beaconViewModel.getById(beaconId, new LoadBeaconEvent() {
            @Override
            public void onSuccess(Beacon loadedBeacon) {
                if (loadedBeacon != null) {
                    beacon = loadedBeacon;
                    beaconName = loadedBeacon.getName();
                    groupApiKeyViewModel.getByGroupId(beacon.getGroupId(), new LoadGroupApiKeyEvent() {
                        @Override
                        public void onSuccess(GroupApiKey groupApiKey) {
                            PendingConfigurationActivity.this.groupApiKey = groupApiKey;
                            KontaktSDK.initialize(groupApiKey.getApiKey());
                            kontaktCloud = KontaktCloudFactory.create();
                            setUpToolbar();
                            if (!TextUtils.isEmpty(loadedBeacon.getPendingConfiguration())) {
                                showDifferences(loadedBeacon, (new Gson()).fromJson(loadedBeacon.getPendingConfiguration(), PendingConfiguration.class));
                            } else {
                                if (loadedBeacon.getPendingConfiguration() == null) {
                                    isPendingConfigNull = true;
                                }
                            }
                        }

                        @Override
                        public void onError() {
                            showToast(getString(R.string.general_error), Toast.LENGTH_LONG);
                        }
                    });
                }
            }

            @Override
            public void onError() {
                showToast(getString(R.string.general_error), Toast.LENGTH_LONG);
            }
        });
    }

    private void showDifferences(Beacon beacon, PendingConfiguration pendingConfiguration) {
        if (containerView.getChildCount() > 1) {
            containerView.removeViews(1, containerView.getChildCount() - 1);
        }
        txtEmpty.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        if (pendingConfiguration != null) {
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
            isPendingConfigNull = false;
        } else {
            isPendingConfigNull = true;
        }
    }

    private void setSectionTitle(LinearLayout section, String title) {
        TextView textView = section.findViewById(R.id.section_title);
        textView.setText(title);
    }

    private LinearLayout addTextDifference(String oldValue, String newValue, String title, LinearLayout parent) {
        View differenceText = null;
        if (!oldValue.equalsIgnoreCase(newValue)) {
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
            txtEmpty.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    private SecureProfileListener createSecureProfileListener() {
        return new SimpleSecureProfileListener() {
            @Override
            public void onProfileDiscovered(final ISecureProfile profile) {
                super.onProfileDiscovered(profile);
                updateBatteryStatus(profile);
                updateBeaconNearby(profile);
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
                if (beacon.getManufacturerId().equals(profile.getUniqueId())) {
                    secureProfile = null;
                    btnApplyNow.setEnabled(false);
                }
            }

            private void updateBeaconNearby(ISecureProfile profile) {
                if (beacon.getManufacturerId().equals(profile.getUniqueId())) {
                    secureProfile = profile;
                    if ((beacon.getStatus().equals(Beacon.STATUS_CONFIGURATION_PENDING)) && !isPendingConfigNull) {
                        btnApplyNow.setEnabled(true);
                    }
                }
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
        };
    }

    private void setUpToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        if (!TextUtils.isEmpty(beaconName)) {
            toolbar.setTitle(beaconName);
        }
    }

    private void showToast(String string, int length) {
        Toast.makeText(this, string, length).show();
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
                            Log.d(AdminApplication.LOG_TAG, "Response from sendToCloud: " + response.toString());
                        }
                        Log.d(AdminApplication.LOG_TAG, "sendToCloud successful");
                        hideProgressDialog();
                        isPendingConfigNull = true;
                        txtEmpty.setText(getString(R.string.applied_successfully));
                        showDifferences(beacon, null);
                        btnApplyNow.setEnabled(false);
                        BeaconRepository beaconRepository = new BeaconRepository(PendingConfigurationActivity.this);
                        beaconRepository.refreshBeacon(beaconId, null);
                    }

                    @Override
                    public void onError(CloudError error) {
                        Log.e(AdminApplication.LOG_TAG, "sendToCloud failed: " + error.getMessage());
                        hideProgressDialog();
                        isPendingConfigNull = true;
                        txtEmpty.setText(error.getMessage());
                        showDifferences(beacon, null);
                        btnApplyNow.setEnabled(false);
                        Snackbar.make(scrollView, getString(R.string.error_sending_to_cloud), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sendToCloud(secureConfig, writeResponse);
                                    }
                                })
                                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                    @Override
                                    public void onDismissed(Snackbar transientBottomBar, int event) {
                                        if (event != BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) {
                                            PendingSecureConfig pendingSecureConfig = new PendingSecureConfig();
                                            pendingSecureConfig.setConfig((new Gson()).toJson(secureConfig));
                                            pendingSecureConfig.setApiKey(PendingConfigurationActivity.this.groupApiKey.getApiKey());
                                            pendingSecureConfig.setBeaconId(PendingConfigurationActivity.this.beaconId);
                                            pendingSecureConfigViewModel.insert(pendingSecureConfig);
                                        }
                                    }
                                })
                                .show();
                    }
                });
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog(PendingConfigurationActivity.this, R.style.AlertDialogCustom);
        dialog.setMessage(getString(R.string.applying));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    private void hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void sendToBeacon(final ISecureProfile profile) {
        if (profile == null) {
            return;
        }
        showProgressDialog();

        kontaktCloud
                .configs()
                .secure()
                .withIds(profile.getUniqueId())
                .execute(new CloudCallback<Configs>() {
            @Override
            public void onSuccess(Configs response, CloudHeaders headers) {
                if (response != null && response.getContent() != null && response.getContent().size() > 0) {
                    final Config config = response.getContent().get(0);

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
                                }

                                @Override
                                public void onWriteFailure(ErrorCause cause) {
                                    Log.e(AdminApplication.LOG_TAG, "Write failure");
                                    disconnect();
                                    Snackbar.make(scrollView, getString(R.string.error_writing_to_beacon), Snackbar.LENGTH_INDEFINITE)
                                            .setAction(getString(R.string.retry), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    sendToBeacon(profile);
                                                }
                                            })
                                            .show();
                                }
                            });
                        }

                        @Override
                        public void onErrorOccured(int errorCode) {
                            // error code 1019
                            Log.e(AdminApplication.LOG_TAG, "Connection failure");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressDialog();
                                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_connecting_to_beacon), Snackbar.LENGTH_INDEFINITE)
                                            .setAction(getString(R.string.retry), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    sendToBeacon(profile);
                                                }
                                            })
                                            .show();
                                }
                            });

                        }

                        @Override
                        public void onDisconnected() {
                            Log.d(AdminApplication.LOG_TAG, "Disconnect");
                        }
                    });
                    kontaktDeviceConnection.connect();
                } else {
                    hideProgressDialog();
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(PendingConfigurationActivity.this, R.style.AlertDialogCustom));
                    builder.setTitle(getString(R.string.error_applying_failed));
                    builder.setMessage(getString(R.string.error_possible_api_key_no_access_to_beacon));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

            @Override
            public void onError(CloudError error) {
                hideProgressDialog();
            }
        });
    }

    private void startScanning() {
            proximityManager.connect(new OnServiceReadyListener() {
                @Override
                public void onServiceReady() {
                    proximityManager.startScanning();
                }
            });
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
                break;
        }
    }

    private void disconnect() {
        if (kontaktDeviceConnection != null) {
            kontaktDeviceConnection.close();
            kontaktDeviceConnection = null;
        }
    }

    @OnClick(R.id.fab_apply_now)
    public void applyPendingConfig(View view) {
        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setMessage(R.string.really_apply_now)
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.apply_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sendToBeacon(secureProfile);
                    }
                }).create();
        dialog.show();
    }
}
