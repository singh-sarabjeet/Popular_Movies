package com.example.sjsingh.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sjsingh.popularmovies.adapters.ReviewAdapter;
import com.example.sjsingh.popularmovies.adapters.TrailerAdapter;
import com.example.sjsingh.popularmovies.data.DatabaseContract;
import com.example.sjsingh.popularmovies.data.DatabaseHelper;
import com.example.sjsingh.popularmovies.items.GridItem;
import com.example.sjsingh.popularmovies.items.ReviewItem;
import com.example.sjsingh.popularmovies.items.TrailerItem;
import com.like.LikeButton;
import com.like.OnLikeListener;
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

/**
 * Created by Sarabjeet Singh on 26-12-2016.
 */

public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

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
    String id;
    ImageView poster_image;
    ImageView backdrop_image;
    String review;
    View rootView;
    private RecyclerView mRecyclerView;
    private TrailerAdapter mAdapter;
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


        Intent intent = getActivity().getIntent();

        if (!haveNetworkConnection())
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        if (intent != null) {

            rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            title = intent.getStringExtra(getString(R.string.title_key));
            rating = intent.getStringExtra(getString(R.string.rating_key));
            r_date = intent.getStringExtra(getString(R.string.release_date_key));
            plot = intent.getStringExtra(getString(R.string.plot_key));
            poster = intent.getStringExtra(getString(R.string.poster_key));
            backdrop = intent.getStringExtra(getString(R.string.backdrop_key));
            review = intent.getStringExtra(getString(R.string.reviews));
            trailer = intent.getStringExtra(getString(R.string.trailers));
            id = intent.getStringExtra(getString(R.string.Id));

            String rDateFinal = "Released:" + r_date;
            String ratingFinal = rating + "/10";

            ((TextView) rootView.findViewById(R.id.original_title_textview))
                    .setText(title);
            ((TextView) rootView.findViewById(R.id.user_rating_textview))
                    .setText(ratingFinal);
            ((TextView) rootView.findViewById(R.id.release_date_textview))
                    .setText(rDateFinal);
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

            LikeButton likeButton = (LikeButton) rootView.findViewById(R.id.like_button);
            DatabaseHelper dbh = new DatabaseHelper(getActivity());
            if (dbh.isExist(id)) {
                likeButton.setLiked(true);
            }
            likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    DatabaseHelper db = new DatabaseHelper(getActivity());
                    if (!(db.isExist(title))) {
                        GridItem item = new GridItem();
                        item.setTitle(title);
                        item.setReview(review);
                        item.setImage(poster);
                        item.setPlot(plot);
                        item.setRating(rating);
                        item.setReleaseDate(r_date);
                        item.setBackdrop(backdrop);
                        item.setTrailer(trailer);
                        item.setId(id);
                        db.addMovie(item, DatabaseContract.FavoriteData.TABLE_NAME);

                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    DatabaseHelper db = new DatabaseHelper(getActivity());
                    db.deleteEntry(id);

                }
            });
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

            //New request for fetching the review data

            rRecyclerView = (RecyclerView) rootView.findViewById(R.id.review_recycle_view);
            rLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rRecyclerView.setHasFixedSize(true);
            rRecyclerView.setLayoutManager(rLayoutManager);
            rRecyclerView.setItemAnimator(new DefaultItemAnimator());

        }
        return rootView;
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
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

        @Override
        protected void onPostExecute(ArrayList<ReviewItem> results) {

            rAdapter = new ReviewAdapter(reviewData, getContext());
            rRecyclerView.setAdapter(rAdapter);
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

        @Override
        protected void onPostExecute(ArrayList<TrailerItem> results) {
            mAdapter = new TrailerAdapter(myDataSet, getContext());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new TrailerAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    TrailerItem item = myDataSet.get(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getTrailer()));
                    startActivity(intent);
                }
            });
        }


    }


}
