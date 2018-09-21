package com.tyari.campus.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.adapter.SolvedQuestionAdapter;
import com.tyari.campus.common.Constants;
import com.tyari.campus.common.RecyclerItemClickListener;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.Question;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SolvedQuestionFragment extends BaseFragment {
    private static final String TAG = LogUtils.makeLogTag(SolvedQuestionFragment.class);

    private View mRootVw;
    private List<Question> mSolvedQuestions;
    private SolvedQuestionAdapter mAdapter;

    public SolvedQuestionFragment() {
    }

    public static SolvedQuestionFragment newInstance() {
        return new SolvedQuestionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootVw = inflater.inflate(R.layout.fragment_solved_question, container, false);
        initUi();
        return mRootVw;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchSolvedQuestions();
    }

    private void initUi() {
        mSolvedQuestions = new ArrayList<>();
        RecyclerView mSolvedQuestionsRecyclerVw = mRootVw.findViewById(R.id.recyclerVwSolvedQuestions);
        mSolvedQuestionsRecyclerVw.setLayoutManager(new LinearLayoutManager(mActivity));
        mSolvedQuestionsRecyclerVw.setHasFixedSize(true);
        mAdapter = new SolvedQuestionAdapter(getActivity(), mSolvedQuestions);
        mSolvedQuestionsRecyclerVw.setAdapter(mAdapter);
        mSolvedQuestionsRecyclerVw.addOnItemTouchListener(new RecyclerItemClickListener(mActivity, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Question question = mSolvedQuestions.get(position);
                if (question.isCorrect()) {
                    question.setCorrect(false);
                } else {
                    question.setCorrect(true);
                }

                if (null != mAdapter) {
                    mAdapter.notifyItemChanged(position);
                }
            }
        }));
    }

    private void fetchSolvedQuestions() {
        if (!AppUtils.getInstance().isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(getActivity(), getString(R.string.loading));

        Callback<GenericResponse<List<Question>>> responseCallback = new Callback<GenericResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Question>>> call, Response<GenericResponse<List<Question>>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<List<Question>> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        List<Question> solvedQuestions = genericResponse.getData();
                        if (null != solvedQuestions && solvedQuestions.size() > 0) {
                            mSolvedQuestions.clear();
                            mSolvedQuestions.addAll(solvedQuestions);
                            if (null != mAdapter) {
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Question>>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitUtils.getInstance().getService(getActivity()).fetchSolvedQuestions().enqueue(responseCallback);
    }
}
