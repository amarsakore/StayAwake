package com.example.stayawake;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * While this service runs, the screen stays on.
 *
 * Main mechanism: an invisible 1x1 overlay window with FLAG_KEEP_SCREEN_ON. The
 * window manager honours that flag on every ROM (it's what video players use).
 * A screen wake lock is also held as a backup, but ColorOS and some other ROMs
 * don't reliably honour wake locks from background apps.
 *
 * Runs as a foreground service so the system keeps the process alive.
 */
public class AwakeService extends Service {
    static final String ACTION_STOP = "com.example.stayawake.STOP";
    private static final String CHANNEL = "awake";
    private static volatile boolean running;

    private PowerManager.WakeLock lock;
    private View overlay;

    static boolean isRunning() { return running; }

    static boolean canOverlay(Context c) { return Settings.canDrawOverlays(c); }

    static void start(Context c) {
        c.startForegroundService(new Intent(c, AwakeService.class));
    }

    static void stop(Context c) {
        c.stopService(new Intent(c, AwakeService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }
        goForeground();
        addOverlay();
        acquireLock();
        running = true;
        refreshTile();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        running = false;
        removeOverlay();
        if (lock != null && lock.isHeld()) lock.release();
        lock = null;
        refreshTile();
        super.onDestroy();
    }

    private void addOverlay() {
        if (overlay != null || !canOverlay(this)) return;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                1, 1,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.setTitle("StayAwake");
        View v = new View(this);
        try {
            getSystemService(WindowManager.class).addView(v, lp);
            overlay = v;
        } catch (RuntimeException e) {
            Log.w("StayAwake", "could not add keep-screen-on window", e);
        }
    }

    private void removeOverlay() {
        if (overlay == null) return;
        try {
            getSystemService(WindowManager.class).removeView(overlay);
        } catch (RuntimeException ignored) { }
        overlay = null;
    }

    @SuppressWarnings("deprecation") // SCREEN_BRIGHT_WAKE_LOCK: backup only
    private void acquireLock() {
        if (lock != null) return;
        lock = getSystemService(PowerManager.class).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                "StayAwake:screen");
        lock.acquire();
    }

    private void refreshTile() {
        TileService.requestListeningState(this, new ComponentName(this, StayAwakeTile.class));
    }

    private void goForeground() {
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel(
                CHANNEL, "Stay awake", NotificationManager.IMPORTANCE_LOW));

        PendingIntent stop = PendingIntent.getService(this, 0,
                new Intent(this, AwakeService.class).setAction(ACTION_STOP),
                PendingIntent.FLAG_IMMUTABLE);

        Notification n = new Notification.Builder(this, CHANNEL)
                .setSmallIcon(R.drawable.ic_coffee)
                .setContentTitle("Screen is staying awake")
                .setContentText("Tap the tile or \"Turn off\" to stop")
                .setOngoing(true)
                .addAction(new Notification.Action.Builder(null, "Turn off", stop).build())
                .build();

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(1, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(1, n);
        }
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
