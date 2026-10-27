package com.example.mnp;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;

public final class BrowserActivity extends Activity {
    static final String EXTRA_TITLE = "link_title";
    static final String EXTRA_URL = "link_url";

    private WebView webView;
    private String url;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String suppliedUrl = getIntent().getStringExtra(EXTRA_URL);
        try {
            url = UrlPolicy.validate(suppliedUrl);
        } catch (IllegalArgumentException exception) {
            Toast.makeText(this, "Blocked invalid link: " + exception.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        LinearLayout root = Ui.column(this);
        root.setBackgroundColor(Ui.WHITE);
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(android.view.Gravity.CENTER_VERTICAL);
        toolbar.setPadding(Ui.dp(this, 8), Ui.dp(this, 4), Ui.dp(this, 8), Ui.dp(this, 4));
        root.addView(toolbar, Ui.params(this, -1, 60));

        android.widget.Button back = Ui.button(this, "Back", false);
        back.setOnClickListener(view -> finish());
        toolbar.addView(back, Ui.params(this, 76, 48));

        LinearLayout labelColumn = Ui.column(this);
        labelColumn.setPadding(Ui.dp(this, 8), 0, Ui.dp(this, 4), 0);
        toolbar.addView(labelColumn, new LinearLayout.LayoutParams(0, -2, 1f));
        TextView pageTitle = Ui.text(this, title == null ? "Website" : title, 16, Ui.INK);
        pageTitle.setSingleLine(true);
        pageTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);
        labelColumn.addView(pageTitle);
        TextView host = Ui.text(this, Uri.parse(url).getHost(), 12, Ui.MUTED);
        host.setSingleLine(true);
        labelColumn.addView(host);

        android.widget.Button reload = Ui.button(this, "Reload", false);
        reload.setOnClickListener(view -> webView.reload());
        toolbar.addView(reload, Ui.params(this, 82, 48));

        status = Ui.text(this, "Loading secure page…", 14, Ui.MUTED);
        status.setPadding(Ui.dp(this, 16), Ui.dp(this, 6), Ui.dp(this, 16), Ui.dp(this, 6));
        root.addView(status);

        webView = new WebView(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_YES);
        }
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(true);
        }
        webView.setWebViewClient(new SecureWebViewClient());
        root.addView(webView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);
        webView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    private final class SecureWebViewClient extends WebViewClient {
        @Override
        public android.webkit.WebResourceResponse shouldInterceptRequest(
                WebView view, WebResourceRequest request) {
            if (AdBlocker.shouldBlock(request.getUrl().toString())) {
                return blockedAdResponse();
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public android.webkit.WebResourceResponse shouldInterceptRequest(WebView view, String requestUrl) {
            if (AdBlocker.shouldBlock(requestUrl)) {
                return blockedAdResponse();
            }
            return super.shouldInterceptRequest(view, requestUrl);
        }

        private android.webkit.WebResourceResponse blockedAdResponse() {
            return new android.webkit.WebResourceResponse(
                    "text/plain", "utf-8", new ByteArrayInputStream(new byte[0]));
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return !isHttps(request.getUrl());
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String nextUrl) {
            return !isHttps(Uri.parse(nextUrl));
        }

        private boolean isHttps(Uri uri) {
            return uri != null
                    && "https".equalsIgnoreCase(uri.getScheme())
                    && uri.getHost() != null
                    && uri.getUserInfo() == null;
        }

        @Override
        public void onPageStarted(WebView view, String pageUrl, Bitmap favicon) {
            status.setText("Loading secure page…");
            status.setVisibility(TextView.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String pageUrl) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                status.setText("Use Android Autofill or your password manager to save and fill this site's login. MNP Links does not store passwords.");
                status.setVisibility(TextView.VISIBLE);
            } else {
                status.setVisibility(TextView.GONE);
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (request.isForMainFrame()) {
                showLoadError();
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) {
            handler.cancel();
            status.setText("Secure connection blocked: the site's certificate could not be verified.");
            status.setVisibility(TextView.VISIBLE);
        }

        @Override
        public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType,
                                      SafeBrowsingResponse callback) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                callback.backToSafety(true);
            }
            status.setText("This site was blocked by Android Safe Browsing.");
            status.setVisibility(TextView.VISIBLE);
        }
    }

    private void showLoadError() {
        status.setText("The page could not be loaded.");
        status.setVisibility(TextView.VISIBLE);
        new AlertDialog.Builder(this)
                .setTitle("Could not load website")
                .setMessage("Check your connection, then try reloading.")
                .setNegativeButton("Close", null)
                .setPositiveButton("Retry", (dialog, which) -> webView.reload())
                .show();
    }
}
