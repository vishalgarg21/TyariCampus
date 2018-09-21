package com.tyari.campus.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tyari.campus.R;
import com.tyari.campus.adapter.TestAdapter;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.Test;
import com.tyari.campus.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class PackageInfoActivity extends BaseActivity implements View.OnClickListener {
    private List<Test> mTests;
    private String mTitle;
    private int mTestsCount;
    private int mTestsPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_info);
        mTests = new ArrayList<>();
        getBundleData();

        TextView mTitleTxtVw = findViewById(R.id.txtVwTitle);
        TextView mCountTxtVw = findViewById(R.id.txtVwCount);
        TextView mLanguageTxtVw = findViewById(R.id.txtVwLanguage);
        TextView mPriceTxtVw = findViewById(R.id.txtVwPrice);
        TextView mTotalPriceTxtVw = findViewById(R.id.txtVwTotalPrice);

        TextView mPayNowTxtVw = findViewById(R.id.txtVwPayNow);
        mPayNowTxtVw.setOnClickListener(this);

        if (mTests.size() > 0) {
            mTestsCount = mTests.size();
            mTitleTxtVw.setText(mTitle);

            mCountTxtVw.setText(String.format(getString(R.string.pack_tests), String.valueOf(mTestsCount)));

            String language = PreferenceUtils.getInstance(this).getString(PreferenceUtils.KEY_LANG);
            mLanguageTxtVw.setText(String.format(getString(R.string.pack_language), language));

            mTestsPrice = getTestsPrice();
            mPriceTxtVw.setText(String.format(getString(R.string.pack_price), String.valueOf(mTestsPrice)));
            mTotalPriceTxtVw.setText(String.format(getString(R.string.pack_total_price), String.valueOf(mTestsPrice)));
        }

        RecyclerView mTestsRecyclerVw = findViewById(R.id.recyclerVwTests);
        mTestsRecyclerVw.setLayoutManager(new LinearLayoutManager(this));
        mTestsRecyclerVw.setHasFixedSize(true);
        TestAdapter adapter = new TestAdapter(this, mTests);
        mTestsRecyclerVw.setAdapter(adapter);
    }

    private int getTestsPrice() {
        mTestsPrice = 0;
        for (int i = 0; i < mTestsCount; i++) {
            Test test = mTests.get(i);
            mTestsPrice = mTestsPrice + test.getPrice();
        }
        return mTestsPrice;
    }

    private void getBundleData() {
        if (null != getIntent()) {
            mTests = getIntent().getParcelableArrayListExtra(Constants.KEY_TEST_LIST);
            mTitle = getIntent().getStringExtra(Constants.KEY_TITLE);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
