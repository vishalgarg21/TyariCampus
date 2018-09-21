package com.tyari.campus.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.adapter.HomePagerAdapter;
import com.tyari.campus.callback.CustomDialogCallBack;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.UserRequest;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.User;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.PreferenceUtils;
import com.tyari.campus.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = LogUtils.makeLogTag(HomeActivity.class);

    private boolean mIsDoubleBackPressed = false;
    private static Handler sHandler = null;
    private static Runnable sRunnable = null;
    private Toast mDoubleBackToast = null;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewPager homePager = findViewById(R.id.viewPagerHome);
        TabLayout tabLayout = findViewById(R.id.tabLytHome);

        HomePagerAdapter mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        homePager.setAdapter(mHomePagerAdapter);

        homePager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(homePager));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView mSubjectTitleTxtVw = findViewById(R.id.txtVwSubjectTitle);
        mSubjectTitleTxtVw.setOnClickListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView mNameTxtVw = headerView.findViewById(R.id.txtVwName);
        TextView mEmailTxtVw = headerView.findViewById(R.id.txtVwEmail);

        mUser = (User) PreferenceUtils.getInstance(this).getObject(PreferenceUtils.KEY_USER);

        if (null != mUser) {
            if (!TextUtils.isEmpty(mUser.getName())) {
                mNameTxtVw.setText(mUser.getName());
            }

            if (!TextUtils.isEmpty(mUser.getEmail())) {
                mEmailTxtVw.setText(mUser.getEmail());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            launchSettingsActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(Constants.KEY_LAUNCH_FROM, Constants.LAUNCH_FROM_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_update:
                if (null != mUser) {
                    launchActivity(UpdateProfileActivity.class);
                } else {
                    launchActivity(LoginActivity.class);
                }
                break;

            case R.id.nav_change_password:
                if (null != mUser) {
                    launchActivity(ChangePasswordActivity.class);
                } else {
                    launchActivity(LoginActivity.class);
                }
                break;

            case R.id.nav_logout:
                showLogoutConfirmationDialog();

                break;

            case R.id.nav_results:
                launchActivity(MyResultActivity.class);
                break;

            case R.id.nav_tests:
                launchActivity(MyTestActivity.class);
                break;

            case R.id.nav_settings:
                launchSettingsActivity();
                break;

            case R.id.nav_refer:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

            case R.id.nav_contact_us:
                launchWeViewActivity(getString(R.string.contact_us_url));
                break;

            case R.id.nav_help:
                launchWeViewActivity(getString(R.string.help_url));
                break;

            case R.id.nav_about:
                launchWeViewActivity(getString(R.string.about_url));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SubjectActivity.class);
        intent.putExtra(Constants.KEY_LAUNCH_FROM, Constants.LAUNCH_FROM_HOME);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (mDoubleBackToast != null) {
            mDoubleBackToast.cancel();
            mDoubleBackToast = null;
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mIsDoubleBackPressed) {
                sendBroadcast(new Intent(Constants.ACTION_EXIT_APP));
            }

            mIsDoubleBackPressed = true;
            if (mDoubleBackToast == null) {
                mDoubleBackToast = Toast.makeText(this, getString(R.string.press_back_again_to_exit_message), Toast.LENGTH_SHORT);
                mDoubleBackToast.show();
            }
            sHandler = new Handler();
            sRunnable = new Runnable() {
                @Override
                public void run() {
                    mIsDoubleBackPressed = false;
                    mDoubleBackToast = null;
                }
            };
            sHandler.postDelayed(sRunnable, Constants.DOUBLE_TAP_TIME_GAP_TO_EXIT);
        }
    }

    private void showLogoutConfirmationDialog() {
        AppUtils.getInstance().showCustomDialogWithCallBack(this, getString(R.string.logout), getString(R.string.logout_confirmation), getString(R.string.yes), getString(R.string.no), new CustomDialogCallBack() {
            @Override
            public void positiveCallBack(Dialog dialog) {
                dialog.dismiss();
                logout();
            }

            @Override
            public void negativeCallBack(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    private void logout() {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(this, getString(R.string.loading));

        Callback<GenericResponse> responseCallback = new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(HomeActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        Toast.makeText(HomeActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        PreferenceUtils.getInstance(HomeActivity.this).removePreference(PreferenceUtils.KEY_USER);
                        launchLoginActivity();
                        finish();
                    } else {
                        Toast.makeText(HomeActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(HomeActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        if (null != mUser && null != mUser.getId()) {
            UserRequest request = new UserRequest();
            request.setUserId(mUser.getId());
            RetrofitUtils.getInstance().getService(this).logout(request).enqueue(responseCallback);
        } else {
            launchActivity(LoginActivity.class);
        }
    }
}

