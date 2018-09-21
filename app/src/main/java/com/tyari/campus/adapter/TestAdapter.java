package com.tyari.campus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tyari.campus.R;
import com.tyari.campus.model.Test;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    private Context mContext;
    private List<Test> mTests;

    public TestAdapter(Context context, List<Test> tests){
        mContext = context;
        mTests = tests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_test, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Test test = mTests.get(position);
        holder.mTitleTxtVw.setText(test.getTitle());
        holder.mQuestionsTxtVw.setText(String.format(mContext.getString(R.string.question_count), String.valueOf(test.getQuestions())));
        holder.mDurationTxtVw.setText(String.format(mContext.getString(R.string.test_duration), String.valueOf(test.getDuration())));
    }

    @Override
    public int getItemCount() {
        return mTests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTitleTxtVw;
        private TextView mQuestionsTxtVw;
        private TextView mDurationTxtVw;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleTxtVw = itemView.findViewById(R.id.txtVwTitle);
            mQuestionsTxtVw = itemView.findViewById(R.id.txtVwQuestions);
            mDurationTxtVw = itemView.findViewById(R.id.txtVwDuration);
        }
    }
}
