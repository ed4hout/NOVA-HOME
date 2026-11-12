package nl.nova.home;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * NOVA Home voor Google TV.
 * Toont de HUD-pagina (assets/index.html) en geeft die via "Nova" toegang tot
 * de geïnstalleerde apps, hun logo's en de HDMI-ingangen van de tv.
 */
public class MainActivity extends Activity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);
        web.setBackgroundColor(Color.BLACK);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setTextZoom(100);
        // Geen automatisch in- of uitzoomen: NOVA schaalt zichzelf naar het scherm.
        s.setUseWideViewPort(false);
        s.setLoadWithOverviewMode(false);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);

        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new Bridge(), "Nova");
        web.setFocusable(true);
        web.setFocusableInTouchMode(true);

        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
        web.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemBars();
        if (web != null) {
            web.requestFocus();
            // Apps opnieuw ophalen: misschien is er iets geïnstalleerd of verwijderd.
            web.evaluateJavascript("window.novaResume && novaResume()", null);
        }
    }

    @SuppressWarnings("deprecation")
    private void hideSystemBars() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        // Terug-knop doet niets: NOVA blijft in beeld. Gebruik Home om te verlaten.
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            web.destroy();
            web = null;
        }
        super.onDestroy();
    }

    /* ------------------------------------------------------------------ */

    private boolean start(Intent i) {
        try {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String toDataUri(Drawable d, int maxW, int maxH) {
        if (d == null) return null;
        int w = d.getIntrinsicWidth();
        int h = d.getIntrinsicHeight();
        if (w <= 0 || h <= 0) { w = maxW; h = maxH; }
        float scale = Math.min(1f, Math.min((float) maxW / w, (float) maxH / h));
        int bw = Math.max(1, Math.round(w * scale));
        int bh = Math.max(1, Math.round(h * scale));

        Bitmap bmp = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        d.setBounds(0, 0, bw, bh);
        d.draw(c);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        bmp.recycle();
        return "data:image/png;base64," + Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
    }

    /** Alles wat de pagina via window.Nova kan aanroepen. */
    private class Bridge {

        /** Lijst van tv-apps met naam, pakketnaam en logo (banner als die er is). */
        @JavascriptInterface
        public String getApps() {
            JSONArray arr = new JSONArray();
            try {
                PackageManager pm = getPackageManager();
                Intent q = new Intent(Intent.ACTION_MAIN);
                q.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
                List<ResolveInfo> list = pm.queryIntentActivities(q, 0);
                for (ResolveInfo r : list) {
                    try {
                        ActivityInfo ai = r.activityInfo;
                        if (getPackageName().equals(ai.packageName)) continue;
                        Drawable banner = ai.loadBanner(pm);
                        if (banner == null) banner = ai.applicationInfo.loadBanner(pm);
                        Drawable img = banner != null ? banner : r.loadIcon(pm);

                        JSONObject o = new JSONObject();
                        o.put("pkg", ai.packageName);
                        o.put("label", String.valueOf(r.loadLabel(pm)));
                        o.put("banner", banner != null);
                        o.put("icon", toDataUri(img, 320, 180));
                        arr.put(o);
                    } catch (Exception ignored) { }
                }
            } catch (Exception ignored) { }
            return arr.toString();
        }

        /** Versienummer van de app. */
        @JavascriptInterface
        public String getVersion() {
            return BuildConfig.VERSION_NAME;
        }

        /** Zichtbare afmeting van de weergave in CSS-pixels. */
        @JavascriptInterface
        public String getViewport() {
            float d = getResources().getDisplayMetrics().density;
            WebView w = web;
            if (w == null || d <= 0) return "{}";
            return "{\"w\":" + (w.getWidth() / d) + ",\"h\":" + (w.getHeight() / d) + "}";
        }

        /** HDMI- en andere externe ingangen van de tv. */
        @JavascriptInterface
        public String getInputs() {
            JSONArray arr = new JSONArray();
            try {
                TvInputManager tim = (TvInputManager) getSystemService(TV_INPUT_SERVICE);
                if (tim == null) return arr.toString();
                for (TvInputInfo info : tim.getTvInputList()) {
                    if (!info.isPassthroughInput()) continue;
                    if (info.isHidden(MainActivity.this)) continue;
                    JSONObject o = new JSONObject();
                    o.put("id", info.getId());
                    CharSequence label = info.loadLabel(MainActivity.this);
                    o.put("label", label != null ? label.toString() : "Ingang");
                    o.put("type", info.getType());
                    arr.put(o);
                }
            } catch (Exception ignored) { }
            return arr.toString();
        }

        /** Start een app op pakketnaam. */
        @JavascriptInterface
        public boolean launch(String pkg) {
            if (pkg == null || pkg.isEmpty()) return false;
            PackageManager pm = getPackageManager();
            Intent i = pm.getLeanbackLaunchIntentForPackage(pkg);
            if (i == null) i = pm.getLaunchIntentForPackage(pkg);
            return i != null && start(i);
        }

        /** Schakel naar een HDMI-ingang. */
        @JavascriptInterface
        public boolean openInput(String inputId) {
            if (inputId == null || inputId.isEmpty()) return false;
            return start(new Intent(Intent.ACTION_VIEW,
                    TvContract.buildChannelUriForPassthroughInput(inputId)));
        }

        /** Vaste acties: "livetv" en "settings". */
        @JavascriptInterface
        public boolean action(String what) {
            if ("settings".equals(what)) {
                return start(new Intent(Settings.ACTION_SETTINGS));
            }
            if ("livetv".equals(what)) {
                // Sony's eigen tv-app; anders het algemene kanalenoverzicht.
                if (launch("com.sony.dtv.tvx")) return true;
                return start(new Intent(Intent.ACTION_VIEW, TvContract.Channels.CONTENT_URI));
            }
            return false;
        }
    }
}
