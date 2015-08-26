package uk.appinvent.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import uk.appinvent.popularmovies.BuildConfig;
import uk.appinvent.popularmovies.data.MovieContract.Movie;
import uk.appinvent.popularmovies.data.MovieContract.Video;
import uk.appinvent.popularmovies.data.MovieContract.Review;

/**
 * Created by mudasar on 8/25/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String TAG = MovieDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "movies.db";


    public static final String SQL_CREATE_TABLE_MOVIE = "CREATE TABLE IF NOT EXISTS "
            + Movie.TABLE_NAME + " ( "
            + Movie._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Movie.TITLE + " TEXT NOT NULL, "
            + Movie.TMDB_ID + " TEXT NOT NULL, "
            + Movie.RELEASE_DATE + " INTEGER NOT NULL, "
            + Movie.POSTER + " TEXT NOT NULL, "
            + Movie.POPULARITY + " REAL NOT NULL, "
            + Movie.VOTE_AVERAGE + " REAL NOT NULL, "
            + Movie.PLOT + " TEXT NOT NULL "
            + " );";

    public static final String SQL_CREATE_TABLE_REVIEW = "CREATE TABLE IF NOT EXISTS "
            + Review.TABLE_NAME + " ( "
            + Review._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Review.AUTHOR + " TEXT NOT NULL, "
            + Review.MOVIE_ID + " INTEGER NOT NULL, "
            + Review.URL + " TEXT NOT NULL, "
            + Review.TMDB_ID + " TEXT NOT NULL, "
            + Review.CONTENT + " TEXT NOT NULL "
            + ", CONSTRAINT fk_movie_id FOREIGN KEY (" + Review.MOVIE_ID + ") REFERENCES movie (_id) ON DELETE CASCADE"
            + " );";

    public static final String SQL_CREATE_TABLE_VIDEO = "CREATE TABLE IF NOT EXISTS "
            + Video.TABLE_NAME + " ( "
            + Video._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Video.KEY + " TEXT NOT NULL, "
            + Video.MOVIE_ID + " INTEGER NOT NULL, "
            + Video.SITE + " TEXT NOT NULL, "
            + Video.TMDB_ID + " TEXT NOT NULL, "
            + Video.NAME + " TEXT NOT NULL, "
            + Video.TYPE + " TEXT NOT NULL "
            + ", CONSTRAINT fk_movie_id FOREIGN KEY (" + Video.MOVIE_ID + ") REFERENCES movie (_id) ON DELETE CASCADE"
            + " );";



    public MovieDbHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");

        db.execSQL(SQL_CREATE_TABLE_MOVIE);
        db.execSQL(SQL_CREATE_TABLE_REVIEW);
        db.execSQL(SQL_CREATE_TABLE_VIDEO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // remove the previous tables

        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Movie.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Review.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Video.TABLE_NAME);
        onCreate(db);
    }



}
