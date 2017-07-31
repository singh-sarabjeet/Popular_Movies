package com.example.sjsingh.popularmovies.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sjsingh.popularmovies.R;
import com.example.sjsingh.popularmovies.adapters.FavoritesAdapter;
import com.example.sjsingh.popularmovies.data.DatabaseContract;

import static com.example.sjsingh.popularmovies.data.DatabaseContract.FavoriteData.COLUMN_BACKDROP;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.FavoriteData.COLUMN_ID;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.FavoriteData.COLUMN_MOVIE_TITLE;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.FavoriteData.COLUMN_PLOT;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.FavoriteData.COLUMN_POSTER;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.FavoriteData.COLUMN_RATING;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.FavoriteData.COLUMN_RELEASE_DATE;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.FavoriteData.COLUMN_REVIEW;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.FavoriteData.COLUMN_TRAILER;

public class Favorites extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    String[] projection = {COLUMN_MOVIE_TITLE, COLUMN_POSTER, COLUMN_PLOT, COLUMN_RATING, COLUMN_RELEASE_DATE, COLUMN_BACKDROP,
            COLUMN_TRAILER, COLUMN_REVIEW, COLUMN_ID};
    private FavoritesAdapter favoritesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        RecyclerView favoritesRecyclerView = (RecyclerView) rootView.findViewById(R.id.favorites_recycler_view);
        favoritesAdapter = new FavoritesAdapter(getActivity());
        favoritesRecyclerView.setAdapter(favoritesAdapter);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 2);
        favoritesRecyclerView.setLayoutManager(glm);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), DatabaseContract.FavoriteData.URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        favoritesAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favoritesAdapter.setCursor(null);
    }
}
