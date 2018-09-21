package com.tyari.campus.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;

public class BaseFragment extends Fragment {
    private static final String TAG = LogUtils.makeLogTag(BaseFragment.class);
    protected Activity mActivity;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.getInstance().setLocale(mActivity);
    }

    protected void launchActivity(Class activity) {
        Intent intent = new Intent(mActivity, activity);
        mActivity.startActivity(intent);
    }
}
