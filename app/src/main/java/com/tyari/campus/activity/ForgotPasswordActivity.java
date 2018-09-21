package com.tyari.campus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.ForgotPasswordRequest;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(ForgotPasswordActivity.class);

    private EditText mMobileEdtTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mMobileEdtTxt = findViewById(R.id.edtTxtMobile);

        ImageView mNextBtn = findViewById(R.id.btnNext);
        mNextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                String mobile = mMobileEdtTxt.getText().toString();

                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(this, getString(R.string.please_enter_number), Toast.LENGTH_LONG).show();
                } else if (!AppUtils.getInstance().isValidMobileNumber(mobile)) {
                    Toast.makeText(this, getString(R.string.please_enter_valid_number), Toast.LENGTH_LONG).show();
                } else {
                    forgotPassword(mobile);
                }
                break;
        }
    }

    private void launchResetPasswordActivity(String mobile) {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        intent.putExtra(Constants.KEY_MOBILE, mobile);
        startActivity(intent);
    }

    private void forgotPassword(final String mobile) {
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
                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        Toast.makeText(ForgotPasswordActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        launchResetPasswordActivity(mobile);
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setMobile(mobile);
        RetrofitUtils.getInstance().getService(this).forgotPassword(request).enqueue(responseCallback);
    }
}
