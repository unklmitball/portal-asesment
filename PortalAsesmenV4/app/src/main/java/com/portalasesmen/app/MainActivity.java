package com.portalasesmen.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String HOME_URL = "https://asesemen-ekamas.carrd.co";

    private WebView webView;
    private TextView batteryText;
    private BatteryIconView batteryIcon;
    private BroadcastReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureSystemBars();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        // One fixed-width toolbar layer: the pill is centered against the screen,
        // not placed in the remaining space between the clock and battery labels.
        root.addView(createToolbar(), new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(50)));

        webView = new WebView(this);
        webView.setBackgroundColor(Color.WHITE);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        configureWebView(webView);
        webView.setWebViewClient(new PortalWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        root.addView(webView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);

        if (savedInstanceState != null && webView.restoreState(savedInstanceState) != null) {
            // Restored page and its navigation history.
        } else {
            webView.loadUrl(HOME_URL);
        }

        registerBatteryUpdates();
    }

    private void configureSystemBars() {
        Window window = getWindow();
        window.setStatusBarColor(Color.BLACK);
        // Match the blue-violet navigation bar from the user's reference image.
        window.setNavigationBarColor(Color.rgb(132, 139, 255));
        // Dark status-bar icons on white; navigation-bar light mode is deliberately
        // NOT enabled so the Back/Home/Recents icons remain white on the blue bar.
        // No LIGHT_STATUS_BAR flag: Android draws status-bar icons in white.
        window.getDecorView().setSystemUiVisibility(0);
    }

    private FrameLayout createToolbar() {
        FrameLayout row = new FrameLayout(this);
        row.setBackgroundColor(Color.WHITE);

        int screenWidthPx = getResources().getDisplayMetrics().widthPixels;
        int pillWidthPx = Math.round(screenWidthPx * 0.593f);
        int pillHeightPx = Math.round(pillWidthPx / 5.22f);
        int sideWidthPx = Math.max(dp(48), (screenWidthPx - pillWidthPx) / 2);

        TextClock clock = new TextClock(this);
        // Use a 24-hour clock in both device settings to avoid the AM/PM
        // suffix being clipped between the clock and the centered toolbar pill.
        clock.setFormat24Hour("HH:mm:ss");
        clock.setFormat12Hour("HH:mm:ss");
        clock.setTextColor(Color.BLACK);
        clock.setTextSize(12);
        clock.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        clock.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        clock.setSingleLine(true);
        clock.setIncludeFontPadding(false);
        FrameLayout.LayoutParams clockParams = new FrameLayout.LayoutParams(
                Math.max(dp(40), sideWidthPx - dp(12)),
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.START | Gravity.CENTER_VERTICAL);
        clockParams.leftMargin = dp(8);
        clockParams.rightMargin = dp(4);
        row.addView(clock, clockParams);

        LinearLayout batteryGroup = new LinearLayout(this);
        batteryGroup.setOrientation(LinearLayout.HORIZONTAL);
        batteryGroup.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        batteryText = new TextView(this);
        batteryText.setText("--%");
        batteryText.setTextColor(Color.BLACK);
        batteryText.setTextSize(13);
        batteryText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        batteryText.setGravity(Gravity.CENTER_VERTICAL);
        batteryText.setIncludeFontPadding(false);
        batteryGroup.addView(batteryText, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        batteryIcon = new BatteryIconView(this);
        LinearLayout.LayoutParams batteryIconParams = new LinearLayout.LayoutParams(dp(26), dp(16));
        batteryIconParams.leftMargin = dp(5);
        batteryGroup.addView(batteryIcon, batteryIconParams);

        FrameLayout.LayoutParams batteryParams = new FrameLayout.LayoutParams(
                sideWidthPx - dp(8),
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.END | Gravity.CENTER_VERTICAL);
        batteryParams.rightMargin = dp(5);
        row.addView(batteryGroup, batteryParams);

        LinearLayout pill = new LinearLayout(this);
        pill.setOrientation(LinearLayout.HORIZONTAL);
        pill.setGravity(Gravity.CENTER);
        pill.setPadding(dp(12), 0, dp(12), 0);
        pill.setBackground(roundRect(Color.BLACK, pillHeightPx / 2));
        pill.setElevation(dp(5));
        pill.setClipToOutline(true);

        ToolbarGlyphView refreshGlyph = new ToolbarGlyphView(this, ToolbarGlyphView.REFRESH);
        refreshGlyph.setContentDescription("Refresh halaman");
        refreshGlyph.setClickable(true);
        refreshGlyph.setFocusable(true);
        refreshGlyph.setOnClickListener(v -> {
            if (webView != null) {
                webView.reload();
            }
        });
        pill.addView(refreshGlyph,
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        ToolbarGlyphView expandGlyph = new ToolbarGlyphView(this, ToolbarGlyphView.EXPAND);
        expandGlyph.setContentDescription("Dekoratif");
        pill.addView(expandGlyph,
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        ToolbarGlyphView closeGlyph = new ToolbarGlyphView(this, ToolbarGlyphView.CLOSE);
        closeGlyph.setContentDescription("Dekoratif");
        pill.addView(closeGlyph,
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        FrameLayout.LayoutParams pillParams = new FrameLayout.LayoutParams(
                pillWidthPx, pillHeightPx, Gravity.CENTER);
        row.addView(pill, pillParams);
        return row;
    }

    private void configureWebView(WebView target) {
        WebSettings settings = target.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // With multiple-window support disabled, target="_blank" and window.open()
        // navigate in this same WebView instead of being discarded or opening a tab.
        settings.setSupportMultipleWindows(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(target, true);
    }

    private class PortalWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return handleUrlScheme(request.getUrl());
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return handleUrlScheme(Uri.parse(url));
        }

        private boolean handleUrlScheme(Uri uri) {
            String scheme = uri.getScheme();
            if (scheme == null || "http".equalsIgnoreCase(scheme)
                    || "https".equalsIgnoreCase(scheme)
                    || "about".equalsIgnoreCase(scheme)
                    || "javascript".equalsIgnoreCase(scheme)
                    || "blob".equalsIgnoreCase(scheme)
                    || "data".equalsIgnoreCase(scheme)) {
                // Let WebView load normal web URLs internally, including redirects.
                return false;
            }

            // Non-web links such as mailto: or tel: are handed to Android if an app exists.
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception ignored) {
                // Keep the user in the app when no external handler is available.
            }
            return true;
        }
    }

    private void registerBatteryUpdates() {
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level >= 0 && scale > 0) {
                    int percent = Math.round(level * 100f / scale);
                    batteryText.setText(percent + "%");
                    batteryIcon.setLevel(percent);
                }
            }
        };
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private GradientDrawable roundRect(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (batteryReceiver != null) {
            try {
                unregisterReceiver(batteryReceiver);
            } catch (IllegalArgumentException ignored) {
                // Receiver was already unregistered.
            }
        }
        if (webView != null) {
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
        }
        super.onDestroy();
    }

    private class BatteryIconView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int level = 50;

        BatteryIconView(Context context) {
            super(context);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        void setLevel(int value) {
            level = Math.max(0, Math.min(100, value));
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float d = getResources().getDisplayMetrics().density;
            float left = 1.2f * d;
            float top = 2.0f * d;
            float right = getWidth() - 4.0f * d;
            float bottom = getHeight() - 2.0f * d;

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.2f * d);
            paint.setColor(Color.rgb(35, 35, 35));
            RectF body = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(body, 2.4f * d, 2.4f * d, paint);

            paint.setStyle(Paint.Style.FILL);
            float terminalLeft = right + 0.8f * d;
            canvas.drawRoundRect(terminalLeft, getHeight() * 0.34f,
                    getWidth() - 0.4f * d, getHeight() * 0.66f,
                    1.0f * d, 1.0f * d, paint);

            float innerLeft = left + 2.0f * d;
            float innerTop = top + 2.0f * d;
            float innerRight = right - 2.0f * d;
            float innerBottom = bottom - 2.0f * d;
            paint.setColor(Color.rgb(35, 35, 35));
            float fillRight = innerLeft + Math.max(0, innerRight - innerLeft) * (level / 100f);
            if (fillRight > innerLeft) {
                canvas.drawRoundRect(innerLeft, innerTop, fillRight, innerBottom,
                        1.1f * d, 1.1f * d, paint);
            }
        }
    }

    private static class ToolbarGlyphView extends View {
        static final int REFRESH = 0;
        static final int EXPAND = 1;
        static final int CLOSE = 2;

        private final int glyph;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        ToolbarGlyphView(Context context, int glyph) {
            super(context);
            this.glyph = glyph;
            setClickable(false);
            setFocusable(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float d = getResources().getDisplayMetrics().density;
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.2f * d);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);

            if (glyph == REFRESH) {
                RectF arc = new RectF(cx - 4.4f * d, cy - 4.4f * d,
                        cx + 4.4f * d, cy + 4.4f * d);
                canvas.drawArc(arc, 48, 285, false, paint);
                Path arrow = new Path();
                arrow.moveTo(cx + 2.6f * d, cy - 3.6f * d);
                arrow.lineTo(cx + 5.4f * d, cy - 4.4f * d);
                arrow.lineTo(cx + 4.9f * d, cy - 1.4f * d);
                canvas.drawPath(arrow, paint);
            } else if (glyph == EXPAND) {
                Path arrows = new Path();
                // North-east arrow
                arrows.moveTo(cx - 0.8f * d, cy + 0.8f * d);
                arrows.lineTo(cx + 4.3f * d, cy - 4.3f * d);
                arrows.moveTo(cx + 1.2f * d, cy - 4.3f * d);
                arrows.lineTo(cx + 4.3f * d, cy - 4.3f * d);
                arrows.lineTo(cx + 4.3f * d, cy - 1.2f * d);
                // South-west arrow
                arrows.moveTo(cx + 0.8f * d, cy - 0.8f * d);
                arrows.lineTo(cx - 4.3f * d, cy + 4.3f * d);
                arrows.moveTo(cx - 1.2f * d, cy + 4.3f * d);
                arrows.lineTo(cx - 4.3f * d, cy + 4.3f * d);
                arrows.lineTo(cx - 4.3f * d, cy + 1.2f * d);
                canvas.drawPath(arrows, paint);
            } else {
                canvas.drawLine(cx - 3.2f * d, cy - 3.2f * d,
                        cx + 3.2f * d, cy + 3.2f * d, paint);
                canvas.drawLine(cx - 3.2f * d, cy + 3.2f * d,
                        cx + 3.2f * d, cy - 3.2f * d, paint);
            }
        }
    }
}
