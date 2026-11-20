# Stay Awake

A Quick Settings tile for Android that keeps your screen from turning off.

- **Tap on:** the screen stays on (a small "Screen is staying awake" notification shows).
- **Tap off:** the screen goes back to its normal timeout.

Works even when the app isn't running: the tile starts it. It doesn't change any
system settings; it holds a screen wake lock in a foreground service while on.
(Changing the screen-timeout setting doesn't work on ColorOS and some other ROMs,
which reset it as soon as the app leaves the screen.)

## Install

1. Download `StayAwake.apk` from the [latest release](../../releases/latest) on your phone and open it (allow installs from your browser if asked).
2. Open the **Stay Awake** app once and allow notifications (optional, for the status notification).
3. Pull down Quick Settings, tap the edit (pencil) button, and drag **Stay awake** into your tiles.

Requires Android 8.0+.

## Build

```bash
./gradlew assembleDebug
```
