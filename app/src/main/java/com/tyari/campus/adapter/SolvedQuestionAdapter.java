package com.tyari.campus.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tyari.campus.R;
import com.tyari.campus.model.Question;

import java.util.ArrayList;
import java.util.List;

public class SolvedQuestionAdapter extends RecyclerView.Adapter<SolvedQuestionAdapter.ViewHolder> {

    private Activity mActivity;
    private List<Question> mQuestions;

    public SolvedQuestionAdapter(Activity activity, List<Question> questions) {
        mActivity = activity;
        mQuestions = questions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.recycler_view_item_solved_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Question question = mQuestions.get(position);

        holder.mQuestionTxtVw.setText(question.getQuestion());
        holder.mAnswerTxtVw.setText(question.getAnswer());
        holder.mSolutionRecyclerVw.setLayoutManager(new LinearLayoutManager(mActivity));
        holder.mSolutionRecyclerVw.setHasFixedSize(true);
        holder.mSolutionRecyclerVw.setAdapter(new SolutionAdapter(mActivity, question.getSteps()));

        if (question.isCorrect()) {
            holder.mDetailsCntLyt.setVisibility(View.VISIBLE);
        } else {
            holder.mDetailsCntLyt.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mQuestionTxtVw;
        private TextView mAnswerTxtVw;
        private ConstraintLayout mDetailsCntLyt;
        private RecyclerView mSolutionRecyclerVw;

        public ViewHolder(View itemView) {
            super(itemView);
            mQuestionTxtVw = itemView.findViewById(R.id.txtVwQuestion);
            mAnswerTxtVw = itemView.findViewById(R.id.txtVwAnswer);
            mDetailsCntLyt = itemView.findViewById(R.id.cntLytDetails);
            mSolutionRecyclerVw = itemView.findViewById(R.id.recyclerVwSolution);
        }
    }
}
