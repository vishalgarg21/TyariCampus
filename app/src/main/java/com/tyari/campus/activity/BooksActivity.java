package com.tyari.campus.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.tyari.campus.R;
import com.tyari.campus.adapter.BookAdapter;
import com.tyari.campus.common.Constants;
import com.tyari.campus.common.RecyclerItemClickListener;
import com.tyari.campus.model.Book;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.Job;
import com.tyari.campus.utils.AppUtils;
import com.tyari.campus.utils.LogUtils;
import com.tyari.campus.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BooksActivity extends BaseActivity{
    private static final String TAG = LogUtils.makeLogTag(BooksActivity.class);

    private List<Book> mBooks;
    private BookAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        mBooks = new ArrayList<>();
        mAdapter = new BookAdapter(this, mBooks);
        RecyclerView mRecyclerVw = findViewById(R.id.recyclerVwBooks);
        mRecyclerVw.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerVw.setHasFixedSize(true);
        mRecyclerVw.setAdapter(mAdapter);
        mRecyclerVw.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                launchWeViewActivity(mBooks.get(position).getUrl());
            }
        }));

        getBooks();
    }

    private void getBooks() {
        if (!AppUtils.getInstance().isInternetAvailable(this)) {
            Toast.makeText(this, getString(R.string.please_check_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        AppUtils.showProgress(this, getString(R.string.loading));

        Callback<GenericResponse<List<Book>>> responseCallback = new Callback<GenericResponse<List<Book>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Book>>> call, Response<GenericResponse<List<Book>>> response) {
                LogUtils.checkIf(TAG, "Response: " + response);
                AppUtils.dismissProgress();

                if (response != null && response.isSuccessful()) {
                    GenericResponse<List<Book>> genericResponse = response.body();
                    if (genericResponse == null) {
                        Toast.makeText(BooksActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (genericResponse.getCode() == Constants.STATUS_SUCCESS) {
                        List<Book> books = genericResponse.getData();
                        if (null != books && books.size() > 0) {
                            mBooks.addAll(books);
                            if (null != mAdapter) {
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(BooksActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(BooksActivity.this, genericResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(BooksActivity.this, getString(R.string.server_down_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Book>>> call, Throwable t) {
                LogUtils.checkIf(TAG, "Throwable: " + t.toString());
                AppUtils.dismissProgress();
                Toast.makeText(BooksActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitUtils.getInstance().getService(this).getBooks().enqueue(responseCallback);
    }
}
