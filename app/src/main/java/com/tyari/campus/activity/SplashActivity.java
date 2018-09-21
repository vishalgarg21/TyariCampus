package com.tyari.campus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.tyari.campus.R;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.User;
import com.tyari.campus.utils.PreferenceUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                String language = PreferenceUtils.getInstance(SplashActivity.this).getString(PreferenceUtils.KEY_LANG);
                List<String> subjects = (List) PreferenceUtils.getInstance(SplashActivity.this).getObject(PreferenceUtils.KEY_SELECTED_SUBJECTS);
                User user = (User) PreferenceUtils.getInstance(SplashActivity.this).getObject(PreferenceUtils.KEY_USER);

                Intent intent;
                if (TextUtils.isEmpty(language)) {
                    intent = new Intent(SplashActivity.this, SettingsActivity.class);
                    intent.putExtra(Constants.KEY_LAUNCH_FROM, Constants.LAUNCH_FROM_SPLASH);
                } else if (null == subjects || subjects.size() <= 0) {
                    intent = new Intent(SplashActivity.this, SubjectActivity.class);
                    intent.putExtra(Constants.KEY_LAUNCH_FROM, Constants.LAUNCH_FROM_SPLASH);
                } else if (null != user) {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 4000);
    }

    @Override
    protected void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onDestroy();
    }
}
