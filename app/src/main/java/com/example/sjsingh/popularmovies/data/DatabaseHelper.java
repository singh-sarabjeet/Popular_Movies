package com.example.sjsingh.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sjsingh.popularmovies.Items.GridItem;
import com.example.sjsingh.popularmovies.data.DatabaseContract.FavoriteData;
import com.example.sjsingh.popularmovies.data.DatabaseContract.PopularMovieData;
import com.example.sjsingh.popularmovies.data.DatabaseContract.TopMovieData;

import java.util.ArrayList;

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
 * Created by Sarabjeet Singh on 14-12-2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Movie_database.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_POPULAR_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS " + PopularMovieData.TABLE_NAME + " (" +
                PopularMovieData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                PopularMovieData.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +

                PopularMovieData.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                PopularMovieData.COLUMN_RATING + " INTEGER NOT NULL," +
                PopularMovieData.COLUMN_PLOT + " TEXT NOT NULL, " +

                PopularMovieData.COLUMN_POSTER + " TEXT NOT NULL, " +
                PopularMovieData.COLUMN_BACKDROP + " TEXT NOT NULL, " +

                PopularMovieData.COLUMN_TRAILER + " TEXT NOT NULL, " +
                PopularMovieData.COLUMN_REVIEW + " TEXT NOT NULL, " +
                PopularMovieData.COLUMN_ID + " TEXT NOT NULL" + ");";


        final String SQL_CREATE_TOP_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS " + TopMovieData.TABLE_NAME + " (" +

                TopMovieData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                TopMovieData.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                TopMovieData.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +

                TopMovieData.COLUMN_RATING + " INTEGER NOT NULL," +
                TopMovieData.COLUMN_PLOT + " TEXT NOT NULL, " +

                TopMovieData.COLUMN_POSTER + " TEXT NOT NULL, " +
                TopMovieData.COLUMN_BACKDROP + " TEXT NOT NULL, " +

                TopMovieData.COLUMN_TRAILER + " TEXT NOT NULL, " +
                TopMovieData.COLUMN_REVIEW + " TEXT NOT NULL, " +
                TopMovieData.COLUMN_ID + " TEXT NOT NULL" + ");";

        final String SQL_CREATE_FAVORITE_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS " + FavoriteData.TABLE_NAME + " (" +

                FavoriteData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                FavoriteData.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoriteData.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +

                FavoriteData.COLUMN_RATING + " INTEGER NOT NULL," +
                FavoriteData.COLUMN_PLOT + " TEXT NOT NULL, " +

                FavoriteData.COLUMN_POSTER + " TEXT NOT NULL, " +
                FavoriteData.COLUMN_BACKDROP + " TEXT NOT NULL, " +

                FavoriteData.COLUMN_TRAILER + " TEXT NOT NULL, " +
                FavoriteData.COLUMN_REVIEW + " TEXT NOT NULL, " +
                PopularMovieData.COLUMN_ID + " TEXT NOT NULL" + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TOP_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMovieData.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TopMovieData.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteData.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addMovie(GridItem item, String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIE_TITLE, item.getTitle());
        values.put(COLUMN_POSTER, item.getImage());
        values.put(COLUMN_BACKDROP, item.getBackdrop());
        values.put(COLUMN_PLOT, item.getPlot());
        values.put(COLUMN_RATING, item.getRating());
        values.put(COLUMN_RELEASE_DATE, item.getReleaseDate());
        values.put(COLUMN_TRAILER, item.getTrailer());
        values.put(COLUMN_REVIEW, item.getReview());
        values.put(COLUMN_ID, item.getId());

        db.insert(tableName, null, values);
        db.close();

    }

    public void deleteEntry(String ID) {
        SQLiteDatabase wDb = this.getWritableDatabase();
        wDb.execSQL("DELETE FROM " + FavoriteData.TABLE_NAME + " WHERE id = " + ID);
    }

    public void deleteEntries(String tableName) {

        SQLiteDatabase wDb = this.getWritableDatabase();
        wDb.execSQL("DELETE FROM " + tableName);
    }

    public ArrayList<GridItem> getAllMovies(String Table_name) {
        ArrayList<GridItem> movieList = new ArrayList<GridItem>();

        String selectQuery = "SELECT * FROM " + Table_name;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                GridItem item = new GridItem();

                item.setTitle(cursor.getString(1));
                item.setReleaseDate(cursor.getString(2));
                item.setRating(cursor.getString(3));
                item.setPlot(cursor.getString(4));
                item.setImage(cursor.getString(5));
                item.setBackdrop(cursor.getString(6));
                item.setTrailer(cursor.getString(7));
                item.setReview(cursor.getString(8));
                item.setId(cursor.getString(9));

                movieList.add(item);
            } while (cursor.moveToNext());
        }

        return movieList;
    }

    public boolean isExist(String ID) {
        String selectQuery = "SELECT * FROM " + FavoriteData.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                if (ID.equals(cursor.getString(9)))
                    return true;
            } while (cursor.moveToNext());

        }
        return false;
    }


}
