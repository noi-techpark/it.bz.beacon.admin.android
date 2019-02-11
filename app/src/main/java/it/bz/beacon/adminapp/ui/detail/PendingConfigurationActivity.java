package it.bz.beacon.adminapp.ui.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.kontakt.sdk.android.ble.connection.ErrorCause;
import com.kontakt.sdk.android.ble.connection.KontaktDeviceConnection;
import com.kontakt.sdk.android.ble.connection.KontaktDeviceConnectionFactory;
import com.kontakt.sdk.android.ble.connection.SyncableKontaktDeviceConnection;
import com.kontakt.sdk.android.ble.connection.WriteListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
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

import butterknife.ButterKnife;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconImage;
import it.bz.beacon.adminapp.data.viewmodel.BeaconImageViewModel;
import it.bz.beacon.adminapp.data.viewmodel.BeaconViewModel;
import it.bz.beacon.adminapp.ui.BaseActivity;

public class PendingConfigurationActivity extends BaseActivity {

    public static final String EXTRA_BEACON = "EXTRA_BEACON";
    public static final String EXTRA_SECURE_PROFILE = "EXTRA_SECURE_PROFILE";

    private Beacon beacon;

    private BeaconViewModel beaconViewModel;

    private SyncableKontaktDeviceConnection syncableKontaktDeviceConnection;
    private KontaktCloud kontaktCloud;
    private KontaktDeviceConnection kontaktDeviceConnection;
    private ISecureProfile secureProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            Gson gson = new Gson();
            beacon = gson.fromJson(getIntent().getStringExtra(EXTRA_BEACON), Beacon.class);
            secureProfile = getIntent().getParcelableExtra(EXTRA_SECURE_PROFILE);
        }

        beaconViewModel = ViewModelProviders.of(this).get(BeaconViewModel.class);
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
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pending_configuration;
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
