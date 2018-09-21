package com.tyari.campus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tyari.campus.R;
import com.tyari.campus.common.Constants;
import com.tyari.campus.utils.PreferenceUtils;

public class SettingsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private String mLaunchFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (null != getIntent()) {
            mLaunchFrom = getIntent().getStringExtra(Constants.KEY_LAUNCH_FROM);
        }

        ListView mLanguageListVw = findViewById(R.id.listVwLanguage);
        String[] displayLanguages = getResources().getStringArray(R.array.display_languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayLanguages);
        mLanguageListVw.setAdapter(adapter);
        mLanguageListVw.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String[] languages = getResources().getStringArray(R.array.languages);
        String language = languages[position];
        PreferenceUtils.getInstance(SettingsActivity.this).putString(PreferenceUtils.KEY_LANG, language);

        if (null != mLaunchFrom && mLaunchFrom.equalsIgnoreCase(Constants.LAUNCH_FROM_SPLASH)) {
            Intent intent = new Intent(SettingsActivity.this, SubjectActivity.class);
            intent.putExtra(Constants.KEY_LAUNCH_FROM, Constants.LAUNCH_FROM_SETTING);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            launchHomeActivity();
            finish();
        }
    }
}
