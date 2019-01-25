package it.bz.beacon.adminapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Storage {

    private final static String LOGIN_USER_TOKEN = "LOGIN_USER_TOKEN";
    private final static String LOGIN_USER_NAME = "LOGIN_USER_NAME";
    private final static String LAST_SYNCHRONIZATION_BEACONS = "LAST_SYNCHRONIZATION_BEACONS";

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
                .commit();
    }

    public void setLastSynchronizationBeacons(long lastSync) {
        sharedPreferences.edit().putLong(LAST_SYNCHRONIZATION_BEACONS, lastSync).apply();
    }

    public long getLastSynchronizationBeacons() {
        return sharedPreferences.getLong(LAST_SYNCHRONIZATION_BEACONS, 0L);
    }

    public void clearStorage() {
        sharedPreferences.edit().clear().commit();
    }
}
