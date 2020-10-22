package it.bz.beacon.adminapp;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bz.beacon.adminapp.data.BeaconDatabase;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.data.event.RenewLoginEvent;
import it.bz.beacon.adminapp.swagger.client.ApiCallback;
import it.bz.beacon.adminapp.swagger.client.ApiClient;
import it.bz.beacon.adminapp.swagger.client.ApiException;
import it.bz.beacon.adminapp.swagger.client.api.AuthControllerApi;
import it.bz.beacon.adminapp.swagger.client.api.BeaconControllerApi;
import it.bz.beacon.adminapp.swagger.client.api.GroupControllerApi;
import it.bz.beacon.adminapp.swagger.client.api.ImageControllerApi;
import it.bz.beacon.adminapp.swagger.client.api.InfoControllerApi;
import it.bz.beacon.adminapp.swagger.client.api.IssueControllerApi;
import it.bz.beacon.adminapp.swagger.client.api.TrustedBeaconControllerApi;
import it.bz.beacon.adminapp.swagger.client.model.AuthenticationRequest;
import it.bz.beacon.adminapp.swagger.client.model.AuthenticationToken;
import it.bz.beacon.adminapp.swagger.client.model.UserRoleGroup;
import it.bz.beacon.adminapp.ui.login.LoginActivity;
import it.bz.beacon.beaconsuedtirolsdk.NearbyBeaconManager;

public class AdminApplication extends Application {

    private static AuthControllerApi authControllerApi;
    private static BeaconControllerApi beaconControllerApi;
    private static InfoControllerApi infoControllerApi;
    private static ImageControllerApi imageControllerApi;
    private static IssueControllerApi issueControllerApi;
    private static TrustedBeaconControllerApi trustedBeaconControllerApi;
    private static GroupControllerApi groupControllerApi;
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
        infoControllerApi = new InfoControllerApi();
        imageControllerApi = new ImageControllerApi();
        issueControllerApi = new IssueControllerApi();
        trustedBeaconControllerApi = new TrustedBeaconControllerApi();
        groupControllerApi = new GroupControllerApi();
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

    public static InfoControllerApi getInfoControllerApi() {
        return infoControllerApi;
    }

    public static ImageControllerApi getImageApi() {
        return imageControllerApi;
    }

    public static IssueControllerApi getIssueApi() {
        return issueControllerApi;
    }

    public static TrustedBeaconControllerApi getTrustedBeaconControllerApi() {
        return trustedBeaconControllerApi;
    }

    public static GroupControllerApi getGroupControllerApi() {
        return groupControllerApi;
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

    public static void renewLogin(final Activity activity) {
        AdminApplication.renewLogin(activity, () -> {});
    }

    public static void renewLogin(final Activity activity, final RenewLoginEvent renewLoginEvent) {
        if (activity == null) {
            return;
        }
        String username = AdminApplication.getStorage().getLoginUserName();
        String password = AdminApplication.getStorage().getLoginPassword();

        if (TextUtils.isEmpty(password)) {
            logout(activity);
        }

        final Dialog dialogTransparent = new Dialog(activity, android.R.style.Theme_Black);
        View view = LayoutInflater.from(activity).inflate(
                R.layout.remove_border, null);
        dialogTransparent.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTransparent.getWindow().setBackgroundDrawableResource(
                R.color.semitransparent);
        dialogTransparent.setContentView(view);
        AdminApplication.setBearerToken("");

        try {
            AuthenticationRequest request = new AuthenticationRequest();
            request.setUsername(username);
            request.setPassword(password);

            dialogTransparent.show();

            authControllerApi.signinUsingPOSTAsync(request, new ApiCallback<AuthenticationToken>() {

                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (dialogTransparent.isShowing()) {
                                dialogTransparent.dismiss();
                            }
                            if ((statusCode == 403) || (statusCode == 401)) {
                                Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.error_incorrect_login), Snackbar.LENGTH_LONG)
                                        .show();
                            } else {
                                Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE)
                                        .setAction(activity.getString(R.string.retry), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                renewLogin(activity);
                                            }
                                        })
                                        .show();
                            }
                        }
                    });
                }

                @Override
                public void onSuccess(AuthenticationToken result, int statusCode, Map<String, List<String>> responseHeaders) {
                    storage.setUser(username, password, result.getToken());
                    AdminApplication.setBearerToken(result.getToken());
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (dialogTransparent.isShowing()) {
                                dialogTransparent.dismiss();
                            }
                            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.logged_in), Snackbar.LENGTH_SHORT)
                                    .show();
                            renewLoginEvent.onSuccess();
                        }
                    });
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch (Exception e) {
            if (dialogTransparent.isShowing()) {
                dialogTransparent.dismiss();
            }
            Log.e(AdminApplication.LOG_TAG, e.getMessage());
        }
    }

    public static HashMap<String, Object> getTokenBody() {
        String token = storage.getLoginUserToken();
        if (!TextUtils.isEmpty(storage.getLoginUserToken())) {
            try {
                String[] split = token.split("\\.");
                byte[] decodedBytes = Base64.decode(split[1], Base64.URL_SAFE);
                String bodyJson = new String(decodedBytes, "UTF-8");
                return new Gson().fromJson(bodyJson, new TypeToken<HashMap<String, Object>>() {
                }.getType());
            } catch (Exception e) {
                Log.e(AdminApplication.LOG_TAG, e.getMessage());
            }
        }
        return null;
    }

    public static boolean isAdmin() {
        HashMap<String, Object> tokenBody = getTokenBody();
        if(tokenBody != null) {
            return (boolean) tokenBody.getOrDefault("admin", false);
        }
        return false;
    }

    public static List<UserRoleGroup> getUserRoleGroups() {
        HashMap<String, Object> tokenBody = getTokenBody();
        if(tokenBody != null) {
            return new Gson().fromJson(new Gson().toJson(tokenBody.getOrDefault("groups", new Object())),
                    new TypeToken<ArrayList<UserRoleGroup>>() {
                    }.getType());
        }
        return new ArrayList<>();
    }
}
