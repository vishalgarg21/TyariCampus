package com.tyari.campus.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tyari.campus.R;
import com.tyari.campus.model.Offer;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private Activity mActivity;
    private List<Offer> mOffers;

    public OfferAdapter(Activity activity, List<Offer> offers) {
        mActivity = activity;
        mOffers = offers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.recycler_view_item_offer, parent, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        view.getLayoutParams().width = (int) (width * .99f);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Offer offer = mOffers.get(position);
        //TODO: Need to change
        if (position == 0) {
            holder.mOfferImgVw.setImageResource(R.drawable.offer);
        } else {
            Picasso.with(mActivity).load(offer.getImage()).placeholder(R.drawable.ic_offer_place_holder).error(R.drawable.ic_offer_place_holder).fit().into(holder.mOfferImgVw);
        }
    }

    @Override
    public int getItemCount() {
        return mOffers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mOfferImgVw;

        public ViewHolder(View itemView) {
            super(itemView);
            mOfferImgVw = itemView.findViewById(R.id.imgVwOffer);
        }
    }
}
