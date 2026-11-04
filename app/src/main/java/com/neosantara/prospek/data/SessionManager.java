package com.neosantara.prospek.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREFERENCES_NAME = "prospek_session";
    private static final String TOKEN_KEY = "api_token";
    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getApplicationContext()
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public String getToken() {
        return preferences.getString(TOKEN_KEY, null);
    }

    public void saveToken(String token) {
        preferences.edit().putString(TOKEN_KEY, token).apply();
    }

    public void clearToken() {
        preferences.edit().remove(TOKEN_KEY).apply();
    }
}
