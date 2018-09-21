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
import com.tyari.campus.model.User;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.PreferenceUtils;
import com.tyari.campus.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(UpdateProfileActivity.class);

    private User mUser;
    private EditText mNameEdtTxt;
    private EditText mMobileEdtTxt;
    private EditText mAddressEdtTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        mNameEdtTxt = findViewById(R.id.edtTxtName);
        mMobileEdtTxt = findViewById(R.id.edtTxtMobile);
        mAddressEdtTxt = findViewById(R.id.edtTxtAddress);

        mUser = (User) PreferenceUtils.getInstance(this).getObject(PreferenceUtils.KEY_USER);

        if (null != mUser) {
            if (!TextUtils.isEmpty(mUser.getName())) {
                mNameEdtTxt.setText(mUser.getName());
            }

            if (!TextUtils.isEmpty(mUser.getMobile())) {
                mMobileEdtTxt.setText(mUser.getMobile());
            }

            if (!TextUtils.isEmpty(mUser.getAddress())) {
                mAddressEdtTxt.setText(mUser.getAddress());
            }
        }

        ImageView mRegisterBtn = findViewById(R.id.btnNext);
        mRegisterBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                String name = mNameEdtTxt.getText().toString();
                String mobile = mMobileEdtTxt.getText().toString();
                String address = mAddressEdtTxt.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, getString(R.string.please_enter_name), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(this, getString(R.string.please_enter_number), Toast.LENGTH_LONG).show();
                } else if (!AppUtils.getInstance().isValidMobileNumber(mobile)) {
                    Toast.makeText(this, getString(R.string.please_enter_valid_number), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(address)) {
                    Toast.makeText(this, getString(R.string.please_enter_address), Toast.LENGTH_LONG).show();
                } else {
                    RegisterRequest registerRequest = new RegisterRequest();
                    registerRequest.setUserId(mUser.getId());
                    registerRequest.setName(name);
                    registerRequest.setMobile(mobile);
                    registerRequest.setAddress(address);
                    updateProfile(registerRequest);
                }
                break;
        }
    }

    private void updateProfile(final RegisterRequest registerRequest) {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(UpdateProfileActivity.this, getString(R.string.loading));

        Callback<GenericResponse> responseCallback = new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(UpdateProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        Toast.makeText(UpdateProfileActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();

                        mUser.setName(registerRequest.getName());
                        mUser.setMobile(registerRequest.getMobile());
                        mUser.setAddress(registerRequest.getAddress());
                        PreferenceUtils.getInstance(UpdateProfileActivity.this).putObject(PreferenceUtils.KEY_USER, mUser);

                        finish();
                    } else {
                        Toast.makeText(UpdateProfileActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UpdateProfileActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(UpdateProfileActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };
        RetrofitUtils.getInstance().getService(this).updateProfile(registerRequest).enqueue(responseCallback);
    }
}
