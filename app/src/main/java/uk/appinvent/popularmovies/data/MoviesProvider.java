package uk.appinvent.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import uk.appinvent.popularmovies.data.MovieContract.*;
import uk.appinvent.popularmovies.data.MovieDbHelper;

/**
 * Created by mudasar on 8/25/2015.
 */
public class MoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_ID = 101;
    static final int REVIEW = 200;
    static final int REVIEW_ID = 201;
    static final int REVIEW_WITH_MOVIE = 202;

    static final int VIDEO = 300;
    static final int VIDEO_ID = 301;
    static final int VIDEO_WITH_MOVIE = 302;

    static String MOVIE_SELECTION_BY_ID = Movie.TABLE_NAME + "." + Movie._ID + " = ?";

    private static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_ID);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW + "/*/#", REVIEW_WITH_MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW, REVIEW);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW + "/#", REVIEW_ID);


        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_VIDEO + "/*/#", VIDEO_WITH_MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_VIDEO, VIDEO);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_VIDEO + "/#", VIDEO_ID);



        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    public Cursor getVideosByMovieId(Uri uri, String[] projection, String sortOrder){

        Long movieId = Video.getMovieIdFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(Video.TABLE_NAME, projection,
                MovieContract.Video.TABLE_NAME + "." + MovieContract.Video.MOVIE_ID +  " = ?",
                new String[] {Long.toString(movieId)},
                null,
                null,
                sortOrder
                );
    }

    public Cursor getFavouriteMovies(String[] selectionArgs){
        return mOpenHelper.getReadableDatabase().rawQuery("SELECT " +Movie.TABLE_NAME + "." + Movie._ID + ", " + Movie.TABLE_NAME + "." + Movie.POSTER + " FROM " + Movie.TABLE_NAME + " WHERE " + Movie._ID + " IN (?)", selectionArgs);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case REVIEW_WITH_MOVIE:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(Review.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            // "weather/*"
            case VIDEO_WITH_MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(Video.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            // "Movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(Movie.TABLE_NAME, projection, selection, selectionArgs, null,null, sortOrder);
                break;
            }
            case MOVIE_ID:{

                retCursor = mOpenHelper.getReadableDatabase().query(
                        Movie.TABLE_NAME,
                        projection,
                        MOVIE_SELECTION_BY_ID,
                        new String[]{ Long.toString(Movie.getMovieIdFromUri(uri)) },
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "Review"
            case REVIEW: {
                Long movieId = Review.getMovieIdFromUri(uri);
                if (movieId != 0){
                    String reviewSelection = Review.TABLE_NAME + "." + Review.MOVIE_ID + " = ?";
                    retCursor = mOpenHelper.getReadableDatabase().query(Review.TABLE_NAME, projection, reviewSelection, new String[]{Long.toString(movieId)}, null,null, sortOrder);
                }else   {
                    retCursor = mOpenHelper.getReadableDatabase().query(Review.TABLE_NAME, projection, selection, selectionArgs, null,null, sortOrder);
                }


                break;
            }

            case VIDEO: {
                Long movieId = Video.getMovieIdFromUri(uri);
                if (movieId != 0){
                    String videoSelection = Video.TABLE_NAME + "." + Video.MOVIE_ID + " = ?";
                    retCursor = mOpenHelper.getReadableDatabase().query(Video.TABLE_NAME, projection, videoSelection, new String[]{Long.toString(movieId)}, null,null, sortOrder);
                }else {
                    retCursor = mOpenHelper.getReadableDatabase().query(Video.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return Movie.CONTENT_TYPE;
            case MOVIE_ID:
                return Movie.CONTENT_ITEM_TYPE;
            case REVIEW:
                return Review.CONTENT_TYPE;
            case REVIEW_ID:
                return Review.CONTENT_ITEM_TYPE;
            case REVIEW_WITH_MOVIE:
                return Review.CONTENT_TYPE;
            case VIDEO:
                return Video.CONTENT_TYPE;
            case VIDEO_ID:
                return Video.CONTENT_ITEM_TYPE;
            case VIDEO_WITH_MOVIE:
                return Video.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(Movie.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Movie.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEO:{
                long _id = db.insert(Video.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Video.buildVideoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW:{
                long _id = db.insert(Review.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Review.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    db.delete(Movie.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case VIDEO:
                db.beginTransaction();
                returnCount = 0;
                try {
                    db.delete(Video.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW:
                db.beginTransaction();
                returnCount = 0;
                try {
                    db.delete(Video.TABLE_NAME, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    db.update(Movie.TABLE_NAME, values, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case VIDEO:
                db.beginTransaction();
                returnCount = 0;
                try {
                    db.update(Video.TABLE_NAME, values, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW:
                db.beginTransaction();
                returnCount = 0;
                try {
                    db.update(Video.TABLE_NAME, values, selection, selectionArgs);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Movie.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case VIDEO:
                db.beginTransaction();
                 returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Video.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW:
                db.beginTransaction();
                 returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Review.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
