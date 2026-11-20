# Stay Awake

A Quick Settings tile for Android that keeps your screen from turning off.

- **Tap on:** the screen stays on (a small "Screen is staying awake" notification shows).
- **Tap off:** the screen goes back to its normal timeout.

Works even when the app isn't running: the tile starts it. Nothing runs while the tile is off.

## How it works

While on, a foreground service shows an invisible 1×1 overlay window with
`FLAG_KEEP_SCREEN_ON`, so the window manager keeps the screen on (the same thing
video players do). A screen wake lock is held as a backup.

Why not something simpler? On ColorOS (OPPO/OnePlus/realme) and some other ROMs:
- changing the screen-timeout setting is reverted as soon as the app leaves the screen, and
- wake locks from background apps are not reliably honoured.

The overlay window is honoured, and it also stops ColorOS from freezing the app.

## Install

1. Download `StayAwake.apk` from the [latest release](../../releases/latest) on your phone and open it (allow installs from your browser if asked).
2. Open the **Stay Awake** app, tap the text, and turn on **Display over other apps**. Nothing visible is drawn.
3. Allow notifications if asked (optional, for the status notification).
4. Pull down Quick Settings, tap the edit (pencil) button, and drag **Stay awake** into your tiles.

Requires Android 8.0+.

## Build

```bash
./gradlew assembleDebug
```
