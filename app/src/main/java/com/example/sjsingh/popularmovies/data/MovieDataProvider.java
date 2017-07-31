package com.example.sjsingh.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by sarabjeet.singh on 24-07-2017.
 */

public class MovieDataProvider extends ContentProvider {

    private static final int POPULAR = 100;
    private static final int TOP_RATED = 101;
    private static final int FAV = 102;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private DatabaseHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_POPULAR, POPULAR);
        matcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_TOP_RATED, TOP_RATED);
        matcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_FAV, FAV);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor returnCursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case POPULAR:
                returnCursor = db.query(
                        DatabaseContract.PopularMovieData.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TOP_RATED:
                returnCursor = db.query(
                        DatabaseContract.TopMovieData.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAV:
                returnCursor = db.query(
                        DatabaseContract.FavoriteData.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null) {
            returnCursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        switch (uriMatcher.match(uri)) {
            case FAV:
                db.insert(
                        DatabaseContract.FavoriteData.TABLE_NAME,
                        null,
                        contentValues
                );
                returnUri = DatabaseContract.FavoriteData.URI;
                break;
            case POPULAR:
                db.insert(
                        DatabaseContract.PopularMovieData.TABLE_NAME,
                        null,
                        contentValues
                );
                returnUri = DatabaseContract.PopularMovieData.URI;
                break;
            case TOP_RATED:
                db.insert(
                        DatabaseContract.TopMovieData.TABLE_NAME,
                        null,
                        contentValues
                );
                returnUri = DatabaseContract.TopMovieData.URI;
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case FAV:
                db.delete(DatabaseContract.FavoriteData.TABLE_NAME, null, null);
                break;
            case POPULAR:
                db.delete(DatabaseContract.PopularMovieData.TABLE_NAME, null, null);
                break;
            case TOP_RATED:
                db.delete(DatabaseContract.TopMovieData.TABLE_NAME, null, null);
                break;
        }

        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int returnCount = 0;
        Context context = getContext();

        switch (uriMatcher.match(uri)) {
            case POPULAR:
                Log.d("DB", "POPULAR INSERT");
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                DatabaseContract.PopularMovieData.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }


                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return returnCount;
            case TOP_RATED:
                Log.d("DB", "TOP INSERT");
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                DatabaseContract.TopMovieData.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }


    }
}
