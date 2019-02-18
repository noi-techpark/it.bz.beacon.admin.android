package it.bz.beacon.adminapp.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kontakt.sdk.android.common.model.Config;

public class Storage {

    private final static String LOGIN_USER_TOKEN = "LOGIN_USER_TOKEN";
    private final static String LOGIN_USER_NAME = "LOGIN_USER_NAME";
    private final static String LAST_SYNCHRONIZATION_BEACONS = "LAST_SYNCHRONIZATION_BEACONS";
    private final static String LAST_SYNCHRONIZATION_ISSUES = "LAST_SYNCHRONIZATION_ISSUES";
    private final static String DONT_SHOW_WARNING_AGAIN = "DONT_SHOW_WARNING_AGAIN";

    private SharedPreferences sharedPreferences;

    public Storage(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getLoginUserName() {
        return sharedPreferences.getString(LOGIN_USER_NAME, null);
    }

    public String getLoginUserToken() {
        return sharedPreferences.getString(LOGIN_USER_TOKEN, null);
    }

    public void setUser(String username, String token) {
        sharedPreferences.edit()
                .putString(LOGIN_USER_NAME, username)
                .putString(LOGIN_USER_TOKEN, token)
                .apply();
    }

    public void setLastSynchronizationBeacons(long lastSync) {
        sharedPreferences.edit().putLong(LAST_SYNCHRONIZATION_BEACONS, lastSync).apply();
    }

    public long getLastSynchronizationBeacons() {
        return sharedPreferences.getLong(LAST_SYNCHRONIZATION_BEACONS, 0L);
    }

    public void setLastSynchronizationIssues(long lastSync) {
        sharedPreferences.edit().putLong(LAST_SYNCHRONIZATION_ISSUES, lastSync).apply();
    }

    public long getLastSynchronizationIssues() {
        return sharedPreferences.getLong(LAST_SYNCHRONIZATION_ISSUES, 0L);
    }

    public void setDontShowWarningAgain(boolean dontShowWarningAgain) {
        sharedPreferences.edit().putBoolean(DONT_SHOW_WARNING_AGAIN, dontShowWarningAgain).apply();
    }

    public boolean getDontShowWarningAgain() {
        return sharedPreferences.getBoolean(DONT_SHOW_WARNING_AGAIN, false);
    }

    @SuppressLint("ApplySharedPref")
    public void clearStorage() {
        sharedPreferences.edit().clear().commit();
    }
}
