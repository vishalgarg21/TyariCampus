package com.tyari.campus.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.adapter.QuestionAdapter;
import com.tyari.campus.callback.CustomDialogCallBack;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.GetQuestionRequest;
import com.tyari.campus.model.Question;
import com.tyari.campus.model.Result;
import com.tyari.campus.model.Test;
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

public class TestActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = LogUtils.makeLogTag(TestActivity.class);
    private List<Question> mQuestions;
    private Test mTest;
    private String mTestId;
    private int mCurrentQuestionCount = 0;
    private int mTotalQuestionCount = 0;
    private CountDownTimer mCountDownTimer;

    private QuestionAdapter mAdapter;
    private RecyclerView mQuestionRecyclerVw;

    private TextView mCountTxtVw;
    private TextView mTimerTxtVw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_test);
        getBundleData();
        initUi();

        if (null != mTest) {
            mTestId = mTest.getId();
            getQuestions(mTestId);
        } else if (!TextUtils.isEmpty(mTestId)) {
            getTest(mTestId);
        }
    }

    @Override
    protected void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (null != mCountDownTimer) {
            mCountDownTimer.cancel();
        }

        super.onDestroy();
    }

    private void initUi() {
        mCountTxtVw = findViewById(R.id.txtVwCount);
        mTimerTxtVw = findViewById(R.id.txtVwTimer);

        TextView mSubmitTxtVw = findViewById(R.id.txtVwSubmit);
        TextView mPreviousTxtVw = findViewById(R.id.txtVwPrevious);
        TextView mNextTxtVw = findViewById(R.id.txtVwNext);

        mSubmitTxtVw.setOnClickListener(this);
        mPreviousTxtVw.setOnClickListener(this);
        mNextTxtVw.setOnClickListener(this);

        mQuestionRecyclerVw = findViewById(R.id.recyclerVwQuestions);
        mQuestions = new ArrayList<>();
        mAdapter = new QuestionAdapter(this, mQuestions);
        LinearLayoutManager mManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mQuestionRecyclerVw.setLayoutManager(mManager);
        mQuestionRecyclerVw.setHasFixedSize(true);
        mQuestionRecyclerVw.setAdapter(mAdapter);
        mQuestionRecyclerVw.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        mTotalQuestionCount = mQuestions.size();
        if (mTotalQuestionCount > 0) {
            switch (v.getId()) {
                case R.id.txtVwSubmit:
                    showSubmitConfirmationDialog();
                    break;

                case R.id.txtVwPrevious:
                    if (mCurrentQuestionCount == 0) {
                        Toast.makeText(TestActivity.this, getString(R.string.first_question_message), Toast.LENGTH_LONG).show();
                    } else if (mCurrentQuestionCount > 0) {
                        mCurrentQuestionCount = mCurrentQuestionCount - 1;
                        mQuestionRecyclerVw.smoothScrollToPosition(mCurrentQuestionCount);
                        updateCurrentQuestionText();
                    }
                    break;

                case R.id.txtVwNext:
                    if (mCurrentQuestionCount == mTotalQuestionCount - 1) {
                        Toast.makeText(TestActivity.this, getString(R.string.last_question_message), Toast.LENGTH_LONG).show();
                    } else if (mCurrentQuestionCount < mTotalQuestionCount - 1) {
                        mCurrentQuestionCount = mCurrentQuestionCount + 1;
                        mQuestionRecyclerVw.smoothScrollToPosition(mCurrentQuestionCount);
                        updateCurrentQuestionText();
                    }
                    break;
            }
        } else {
            Toast.makeText(this, getString(R.string.no_question_available), Toast.LENGTH_LONG).show();
        }
    }

    private void showSubmitConfirmationDialog() {
        AppUtils.getInstance().showCustomDialogWithCallBack(this, getString(R.string.test), getString(R.string.submit_test_confirmation), getString(R.string.yes), getString(R.string.no), new CustomDialogCallBack() {
            @Override
            public void positiveCallBack(Dialog dialog) {
                dialog.dismiss();
                saveResult(evaluateResult());
            }

            @Override
            public void negativeCallBack(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    private void showTimeExpireConfirmationDialog() {
        AppUtils.getInstance().showCustomDialogWithCallBack(this, getString(R.string.time_expire), getString(R.string.time_expire_test_confirmation), getString(R.string.yes), getString(R.string.no), new CustomDialogCallBack() {
            @Override
            public void positiveCallBack(Dialog dialog) {
                dialog.dismiss();
                saveResult(evaluateResult());
            }

            @Override
            public void negativeCallBack(Dialog dialog) {
                dialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    private void getBundleData() {
        if (null != getIntent()) {
            mTest = getIntent().getParcelableExtra(Constants.KEY_TEST);
            mTestId = getIntent().getStringExtra(Constants.KEY_TEST_ID);
        }
    }

    private void updateCurrentQuestionText() {
        String text = (mCurrentQuestionCount + 1) + "/" + mTotalQuestionCount;
        mCountTxtVw.setText(text);
    }

    private void updateTimerText() {
        long millisInFuture = mTest.getDuration() * 60 * 1000;

        mTimerTxtVw.setText(getRemainingTime(millisInFuture));

        mCountDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerTxtVw.setText(getRemainingTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                mTimerTxtVw.setText(getRemainingTime(0));
                showTimeExpireConfirmationDialog();
            }
        };

        mCountDownTimer.start();
    }

    private String getRemainingTime(long millisRemaining) {
        String remainingTime;

        int hours = (int) ((millisRemaining / (1000 * 60 * 60)) % 24);
        int minutes = (int) ((millisRemaining / (1000 * 60)) % 60);
        int seconds = (int) (millisRemaining / 1000) % 60;

        if (hours < 10) {
            remainingTime = "0" + hours + ":";
        } else {
            remainingTime = hours + ":";
        }

        if (minutes < 10) {
            remainingTime = remainingTime + "0" + minutes + ":";
        } else {
            remainingTime = remainingTime + minutes + ":";
        }

        if (seconds < 10) {
            remainingTime = remainingTime + "0" + seconds;
        } else {
            remainingTime = remainingTime + seconds;
        }

        return remainingTime;
    }

    private void updateUI() {
        mCurrentQuestionCount = 0;
        mTotalQuestionCount = mQuestions.size();
        updateCurrentQuestionText();
        updateTimerText();

        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getTest(String testId) {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(this, getString(R.string.loading));

        Callback<GenericResponse<Test>> responseCallback = new Callback<GenericResponse<Test>>() {
            @Override
            public void onResponse(Call<GenericResponse<Test>> call, Response<GenericResponse<Test>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<Test> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(TestActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        mTest = genericResponse.getData();
                        if (null != mTest && null != mTest.getId()) {
                            mTestId = mTest.getId();
                            getQuestions(mTestId);
                        } else {
                            Toast.makeText(TestActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(TestActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(TestActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Test>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(TestActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        GetQuestionRequest request = new GetQuestionRequest();
        request.setTestId(testId);
        RetrofitUtils.getInstance().getService(this).getTestById(request).enqueue(responseCallback);
    }

    private void getQuestions(String testId) {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(this, getString(R.string.loading));

        Callback<GenericResponse<List<Question>>> responseCallback = new Callback<GenericResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Question>>> call, Response<GenericResponse<List<Question>>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<List<Question>> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(TestActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        List<Question> questions = genericResponse.getData();
                        if (null != questions && questions.size() > 0) {
                            mQuestions.addAll(questions);
                            updateUI();
                        } else {
                            Toast.makeText(TestActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(TestActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(TestActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Question>>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(TestActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        GetQuestionRequest request = new GetQuestionRequest();
        request.setTestId(testId);
        RetrofitUtils.getInstance().getService(this).getQuestions(request).enqueue(responseCallback);
    }


    private void saveResult(final Result result) {
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
                        Toast.makeText(TestActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        Toast.makeText(TestActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(TestActivity.this, ResultActivity.class);
                        intent.putExtra(Constants.KEY_RESULT, result);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(TestActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(TestActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(TestActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };
        RetrofitUtils.getInstance().getService(this).saveResult(result).enqueue(responseCallback);
    }

    private Result evaluateResult() {
        Result result = null;
        int correct = 0;
        int wrong = 0;
        int skipped = 0;

        for (Question question : mQuestions) {
            if (null != question.getSelected() && question.isCorrect()) {
                correct = correct + 1;
            } else if (null != question.getSelected() && !question.isCorrect()) {
                wrong = wrong + 1;
            }
            if (null == question.getSelected()) {
                skipped = skipped + 1;
            }
        }

        result = new Result();
        result.setCorrect(correct);
        result.setWrong(wrong);
        result.setSkipped(skipped);
        result.setTotal(mQuestions.size());

        result.setTestCode(mTest.getId());
        result.setTitle(mTest.getTitle());
        result.setSubjectCode(mTest.getSubjectId());

        if ((correct + wrong + skipped) == mQuestions.size()) {
            result.setStatus(Constants.TEST_STATUS_COMPLETED);
        } else {
            result.setStatus(Constants.TEST_STATUS_PENDING);
        }

        User user = (User) PreferenceUtils.getInstance(this).getObject(PreferenceUtils.KEY_USER);

        if (null != user && null != user.getId()) {
            result.setUserId(user.getId());
        }

        return result;
    }
}
