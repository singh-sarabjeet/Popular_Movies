package com.example.sjsingh.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    Context context;
    private ArrayList<ReviewItem> dataSet;

    public ReviewAdapter(ArrayList<ReviewItem> data, Context context) {

        this.dataSet = data;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.review_cardview_element, parent, false);


        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int listPosition) {


        ReviewItem item = dataSet.get(listPosition);
        holder.author.setText(item.getAuthor());
        holder.review.setText(item.getReview());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView author;
        public TextView review;

        public ViewHolder(View itemView) {
            super(itemView);
            this.author = (TextView) itemView.findViewById(R.id.reviewer_name_textView);
            this.review = (TextView) itemView.findViewById(R.id.review_textView);
        }
    }
}
