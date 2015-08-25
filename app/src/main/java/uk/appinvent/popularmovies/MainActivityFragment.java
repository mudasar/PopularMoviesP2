package uk.appinvent.popularmovies;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnTaskCompleted {

    private static final String LOG_TAG = MainActivityFragment.class.getName();
    public static final String DETAIL_ID = "DETAIL_ID";
    private final static String API_BASE_URL = "api.themoviedb.org";
    private static final String API_URL = "3/movie/";
    //popular
    private final static String API_KEY ="d1ef9dc0336bed3f42aa90354fdc4abf";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    private String api_method;
    SharedPreferences preferences;
    ImageAdapter imageAdapter;
    ArrayList<Movie> movies = new ArrayList<Movie>();

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        if (savedInstanceState != null){
            movies = savedInstanceState.getParcelableArrayList("movies");
            //Automatic refresh of movies list after setting is updated
            String savedApiMethod = savedInstanceState.getString("api_method");
            autoRefreshMovies(savedApiMethod);
        }else {
            executeAsyncTask();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            executeAsyncTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        autoRefreshMovies(api_method);
        updateMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movies);
        outState.putString("api_method",api_method);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null){
            movies = savedInstanceState.getParcelableArrayList("movies");
        }
    }

    private void autoRefreshMovies(String savedApiMethod){

        String configApiMethod = preferences.getString(getString(R.string.sort_order_key),"popular");
        if (savedApiMethod != null && savedApiMethod != configApiMethod){
            executeAsyncTask();
        }
    }

    private void executeAsyncTask(){
        LoadMoviesTask loadMoviesTask = new LoadMoviesTask(getActivity().getApplication(),this);
        AsyncTask<String, String, ArrayList<Movie>> task = loadMoviesTask.execute(preferences.getString(getString(R.string.sort_order_key), "popular"));
    }

    private void updateMovies(){
        if (movies != null) {
            for (Movie movie : movies) {
                //set image path
                movie.setPosterPath(IMAGE_BASE_URL + "w500" + movie.getPosterPath());
            }
            imageAdapter.moviesList.clear();
            imageAdapter.moviesList.addAll(movies);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =inflater.inflate(R.layout.fragment_movies, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.movies_grid_view);

        final Context appContext = this.getActivity().getApplicationContext();

        List<Movie> movies = new ArrayList<Movie>();


        imageAdapter = new ImageAdapter(appContext, movies);

        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) imageAdapter.getItem(position);
                Intent detailIntent = new Intent(appContext, DetailsActivity.class);
                detailIntent.putExtra("movie", movie);

                startActivity(detailIntent);
            }
        });

        return rootView;
    }


    @Override
    public void onTaskCompleted(ArrayList<Movie> movieList) {
        movies = movieList;
        updateMovies();
        // Have to call explicitly to make it work on first load
        imageAdapter.notifyDataSetChanged();
    }
}
