package com.tyari.campus.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.ChangePasswordRequest;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.User;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.PreferenceUtils;
import com.tyari.campus.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(ChangePasswordActivity.class);

    private EditText mOldPasswordEdtTxt;
    private EditText mNewPasswordEdtTxt;
    private EditText mConfirmPasswordEdtTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mOldPasswordEdtTxt = findViewById(R.id.edtTxtOldPassword);
        mNewPasswordEdtTxt = findViewById(R.id.edtTxtNewPassword);
        mConfirmPasswordEdtTxt = findViewById(R.id.edtTxtConfirmPassword);

        ImageView mLoginBtn = findViewById(R.id.btnNext);
        mLoginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                String oldPassword = mOldPasswordEdtTxt.getText().toString();
                String newPassword = mNewPasswordEdtTxt.getText().toString();
                String confirmPassword = mConfirmPasswordEdtTxt.getText().toString();

                if (TextUtils.isEmpty(oldPassword)) {
                    Toast.makeText(this, getString(R.string.please_enter_old_password), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(this, getString(R.string.please_enter_new_password), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(this, getString(R.string.please_confirm_new_password), Toast.LENGTH_LONG).show();
                } else if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(this, getString(R.string.passwords_dint_match), Toast.LENGTH_LONG).show();
                } else if (newPassword.equals(oldPassword)) {
                    Toast.makeText(this, getString(R.string.new_password_cannot_be_same_as_old_password), Toast.LENGTH_LONG).show();
                } else {
                    User user = (User) PreferenceUtils.getInstance(this).getObject(PreferenceUtils.KEY_USER);

                    if (null != user && null != user.getId()) {
                        ChangePasswordRequest request = new ChangePasswordRequest();
                        request.setUserId(user.getId());
                        request.setOldPassword(oldPassword);
                        request.setNewPassword(newPassword);
                        changePassword(request);
                    } else {
                        Toast.makeText(this, getString(R.string.invalid_user_message), Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void changePassword(ChangePasswordRequest request) {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(ChangePasswordActivity.this, getString(R.string.loading));

        Callback<GenericResponse> responseCallback = new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(ChangePasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        Toast.makeText(ChangePasswordActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(ChangePasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };
        RetrofitUtils.getInstance().getService(this).changePassword(request).enqueue(responseCallback);
    }
}
