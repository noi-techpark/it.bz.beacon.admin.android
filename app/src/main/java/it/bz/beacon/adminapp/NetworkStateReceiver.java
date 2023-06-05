// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.kontakt.sdk.android.cloud.KontaktCloud;
import com.kontakt.sdk.android.cloud.KontaktCloudFactory;
import com.kontakt.sdk.android.cloud.response.CloudCallback;
import com.kontakt.sdk.android.cloud.response.CloudError;
import com.kontakt.sdk.android.cloud.response.CloudHeaders;
import com.kontakt.sdk.android.cloud.response.paginated.Configs;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.model.Config;

import java.util.List;

import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.data.entity.PendingSecureConfig;
import it.bz.beacon.adminapp.data.repository.BeaconRepository;
import it.bz.beacon.adminapp.data.repository.PendingSecureConfigRepository;

public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                Storage storage = AdminApplication.getStorage();

                if (networkInfo != null
                        && networkInfo.isConnectedOrConnecting()
                        && (storage.getLoginUserToken() != null)) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(AdminApplication.LOG_TAG, "trying to send pending secureConfigs");
                            PendingSecureConfigRepository pendingSecureConfigRepository = new PendingSecureConfigRepository(context);
                            List<String> pendingSecureApiKeys = pendingSecureConfigRepository.getAllDistinctApiKey();

                            for (String pendingSecureApiKey: pendingSecureApiKeys) {
                                KontaktSDK.initialize(pendingSecureApiKey);
                                KontaktCloud kontaktCloud = KontaktCloudFactory.create();

                                List<PendingSecureConfig> pendingSecureConfigs =
                                        pendingSecureConfigRepository.getListByApiKey(pendingSecureApiKey);

                                for (final PendingSecureConfig pendingSecureConfig : pendingSecureConfigs) {
                                    Config secureConfig = (new Gson()).fromJson(pendingSecureConfig.getConfig(), Config.class);
                                    kontaktCloud.devices()
                                            .applySecureConfigs(secureConfig)
                                            .execute(new CloudCallback<Configs>() {
                                                @Override
                                                public void onSuccess(Configs response, CloudHeaders headers) {
                                                    if (response != null && response.getContent() != null && response.getContent().size() > 0) {
                                                        Log.d(AdminApplication.LOG_TAG, "Response from sendToCloud: " + response.toString());
                                                    }
                                                    Log.d(AdminApplication.LOG_TAG, "sendToCloud successful");
                                                    BeaconRepository beaconRepository = new BeaconRepository(context);
                                                    beaconRepository.refreshBeacon(pendingSecureConfig.getBeaconId(), null);
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            pendingSecureConfigRepository.deletePendingSecureConfig(pendingSecureConfig);
                                                        }
                                                    }).start();
                                                }

                                                @Override
                                                public void onError(CloudError error) {
                                                    Log.e(AdminApplication.LOG_TAG, "sendToCloud failed: " + error.getMessage());
                                                }
                                            });
                                }
                            }
                            Log.d(AdminApplication.LOG_TAG, "finished sending pending secureConfigs");
                        }
                    }).start();
                }
            }
        }
    }
}