package com.example.stayawake;

import android.os.Build;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) tile.setSubtitle(on ? "On" : "Off");
        tile.updateTile();
    }
}
