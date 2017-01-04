package com.example.sjsingh.popularmovies;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sjsingh.popularmovies.data.DatabaseContract;
import com.example.sjsingh.popularmovies.data.DatabaseHelper;

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

    static final int SORT_ORDER_REPLY = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    DatabaseHelper db;
    String BASE_URL;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private ImageListAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private String TABLE_NAME;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i("fragment", "onCreate");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            startActivityForResult(settingsIntent, SORT_ORDER_REPLY);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SORT_ORDER_REPLY) {

            updateData();
            Log.i("fragment", "onActivityResult");
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("fragment", "onAttach");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("fragment", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("fragment", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();

        mGridAdapter.clear();
        Log.i("fragment", "onStop");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i("fragment", "onStart");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Log.i("fragment", "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview_poster);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        db = new DatabaseHelper(getActivity());

        mGridData = new ArrayList<>();

        if (!haveNetworkConnection()) {
            checkSharedPreferences();
            mGridData = db.getAllMovies(TABLE_NAME);
        }

        mGridAdapter = new ImageListAdapter(getActivity(), mGridData);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridItem item = mGridData.get(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(getString(R.string.title_key), item.getTitle());
                intent.putExtra(getString(R.string.plot_key), item.getPlot());
                intent.putExtra(getString(R.string.rating_key), item.getRating());
                intent.putExtra(getString(R.string.release_date_key), item.getReleaseDate());
                intent.putExtra(getString(R.string.poster_key), item.getImage());
                intent.putExtra(getString(R.string.backdrop_key), item.getBackdrop());
                intent.putExtra(getString(R.string.trailers), item.getTrailer());
                intent.putExtra(getString(R.string.reviews), item.getReview());
                intent.putExtra("Id", item.getId());
                startActivity(intent);
            }
        });

        updateData();
        Log.v(LOG_TAG, "In on create view");
        return rootView;

    }

    public void updateData() {

        checkSharedPreferences();

        if (!haveNetworkConnection()) {
            mGridData = db.getAllMovies(TABLE_NAME);
            mGridAdapter.setGridData(mGridData);
            mGridView.setAdapter(mGridAdapter);
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        } else if (TABLE_NAME.equals(DatabaseContract.FavoriteData.TABLE_NAME)) {

            mGridData = db.getAllMovies(TABLE_NAME);
            Log.v(LOG_TAG, "IN Favorite block");
            mGridAdapter.setGridData(mGridData);
            mGridView.setAdapter(mGridAdapter);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mGridAdapter.clear();
            //  Toast.makeText(getActivity(), "Downloading Data", Toast.LENGTH_SHORT).show();
            new FetchMovie().execute();
            mProgressBar.setVisibility(View.VISIBLE);

        }
        }


    private boolean haveNetworkConnection() {
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

    private ArrayList<GridItem> formatDataFromJson(String movieJsonStr) throws JSONException {

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

        db.deleteEntries(TABLE_NAME);
        GridItem item;

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


            item = new GridItem();

            item.setTitle(title);
            item.setReview(REVIEW_BASE);
            item.setImage(POSTER_URL);
            item.setPlot(synopsis);
            item.setRating(rating);
            item.setReleaseDate(release_date);
            item.setBackdrop(BACKDROP_URL);
            item.setTrailer(TRAILER_BASE);
            item.setId(id);

            mGridData.add(item);


            db.addMovie(item, TABLE_NAME);

        }

        return mGridData;

    }

    public void checkSharedPreferences() {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String orderType = sharedPrefs.getString(
                getString(R.string.pref_order_key),
                getString(R.string.pref_most_popular));


        if (orderType.equals(getString(R.string.pref_top_rated))) {
            TABLE_NAME = DatabaseContract.TopMovieData.TABLE_NAME;
            BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
        } else if (orderType.equals(getString(R.string.pref_most_popular))) {
            TABLE_NAME = DatabaseContract.PopularMovieData.TABLE_NAME;
            BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
        } else {
            TABLE_NAME = DatabaseContract.FavoriteData.TABLE_NAME;
        }
    }

    public class FetchMovie extends AsyncTask<Void, Void, ArrayList<GridItem>> {


        @Override
        protected ArrayList<GridItem> doInBackground(Void... params) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJSONStr = null;


            try {

                checkSharedPreferences();


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

            if (result != null) {

                mGridAdapter.setGridData(result);
            }
            mProgressBar.setVisibility(View.GONE);
        }
    }
}



