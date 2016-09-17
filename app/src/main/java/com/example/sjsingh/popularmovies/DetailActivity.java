package com.example.sjsingh.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

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

        String title;
        String rating;
        String r_date;
        String plot;
        String poster;
        String backdrop;
        ImageView poster_image;
        ImageView backdrop_image;

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
            return rootView;
        }

    }
}
