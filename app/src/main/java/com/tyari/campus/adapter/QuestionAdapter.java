package com.tyari.campus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tyari.campus.R;
import com.tyari.campus.model.Question;
import com.tyari.campus.utils.AppUtils;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private Context mContext;
    private List<Question> mQuestions;

    public QuestionAdapter(Context context, List<Question> questions) {
        mContext = context;
        mQuestions = questions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Question question = mQuestions.get(position);

        holder.mQuestion = question;
        holder.mQuestionNoTxtVw.setText(String.format(mContext.getString(R.string.question_number_format), position + 1));

        AppUtils.setText(holder.mQuestionVw, question.getQuestion());
        AppUtils.setText(holder.mOption1Vw, question.getOption1());
        AppUtils.setText(holder.mOption2Vw, question.getOption2());
        AppUtils.setText(holder.mOption3Vw, question.getOption3());
        AppUtils.setText(holder.mOption4Vw, question.getOption4());

        if (!TextUtils.isEmpty(question.getOption5())) {
            holder.mOption5Vw.setVisibility(View.VISIBLE);
            holder.mOption5Btn.setVisibility(View.VISIBLE);
            AppUtils.setText(holder.mOption5Vw, question.getOption5());
        } else {
            holder.mOption5Vw.setVisibility(View.GONE);
            holder.mOption5Btn.setVisibility(View.GONE);
        }

        holder.clearSelection();
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Question mQuestion;
        private TextView mQuestionNoTxtVw;
        private ImageView mOption1Btn;
        private ImageView mOption2Btn;
        private ImageView mOption3Btn;
        private ImageView mOption4Btn;
        private ImageView mOption5Btn;

        private WebView mQuestionVw;
        private WebView mOption1Vw;
        private WebView mOption2Vw;
        private WebView mOption3Vw;
        private WebView mOption4Vw;
        private WebView mOption5Vw;

        public ViewHolder(View itemView) {
            super(itemView);
            mQuestionNoTxtVw = itemView.findViewById(R.id.txtVwQuestionNo);
            mOption1Btn = itemView.findViewById(R.id.radioOption1);
            mOption2Btn = itemView.findViewById(R.id.radioOption2);
            mOption3Btn = itemView.findViewById(R.id.radioOption3);
            mOption4Btn = itemView.findViewById(R.id.radioOption4);
            mOption5Btn = itemView.findViewById(R.id.radioOption5);

            mQuestionVw = itemView.findViewById(R.id.webVwQuestion);
            mOption1Vw = itemView.findViewById(R.id.webVwOption1);
            mOption2Vw = itemView.findViewById(R.id.webVwOption2);
            mOption3Vw = itemView.findViewById(R.id.webVwOption3);
            mOption4Vw = itemView.findViewById(R.id.webVwOption4);
            mOption5Vw = itemView.findViewById(R.id.webVwOption5);

            mOption1Btn.setOnClickListener(this);
            mOption2Btn.setOnClickListener(this);
            mOption3Btn.setOnClickListener(this);
            mOption4Btn.setOnClickListener(this);
            mOption5Btn.setOnClickListener(this);
        }

        private void clearSelection() {
            mOption1Btn.setImageResource(R.drawable.ic_unselected);
            mOption2Btn.setImageResource(R.drawable.ic_unselected);
            mOption3Btn.setImageResource(R.drawable.ic_unselected);
            mOption4Btn.setImageResource(R.drawable.ic_unselected);
            mOption5Btn.setImageResource(R.drawable.ic_unselected);
        }

        @Override
        public void onClick(View v) {
            clearSelection();
            switch (v.getId()) {
                case R.id.radioOption1:
                    mOption1Btn.setImageResource(R.drawable.ic_selected);
                    mQuestion.setSelected(mQuestion.getOption1());
                    break;

                case R.id.radioOption2:
                    mOption2Btn.setImageResource(R.drawable.ic_selected);
                    mQuestion.setSelected(mQuestion.getOption2());
                    break;

                case R.id.radioOption3:
                    mOption3Btn.setImageResource(R.drawable.ic_selected);
                    mQuestion.setSelected(mQuestion.getOption3());
                    break;

                case R.id.radioOption4:
                    mOption4Btn.setImageResource(R.drawable.ic_selected);
                    mQuestion.setSelected(mQuestion.getOption4());
                    break;

                case R.id.radioOption5:
                    mOption5Btn.setImageResource(R.drawable.ic_selected);
                    mQuestion.setSelected(mQuestion.getOption5());
                    break;
            }
        }
    }
}
