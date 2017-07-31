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

import com.airbnb.lottie.LottieAnimationView;
import com.example.sjsingh.popularmovies.R;
import com.example.sjsingh.popularmovies.adapters.PopularAdapter;
import com.example.sjsingh.popularmovies.data.DatabaseContract;

import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_BACKDROP;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_ID;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_MOVIE_TITLE;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_PLOT;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_POSTER;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_RATING;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_RELEASE_DATE;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_REVIEW;
import static com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData.COLUMN_TRAILER;


public class Popular extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    String[] projection = {COLUMN_MOVIE_TITLE, COLUMN_POSTER, COLUMN_PLOT, COLUMN_RATING, COLUMN_RELEASE_DATE, COLUMN_BACKDROP,
            COLUMN_TRAILER, COLUMN_REVIEW, COLUMN_ID};
    LottieAnimationView loaderView;
    private PopularAdapter popularAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular, container, false);
        RecyclerView popularRecyclerView = (RecyclerView) rootView.findViewById(R.id.popular_recycler_view);
        popularAdapter = new PopularAdapter(getActivity());
        popularRecyclerView.setAdapter(popularAdapter);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 2);
        popularRecyclerView.setLayoutManager(glm);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //  loaderView = (LottieAnimationView) getActivity().findViewById(R.id.loader);
        //    loaderView.setAnimation("video_cam.json");
        //   loaderView.loop(true);
        //    loaderView.playAnimation();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), DatabaseContract.PopularMovieData.URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //  loaderView.setVisibility(View.GONE);
        popularAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        popularAdapter.setCursor(null);
    }

}
