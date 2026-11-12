package nl.nova.home;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Startscherm-modus: brengt NOVA naar voren als de tv aangaat
 * of als het startscherm van Google TV in beeld komt (Home-knop).
 */
public class NovaWatcher extends AccessibilityService {

    /** True zolang de gebruiker bewust naar Google TV is gegaan via de tegel. */
    static volatile boolean bypass = false;

    private static final Set<String> HOMES = new HashSet<>(Arrays.asList(
            "com.google.android.apps.tv.launcherx",   // Google TV
            "com.google.android.tvlauncher",          // Android TV
            "com.google.android.leanbacklauncher"));  // oudere Android TV

    private final Handler handler = new Handler(Looper.getMainLooper());
    private long lastOpen = 0;
    private boolean registered = false;

    private final BroadcastReceiver screenOn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            bypass = false;
            handler.postDelayed(new Runnable() {
                @Override public void run() { openNova(); }
            }, 1500);
        }
    };

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        if (!registered) {
            registerReceiver(screenOn, new IntentFilter(Intent.ACTION_SCREEN_ON));
            registered = true;
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent e) {
        if (e == null || e.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return;
        CharSequence p = e.getPackageName();
        if (p == null) return;
        String pkg = p.toString();
        if (pkg.equals(getPackageName())) return;

        if (HOMES.contains(pkg)) {
            if (!bypass) openNova();
        } else if (getPackageManager().getLeanbackLaunchIntentForPackage(pkg) != null) {
            // Een gewone app is gestart: daarna geldt Home weer als NOVA.
            bypass = false;
        }
    }

    private void openNova() {
        long now = SystemClock.elapsedRealtime();
        if (now - lastOpen < 1500) return;
        lastOpen = now;
        try {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        } catch (Exception ignored) { }
    }

    @Override
    public void onInterrupt() { }

    @Override
    public void onDestroy() {
        if (registered) {
            try { unregisterReceiver(screenOn); } catch (Exception ignored) { }
            registered = false;
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
