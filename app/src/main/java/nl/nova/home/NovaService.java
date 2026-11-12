package nl.nova.home;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;

/**
 * Startscherm-modus: draait op de achtergrond en opent NOVA
 * zodra de tv aangaat. Werkt alleen als NOVA toestemming heeft
 * voor "Weergeven boven andere apps".
 */
public class NovaService extends Service {

    static final String EXTRA_BOOT = "boot";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean registered = false;
    private long lastOpen = 0;

    private final BroadcastReceiver screenOn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            openLater(1500);
        }
    };

    static void start(Context c, boolean boot) {
        try {
            Intent i = new Intent(c, NovaService.class);
            i.putExtra(EXTRA_BOOT, boot);
            if (Build.VERSION.SDK_INT >= 26) c.startForegroundService(i);
            else c.startService(i);
        } catch (Exception ignored) { }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        goForeground();
        if (!registered) {
            registerReceiver(screenOn, new IntentFilter(Intent.ACTION_SCREEN_ON));
            registered = true;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getBooleanExtra(EXTRA_BOOT, false)) openLater(4000);
        return START_STICKY;
    }

    @SuppressWarnings("deprecation")
    private void goForeground() {
        try {
            Notification n;
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationChannel ch = new NotificationChannel("nova", "NOVA Home",
                        NotificationManager.IMPORTANCE_MIN);
                if (nm != null) nm.createNotificationChannel(ch);
                n = new Notification.Builder(this, "nova")
                        .setContentTitle("NOVA Home")
                        .setContentText("Startscherm-modus actief")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();
            } else {
                n = new Notification.Builder(this)
                        .setContentTitle("NOVA Home")
                        .setContentText("Startscherm-modus actief")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();
            }
            startForeground(1, n);
        } catch (Exception ignored) { }
    }

    private void openLater(long delay) {
        handler.postDelayed(new Runnable() {
            @Override public void run() { openNova(); }
        }, delay);
    }

    private void openNova() {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) return;
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
    public IBinder onBind(Intent intent) { return null; }

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
