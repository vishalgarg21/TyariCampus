package com.tyari.campus.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.adapter.VideoAdapter;
import com.tyari.campus.common.Constants;
import com.tyari.campus.common.RecyclerItemClickListener;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.Job;
import com.tyari.campus.model.Video;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideosActivity extends BaseActivity{
    private static final String TAG = LogUtils.makeLogTag(VideosActivity.class);

    private List<Video> mVideos;
    private VideoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        mVideos = new ArrayList<>();
        mAdapter = new VideoAdapter(this, mVideos);
        RecyclerView mRecyclerVw = findViewById(R.id.recyclerVwVideos);
        mRecyclerVw.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerVw.setHasFixedSize(true);
        mRecyclerVw.setAdapter(mAdapter);
        mRecyclerVw.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                launchWeViewActivity(mVideos.get(position).getUrl());
            }
        }));

        getVideos();
    }

    private void getVideos() {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(this, getString(R.string.loading));

        Callback<GenericResponse<List<Video>>> responseCallback = new Callback<GenericResponse<List<Video>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Video>>> call, Response<GenericResponse<List<Video>>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<List<Video>> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(VideosActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        List<Video> videos = genericResponse.getData();
                        if (null != videos && videos.size() > 0) {
                            mVideos.addAll(videos);
                            if (null != mAdapter) {
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(VideosActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(VideosActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(VideosActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Video>>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(VideosActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitUtils.getInstance().getService(this).getVideos().enqueue(responseCallback);
    }
}
