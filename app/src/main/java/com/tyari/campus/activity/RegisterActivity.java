package com.tyari.campus.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.RegisterRequest;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(RegisterActivity.class);

    private EditText mNameEdtTxt;
    private EditText mEmailEdtTxt;
    private EditText mMobileEdtTxt;
    private EditText mAddressEdtTxt;
    private EditText mPasswordEdtTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNameEdtTxt = findViewById(R.id.edtTxtName);
        mEmailEdtTxt = findViewById(R.id.edtTxtEmail);
        mMobileEdtTxt = findViewById(R.id.edtTxtMobile);
        mAddressEdtTxt = findViewById(R.id.edtTxtAddress);
        mPasswordEdtTxt = findViewById(R.id.edtTxtPassword);

        ImageView mRegisterBtn = findViewById(R.id.btnNext);
        mRegisterBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                String name = mNameEdtTxt.getText().toString();
                String email = mEmailEdtTxt.getText().toString();
                String mobile = mMobileEdtTxt.getText().toString();
                String address = mAddressEdtTxt.getText().toString();
                String password = mPasswordEdtTxt.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, getString(R.string.please_enter_name), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(this, getString(R.string.please_enter_email), Toast.LENGTH_LONG).show();
                } else if (!AppUtils.getInstance().isValidEmail(email)) {
                    Toast.makeText(this, getString(R.string.please_enter_valid_email), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(this, getString(R.string.please_enter_number), Toast.LENGTH_LONG).show();
                } else if (!AppUtils.getInstance().isValidMobileNumber(mobile)) {
                    Toast.makeText(this, getString(R.string.please_enter_valid_number), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(address)) {
                    Toast.makeText(this, getString(R.string.please_enter_address), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, getString(R.string.please_enter_password), Toast.LENGTH_LONG).show();
                } else {
                    RegisterRequest registerRequest = new RegisterRequest();
                    registerRequest.setName(name);
                    registerRequest.setEmail(email);
                    registerRequest.setMobile(mobile);
                    registerRequest.setAddress(address);
                    registerRequest.setPassword(password);
                    register(registerRequest);
                }
                break;
        }
    }

    private void register(RegisterRequest registerRequest) {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(RegisterActivity.this, getString(R.string.loading));

        Callback<GenericResponse> responseCallback = new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        Toast.makeText(RegisterActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(RegisterActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };
        RetrofitUtils.getInstance().getService(this).register(registerRequest).enqueue(responseCallback);
    }
}
