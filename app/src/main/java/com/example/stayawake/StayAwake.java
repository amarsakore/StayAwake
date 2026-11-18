package com.example.stayawake;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

/** Turns "stay awake" on/off by changing the system screen-off timeout. */
final class StayAwake {
    private static final String PREFS = "stay_awake";
    private static final String KEY_SAVED = "saved_timeout";
    private static final int FOREVER = Integer.MAX_VALUE;
    private static final int FALLBACK = 30_000; // 30 s, used if nothing was saved

    static boolean canWrite(Context c) {
        return Settings.System.canWrite(c);
    }

    static boolean isOn(Context c) {
        return currentTimeout(c) == FOREVER;
    }

    /** Flips the state. Returns the new state. Caller must check canWrite() first. */
    static boolean toggle(Context c) {
        SharedPreferences p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (isOn(c)) {
            int restore = p.getInt(KEY_SAVED, FALLBACK);
            set(c, restore);
            return false;
        } else {
            p.edit().putInt(KEY_SAVED, currentTimeout(c)).apply();
            set(c, FOREVER);
            return true;
        }
    }

    private static int currentTimeout(Context c) {
        return Settings.System.getInt(c.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, FALLBACK);
    }

    private static void set(Context c, int ms) {
        Settings.System.putInt(c.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, ms);
    }
}
