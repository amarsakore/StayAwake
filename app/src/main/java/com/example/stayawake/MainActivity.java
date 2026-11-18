package com.example.stayawake;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.TextView;

/** Only here so the app has an icon: asks for permission, then explains how to add the tile. */
public class MainActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        TextView t = new TextView(this);
        t.setGravity(Gravity.CENTER);
        t.setPadding(64, 64, 64, 64);
        t.setTextSize(18);
        if (StayAwake.canWrite(this)) {
            t.setText("All set.\n\nPull down Quick Settings, tap the pencil (edit) icon, "
                    + "and drag the \"Stay awake\" tile into your tiles.");
        } else {
            t.setText("Tap here to allow Stay Awake to change the screen timeout.");
            t.setOnClickListener(v -> startActivity(new Intent(
                    Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:" + getPackageName()))));
        }
        setContentView(t);
    }
}
