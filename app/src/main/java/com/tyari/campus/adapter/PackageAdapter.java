package com.tyari.campus.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.activity.PackageInfoActivity;
import com.tyari.campus.activity.TestActivity;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.PackageInfo;
import com.tyari.campus.model.Test;

import java.util.ArrayList;
import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    private Activity mActivity;
    private List<PackageInfo> mPackageInfoList;

    public PackageAdapter(Activity activity, List<PackageInfo> packageInfoList) {
        mActivity = activity;
        mPackageInfoList = packageInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.recycler_view_item_package_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PackageInfo info = mPackageInfoList.get(position);

        holder.packageInfo = info;
        holder.title = String.format(mActivity.getString(R.string.package_title_format), info.getExamTitle(), info.getSubjectTitle());
        holder.titleTxtVw.setText(holder.title);

        int freeTestsCount = info.getFreeTests().size();
        int platinumTestsCount = info.getPlatinumTests().size();
        int goldTestsCount = info.getGoldTests().size();
        int silverTestsCount = info.getSilverTests().size();

        holder.freeCountTxtVw.setText(String.format(mActivity.getString(R.string.total_tests_available), freeTestsCount));
        holder.platinumCountTxtVw.setText(String.format(mActivity.getString(R.string.total_tests_available), platinumTestsCount));
        holder.goldCountTxtVw.setText(String.format(mActivity.getString(R.string.total_tests_available), goldTestsCount));
        holder.silverCountTxtVw.setText(String.format(mActivity.getString(R.string.total_tests_available), silverTestsCount));

        if (freeTestsCount > 0) {
            holder.freeCardVw.setVisibility(View.VISIBLE);
        } else {
            holder.freeCardVw.setVisibility(View.GONE);
        }

        if (platinumTestsCount > 0) {
            holder.platinumCardVw.setVisibility(View.VISIBLE);
        } else {
            holder.platinumCardVw.setVisibility(View.GONE);
        }

        if (goldTestsCount > 0) {
            holder.goldCardVw.setVisibility(View.VISIBLE);
        } else {
            holder.goldCardVw.setVisibility(View.GONE);
        }

        if (silverTestsCount > 0) {
            holder.silverCardVw.setVisibility(View.VISIBLE);
        } else {
            holder.silverCardVw.setVisibility(View.GONE);
        }

        if (platinumTestsCount <= 0 && goldTestsCount <= 0 && silverTestsCount <= 0) {
            holder.subTitleTxtVw.setText(R.string.no_more_tests_available);
        } else {
            holder.subTitleTxtVw.setText(R.string.upgrade_tests_available);
        }
    }

    @Override
    public int getItemCount() {
        return mPackageInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private String title;
        private PackageInfo packageInfo;
        private TextView titleTxtVw;
        private TextView subTitleTxtVw;

        private TextView freeCountTxtVw;
        private TextView platinumCountTxtVw;
        private TextView goldCountTxtVw;
        private TextView silverCountTxtVw;

        private Button freeBtn;
        private Button platinumBtn;
        private Button goldBtn;
        private Button silverBtn;

        private CardView freeCardVw;
        private CardView platinumCardVw;
        private CardView goldCardVw;
        private CardView silverCardVw;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTxtVw = itemView.findViewById(R.id.txtVwTitle);
            subTitleTxtVw = itemView.findViewById(R.id.txtVwPackageTitle);

            freeCountTxtVw = itemView.findViewById(R.id.txtVwFreeCount);
            platinumCountTxtVw = itemView.findViewById(R.id.txtVwPlatinumCount);
            goldCountTxtVw = itemView.findViewById(R.id.txtVwGoldCount);
            silverCountTxtVw = itemView.findViewById(R.id.txtVwSilverCount);

            freeBtn = itemView.findViewById(R.id.btnStart);
            platinumBtn = itemView.findViewById(R.id.btnPlatinumBuy);
            goldBtn = itemView.findViewById(R.id.btnGoldBuy);
            silverBtn = itemView.findViewById(R.id.btnSilverBuy);

            freeCardVw = itemView.findViewById(R.id.cardVwFree);
            platinumCardVw = itemView.findViewById(R.id.cardVwPlatinum);
            goldCardVw = itemView.findViewById(R.id.cardVwGold);
            silverCardVw = itemView.findViewById(R.id.cardVwSilver);

            freeBtn.setOnClickListener(this);
            platinumBtn.setOnClickListener(this);
            goldBtn.setOnClickListener(this);
            silverBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnStart:
                    if (packageInfo.getFreeTests().size() > 0) {
                        Intent startTestIntent = new Intent(mActivity, TestActivity.class);
                        startTestIntent.putExtra(Constants.KEY_TEST, packageInfo.getFreeTests().get(0));
                        mActivity.startActivity(startTestIntent);
                    } else {
                        Toast.makeText(mActivity, mActivity.getString(R.string.no_free_test_available), Toast.LENGTH_LONG).show();
                    }
                    break;

                case R.id.btnPlatinumBuy:
                    launchPackageInfoActivity((ArrayList<Test>) packageInfo.getPlatinumTests());
                    break;

                case R.id.btnGoldBuy:
                    launchPackageInfoActivity((ArrayList<Test>) packageInfo.getGoldTests());
                    break;

                case R.id.btnSilverBuy:
                    launchPackageInfoActivity((ArrayList<Test>) packageInfo.getSilverTests());
                    break;
            }
        }

        private void launchPackageInfoActivity(ArrayList<Test> tests) {
            Intent intent = new Intent(mActivity, PackageInfoActivity.class);
            intent.putParcelableArrayListExtra(Constants.KEY_TEST_LIST, tests);
            intent.putExtra(Constants.KEY_TITLE, title);
            mActivity.startActivity(intent);
        }
    }
}
