package com.tyari.campus.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tyari.campus.R;

import java.util.List;

public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.ViewHolder> {

    private Activity mActivity;
    private List<String> mSolutionSteps;

    SolutionAdapter(Activity activity, List<String> solutionSteps) {
        mActivity = activity;
        mSolutionSteps = solutionSteps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.recycler_view_item_solution, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String step = mSolutionSteps.get(position);

        holder.mTitleTxtVw.setText(step);
    }

    @Override
    public int getItemCount() {
        return mSolutionSteps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTxtVw;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleTxtVw = itemView.findViewById(R.id.txtVwTitle);
        }
    }
}
