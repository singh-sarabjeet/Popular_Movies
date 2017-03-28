package com.example.sjsingh.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.sjsingh.popularmovies.R;
import com.example.sjsingh.popularmovies.items.TrailerItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Sarabjeet Singh on 04-12-2016.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private static ClickListener clickListener;
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
    public void onBindViewHolder(ViewHolder holder, int listPosition) {


        TrailerItem item = dataSet.get(listPosition);
        Context context = holder.imageViewIcon.getContext();
        Picasso.with(context)
                .load(item.getImage())
                .into(holder.imageViewIcon);


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        TrailerAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageViewIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.trailer_thumbnail);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }


    }
}

