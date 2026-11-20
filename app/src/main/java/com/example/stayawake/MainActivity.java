package com.example.stayawake;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.TextView;

/** Setup screen: asks for "Display over other apps", then explains how to add the tile. */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 33) {
            // Optional: lets the "Screen is staying awake" notification show.
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView t = new TextView(this);
        t.setGravity(Gravity.CENTER);
        t.setPadding(64, 64, 64, 64);
        t.setTextSize(18);
        if (AwakeService.canOverlay(this)) {
            t.setText("All set.\n\nPull down Quick Settings, tap the edit (pencil) button, "
                    + "and drag the \"Stay awake\" tile into your tiles.\n\n"
                    + "Tap it to keep the screen on. Tap again to stop.");
        } else {
            t.setText("One step: tap here and turn on \"Display over other apps\" "
                    + "for Stay Awake.\n\nIt shows an invisible window that keeps "
                    + "the screen on. Nothing appears on screen.");
            t.setOnClickListener(v -> startActivity(new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()))));
        }
        setContentView(t);
    }
}
