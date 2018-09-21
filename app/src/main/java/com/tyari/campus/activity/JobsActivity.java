package com.tyari.campus.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.adapter.JobAdapter;
import com.tyari.campus.common.Constants;
import com.tyari.campus.common.RecyclerItemClickListener;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.Job;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobsActivity extends BaseActivity{
    private static final String TAG = LogUtils.makeLogTag(JobsActivity.class);

    private List<Job> mJobs;
    private JobAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        mJobs = new ArrayList<>();
        mAdapter = new JobAdapter(this, mJobs);
        RecyclerView mJobRecyclerVw = findViewById(R.id.recyclerVwJobs);
        mJobRecyclerVw.setLayoutManager(new LinearLayoutManager(this));
        mJobRecyclerVw.setHasFixedSize(true);
        mJobRecyclerVw.setAdapter(mAdapter);
        mJobRecyclerVw.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                launchWeViewActivity(mJobs.get(position).getUrl());
            }
        }));

        getJobs();
    }

    private void getJobs() {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(this, getString(R.string.loading));

        Callback<GenericResponse<List<Job>>> responseCallback = new Callback<GenericResponse<List<Job>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Job>>> call, Response<GenericResponse<List<Job>>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<List<Job>> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(JobsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        List<Job> jobs = genericResponse.getData();
                        if (null != jobs && jobs.size() > 0) {
                            mJobs.addAll(jobs);
                            if (null != mAdapter) {
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(JobsActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(JobsActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(JobsActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Job>>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(JobsActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitUtils.getInstance().getService(this).getJobs().enqueue(responseCallback);
    }
}
