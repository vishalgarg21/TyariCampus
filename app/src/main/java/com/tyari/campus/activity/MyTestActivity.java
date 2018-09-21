package com.tyari.campus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.adapter.TestAdapter;
import com.tyari.campus.common.Constants;
import com.tyari.campus.common.RecyclerItemClickListener;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.Test;
import com.tyari.campus.model.User;
import com.tyari.campus.model.UserRequest;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.PreferenceUtils;
import com.tyari.campus.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTestActivity extends BaseActivity {
    private static final String TAG = LogUtils.makeLogTag(MyTestActivity.class);

    private List<Test> mTests;
    private TestAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tests);

        mTests = new ArrayList<>();
        mAdapter = new TestAdapter(this, mTests);
        RecyclerView mTestRecyclerVw = findViewById(R.id.recyclerVwTests);
        mTestRecyclerVw.setLayoutManager(new LinearLayoutManager(this));
        mTestRecyclerVw.setHasFixedSize(true);
        mTestRecyclerVw.setAdapter(mAdapter);
        mTestRecyclerVw.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent startTestIntent = new Intent(MyTestActivity.this, TestActivity.class);
                startTestIntent.putExtra(Constants.KEY_TEST, mTests.get(position));
                startActivity(startTestIntent);
            }
        }));

        fetchPurchasedTests();
    }

    private void fetchPurchasedTests() {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(this, getString(R.string.loading));

        Callback<GenericResponse<List<Test>>> responseCallback = new Callback<GenericResponse<List<Test>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Test>>> call, Response<GenericResponse<List<Test>>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<List<Test>> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(MyTestActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        List<Test> tests = genericResponse.getData();
                        if (null != tests && tests.size() > 0) {
                            mTests.addAll(tests);
                            if (null != mAdapter) {
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(MyTestActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MyTestActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MyTestActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Test>>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(MyTestActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        User user = (User) PreferenceUtils.getInstance(this).getObject(PreferenceUtils.KEY_USER);

        if (null != user && null != user.getId()) {
            UserRequest request = new UserRequest();
            request.setUserId(user.getId());
            RetrofitUtils.getInstance().getService(this).fetchPurchasedTests(request).enqueue(responseCallback);
        }
    }
}
