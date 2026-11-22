package com.portalasesmen.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.BatteryManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String HOME_URL = "https://asesemen-ekamas.carrd.co";
    private WebView webView;
    private TextView batteryText;
    private BroadcastReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        root.addView(createToolbarRow(), new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(76)));

        webView = new WebView(this);
        webView.setBackgroundColor(Color.WHITE);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);
        settings.setLoadsImagesAutomatically(true);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        root.addView(webView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl(HOME_URL);
        }
        registerBatteryUpdates();
    }

    private View createToolbarRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), 0, dp(12), 0);
        row.setBackgroundColor(Color.WHITE);

        TextClock clock = new TextClock(this);
        clock.setFormat24Hour("HH:mm:ss");
        clock.setFormat12Hour("hh:mm:ss a");
        clock.setTextColor(Color.BLACK);
        clock.setTextSize(18);
        clock.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        row.addView(clock, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout pill = new LinearLayout(this);
        pill.setOrientation(LinearLayout.HORIZONTAL);
        pill.setGravity(Gravity.CENTER);
        pill.setBackground(roundRect(Color.BLACK, dp(48)));
        pill.setElevation(dp(5));
        pill.setPadding(dp(16), 0, dp(16), 0);

        // Decorative controls only: intentionally no click listeners.
        addDecorativeIcon(pill, "⟳");
        addDecorativeIcon(pill, "⤢");
        addDecorativeIcon(pill, "×");

        LinearLayout.LayoutParams pillParams = new LinearLayout.LayoutParams(0, dp(58), 1f);
        pillParams.setMargins(dp(10), 0, dp(8), 0);
        row.addView(pill, pillParams);

        batteryText = new TextView(this);
        batteryText.setText("--%");
        batteryText.setTextColor(Color.BLACK);
        batteryText.setTextSize(17);
        batteryText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        row.addView(batteryText, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View battery = createBatteryShape();
        LinearLayout.LayoutParams batteryParams = new LinearLayout.LayoutParams(dp(28), dp(16));
        batteryParams.setMargins(dp(7), 0, 0, 0);
        row.addView(battery, batteryParams);
        return row;
    }

    private void addDecorativeIcon(LinearLayout parent, String symbol) {
        TextView icon = new TextView(this);
        icon.setText(symbol);
        icon.setTextColor(Color.WHITE);
        icon.setTextSize(27);
        icon.setGravity(Gravity.CENTER);
        icon.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        icon.setClickable(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(54), 1f);
        parent.addView(icon, params);
    }

    private View createBatteryShape() {
        LinearLayout outer = new LinearLayout(this);
        outer.setPadding(dp(2), dp(2), dp(2), dp(2));
        outer.setBackground(roundRect(Color.WHITE, dp(3)));
        View fill = new View(this);
        fill.setBackground(roundRect(Color.BLACK, dp(2)));
        outer.addView(fill, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        return outer;
    }

    private GradientDrawable roundRect(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private void registerBatteryUpdates() {
        batteryReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level >= 0 && scale > 0 && batteryText != null) {
                    batteryText.setText(Math.round(level * 100f / scale) + "%");
                }
            }
        };
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (batteryReceiver != null) {
            try { unregisterReceiver(batteryReceiver); } catch (Exception ignored) { }
        }
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
