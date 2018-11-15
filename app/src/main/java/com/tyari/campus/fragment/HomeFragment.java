package com.tyari.campus.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.activity.BooksActivity;
import com.tyari.campus.activity.JobsActivity;
import com.tyari.campus.activity.VideosActivity;
import com.tyari.campus.adapter.OfferAdapter;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.Offer;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
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

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootVw = inflater.inflate(R.layout.fragment_home, container, false);

        initUi();
        return mRootVw;
    }

    @Override
    public void onResume() {
        super.onResume();
        getOffers();
    }

    private void initUi() {
        TextView mJobsTxtVw = mRootVw.findViewById(R.id.txtVwJobs);
        TextView mBooksTxtVw = mRootVw.findViewById(R.id.txtVwBooks);
        TextView mVideosTxtVw = mRootVw.findViewById(R.id.txtVwVideos);

        TextView mInfo1TxtVw = mRootVw.findViewById(R.id.txtVwInfo1);
        TextView mInfo2TxtVw = mRootVw.findViewById(R.id.txtVwInfo2);
        TextView mInfo3TxtVw = mRootVw.findViewById(R.id.txtVwInfo3);
        TextView mInfo4TxtVw = mRootVw.findViewById(R.id.txtVwInfo4);
        TextView mInfo5TxtVw = mRootVw.findViewById(R.id.txtVwInfo5);
        TextView mInfo6TxtVw = mRootVw.findViewById(R.id.txtVwInfo6);

        mJobsTxtVw.setOnClickListener(this);
        mBooksTxtVw.setOnClickListener(this);
        mVideosTxtVw.setOnClickListener(this);

        mInfo1TxtVw.setOnClickListener(this);
        mInfo2TxtVw.setOnClickListener(this);
        mInfo3TxtVw.setOnClickListener(this);
        mInfo4TxtVw.setOnClickListener(this);
        mInfo5TxtVw.setOnClickListener(this);
        mInfo6TxtVw.setOnClickListener(this);

        mOffers = new ArrayList<>();
        mOfferAdapter = new OfferAdapter(getActivity(), mOffers);
        RecyclerView mOffersRecyclerVw = mRootVw.findViewById(R.id.recyclerVwOffers);
        mOffersRecyclerVw.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mOffersRecyclerVw.setHasFixedSize(true);
        mOffersRecyclerVw.setAdapter(mOfferAdapter);
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

            case R.id.txtVwInfo1:
                launchWeViewActivity(getString(R.string.exam_information_url_tgt_pgt));
                break;

            case R.id.txtVwInfo2:
                launchWeViewActivity(getString(R.string.exam_information_url_ctet));
                break;

            case R.id.txtVwInfo3:
                launchWeViewActivity(getString(R.string.exam_information_url_ssc));
                break;

            case R.id.txtVwInfo4:
                launchWeViewActivity(getString(R.string.exam_information_url_ibps));
                break;

            case R.id.txtVwInfo5:
                launchWeViewActivity(getString(R.string.exam_information_url_nda));
                break;

            case R.id.txtVwInfo6:
                launchWeViewActivity(getString(R.string.exam_information_url_vdo));
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
}
