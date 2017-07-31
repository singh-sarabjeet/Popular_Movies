package com.example.sjsingh.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sjsingh.popularmovies.R;
import com.example.sjsingh.popularmovies.ui.DetailActivity;
import com.squareup.picasso.Picasso;

import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_BACKDROP;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_ID;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_MOVIE_TITLE;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_PLOT;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_POSTER;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_RATING;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_RELEASE_DATE;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_REVIEW;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_TRAILER;

/**
 * Created by sarabjeet.singh on 25-07-2017.
 */

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public PopularAdapter(Context context) {
        mContext = context;
    }

    @Override
    public PopularAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_view_element, parent, false);
        final PopularAdapter.ViewHolder vh = new PopularAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(PopularAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Picasso.with(mContext)
                .load(mCursor.getString(mCursor.getColumnIndex(COLUMN_POSTER)))
                .into(holder.poster);
        holder.title.setText(mCursor.getString(mCursor.getColumnIndex(COLUMN_MOVIE_TITLE)));
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mCursor != null) {
            count = mCursor.getCount();
        }
        return count;
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView poster;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.grid_item_image);
            title = (TextView) itemView.findViewById(R.id.grid_item_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCursor.moveToPosition(getAdapterPosition());
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra(mContext.getString(R.string.title_key), mCursor.getString(mCursor.getColumnIndex(COLUMN_MOVIE_TITLE)));
            intent.putExtra(mContext.getString(R.string.poster_key), mCursor.getString(mCursor.getColumnIndex(COLUMN_POSTER)));
            intent.putExtra(mContext.getString(R.string.plot_key), mCursor.getString(mCursor.getColumnIndex(COLUMN_PLOT)));
            intent.putExtra(mContext.getString(R.string.rating_key), mCursor.getString(mCursor.getColumnIndex(COLUMN_RATING)));
            intent.putExtra(mContext.getString(R.string.release_date_key), mCursor.getString(mCursor.getColumnIndex(COLUMN_RELEASE_DATE)));
            intent.putExtra(mContext.getString(R.string.backdrop_key), mCursor.getString(mCursor.getColumnIndex(COLUMN_BACKDROP)));
            intent.putExtra(mContext.getString(R.string.trailers), mCursor.getString(mCursor.getColumnIndex(COLUMN_TRAILER)));
            intent.putExtra(mContext.getString(R.string.reviews), mCursor.getString(mCursor.getColumnIndex(COLUMN_REVIEW)));
            intent.putExtra(mContext.getString(R.string.Id), mCursor.getString(mCursor.getColumnIndex(COLUMN_ID)));
            mContext.startActivity(intent);
        }
    }
}
