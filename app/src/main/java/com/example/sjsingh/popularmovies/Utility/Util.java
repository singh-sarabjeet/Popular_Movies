package com.example.sjsingh.popularmovies.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.sjsingh.popularmovies.BuildConfig;
import com.example.sjsingh.popularmovies.data.DatabaseContract;

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
import java.util.List;

import static com.example.sjsingh.popularmovies.Utility.Constants.POPULAR;
import static com.example.sjsingh.popularmovies.Utility.Constants.TOP_RATED;
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

public class Util {

    private String BASE_URL;
    private Context mContext;
    private String mTableType;

    public Util(String url, Context context, String tableType) {
        BASE_URL = url;
        mContext = context;
        mTableType = tableType;
    }

    public Util() {

    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private void formatDataFromJson(String movieJsonStr) throws JSONException {

        final String MOVIE_RESULTS = "results";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_SYNOPSIS = "overview";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String BACKDROP = "backdrop_path";
        final String ID = "id";
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";
        final String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w780/";
        final String TRAILER_BASE_URL = "http://api.themoviedb.org/3/movie/";


        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(MOVIE_RESULTS);

        if (mTableType.equals(POPULAR)) {
            mContext.getContentResolver().delete(DatabaseContract.PopularMovieData.URI, null, null);
        } else {
            mContext.getContentResolver().delete(DatabaseContract.TopMovieData.URI, null, null);
        }
        List<ContentValues> cvList = new ArrayList<>();
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieResultObj = movieArray.getJSONObject(i);

            String title = movieResultObj.getString(MOVIE_TITLE);

            String poster_path = movieResultObj.getString(MOVIE_POSTER_PATH);
            String backdrop_path = movieResultObj.getString(BACKDROP);

            String synopsis = movieResultObj.getString(MOVIE_SYNOPSIS);
            String rating = movieResultObj.getString(MOVIE_RATING);
            String release_date = movieResultObj.getString(MOVIE_RELEASE_DATE);
            String id = movieResultObj.getString(ID);

            String POSTER_URL = POSTER_BASE_URL + poster_path;
            String BACKDROP_URL = BACKDROP_BASE_URL + backdrop_path;
            String TRAILER_BASE = TRAILER_BASE_URL + id + "/videos?";
            String REVIEW_BASE = TRAILER_BASE_URL + id + "/reviews?";

            ContentValues values = new ContentValues();
            switch (mTableType) {
                case POPULAR:
                    values.put(COLUMN_MOVIE_TITLE, title);
                    values.put(COLUMN_POSTER, POSTER_URL);
                    values.put(COLUMN_BACKDROP, BACKDROP_URL);
                    values.put(COLUMN_PLOT, synopsis);
                    values.put(COLUMN_RATING, rating);
                    values.put(COLUMN_RELEASE_DATE, release_date);
                    values.put(COLUMN_TRAILER, TRAILER_BASE);
                    values.put(COLUMN_REVIEW, REVIEW_BASE);
                    values.put(COLUMN_ID, id);
                    cvList.add(values);
                    break;
                case TOP_RATED:
                    values.put(DatabaseContract.TopMovieData.COLUMN_MOVIE_TITLE, title);
                    values.put(DatabaseContract.TopMovieData.COLUMN_POSTER, POSTER_URL);
                    values.put(DatabaseContract.TopMovieData.COLUMN_BACKDROP, BACKDROP_URL);
                    values.put(DatabaseContract.TopMovieData.COLUMN_PLOT, synopsis);
                    values.put(DatabaseContract.TopMovieData.COLUMN_RATING, rating);
                    values.put(DatabaseContract.TopMovieData.COLUMN_RELEASE_DATE, release_date);
                    values.put(DatabaseContract.TopMovieData.COLUMN_TRAILER, TRAILER_BASE);
                    values.put(DatabaseContract.TopMovieData.COLUMN_REVIEW, REVIEW_BASE);
                    values.put(DatabaseContract.TopMovieData.COLUMN_ID, id);
                    cvList.add(values);
                    break;
            }
        }
        ContentValues[] cv = cvList.toArray(new ContentValues[cvList.size()]);
        switch (mTableType) {
            case POPULAR:
                mContext.getContentResolver().bulkInsert(DatabaseContract.PopularMovieData.URI, cv);
                Log.d("alert", "data inserted");
                break;
            case TOP_RATED:
                mContext.getContentResolver().bulkInsert(DatabaseContract.TopMovieData.URI, cv);
                Log.d("alert", "data inserted in top");
                break;
        }

    }

    public class FetchMovie extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("alert", "In doinBackground");
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJSONStr = null;

            try {
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY).build();

                URL url = new URL(builtUri.toString());

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
                    return null;
                }
                movieJSONStr = buffer.toString();

            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Error", "Error Closing Stream", e);
                    }
                }
            }

            try {
                formatDataFromJson(movieJSONStr);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

    }
}
