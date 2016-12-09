package com.example.sjsingh.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Sarabjeet Singh on 04-12-2016.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    Context context;
    private ArrayList<TrailerItem> dataSet;

    public TrailerAdapter(ArrayList<TrailerItem> data, Context context) {

        this.dataSet = data;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.trailer_cardview_element, parent, false);


        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {


        TrailerItem item = new TrailerItem();
        Context context = holder.imageViewIcon.getContext();
        Picasso.with(context)
                .load(item.getImage())
                .into(holder.imageViewIcon);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.trailer_thumbnail);
        }
    }
}