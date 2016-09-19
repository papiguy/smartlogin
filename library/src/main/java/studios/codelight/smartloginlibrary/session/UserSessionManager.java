package studios.codelight.smartloginlibrary.session;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import studios.codelight.smartloginlibrary.providers.LoginProvider;
import studios.codelight.smartloginlibrary.providers.LoginProviderFactory;
import studios.codelight.smartloginlibrary.providers.LoginProviderId;

/**
 * Copyright (c) 2016 Codelight Studios
 * Created by Kalyan on 9/29/2015.
 */
public class UserSessionManager {

    public static final String USER_SESSION = "user_session_key";
    static final String USER_PREFS = "codelight_studios_user_prefs";
    static final String DEFAULT_SESSION_VALUE = "No logged in user";
    private static final String USER_TYPE = "user_type";

    /**
     * This static method logs out the user that is logged in.
     * This implements facebook and google logout.
     * Custom user logout is left to the user.
     * It also removes the preference entries.
     */
    static SharedPreferences.Editor editor;

    /**
     * This static method can be called to get the logged in user.
     * It reads from the shared preferences and builds a SmartUser object and returns it.
     * If no user is logged in it returns null
     */
    public static SmartUser getCurrentUser(Context context) {
        SmartUser smartUser = null;
        SharedPreferences preferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String sessionUser = preferences.getString(USER_SESSION, DEFAULT_SESSION_VALUE);
        LoginProviderId user_type = LoginProviderId.from(preferences.getInt(USER_TYPE, LoginProviderId.CUSTOM.toInt()));
        if (!sessionUser.equals(DEFAULT_SESSION_VALUE)) {
            smartUser = gson.fromJson(sessionUser, SmartUser.class);
        }

        if (smartUser != null) {
            LoginProvider loginProvider = LoginProviderFactory.getInstanceFor(user_type);
            if (loginProvider.isLoggedIn(smartUser)) {
                return smartUser;
            }
        }
        logout(context, smartUser);
        return null;
    }

    /**
     * This method sets the session object for the current logged in user.
     * This is called from inside the LoginActivity to save the
     * current logged in user to the shared preferences.
     */
    public boolean updateUserSession(Context context, SmartUser smartUser) {
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        if (smartUser != null) {
            try {
                preferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.putInt(USER_TYPE, smartUser.getProviderId().toInt());

                Gson gson = new Gson();
                smartUser.setPassword(null);
                String sessionUser = gson.toJson(smartUser);
                Log.d("GSON", sessionUser);
                editor.putString(USER_SESSION, sessionUser);
                editor.apply();
                return true;
            } catch (Exception e) {
                Log.e("Session Error", e.getMessage());
                return false;
            }
        } else {
            Log.e("Session Error", "User is null");
            return false;
        }
    }

    protected static boolean logout(Context context, SmartUser user) {

        if (user == null) {
            return true;
        }
        try {
            LoginProvider loginProvider = LoginProviderFactory.getInstanceFor(user.getProviderId());
            if (loginProvider != null) {
                if (loginProvider.logout(user)) {
                    SharedPreferences preferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.remove(USER_TYPE);
                    editor.remove(USER_SESSION);
                    editor.apply();
                }
            }

        } catch (Exception e) {
            Log.e("User Logout Error", e.getMessage());
            return false;
        }

        return true;
    }

    public static boolean logout(Context context) {
        SmartUser user = getCurrentUser(context);
        return logout(context, user);
    }
}
