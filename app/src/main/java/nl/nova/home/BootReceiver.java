package nl.nova.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Start de startscherm-modus na het opstarten van de tv. */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        if (i == null) return;
        String a = i.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(a) || "android.intent.action.QUICKBOOT_POWERON".equals(a)) {
            NovaService.start(c, true);
        }
    }
}
