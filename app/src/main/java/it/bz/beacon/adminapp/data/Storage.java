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

    // TODO: uncomment as soon as Login API is ready
//    public FeUserDataUser getUser() {
//        try {
//            String userString = sharedPreferences.getString(USER_KEY, null);
//            if (userString != null) {
//                Gson gson = new Gson();
//                return gson.fromJson(userString, FeUserDataUser.class);
//            }
//            return null;
//        } catch (Exception e){
//            return null;
//        }
//    }
//
//    public void setUser(FeUser user, String login) {
//        Gson gson = new Gson();
//        String userJson = gson.toJson(user.getData().getUser());
//        sharedPreferences.edit()
//                .putString(USER_PASS, password)
//                .putString(USER_NAME, login)
//                .commit();
//    }

    public String getLoginUserName() {
        return sharedPreferences.getString(LOGIN_USER_NAME, null);
    }

    public String getLoginUserToken() {
        return sharedPreferences.getString(LOGIN_USER_TOKEN, null);
    }

    public void clearStorage() {
        sharedPreferences.edit().clear().commit();
    }
}
