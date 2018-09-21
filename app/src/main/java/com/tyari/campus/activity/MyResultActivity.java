package com.tyari.campus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.adapter.ResultAdapter;
import com.tyari.campus.common.Constants;
import com.tyari.campus.common.RecyclerItemClickListener;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.Result;
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

public class MyResultActivity extends BaseActivity {
    private static final String TAG = LogUtils.makeLogTag(MyResultActivity.class);

    private List<Result> mResults;
    private ResultAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_results);

        mResults = new ArrayList<>();
        mAdapter = new ResultAdapter(this, mResults);
        RecyclerView mResultRecyclerVw = findViewById(R.id.recyclerVwResults);
        mResultRecyclerVw.setLayoutManager(new LinearLayoutManager(this));
        mResultRecyclerVw.setHasFixedSize(true);
        mResultRecyclerVw.setAdapter(mAdapter);
        mResultRecyclerVw.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MyResultActivity.this, ResultActivity.class);
                intent.putExtra(Constants.KEY_RESULT, mResults.get(position));
                startActivity(intent);
                finish();
            }
        }));

        fetchResults();
    }

    private void fetchResults() {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(this, getString(R.string.loading));

        Callback<GenericResponse<List<Result>>> responseCallback = new Callback<GenericResponse<List<Result>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Result>>> call, Response<GenericResponse<List<Result>>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<List<Result>> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(MyResultActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        List<Result> results = genericResponse.getData();
                        if (null != results && results.size() > 0) {
                            mResults.addAll(results);
                            if (null != mAdapter) {
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(MyResultActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MyResultActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MyResultActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Result>>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(MyResultActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        User user = (User) PreferenceUtils.getInstance(this).getObject(PreferenceUtils.KEY_USER);

        if (null != user && null != user.getId()) {
            UserRequest request = new UserRequest();
            request.setUserId(user.getId());
            RetrofitUtils.getInstance().getService(this).fetchResults(request).enqueue(responseCallback);
        }
    }
}
