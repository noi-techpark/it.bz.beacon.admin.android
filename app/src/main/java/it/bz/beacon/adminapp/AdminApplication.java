package it.bz.beacon.adminapp;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import it.bz.beacon.adminapp.data.Storage;

public class AdminApplication extends Application {

//    private static AuthApi authApi;
//    private static DefaultApi defaultApi;
    private static Storage storage;
    public static final String LOG_TAG = "Beacon";

    @Override
    public void onCreate() {
        super.onCreate();
        storage = new Storage(getApplicationContext());
//        ApiClient apiClient = new ApiClient();
//        apiClient.setBasePath(getApplicationContext().getString(R.string.basePath));
//        apiClient.setConnectTimeout(getApplicationContext().getResources().getInteger(R.integer.connection_timeout)); // connect timeout
//        apiClient.setReadTimeout(getApplicationContext().getResources().getInteger(R.integer.read_timeout));    // socket timeout
//        apiClient.setUsername(getString(R.string.basic_username));
//        apiClient.setPassword(getString(R.string.basic_password));
//        io.swagger.client.Configuration.setDefaultApiClient(apiClient);

//        authApi = new AuthApi();
//        defaultApi = new DefaultApi();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkStateReceiver(), intentFilter);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static Storage getStorage() {
        return storage;
    }

//    public static AuthApi getAuthApi() {
//        return authApi;
//    }
//
//    public static DefaultApi getDefaultApi() {
//        return defaultApi;
//    }
}
