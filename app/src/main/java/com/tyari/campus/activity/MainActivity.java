package com.tyari.campus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.tyari.campus.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView mEquationWebView = findViewById(R.id.webVwEqu);
        //mEquationWebView.loadUrl("file:///android_asset/mathscribe/COPY-ME.html");


       WebSettings mWebSettings = mEquationWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        String path="file:///android_asset/";
        String js = "<html><head>"
                + "<link rel='stylesheet' href='file:///android_asset/mathscribe/jqmath-0.4.3.css'>"
                + "<script src = 'file:///android_asset/mathscribe/jquery-1.4.3.min.js'></script>"
                + "<script src = 'file:///android_asset/mathscribe/jqmath-etc-0.4.6.min.js'></script>"
                + "</head><body>"
                + "$$x={-b±√{b^2-4ac}}/{2a}$$</body></html>";
        mEquationWebView.loadDataWithBaseURL("",js,"text/html", "UTF-8", "");
    }
}
