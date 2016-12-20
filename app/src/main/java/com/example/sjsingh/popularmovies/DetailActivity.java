package com.example.sjsingh.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);


    }

    public static class DetailFragment extends Fragment {

        private static ArrayList<TrailerItem> myDataSet
                = new ArrayList<TrailerItem>();
        private static ArrayList<ReviewItem> reviewData
                = new ArrayList<ReviewItem>();
        String title;
        String rating;
        String r_date;
        String plot;
        String poster;
        String backdrop;
        String trailer;
        ImageView poster_image;
        ImageView backdrop_image;
        String review;
        private RecyclerView mRecyclerView;
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;

        private RecyclerView rRecyclerView;
        private RecyclerView.Adapter rAdapter;
        private RecyclerView.LayoutManager rLayoutManager;


        public DetailFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            if (intent != null) {
                title = intent.getStringExtra(getString(R.string.title_key));
                rating = intent.getStringExtra(getString(R.string.rating_key));
                r_date = intent.getStringExtra(getString(R.string.release_date_key));
                plot = intent.getStringExtra(getString(R.string.plot_key));
                poster = intent.getStringExtra(getString(R.string.poster_key));
                backdrop = intent.getStringExtra(getString(R.string.backdrop_key));
                review = intent.getStringExtra(getString(R.string.reviews));

                trailer = intent.getStringExtra(getString(R.string.trailers));


                r_date = "Released:" + r_date;
                rating = rating + "/10";

                ((TextView) rootView.findViewById(R.id.original_title_textview))
                        .setText(title);
                ((TextView) rootView.findViewById(R.id.user_rating_textview))
                        .setText(rating);
                ((TextView) rootView.findViewById(R.id.release_date_textview))
                        .setText(r_date);
                ((TextView) rootView.findViewById(R.id.plot_textview))
                        .setText(plot);


                poster_image = (ImageView) rootView.findViewById(R.id.poster);
                backdrop_image = (ImageView) rootView.findViewById(R.id.backdrop);

                Picasso.with(getContext())
                        .load(poster)
                        .into(poster_image);

                Picasso.with(getContext())
                        .load(backdrop)
                        .into(backdrop_image);

            }
            myDataSet.clear();
            reviewData.clear();
            // New request for fetching the trailer data
            new FetchReviews(review).execute();
            new FetchTrailers(trailer).execute();

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailer_recycle_view);
            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());


            mAdapter = new TrailerAdapter(myDataSet, getContext());


            //New request for fetching the review data

            rRecyclerView = (RecyclerView) rootView.findViewById(R.id.review_recycle_view);
            rLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rRecyclerView.setHasFixedSize(true);
            rRecyclerView.setLayoutManager(rLayoutManager);
            rRecyclerView.setItemAnimator(new DefaultItemAnimator());


            rAdapter = new ReviewAdapter(reviewData, getContext());

            mRecyclerView.setAdapter(mAdapter);
            rRecyclerView.setAdapter(rAdapter);

            return rootView;
        }

        private ArrayList<TrailerItem> formatTrailerDataFromJson(String movieJsonStr) throws JSONException {

            final String MOVIE_RESULTS = "results";
            final String KEY = "key";


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_RESULTS);


            TrailerItem item;

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieResultObj = movieArray.getJSONObject(i);


                String key = movieResultObj.getString(KEY);

                String thumbNail = "https://img.youtube.com/vi/" + key + "/mqdefault.jpg";
                String trailerUrl = "https://www.youtube.com/watch?v=" + key;


                item = new TrailerItem();

                item.setImage(thumbNail);
                item.setTrailer(trailerUrl);


                Log.v(LOG_TAG, thumbNail);
                myDataSet.add(item);

            }

            return myDataSet;

        }

        private ArrayList<ReviewItem> formatReviewDataFromJson(String movieJsonStr) throws JSONException {

            final String MOVIE_RESULTS = "results";
            final String AUTHOR = "author";
            final String CONTENT = "content";


            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_RESULTS);


            ReviewItem item;

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieResultObj = movieArray.getJSONObject(i);


                String author = movieResultObj.getString(AUTHOR);
                String content = movieResultObj.getString(CONTENT);


                item = new ReviewItem();

                item.setAuthor(author);
                item.setReview(content);


                reviewData.add(item);

            }

            return reviewData;

        }

        public class FetchReviews extends AsyncTask<Void, Void, ArrayList<ReviewItem>> {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJSONStr = null;
            String BASE_URL;

            public FetchReviews(String searchUrl) {
                BASE_URL = searchUrl;
            }


            @Override
            protected ArrayList<ReviewItem> doInBackground(Void... params) {

                try {

                    final String API_KEY = "api_key";

                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter(API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY).build();


                    URL url = new URL(builtUri.toString());

                    Log.d(LOG_TAG, url.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {

                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }

                    if (buffer.length() == 0) {
                        Log.v(LOG_TAG, "Buffer Length is null");
                        return null;
                    }
                    movieJSONStr = buffer.toString();

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error Closing Stream", e);
                        }
                    }
                }
                try {

                    ArrayList<ReviewItem> results = formatReviewDataFromJson(movieJSONStr);
                    return results;
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }


                return null;
            }

        }

        public class FetchTrailers extends AsyncTask<Void, Void, ArrayList<TrailerItem>> {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJSONStr = null;
            String BASE_URL;

            public FetchTrailers(String searchUrl) {
                BASE_URL = searchUrl;
            }

            @Override
            protected ArrayList<TrailerItem> doInBackground(Void... params) {

                try {

                    final String API_KEY = "api_key";

                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter(API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY).build();


                    URL url = new URL(builtUri.toString());

                    Log.d(LOG_TAG, url.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {

                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }

                    if (buffer.length() == 0) {
                        Log.v(LOG_TAG, "Buffer Length is null");
                        return null;
                    }
                    movieJSONStr = buffer.toString();

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error Closing Stream", e);
                        }
                    }
                }
                try {

                    ArrayList<TrailerItem> results = formatTrailerDataFromJson(movieJSONStr);
                    return results;
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }


                return null;
            }

        }


    }
}
