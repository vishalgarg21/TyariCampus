package com.tyari.campus.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tyari.campus.R;
import com.tyari.campus.model.Book;
import com.tyari.campus.utils.LogUtils;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private static final String TAG = LogUtils.makeLogTag(BookAdapter.class);
    private Activity mActivity;
    private List<Book> mBooks;

    public BookAdapter(Activity activity, List<Book> books) {
        mActivity = activity;
        mBooks = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.recycler_view_item_job, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = mBooks.get(position);

        holder.mTitleTxtVw.setText(book.getTitle());
        holder.mDateTxtVw.setText(book.getUpdated());
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTxtVw;
        private TextView mDateTxtVw;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleTxtVw = itemView.findViewById(R.id.txtVwTitle);
            mDateTxtVw = itemView.findViewById(R.id.txtVwDate);
        }
    }
}
