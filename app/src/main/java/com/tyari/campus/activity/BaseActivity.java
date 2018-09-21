package com.tyari.campus.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tyari.campus.common.Constants;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = LogUtils.makeLogTag(BaseActivity.class);
    protected Activity mActivity;
    private ExitAppBroadcastReceiver mExitAppBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mExitAppBroadcastReceiver = new ExitAppBroadcastReceiver();
        registerReceiver(mExitAppBroadcastReceiver, new IntentFilter(Constants.ACTION_EXIT_APP));
        AppUtils.getInstance().setLocale(this);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mExitAppBroadcastReceiver);
        super.onDestroy();
    }

    protected void setStatusBarColor(int colorResourceId) {
        try {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(AppUtils.getColor(this, colorResourceId));
        } catch (NoSuchMethodError ex) {
            LogUtils.error(TAG, ex.toString(), ex);
        }
    }

    protected void setTransparentStatusBar() {
        try {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.parseColor("#33000000"));
        } catch (NoSuchMethodError ex) {
            LogUtils.error(TAG, ex.toString(), ex);
        }
    }

    protected void launchHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void launchActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    protected void launchWeViewActivity(String url){
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(Constants.KEY_WEB_URL, url);
        startActivity(intent);
    }

    private class ExitAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
}
