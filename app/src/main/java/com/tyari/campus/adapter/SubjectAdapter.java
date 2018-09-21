package com.tyari.campus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.GetSubjectRequest;
import com.tyari.campus.model.Subject;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private static final String TAG = LogUtils.makeLogTag(SubjectAdapter.class);
    private Context mContext;
    private boolean mIsChild;
    private List<Subject> mSubjects;
    private List<String> mSelectedSubjects;

    public SubjectAdapter(Context context, List<Subject> subjects, List<String> selectedSubjects, boolean isChild) {
        mContext = context;
        mSubjects = subjects;
        mSelectedSubjects = selectedSubjects;
        mIsChild = isChild;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject subject = mSubjects.get(position);

        holder.mSubject = subject;
        holder.mTitleTxtVw.setText(subject.getTitle());

        if (subject.getSubjects() > 0) {
            holder.mNextImgVw.setVisibility(View.VISIBLE);
            holder.mCheckedImgVw.setVisibility(View.GONE);
        } else {
            if (mSelectedSubjects.contains(subject.getId())) {
                holder.mCheckedImgVw.setImageResource(R.drawable.ic_square_checked);
                holder.mSubject.setSelected(true);
            } else {
                holder.mCheckedImgVw.setImageResource(R.drawable.ic_square_uncheck);
                holder.mSubject.setSelected(false);
            }

            holder.mCheckedImgVw.setVisibility(View.VISIBLE);
            holder.mSubRecyclerVw.setVisibility(View.GONE);
            holder.mNextImgVw.setVisibility(View.GONE);
        }

        if (mIsChild) {
            holder.mSubDirectoryImgVw.setVisibility(View.VISIBLE);
        } else {
            holder.mSubDirectoryImgVw.setVisibility(View.GONE);
            holder.mCheckedImgVw.setVisibility(View.GONE);
            holder.mSubject.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return mSubjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Subject mSubject;
        private TextView mTitleTxtVw;
        private ImageView mNextImgVw;
        private ImageView mCheckedImgVw;
        private ImageView mSubDirectoryImgVw;
        private RecyclerView mSubRecyclerVw;

        private List<Subject> mInnerSubjects;
        private SubjectAdapter mAdapter;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleTxtVw = itemView.findViewById(R.id.txtVwTitle);
            mNextImgVw = itemView.findViewById(R.id.imgVwNext);
            mCheckedImgVw = itemView.findViewById(R.id.imgVwChecked);
            mSubDirectoryImgVw = itemView.findViewById(R.id.imgVwSubDirectory);
            mSubRecyclerVw = itemView.findViewById(R.id.recyclerVwSub);

            mNextImgVw.setOnClickListener(this);
            mCheckedImgVw.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgVwChecked:
                    if (mSubject.isSelected()) {
                        mCheckedImgVw.setImageResource(R.drawable.ic_square_uncheck);
                        mSubject.setSelected(false);
                        mSelectedSubjects.remove(mSubject.getId());
                    } else {
                        mCheckedImgVw.setImageResource(R.drawable.ic_square_checked);
                        mSubject.setSelected(true);
                        mSelectedSubjects.add(mSubject.getId());
                    }
                    break;

                case R.id.imgVwNext:
                    mInnerSubjects = new ArrayList<>();
                    mAdapter = new SubjectAdapter(mContext, mInnerSubjects, mSelectedSubjects, true);
                    mSubRecyclerVw.setLayoutManager(new LinearLayoutManager(mContext));
                    mSubRecyclerVw.setHasFixedSize(true);
                    mSubRecyclerVw.setAdapter(mAdapter);
                    getSubjects(mSubject.getId());
                    break;
            }
        }

        private void getSubjects(String courseId) {
            if (!AppUtils.getInstance().isInternetAvailable(mContext)) {
                Toast.makeText(mContext, mContext.getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
                return;
            }

            AppUtils.showProgress(mContext, mContext.getString(R.string.loading));

            Callback<GenericResponse<List<Subject>>> responseCallback = new Callback<GenericResponse<List<Subject>>>() {
                @Override
                public void onResponse(Call<GenericResponse<List<Subject>>> call, Response<GenericResponse<List<Subject>>> response) {
                    LogUtils.checkIf(TAG, "Response: " + response);
                    AppUtils.dismissProgress();

                    if (response != null && response.isSuccessful()) {
                        GenericResponse<List<Subject>> genericResponse = response.body();
                        if (genericResponse == null) {
                            Toast.makeText(mContext, mContext.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                            List<Subject> subjects = genericResponse.getData();
                            if (null != subjects && subjects.size() > 0) {
                                mInnerSubjects.addAll(subjects);
                                mSubRecyclerVw.setVisibility(View.VISIBLE);
                                if (null != mAdapter) {
                                    mAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(mContext, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(mContext, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse<List<Subject>>> call, Throwable t) {
                    LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                    AppUtils.dismissProgress();
                    Toast.makeText(mContext, mContext.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                }
            };

            GetSubjectRequest request = new GetSubjectRequest();
            request.setCourseId(courseId);
            RetrofitUtils.getInstance().getService(mContext).getSubjects(request).enqueue(responseCallback);
        }
    }
}
