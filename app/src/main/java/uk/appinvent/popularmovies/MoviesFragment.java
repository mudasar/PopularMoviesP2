package uk.appinvent.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;


import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

import uk.appinvent.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MoviesFragment.class.getName();

    private static final int MOVIES_LOADER = 0;

    SharedPreferences preferences;
    ImageAdapter imageAdapter;
    boolean isLoaded = false;
    String savedSortOrder;

    private int mPosition = GridView.INVALID_POSITION;


    private static final String SELECTED_KEY = "selected_position";

    private GridView gridView;


    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        if (savedInstanceState == null){
            savedSortOrder = Utility.getPreferredSortOrder(getActivity());
        }else {
            savedSortOrder = savedInstanceState.getString("saved-sort-order");
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }

        outState.putString("saved-sort-order", savedSortOrder);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null){
            savedSortOrder = savedInstanceState.getString("saved-sort-order");
        }
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
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateMovies(){
        if (!isLoaded){
            LoadMoviesTask loadMoviesTask = new LoadMoviesTask(getActivity().getApplication());
            String sortOrder = getString(R.string.sort_order_key);
            if (sortOrder.equals("favourites")){
                sortOrder =  getString(R.string.sort_order_default);
            }
            loadMoviesTask.execute(preferences.getString(sortOrder, getString(R.string.sort_order_default)));
            isLoaded = true;
        }

        //reload the loaderManager if sort order is changed
        if (savedSortOrder != null && savedSortOrder != Utility.getPreferredSortOrder(getActivity())){
            getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView =inflater.inflate(R.layout.fragment_movies, container, false);

        gridView = (GridView) rootView.findViewById(R.id.movies_grid_view);

        final Context appContext = this.getActivity().getApplicationContext();

        imageAdapter = new ImageAdapter(appContext, null, 0);

        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {

                    ((Callback) getActivity()).onItemSelected(MovieContract.Movie.buildMovieUri(cursor.getLong(0)));
                }
                mPosition = position;
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

    String sortOrderSetting = Utility.getPreferredSortOrder(getActivity());

    // Sort order:  Ascending, by date.

        if (sortOrderSetting.equals("favourites")){
            String sortOrder = getString(R.string.sort_order_default) + " ASC";
            List<String> favMovies = Utility.getFavouriteMovies(getActivity());
            String movieIds = Joiner.on(", ").join(favMovies);
            String fav_selection = MovieContract.Movie._ID + " IN ( "+ movieIds +" )";

            return new CursorLoader(getActivity(), MovieContract.Movie.CONTENT_URI, null, fav_selection, null, sortOrder);
        }else{
            String sortOrder = sortOrderSetting + " ASC";
            return new CursorLoader(getActivity(), MovieContract.Movie.CONTENT_URI, null, null, null, sortOrder);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        imageAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION){
            gridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        imageAdapter.swapCursor(null);
    }


    public interface Callback {

        public void onItemSelected(Uri contentUri);
    }


}
