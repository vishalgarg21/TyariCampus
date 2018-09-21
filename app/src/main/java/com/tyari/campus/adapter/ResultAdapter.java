package com.tyari.campus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tyari.campus.R;
import com.tyari.campus.model.Result;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private Context mContext;
    private List<Result> mResults;

    public ResultAdapter(Context context, List<Result> results){
        mContext = context;
        mResults = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_test, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Result result = mResults.get(position);
        holder.mTitleTxtVw.setText(result.getTitle());
        holder.mQuestionsTxtVw.setText(String.format(mContext.getString(R.string.question_count), String.valueOf(result.getTotal())));
        holder.mDurationTxtVw.setText(String.format(mContext.getString(R.string.no_correct_answers), String.valueOf(result.getCorrect())));
    }

    @Override
    public int getItemCount() {
        return mResults.size();
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
