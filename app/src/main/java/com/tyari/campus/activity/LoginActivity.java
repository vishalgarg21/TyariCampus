package com.tyari.campus.activity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.ForgotPasswordRequest;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.LoginRequest;
import com.tyari.campus.model.ResetPasswordRequest;
import com.tyari.campus.model.SubjectRequest;
import com.tyari.campus.model.User;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.PreferenceUtils;
import com.tyari.campus.utils.RetrofitUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(LoginActivity.class);
    private EditText mEmailEdtTxt;
    private EditText mPasswordEdtTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailEdtTxt = findViewById(R.id.edtTxtEmail);
        mPasswordEdtTxt = findViewById(R.id.edtTxtPassword);

        TextView mForgotTxtVw = findViewById(R.id.txtVwForgot);

        ImageView mLoginBtn = findViewById(R.id.btnNext);
        Button mRegisterBtn = findViewById(R.id.btnRegister);
        Button mSkipBtn = findViewById(R.id.btnSkip);

        mForgotTxtVw.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
        mSkipBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtVwForgot:
                launchActivity(ForgotPasswordActivity.class);
                break;

            case R.id.btnNext:
                String email = mEmailEdtTxt.getText().toString();
                String password = mPasswordEdtTxt.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(this, getString(R.string.please_enter_email), Toast.LENGTH_LONG).show();
                } else if (!AppUtils.getInstance().isValidEmail(email)) {
                    Toast.makeText(this, getString(R.string.please_enter_valid_email), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, getString(R.string.please_enter_password), Toast.LENGTH_LONG).show();
                } else {
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setEmail(email);
                    loginRequest.setPassword(password);
                    login(loginRequest);
                }

                break;

            case R.id.btnRegister:
                Intent loginIntent = new Intent(this, RegisterActivity.class);
                startActivity(loginIntent);
                break;

            case R.id.btnSkip:
                launchHomeActivity();
                break;
        }
    }

    private void login(LoginRequest loginRequest) {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(LoginActivity.this, getString(R.string.loading));

        Callback<GenericResponse<User>> responseCallback = new Callback<GenericResponse<User>>() {
            @Override
            public void onResponse(Call<GenericResponse<User>> call, Response<GenericResponse<User>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<User> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        User user = genericResponse.getData();
                        if (null != user) {
                            PreferenceUtils.getInstance(LoginActivity.this).putObject(PreferenceUtils.KEY_USER, user);
                            saveSubjects(user.getId());
                        } else {
                            Toast.makeText(LoginActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<User>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };
        RetrofitUtils.getInstance().getService(this).login(loginRequest).enqueue(responseCallback);
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
                launchHomeActivity();
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                launchHomeActivity();
            }
        };

        List<String> subjects = (List) PreferenceUtils.getInstance(this).getObject(PreferenceUtils.KEY_SELECTED_SUBJECTS);
        if (null != subjects && subjects.size() > 0) {
            SubjectRequest request = new SubjectRequest();
            request.setUserId(userId);
            RetrofitUtils.getInstance().getService(this).saveSubjects(request).enqueue(responseCallback);
        } else {
            launchHomeActivity();
        }
    }
}
