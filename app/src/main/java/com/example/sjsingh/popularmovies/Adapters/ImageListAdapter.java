package com.example.sjsingh.popularmovies.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sjsingh.popularmovies.R;
import com.example.sjsingh.popularmovies.items.GridItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Sarabjeet Singh on 07-09-2016.
 */
public class ImageListAdapter extends ArrayAdapter<GridItem> {

    private Context mContext;

    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();

    public ImageListAdapter(Context mContext, ArrayList<GridItem> mGridData) {
        super(mContext, 0, mGridData);
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    public void setGridData(ArrayList<GridItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        ViewHolder holder;

        if (rootView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            rootView = inflater.inflate(R.layout.grid_view_element, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) rootView.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) rootView.findViewById(R.id.grid_item_image);
            rootView.setTag(holder);
        } else {
            holder = (ViewHolder) rootView.getTag();
        }

        GridItem item = mGridData.get(position);
        holder.titleTextView.setText(item.getTitle());

        Picasso.with(mContext)
                .load(item.getImage())
                .into(holder.imageView);
        return rootView;
    }

    public int getCount() {
        return mGridData.size();
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }
}

