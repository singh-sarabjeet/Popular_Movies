package com.example.sjsingh.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sarabjeet Singh on 14-12-2016.
 */

public class DatabaseContract {

    public static final String AUTHORITY = "com.example.sjsingh.popularmovies";
    public static final String PATH_POPULAR = "popularMovie";
    public static final String PATH_TOP_RATED = "top_movie";
    public static final String PATH_FAV = "fav_movie";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final class PopularMovieData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_POPULAR).build();

        public static final String TABLE_NAME = "popularMovie";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_BACKDROP = "backdrop";
        public static final String COLUMN_TRAILER = "trailer";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_ID = "id";
    }

    public static final class TopMovieData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_TOP_RATED).build();

        public static final String TABLE_NAME = "top_movie";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_BACKDROP = "backdrop";
        public static final String COLUMN_TRAILER = "trailer";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_ID = "id";
    }

    public static final class FavoriteData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_FAV).build();

        public static final String TABLE_NAME = "fav_movie";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_BACKDROP = "backdrop";
        public static final String COLUMN_TRAILER = "trailer";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_ID = "id";
    }
}
