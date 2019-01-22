package it.bz.beacon.adminapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.data.event.DataUpdateEvent;
import it.bz.beacon.adminapp.data.repository.BeaconRepository;

public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.d(AdminApplication.LOG_TAG, "Network connectivity change");

        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                Storage storage = AdminApplication.getStorage();
                int synchronizationInterval = context.getResources().getInteger(R.integer.synchronization_interval);

                if (networkInfo != null
                        && networkInfo.isConnectedOrConnecting()
                        && (storage.getLoginUserToken() != null)
                        && (storage.getLastSynchronizationBeacons() + synchronizationInterval * 60000L < System.currentTimeMillis())) {

                    BeaconRepository beaconRepository = new BeaconRepository(context);
                    beaconRepository.refreshBeacons(new DataUpdateEvent() {
                        @Override
                        public void onSuccess() {
                            Log.i(AdminApplication.LOG_TAG, "Beacons refreshed!");
                        }

                        @Override
                        public void onError() {
                            Log.e(AdminApplication.LOG_TAG, "Error refreshing beacons");
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            Log.e(AdminApplication.LOG_TAG, "Authentication failed");
                        }
                    });
                }
                else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    Log.e(AdminApplication.LOG_TAG, "No connection");
                }
            }
        }
    }
}