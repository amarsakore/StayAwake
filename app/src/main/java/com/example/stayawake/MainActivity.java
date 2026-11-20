package com.example.stayawake;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

/** Only here so the app has an icon: explains how to add the tile. */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 33) {
            // Optional: lets the "Screen is staying awake" notification show.
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
        }
        TextView t = new TextView(this);
        t.setGravity(Gravity.CENTER);
        t.setPadding(64, 64, 64, 64);
        t.setTextSize(18);
        t.setText("Pull down Quick Settings, tap the edit (pencil) button, "
                + "and drag the \"Stay awake\" tile into your tiles.\n\n"
                + "Tap it to keep the screen on. Tap again to stop.");
        setContentView(t);
    }
}
