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
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.service.quicksettings.TileService;

/**
 * While this service runs, the screen stays on. Start it to turn stay-awake on,
 * stop it to turn it off. Runs in the foreground so the system doesn't kill it.
 */
public class AwakeService extends Service {
    static final String ACTION_STOP = "com.example.stayawake.STOP";
    private static final String CHANNEL = "awake";
    private static volatile boolean running;

    private PowerManager.WakeLock lock;

    static boolean isRunning() { return running; }

    static void start(Context c) {
        c.startForegroundService(new Intent(c, AwakeService.class));
    }

    static void stop(Context c) {
        c.stopService(new Intent(c, AwakeService.class));
    }

    @Override
    @SuppressWarnings("deprecation") // SCREEN_BRIGHT_WAKE_LOCK: deprecated but the only way without a window
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }
        goForeground();
        if (lock == null) {
            PowerManager pm = getSystemService(PowerManager.class);
            lock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, "StayAwake:screen");
            lock.acquire();
        }
        running = true;
        refreshTile();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        running = false;
        if (lock != null && lock.isHeld()) lock.release();
        lock = null;
        refreshTile();
        super.onDestroy();
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
