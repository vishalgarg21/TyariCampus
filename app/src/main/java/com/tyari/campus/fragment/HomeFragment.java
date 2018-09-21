package com.tyari.campus.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.activity.BooksActivity;
import com.tyari.campus.activity.ExamInformationActivity;
import com.tyari.campus.activity.JobsActivity;
import com.tyari.campus.activity.VideosActivity;
import com.tyari.campus.adapter.OfferAdapter;
import com.tyari.campus.adapter.SubjectAdapter;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.Offer;
import com.tyari.campus.model.Subject;
import com.tyari.campus.model.SubjectRequest;
import com.tyari.campus.model.User;
import com.tyari.campus.model.UserRequest;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.PreferenceUtils;
import com.tyari.campus.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(HomeFragment.class);

    private View mRootVw;
    private List<Offer> mOffers;
    private OfferAdapter mOfferAdapter;

    private List<Subject> mSubjects;
    private List<String> mSelectedSubjects;
    private SubjectAdapter mSubjectAdapter;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootVw = inflater.inflate(R.layout.fragment_home, container, false);

        initUi();
        return mRootVw;
    }

    @Override
    public void onResume() {
        super.onResume();
        getOffers();
        fetchSubjects();
    }

    private void initUi() {
        TextView mJobsTxtVw = mRootVw.findViewById(R.id.txtVwJobs);
        TextView mBooksTxtVw = mRootVw.findViewById(R.id.txtVwBooks);
        TextView mVideosTxtVw = mRootVw.findViewById(R.id.txtVwVideos);
        TextView mExamInfoTxtVw = mRootVw.findViewById(R.id.txtVwExamInfo);
        TextView mSaveTxtVw = mRootVw.findViewById(R.id.txtVwSave);

        mJobsTxtVw.setOnClickListener(this);
        mBooksTxtVw.setOnClickListener(this);
        mVideosTxtVw.setOnClickListener(this);
        mExamInfoTxtVw.setOnClickListener(this);
        mSaveTxtVw.setOnClickListener(this);

        mOffers = new ArrayList<>();
        mOfferAdapter = new OfferAdapter(getActivity(), mOffers);
        RecyclerView mOffersRecyclerVw = mRootVw.findViewById(R.id.recyclerVwOffers);
        mOffersRecyclerVw.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mOffersRecyclerVw.setHasFixedSize(true);
        mOffersRecyclerVw.setAdapter(mOfferAdapter);

        mSubjects = new ArrayList<>();
        mSelectedSubjects = new ArrayList<>();
        mSubjectAdapter = new SubjectAdapter(getActivity(), mSubjects, mSelectedSubjects, false);
        RecyclerView mSubjectsRecyclerVw = mRootVw.findViewById(R.id.recyclerVwSubjects);
        mSubjectsRecyclerVw.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSubjectsRecyclerVw.setHasFixedSize(true);
        mSubjectsRecyclerVw.setAdapter(mSubjectAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtVwJobs:
                launchActivity(JobsActivity.class);
                break;

            case R.id.txtVwBooks:
                launchActivity(BooksActivity.class);
                break;

            case R.id.txtVwVideos:
                launchActivity(VideosActivity.class);
                break;

            case R.id.txtVwExamInfo:
                launchActivity(ExamInformationActivity.class);
                break;

            case R.id.txtVwSave:
                PreferenceUtils.getInstance(getActivity()).putObject(PreferenceUtils.KEY_SELECTED_SUBJECTS, mSelectedSubjects);
                User user = (User) PreferenceUtils.getInstance(getActivity()).getObject(PreferenceUtils.KEY_USER);

                if (null != user && null != user.getId()) {
                    saveSubjects(user.getId());
                }
                break;
        }
    }


    private void getOffers() {
        if (!AppUtils.getInstance().isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(getActivity(), getString(R.string.loading));

        Callback<GenericResponse<List<Offer>>> responseCallback = new Callback<GenericResponse<List<Offer>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Offer>>> call, Response<GenericResponse<List<Offer>>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<List<Offer>> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        List<Offer> offers = genericResponse.getData();
                        if (null != offers && offers.size() > 0) {
                            mOffers.clear();
                            mOffers.addAll(offers);
                            if (null != mOfferAdapter) {
                                mOfferAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<GenericResponse<List<Offer>>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitUtils.getInstance().getService(getActivity()).getOffers().enqueue(responseCallback);
    }

    private void getCourses() {
        if (!AppUtils.getInstance().isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(getActivity(), getString(R.string.loading));

        Callback<GenericResponse<List<Subject>>> responseCallback = new Callback<GenericResponse<List<Subject>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Subject>>> call, Response<GenericResponse<List<Subject>>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<List<Subject>> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        List<Subject> subjects = genericResponse.getData();
                        if (null != subjects && subjects.size() > 0) {
                            mSubjects.clear();
                            mSubjects.addAll(subjects);
                            if (null != mSubjectAdapter) {
                                mSubjectAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<GenericResponse<List<Subject>>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitUtils.getInstance().getService(getActivity()).getCourses().enqueue(responseCallback);
    }

    private void fetchSubjects() {
        if (!AppUtils.getInstance().isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(getActivity(), getString(R.string.loading));

        Callback<GenericResponse<SubjectRequest>> responseCallback = new Callback<GenericResponse<SubjectRequest>>() {
            @Override
            public void onResponse(Call<GenericResponse<SubjectRequest>> call, Response<GenericResponse<SubjectRequest>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<SubjectRequest> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        SubjectRequest subjectResponse = genericResponse.getData();
                        if (null != subjectResponse && null != subjectResponse.getSubjects() && subjectResponse.getSubjects().size() > 0) {
                            mSelectedSubjects.clear();
                            mSelectedSubjects.addAll(subjectResponse.getSubjects());
                        } else {
                            Toast.makeText(getActivity(), genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }

                getCourses();
            }

            @Override
            public void onFailure(Call<GenericResponse<SubjectRequest>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                getCourses();
            }
        };

        User user = (User) PreferenceUtils.getInstance(getActivity()).getObject(PreferenceUtils.KEY_USER);

        if (null != user && null != user.getId()) {
            UserRequest request = new UserRequest();
            request.setUserId(user.getId());
            RetrofitUtils.getInstance().getService(getActivity()).fetchSubjects(request).enqueue(responseCallback);
        } else {
            List<String> subjects = (List) PreferenceUtils.getInstance(getActivity()).getObject(PreferenceUtils.KEY_SELECTED_SUBJECTS);
            if (null != subjects && subjects.size() > 0) {
                mSelectedSubjects.clear();
                mSelectedSubjects.addAll(subjects);
            }
            getCourses();
        }
    }

    private void saveSubjects(String userId) {
        if (!AppUtils.getInstance().isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(getActivity(), getString(R.string.loading));

        Callback<GenericResponse> responseCallback = new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        Toast.makeText(getActivity(), genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        SubjectRequest request = new SubjectRequest();
        request.setUserId(userId);
        request.setSubjects(mSelectedSubjects);
        RetrofitUtils.getInstance().getService(getActivity()).saveSubjects(request).enqueue(responseCallback);
    }
}
