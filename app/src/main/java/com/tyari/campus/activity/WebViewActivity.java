package com.tyari.campus.activity;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tyari.campus.R;
import com.tyari.campus.common.Constants;

public class WebViewActivity extends BaseActivity {

    private String mWebUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if (null != getIntent()) {
            mWebUrl = getIntent().getStringExtra(Constants.KEY_WEB_URL);
        }

        WebView mWebView = findViewById(R.id.webVw);

        if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebView.setWebChromeClient(new TyariCampusWebChromeClient());
        mWebView.setWebViewClient(new TyariCampusWebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setGeolocationDatabasePath(getFilesDir().getPath());

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCachePath(getCacheDir().getAbsolutePath());
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);

        mWebView.loadUrl(mWebUrl);
    }

    private class TyariCampusWebChromeClient extends WebChromeClient {

    }

    public class TyariCampusWebViewClient extends WebViewClient {

    }
}
