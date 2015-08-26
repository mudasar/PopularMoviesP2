package uk.appinvent.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uk.appinvent.popularmovies.data.MovieContract;

/**
 * Created by mudasar on 8/26/2015.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailsFragment.class.getName();

    private static final int DETAIL_LOADER = 0;

    public DetailsFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        Intent pIntent = getActivity().getIntent();

        Movie movie = pIntent.getParcelableExtra("movie");

        if (movie != null){
            TextView titleView = (TextView) rootView.findViewById(R.id.movie_title);
            titleView.setText(movie.title);


            TextView plotView = (TextView) rootView.findViewById(R.id.movie_plot);
            plotView.setText(movie.plot);

            TextView releaseDateView = (TextView) rootView.findViewById(R.id.movie_release_date);
            releaseDateView.setText(movie.releaseDate);

            ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_poster);
            Picasso.with(rootView.getContext()).load(movie.posterPath).into(imageView);

            TextView ratingText = (TextView) rootView.findViewById(R.id.movie_rating);
            ratingText.setText(movie.userRating + " / 10");

            RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.movie_rating_bar);
            ratingBar.setRating((float) movie.userRating / 2);
        }

        //TODO:  parse into int  create a new a sync task to load movie

        //TODO: implement search and settings

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
