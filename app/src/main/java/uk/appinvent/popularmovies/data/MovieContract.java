package uk.appinvent.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mudasar on 8/25/2015.
 */
public class MovieContract  {

    public static final String CONTENT_AUTHORITY = "uk.appinvent.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_MOVIE = "movie";
    public static final String PATH_VIDEO = "video";
    public static final String PATH_REVIEW = "review";


    public static final class Movie implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


        // movies table name
        public static final String TABLE_NAME = "movie";

        public static final String TMDB_ID = "tmdb_id";

        /**
         * title of this movie. For instance, Terminator.
         */
        public static final String TITLE = "title";

        /**
         * release date of movie. For instance, 12/11/2014.
         */
        public static final String RELEASE_DATE = "release_date";

        public static final String POSTER = "poster";

        public static final String VOTE_AVERAGE = "vote_average";

        public static final String PLOT = "plot";


        public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

        // @formatter:off
        public static final String[] ALL_COLUMNS = new String[] {
                _ID,
                TMDB_ID,
                TITLE,
                RELEASE_DATE,
                POSTER,
                VOTE_AVERAGE,
                PLOT
        };

        // @formatter:on

        public static boolean hasColumns(String[] projection) {
            if (projection == null) return true;
            for (String c : projection) {
                if (c.equals(TITLE) || c.contains("." + TITLE)) return true;
                if (c.equals(RELEASE_DATE) || c.contains("." + RELEASE_DATE)) return true;
                if (c.equals(POSTER) || c.contains("." + POSTER)) return true;
                if (c.equals(VOTE_AVERAGE) || c.contains("." + VOTE_AVERAGE)) return true;
                if (c.equals(PLOT) || c.contains("." + PLOT)) return true;
            }
            return false;
        }

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class Video  implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;


        //movie trailers table name
        public static final String TABLE_NAME = "video";

        public static final String KEY = "key";

        public static final String MOVIE_ID = "movie_id";

        public static final String SITE = "site";

        public static final String TMDB_ID = "tmdb_id";

        public static final String NAME = "name";

        public static final String TYPE = "type";


        public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

        // @formatter:off
        public static final String[] ALL_COLUMNS = new String[] {
                _ID,
                KEY,
                MOVIE_ID,
                SITE,
                TMDB_ID,
                NAME,
                TYPE
        };
        // @formatter:on

        public static boolean hasColumns(String[] projection) {
            if (projection == null) return true;
            for (String c : projection) {
                if (c.equals(KEY) || c.contains("." + KEY)) return true;
                if (c.equals(MOVIE_ID) || c.contains("." + MOVIE_ID)) return true;
                if (c.equals(SITE) || c.contains("." + SITE)) return true;
                if (c.equals(TMDB_ID) || c.contains("." + TMDB_ID)) return true;
                if (c.equals(NAME) || c.contains("." + NAME)) return true;
                if (c.equals(TYPE) || c.contains("." + TYPE)) return true;
            }
            return false;
        }

        public static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildVideoUriWithMovieId(long movieId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieId)).build();
        }

        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }

    public static final class Review  implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        // movie reviews table name
        public static final String TABLE_NAME = "review";

        public static final String AUTHOR = "author";

        public static final String MOVIE_ID = "movie_id";

        public static final String URL = "url";

        public static final String TMDB_ID = "tmdb_id";

        public static final String CONTENT = "content";


        public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

        // @formatter:off
        public static final String[] ALL_COLUMNS = new String[] {
                _ID,
                AUTHOR,
                MOVIE_ID,
                URL,
                TMDB_ID,
                CONTENT
        };
        // @formatter:on

        public static boolean hasColumns(String[] projection) {
            if (projection == null) return true;
            for (String c : projection) {
                if (c.equals(AUTHOR) || c.contains("." + AUTHOR)) return true;
                if (c.equals(MOVIE_ID) || c.contains("." + MOVIE_ID)) return true;
                if (c.equals(URL) || c.contains("." + URL)) return true;
                if (c.equals(TMDB_ID) || c.contains("." + TMDB_ID)) return true;
                if (c.equals(CONTENT) || c.contains("." + CONTENT)) return true;
            }
            return false;
        }

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static Uri buildReviewUriWithMovieId(long movieId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieId)).build();
        }

        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }

}
