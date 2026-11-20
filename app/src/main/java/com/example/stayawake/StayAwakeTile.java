package com.example.stayawake;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

public class StayAwakeTile extends TileService {

    @Override
    public void onStartListening() {
        draw(AwakeService.isRunning());
    }

    @Override
    public void onClick() {
        boolean turnOn = !AwakeService.isRunning();
        if (turnOn && !AwakeService.canOverlay(this)) {
            // One-time: "Display over other apps" is what actually keeps the screen on.
            openOverlaySettings();
            return;
        }
        try {
            if (turnOn) AwakeService.start(this); else AwakeService.stop(this);
            draw(turnOn);
        } catch (Exception e) {
            Log.w("StayAwake", "could not toggle", e);
            draw(AwakeService.isRunning());
        }
    }

    private void draw(boolean on) {
        Tile tile = getQsTile();
        if (tile == null) return;
        tile.setState(on ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.setSubtitle(on ? "On" : (AwakeService.canOverlay(this) ? "Off" : "Tap to set up"));
        }
        tile.updateTile();
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    @SuppressWarnings("deprecation")
    private void openOverlaySettings() {
        Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 34) {
            startActivityAndCollapse(PendingIntent.getActivity(this, 0, i,
                    PendingIntent.FLAG_IMMUTABLE));
        } else {
            startActivityAndCollapse(i);
        }
    }
}
