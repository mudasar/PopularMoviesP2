package uk.appinvent.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;

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

import uk.appinvent.popularmovies.data.MovieContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivityFragment.class.getName();
    public static final String DETAIL_ID = "DETAIL_ID";
    private final static String API_BASE_URL = "api.themoviedb.org";
    private static final String API_URL = "3/movie/";
    //popular
    private final static String API_KEY ="d1ef9dc0336bed3f42aa90354fdc4abf";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";


    private static final int MOVIES_LOADER = 0;
    SharedPreferences preferences;
    ImageAdapter imageAdapter;


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
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
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }


//        @Override
//    public void onResume() {
//        super.onResume();
//        updateMovies();
//    }

//
//    private void autoRefreshMovies(String savedApiMethod){
//
//        String configApiMethod = preferences.getString(getString(R.string.sort_order_key),"popular");
//        if (savedApiMethod != null && savedApiMethod != configApiMethod){
//            executeAsyncTask();
//        }
//    }

    private void updateMovies(){
        LoadMoviesTask loadMoviesTask = new LoadMoviesTask(getActivity().getApplication());
        loadMoviesTask.execute(preferences.getString(getString(R.string.sort_order_key), getString(R.string.sort_order_default)));
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String sortOrderSetting = Utility.getPreferredSortOrder(getActivity());

        // Sort order:  Ascending, by date.

        String sortOrder = sortOrderSetting + " ASC";
        Cursor cur = getActivity().getContentResolver().query(MovieContract.Movie.CONTENT_URI,
                null, null, null, sortOrder);

        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        imageAdapter = new ImageAdapter(getActivity(), cur, 0);

        View rootView =inflater.inflate(R.layout.fragment_movies, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.movies_grid_view);

        final Context appContext = this.getActivity().getApplicationContext();

        imageAdapter = new ImageAdapter(appContext, cur, 0);

        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {

                    Intent intent = new Intent(getActivity(), DetailsActivity.class)
                            .setData(MovieContract.Movie.buildMovieUri(cursor.getLong(0)));

                    startActivity(intent);
                }
            }
        });

        return rootView;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrderSetting = Utility.getPreferredSortOrder(getActivity());

        // Sort order:  Ascending, by date.

        String sortOrder = sortOrderSetting + " ASC";
        return new CursorLoader(getActivity(), MovieContract.Movie.CONTENT_URI, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        imageAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        imageAdapter.swapCursor(null);
    }
}
