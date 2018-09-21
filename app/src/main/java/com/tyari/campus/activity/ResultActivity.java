package com.tyari.campus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.Result;

public class ResultActivity extends BaseActivity implements View.OnClickListener {

    private Result mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        if (null != getIntent()) {
            mResult = getIntent().getParcelableExtra(Constants.KEY_RESULT);
        }

        TextView mTitleTxtVw = findViewById(R.id.txtVwTitle);
        TextView mScoreTxtVw = findViewById(R.id.txtVwScore);
        TextView mQuestionsTxtVw = findViewById(R.id.txtVwQuestions);
        TextView mCorrectTxtVw = findViewById(R.id.txtVwCorrect);
        TextView mWrongTxtVw = findViewById(R.id.txtVwWrong);
        TextView mSkippedTxtVw = findViewById(R.id.txtVwSkipped);

        ProgressBar mScoreProgressBar = findViewById(R.id.progressBarScore);
        ProgressBar mCorrectProgressBar = findViewById(R.id.progressBarCorrect);
        ProgressBar mWrongProgressBar = findViewById(R.id.progressBarWrong);
        ProgressBar mSkippedProgressBar = findViewById(R.id.progressBarSkipped);

        Button mAttemptBtn = findViewById(R.id.btnAttempt);
        Button mViewBtn = findViewById(R.id.btnView);

        mAttemptBtn.setOnClickListener(this);
        mViewBtn.setOnClickListener(this);

        if (null != mResult) {
            mTitleTxtVw.setText(mResult.getTitle());
            mScoreTxtVw.setText(String.valueOf(mResult.getCorrect()));
            mCorrectTxtVw.setText(String.valueOf(mResult.getCorrect()));
            mWrongTxtVw.setText(String.valueOf(mResult.getWrong()));
            mSkippedTxtVw.setText(String.valueOf(mResult.getSkipped()));
            mQuestionsTxtVw.setText(String.format(getString(R.string.score_question_format), mResult.getTotal()));

            mScoreProgressBar.setMax(mResult.getTotal());
            mScoreProgressBar.setProgress(mResult.getCorrect());

            mCorrectProgressBar.setMax(mResult.getTotal());
            mCorrectProgressBar.setProgress(mResult.getCorrect());

            mWrongProgressBar.setMax(mResult.getTotal());
            mWrongProgressBar.setProgress(mResult.getWrong());

            mSkippedProgressBar.setMax(mResult.getTotal());
            mSkippedProgressBar.setProgress(mResult.getSkipped());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAttempt:
                Intent startTestIntent = new Intent(this, TestActivity.class);
                startTestIntent.putExtra(Constants.KEY_TEST_ID, mResult.getTestCode());
                startActivity(startTestIntent);
                finish();
                break;

            case R.id.btnView:
                Toast.makeText(this, "Feature will be available soon.", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
