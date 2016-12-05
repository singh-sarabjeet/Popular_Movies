package com.example.sjsingh.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        String title;
        String rating;
        String r_date;
        String plot;
        String poster;
        String backdrop;
        String trailer;
        ImageView poster_image;
        ImageView backdrop_image;
        private RecyclerView mRecyclerView;
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;


        public DetailFragment() {
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

                trailer = intent.getStringExtra("Trailer");

                Log.d(LOG_TAG, trailer);
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

                // Trailers
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailer_recycle_view);
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);

                mAdapter = new TrailerAdapter(myDataSet);
                mRecyclerView.setAdapter(mAdapter);



            }
            return rootView;
        }

        /* EXPERIMENTAL*/

    /*    public class FetchTrailer extends AsyncTask<Void, Void, ArrayList<GridItem>> {


            @Override
            protected ArrayList<GridItem> doInBackground(Void... params) {


                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String movieJSONStr = null;
                String BASE_URL = trailer;


                    final String API_KEY = "api_key";

                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter(API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY).build();
//todo: Get the youtube id from the response and create a link for the trailer
todo: Do the same for the reviews
                    URL url = new URL(builtUri.toString());

                    Log.d(LOG_TAG,url.toString());

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

            */


    }
}
