package uk.appinvent.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import uk.appinvent.popularmovies.data.MovieContract;

/**
 * Created by mudasar on 10/07/2015.
 */
public class LoadMoviesTask extends AsyncTask<String,Void, Void> {

    private final static String API_BASE_URL = "api.themoviedb.org";
    private static final String API_URL = "3/discover/movie";
    private static final String API_DETAIL_URL = "3/movie/";
    private String sort_method;
    //popular
    private static String API_KEY ="";

    private static final String LOG_TAG = LoadMoviesTask.class.getName();

    private Context mContext ;

    public LoadMoviesTask(Context context) {
        mContext = context;
        API_KEY = mContext.getString(R.string.api_key);
    }

    private String makeAPIUrl(String sortOrder){
        sort_method =    sortOrder;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority(API_BASE_URL);
        builder.appendEncodedPath(API_URL);
        //builder.appendQueryParameter("year", "2015");
        //builder.appendQueryParameter("sort_by", sort_method + ".asc");
        builder.appendQueryParameter("api_key", API_KEY);

        Uri url = builder.build();
        return url.toString();
    }

    // type = videos / reviews
    private String makeMovieDetailAPIUrl(long movieId, String type){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority(API_BASE_URL);
        builder.appendEncodedPath(API_DETAIL_URL + movieId + "/" + type);
        builder.appendQueryParameter("api_key", API_KEY);
        Uri url = builder.build();
        return url.toString();
    }


    @Override
    protected Void doInBackground(String... params) {

        String sortOrder = params[0];

        String apiUrl = makeAPIUrl(sortOrder);

        Log.e(LOG_TAG, apiUrl);

        String jsonData = loadApiData(apiUrl);

        Log.e(LOG_TAG, jsonData);

        if (jsonData != null){
            ArrayList<Movie> movieList = parseMoviesJson(jsonData);

            // load movie details data
            for (Movie movie : movieList) {

                //pase videojson data
            // First, check if the location with this city name exists in the db
                long movideId = 0;
                Cursor movieCursor = mContext.getContentResolver().query(
                        MovieContract.Movie.CONTENT_URI,
                        new String[]{MovieContract.Movie._ID,MovieContract.Movie.TMDB_ID},
                        MovieContract.Movie.TMDB_ID + " = ?",
                        new String[]{Long.toString(movie.id)},
                        null);

                if (movieCursor.moveToFirst()) {
                    int locationIdIndex = movieCursor.getColumnIndex(MovieContract.Movie._ID);
                    movideId = movieCursor.getLong(locationIdIndex);
                } else {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.Movie.TITLE, movie.title);
                    movieValues.put(MovieContract.Movie.TMDB_ID, movie.id);
                    movieValues.put(MovieContract.Movie.PLOT, movie.plot);
                    movieValues.put(MovieContract.Movie.POSTER, movie.posterPath);
                    movieValues.put(MovieContract.Movie.RELEASE_DATE, movie.releaseDate);
                    movieValues.put(MovieContract.Movie.POPULARITY, movie.popularity);
                    movieValues.put(MovieContract.Movie.VOTE_AVERAGE, movie.userRating);

                    Uri insertedUri =  mContext.getContentResolver().insert(MovieContract.Movie.CONTENT_URI, movieValues);

                    movideId = ContentUris.parseId(insertedUri);

                    String videoUrl = makeMovieDetailAPIUrl(movie.id, "videos");
                    String videoJsonData = loadApiData(videoUrl);
                    ArrayList<ContentValues> videos = parseVideosJson(videoJsonData, movideId);

                    String reviewsUrl = makeMovieDetailAPIUrl(movie.id, "reviews");
                    String reviewsJsonData = loadApiData(reviewsUrl);

                    ArrayList<ContentValues> reviews = parseReviewsJson(reviewsJsonData, movideId);
                }
                movieCursor.close();
            }
            //insert this data in database
            //return movieList;
        }

        return null;
    }

    private ArrayList<ContentValues> parseVideosJson(String videosJson, long movideId){
        ArrayList<ContentValues> videosVector = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(videosJson);
            JSONArray resultsArray = jsonObject.getJSONArray("results");
            for(int i=0; i < resultsArray.length(); i++){
                JSONObject jsonVideo = resultsArray.getJSONObject(i);

                ContentValues videoValues = new ContentValues();
                videoValues.put(MovieContract.Video.KEY, jsonVideo.getString("key"));
                videoValues.put(MovieContract.Video.TMDB_ID, jsonVideo.getString("id"));
                videoValues.put(MovieContract.Video.NAME, jsonVideo.getString("name"));
                videoValues.put(MovieContract.Video.SITE, jsonVideo.getString("site"));
                videoValues.put(MovieContract.Video.TYPE, jsonVideo.getString("type"));
                videoValues.put(MovieContract.Video.MOVIE_ID, movideId);
                mContext.getContentResolver().insert(MovieContract.Video.CONTENT_URI, videoValues);
                videosVector.add(videoValues);
            }
            return videosVector;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error in parsing videos json");
        }
        return videosVector;
    }

    private ArrayList<ContentValues> parseReviewsJson(String reviewsJson, long movideId){
        ArrayList<ContentValues> reviews = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(reviewsJson);
            JSONArray resultsArray = jsonObject.getJSONArray("results");
            for(int i=0; i < resultsArray.length(); i++){
                JSONObject jsonReview = resultsArray.getJSONObject(i);
                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MovieContract.Review.AUTHOR, jsonReview.getString("author"));
                reviewValues.put(MovieContract.Review.TMDB_ID, jsonReview.getString("id"));
                reviewValues.put(MovieContract.Review.CONTENT, jsonReview.getString("content"));
                reviewValues.put(MovieContract.Review.URL, jsonReview.getString("url"));
                reviewValues.put(MovieContract.Review.MOVIE_ID, movideId);
                mContext.getContentResolver().insert(MovieContract.Review.CONTENT_URI, reviewValues);
                reviews.add(reviewValues);
            }
            return reviews;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error in parsing reviews json");
        }

        return reviews;
    }


    private ArrayList<Movie> parseMoviesJson(String moviesJson){
        ArrayList<Movie> movieList = new ArrayList<Movie>();

        try {
            JSONObject jsonObject = new JSONObject(moviesJson);
            JSONArray resultsArray = jsonObject.getJSONArray("results");
            for(int i=0; i < resultsArray.length(); i++){
                JSONObject jsonMovie = resultsArray.getJSONObject(i);

                Movie movie = new Movie(
                        jsonMovie.getString("original_title"),
                        jsonMovie.getLong("id"),
                        jsonMovie.getString("poster_path"),
                        "genre",
                        jsonMovie.getString("overview"),
                        jsonMovie.getDouble("vote_average"),

                        jsonMovie.getString("release_date")
                );
                movieList.add(movie);
            }
            return movieList;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error in parsing movies json");
        }
        return null;
    }

    private String loadApiData(String apiUrl){

        String jsonData = "";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;



        try{

            URL nUrl = new URL(apiUrl);
            urlConnection = (HttpURLConnection) nUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null){
                return null;
            }

            String line = "";
            reader = new BufferedReader(new InputStreamReader(inputStream));
            if (reader == null){
                return null;
            }

            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine())!= null){
                buffer.append(line);
            }
            jsonData = buffer.toString();

        }catch (IOException e){
            e.printStackTrace();
            Log.e("network_error", e.getMessage());
        }

        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try{
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("reader_error",e.getMessage());
                }
            }
        }
        return jsonData;
    }
}
