package com.psylabs.psychotests.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author a.hatrus.
 */

public class AdManager {

    private String USER_POINTS = "USER_POINTS";
    private int START_POINTS = 5;

    private Context context;
    private SharedPreferences sharedPrefs;

    public AdManager(Context context, SharedPreferences prefs) {
        this.context = context;
        this.sharedPrefs = prefs;
    }

    public void rewardUser(int points) {
        int currentPoints = sharedPrefs.getInt(USER_POINTS, START_POINTS);
        sharedPrefs.edit().putInt(USER_POINTS, currentPoints + points).apply();

    }

    public void punishUser(int points) {
        int currentPoints = sharedPrefs.getInt(USER_POINTS, START_POINTS);
        sharedPrefs.edit().putInt(USER_POINTS, currentPoints - points).apply();
    }

    public boolean isUserAllowedToSkipAd() {
        int currentPoints = sharedPrefs.getInt(USER_POINTS, START_POINTS);
        Log.d("AdManager", "CURRENT POINTS" + currentPoints);
        if (currentPoints >= 0) {
            return true;
        }
        if (currentPoints < 5) {
            sharedPrefs.edit().putInt(USER_POINTS, 0).apply();
        }
        return false;
    }
}
