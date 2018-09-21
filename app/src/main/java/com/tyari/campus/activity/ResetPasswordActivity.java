package com.tyari.campus.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.ForgotPasswordRequest;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.ResetPasswordRequest;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(ResetPasswordActivity.class);

    private String mMobile;
    private EditText mVerificationCodeEdtTxt;
    private EditText mNewPasswordEdtTxt;
    private EditText mConfirmPasswordEdtTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        if (null != getIntent()){
            mMobile = getIntent().getStringExtra(Constants.KEY_MOBILE);
        }
        mVerificationCodeEdtTxt = findViewById(R.id.edtTxtVerificationCode);
        mNewPasswordEdtTxt = findViewById(R.id.edtTxtNewPassword);
        mConfirmPasswordEdtTxt = findViewById(R.id.edtTxtConfirmPassword);

        TextView mResendTxtVw = findViewById(R.id.txtVwResend);
        ImageView mNextBtn = findViewById(R.id.btnNext);
        mResendTxtVw.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtVwResend:
                forgotPassword(mMobile);
                break;

            case R.id.btnNext:
                String verificationCode = mVerificationCodeEdtTxt.getText().toString();
                String newPassword = mNewPasswordEdtTxt.getText().toString();
                String confirmPassword = mConfirmPasswordEdtTxt.getText().toString();

                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(this, getString(R.string.please_enter_verification_code), Toast.LENGTH_LONG).show();
                } else if (verificationCode.length() != Constants.VERIFICATION_CODE_LENGTH) {
                    Toast.makeText(this, getString(R.string.verification_code_length_error), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(this, getString(R.string.please_enter_new_password), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(this, getString(R.string.please_confirm_new_password), Toast.LENGTH_LONG).show();
                } else if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(this, getString(R.string.passwords_dint_match), Toast.LENGTH_LONG).show();
                } else {
                    ResetPasswordRequest request = new ResetPasswordRequest();
                    request.setMobile(mMobile);
                    request.setVerificationCode(verificationCode);
                    request.setPassword(newPassword);
                    resetPassword(request);
                }
                break;
        }
    }

    private void forgotPassword(String mobile) {
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
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        Toast.makeText(ResetPasswordActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(ResetPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setMobile(mobile);
        RetrofitUtils.getInstance().getService(this).forgotPassword(request).enqueue(responseCallback);
    }


    private void resetPassword(ResetPasswordRequest request) {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(ResetPasswordActivity.this, getString(R.string.loading));

        Callback<GenericResponse> responseCallback = new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        Toast.makeText(ResetPasswordActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(ResetPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };
        RetrofitUtils.getInstance().getService(this).resetPassword(request).enqueue(responseCallback);
    }
}
