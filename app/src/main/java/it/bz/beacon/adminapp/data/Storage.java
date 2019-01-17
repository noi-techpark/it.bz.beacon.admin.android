package it.bz.beacon.adminapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Storage {

    private final static String LOGIN_USER_TOKEN = "LOGIN_USER_TOKEN";
    private final static String LOGIN_USER_NAME = "LOGIN_USER_NAME";

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

    public void clearStorage() {
        sharedPreferences.edit().clear().commit();
    }
}
