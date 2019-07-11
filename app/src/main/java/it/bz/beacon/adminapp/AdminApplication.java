package it.bz.beacon.adminapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.MapsInitializer;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.swagger.client.ApiClient;
import it.bz.beacon.adminapp.swagger.client.api.AuthControllerApi;
import it.bz.beacon.adminapp.swagger.client.api.BeaconControllerApi;
import it.bz.beacon.adminapp.swagger.client.api.ImageControllerApi;
import it.bz.beacon.adminapp.swagger.client.api.IssueControllerApi;
import it.bz.beacon.adminapp.ui.login.LoginActivity;
import it.bz.beacon.beaconsuedtirolsdk.NearbyBeaconManager;

public class AdminApplication extends Application {

    private static AuthControllerApi authControllerApi;
    private static BeaconControllerApi beaconControllerApi;
    private static ImageControllerApi imageControllerApi;
    private static IssueControllerApi issueControllerApi;
    private static Storage storage;
    public static final String LOG_TAG = "BeaconAdmin";

    @Override
    public void onCreate() {
        super.onCreate();
        storage = new Storage(getApplicationContext());
        NearbyBeaconManager.initialize(this);

        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(getApplicationContext().getString(R.string.basePath));
        apiClient.setConnectTimeout(getApplicationContext().getResources().getInteger(R.integer.connection_timeout));
        apiClient.setReadTimeout(getApplicationContext().getResources().getInteger(R.integer.read_timeout));

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        apiClient.getHttpClient().networkInterceptors().add(interceptor);

        it.bz.beacon.adminapp.swagger.client.Configuration.setDefaultApiClient(apiClient);
        authControllerApi = new AuthControllerApi();
        beaconControllerApi = new BeaconControllerApi();
        imageControllerApi = new ImageControllerApi();
        issueControllerApi = new IssueControllerApi();
        if (!TextUtils.isEmpty(storage.getLoginUserToken())) {
            setBearerToken(storage.getLoginUserToken());
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkStateReceiver(), intentFilter);
        MapsInitializer.initialize(this);
    }

    public static void setBearerToken(String bearerToken) {
        beaconControllerApi.getApiClient().setApiKeyPrefix("Bearer");
        beaconControllerApi.getApiClient().setApiKey(bearerToken);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    public static AuthControllerApi getAuthApi() {
        return authControllerApi;
    }

    public static BeaconControllerApi getBeaconApi() {
        return beaconControllerApi;
    }

    public static ImageControllerApi getImageApi() {
        return imageControllerApi;
    }

    public static IssueControllerApi getIssueApi() {
        return issueControllerApi;
    }

    public static void logout(Context context) {
        storage.clearStorage();
        context.deleteDatabase(BeaconDatabase.DB_NAME);
        BeaconDatabase.removeInstance();
        AdminApplication.setBearerToken("");
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        if (context instanceof Activity) {
            ((Activity) context).finishAffinity();
        }
    }

    public static void renewLogin(Context context) {
        AdminApplication.setBearerToken("");
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        if (context instanceof Activity) {
            ((Activity) context).finishAffinity();
        }
    }
}
