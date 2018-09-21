package com.tyari.campus.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tyari.campus.R;

public class ExamInformationActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_information);


        TextView mInfo1TxtVw = findViewById(R.id.txtVwInfo1);
        TextView mInfo2TxtVw = findViewById(R.id.txtVwInfo2);
        TextView mInfo3TxtVw = findViewById(R.id.txtVwInfo3);
        TextView mInfo4TxtVw = findViewById(R.id.txtVwInfo4);
        TextView mInfo5TxtVw = findViewById(R.id.txtVwInfo5);
        TextView mInfo6TxtVw = findViewById(R.id.txtVwInfo6);

        mInfo1TxtVw.setOnClickListener(this);
        mInfo2TxtVw.setOnClickListener(this);
        mInfo3TxtVw.setOnClickListener(this);
        mInfo4TxtVw.setOnClickListener(this);
        mInfo5TxtVw.setOnClickListener(this);
        mInfo6TxtVw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.txtVwInfo1:
                launchWeViewActivity(getString(R.string.exam_information_url_tgt_pgt));
                break;

            case R.id.txtVwInfo2:
                launchWeViewActivity(getString(R.string.exam_information_url_ctet));
                break;

            case R.id.txtVwInfo3:
                launchWeViewActivity(getString(R.string.exam_information_url_ssc));
                break;

            case R.id.txtVwInfo4:
                launchWeViewActivity(getString(R.string.exam_information_url_ibps));
                break;

            case R.id.txtVwInfo5:
                launchWeViewActivity(getString(R.string.exam_information_url_nda));
                break;

            case R.id.txtVwInfo6:
                launchWeViewActivity(getString(R.string.exam_information_url_vdo));
                break;
        }
    }
}
