package com.example.sjsingh.popularmovies;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
 * Created by Sarabjeet Singh on 05-09-2016.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private GridView mGridView;
    private ProgressBar mProgressBar;
    private ImageListAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview_poster);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mGridData = new ArrayList<>();
        mGridAdapter = new ImageListAdapter(getActivity(), mGridData);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "detail Activity working", Toast.LENGTH_SHORT).show();
            }
        });

//Starting download
        updateData();

        return rootView;

    }

    public void updateData() {

        mGridAdapter.clear();
        new FetchMovie().execute();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();
    }

    private ArrayList<GridItem> formatDataFromJson(String movieJsonStr) throws JSONException {

        final String MOVIE_RESULTS = "results";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_SYNOPSIS = "overview";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(MOVIE_RESULTS);


        GridItem item;

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieResultObj = movieArray.getJSONObject(i);

            String title = movieResultObj.getString(MOVIE_TITLE);
            String synopsis = movieResultObj.getString(MOVIE_SYNOPSIS);
            String poster_path = movieResultObj.getString(MOVIE_POSTER_PATH);
            String rating = movieResultObj.getString(MOVIE_RATING);
            String release_date = movieResultObj.getString(MOVIE_RELEASE_DATE);

            String POSTER_URL = IMAGE_BASE_URL + poster_path;

            item = new GridItem();
            item.setTitle(title);
            item.setImage(POSTER_URL);
            Log.v(LOG_TAG, POSTER_URL);
            mGridData.add(item);

        }

        Log.v(LOG_TAG, "mGridData Received");
        return mGridData;


    }

    public class FetchMovie extends AsyncTask<Void, Void, ArrayList<GridItem>> {

        @Override
        protected ArrayList<GridItem> doInBackground(Void... params) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJSONStr = null;
            String BASE_URL;

            try {

                SharedPreferences sharedPrefs =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                String orderType = sharedPrefs.getString(
                        getString(R.string.pref_order_key),
                        getString(R.string.pref_most_popular));


                if (orderType.equals(getString(R.string.pref_top_rated))) {
                    BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
                } else {
                    BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
                }

                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY).build();


                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    Log.v(LOG_TAG, "inputStream is null");
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
                Log.v(LOG_TAG, movieJSONStr);


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
                Log.v(LOG_TAG, "results received");
                ArrayList<GridItem> results = formatDataFromJson(movieJSONStr);

                return results;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<GridItem> result) {

            Log.v(LOG_TAG, "onPOstExecute is executed");
            if (result != null) {

                mGridAdapter.setGridData(result);
                mProgressBar.setVisibility(View.GONE);


            }
        }
    }

}

