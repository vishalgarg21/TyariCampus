package com.tyari.campus.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.adapter.SubjectAdapter;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.UserRequest;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.Subject;
import com.tyari.campus.model.SubjectRequest;
import com.tyari.campus.model.User;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.PreferenceUtils;
import com.tyari.campus.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubjectActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(SubjectActivity.class);

    private String mLaunchFrom;
    private List<Subject> mSubjects;
    private List<String> mSelectedSubjects;
    private SubjectAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        if (null != getIntent()) {
            mLaunchFrom = getIntent().getStringExtra(Constants.KEY_LAUNCH_FROM);
        }

        TextView mCancelTxtVw = findViewById(R.id.txtVwCancel);
        TextView mSaveTxtVw = findViewById(R.id.txtVwSave);

        mCancelTxtVw.setOnClickListener(this);
        mSaveTxtVw.setOnClickListener(this);

        mSubjects = new ArrayList<>();
        mSelectedSubjects = new ArrayList<>();
        mAdapter = new SubjectAdapter(this, mSubjects, mSelectedSubjects, false);
        RecyclerView mSubjectsRecyclerVw = findViewById(R.id.recyclerVwSubjects);
        mSubjectsRecyclerVw.setLayoutManager(new LinearLayoutManager(this));
        mSubjectsRecyclerVw.setHasFixedSize(true);
        mSubjectsRecyclerVw.setAdapter(mAdapter);

        fetchSubjects();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtVwCancel:
                finish();
                break;

            case R.id.txtVwSave:
                PreferenceUtils.getInstance(this).putObject(PreferenceUtils.KEY_SELECTED_SUBJECTS, mSelectedSubjects);
                User user = (User) PreferenceUtils.getInstance(this).getObject(PreferenceUtils.KEY_USER);

                if (null != user && null != user.getId()) {
                    saveSubjects(user.getId());
                } else {
                    if (null != mLaunchFrom && mLaunchFrom.equalsIgnoreCase(Constants.LAUNCH_FROM_HOME)) {
                        finish();
                    } else {
                        launchLoginActivity();
                        finish();
                    }
                }
                break;
        }
    }

    private void getCourses() {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(this, getString(R.string.loading));

        Callback<GenericResponse<List<Subject>>> responseCallback = new Callback<GenericResponse<List<Subject>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Subject>>> call, Response<GenericResponse<List<Subject>>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<List<Subject>> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(SubjectActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        List<Subject> subjects = genericResponse.getData();
                        if (null != subjects && subjects.size() > 0) {
                            mSubjects.addAll(subjects);
                            if (null != mAdapter) {
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(SubjectActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SubjectActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SubjectActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Subject>>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(SubjectActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitUtils.getInstance().getService(this).getCourses().enqueue(responseCallback);
    }

    private void fetchSubjects() {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(this, getString(R.string.loading));

        Callback<GenericResponse<SubjectRequest>> responseCallback = new Callback<GenericResponse<SubjectRequest>>() {
            @Override
            public void onResponse(Call<GenericResponse<SubjectRequest>> call, Response<GenericResponse<SubjectRequest>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<SubjectRequest> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(SubjectActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        SubjectRequest subjectResponse = genericResponse.getData();
                        if (null != subjectResponse && null != subjectResponse.getSubjects() && subjectResponse.getSubjects().size() > 0) {
                            mSelectedSubjects.addAll(subjectResponse.getSubjects());
                        } else {
                            Toast.makeText(SubjectActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SubjectActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SubjectActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }

                getCourses();
            }

            @Override
            public void onFailure(Call<GenericResponse<SubjectRequest>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(SubjectActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                getCourses();
            }
        };

        User user = (User) PreferenceUtils.getInstance(this).getObject(PreferenceUtils.KEY_USER);

        if (null != user && null != user.getId()) {
            UserRequest request = new UserRequest();
            request.setUserId(user.getId());
            RetrofitUtils.getInstance().getService(this).fetchSubjects(request).enqueue(responseCallback);
        } else {
            List<String> subjects = (List) PreferenceUtils.getInstance(this).getObject(PreferenceUtils.KEY_SELECTED_SUBJECTS);
            if (null != subjects && subjects.size() > 0) {
                mSelectedSubjects.addAll(subjects);
            }
            getCourses();
        }
    }

    private void saveSubjects(String userId) {
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
                        Toast.makeText(SubjectActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        Toast.makeText(SubjectActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        if (null != mLaunchFrom && mLaunchFrom.equalsIgnoreCase(Constants.LAUNCH_FROM_HOME)) {
                            finish();
                        } else {
                            launchLoginActivity();
                            finish();
                        }
                    } else {
                        Toast.makeText(SubjectActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SubjectActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(SubjectActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        SubjectRequest request = new SubjectRequest();
        request.setUserId(userId);
        request.setSubjects(mSelectedSubjects);
        RetrofitUtils.getInstance().getService(this).saveSubjects(request).enqueue(responseCallback);
    }
}
