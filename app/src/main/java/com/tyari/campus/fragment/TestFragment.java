package com.tyari.campus.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.adapter.PackageAdapter;
import com.tyari.campus.common.Constants;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.GetTestRequest;
import com.tyari.campus.model.PackageInfo;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.PreferenceUtils;
import com.tyari.campus.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestFragment extends BaseFragment {
    private static final String TAG = LogUtils.makeLogTag(TestFragment.class);

    private View mRootVw;
    private List<PackageInfo> mPackageInfoList;
    private PackageAdapter mPackageAdapter;

    public TestFragment() {
    }

    public static TestFragment newInstance() {
        return new TestFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootVw = inflater.inflate(R.layout.fragment_test, container, false);
        initUi();
        return mRootVw;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<String> subjects = (List) PreferenceUtils.getInstance(getActivity()).getObject(PreferenceUtils.KEY_SELECTED_SUBJECTS);
        if (null != subjects && subjects.size() > 0) {
            GetTestRequest request = new GetTestRequest();
            request.setSubjects(subjects);
            getTests(request);
        }
    }

    private void initUi() {
        mPackageInfoList = new ArrayList<>();
        RecyclerView mPackageRecyclerVw = mRootVw.findViewById(R.id.recyclerVwPackage);
        mPackageAdapter = new PackageAdapter(getActivity(), mPackageInfoList);
        mPackageRecyclerVw.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPackageRecyclerVw.setHasFixedSize(true);
        mPackageRecyclerVw.setAdapter(mPackageAdapter);
    }

    private void getTests(GetTestRequest request) {
        if (!AppUtils.getInstance().isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(getActivity(), getString(R.string.loading));

        Callback<GenericResponse<List<PackageInfo>>> responseCallback = new Callback<GenericResponse<List<PackageInfo>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<PackageInfo>>> call, Response<GenericResponse<List<PackageInfo>>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<List<PackageInfo>> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        List<PackageInfo> packageInfoList = genericResponse.getData();
                        if (null != packageInfoList && packageInfoList.size() > 0) {
                            mPackageInfoList.clear();
                            mPackageInfoList.addAll(packageInfoList);
                            if (null != mPackageAdapter) {
                                mPackageAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<GenericResponse<List<PackageInfo>>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitUtils.getInstance().getService(getActivity()).getTests(request).enqueue(responseCallback);
    }
}
