package uk.appinvent.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uk.appinvent.popularmovies.data.MovieContract;
import uk.appinvent.popularmovies.data.MoviesProvider;

/**
 * Created by mudasar on 8/26/2015.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailsFragment.class.getName();

    private static final int DETAIL_LOADER = 0;
    private static final int VIDEOS_LOADER = 1;
    private static final int REVIEWS_LOADER = 2;



    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_RELEASE_DATE = 3;
    private static final int COL_MOVIE_POSTER = 4;
    private static final int COL_MOVIE_POPULARITY = 5;
    private static final int COL_MOVIE_VOTE_AVERAGE = 6;
    private static final int COL_MOVIE_PLOT = 7;

    private static final int COL_VIDEO_ID = 0;
    private static final int COL_VIDEO_KEY = 1;
    private static final int COL_VIDEO_SITE = 3;
    private static final int COL_VIDEO_NAME = 5;


    public DetailsFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(VIDEOS_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);



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


        switch (id){
            case DETAIL_LOADER:
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

            case VIDEOS_LOADER:
                Long movieId = MovieContract.Movie.getMovieIdFromUri(intent.getData());

                return  new CursorLoader(
                        getActivity(),
                        MovieContract.Video.buildVideoUriWithMovieId(movieId),
                        null,
                        null,
                        null,
                        null
                );

            case REVIEWS_LOADER:

                 movieId = MovieContract.Movie.getMovieIdFromUri(intent.getData());

                return  new CursorLoader(
                        getActivity(),
                        MovieContract.Review.buildReviewUriWithMovieId(movieId),
                        null,
                        null,
                        null,
                        null
                );
        }

    return null;
        //String data = ;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }
        int id = loader.getId();
        switch (id){
            case DETAIL_LOADER:
                TextView titleView = (TextView) getView().findViewById(R.id.movie_title);
                titleView.setText(data.getString(COL_MOVIE_TITLE));


                TextView plotView = (TextView) getView().findViewById(R.id.movie_plot);
                plotView.setText(data.getString(COL_MOVIE_PLOT));

                TextView releaseDateView = (TextView) getView().findViewById(R.id.movie_release_date);
                releaseDateView.setText(data.getString(COL_MOVIE_RELEASE_DATE));

                ImageView imageView = (ImageView) getView().findViewById(R.id.movie_poster);

                String full_poster_path = Utility.getPosterPath("w500" , data.getString(COL_MOVIE_POSTER));
                Picasso.with(getView().getContext()).load(full_poster_path).into(imageView);

                TextView ratingText = (TextView) getView().findViewById(R.id.movie_rating);
                ratingText.setText(data.getDouble(COL_MOVIE_VOTE_AVERAGE) + " / 10");

                RatingBar ratingBar = (RatingBar) getView().findViewById(R.id.movie_rating_bar);
                ratingBar.setRating((float) data.getDouble(COL_MOVIE_VOTE_AVERAGE) / 2);

                break;
            case VIDEOS_LOADER:

                ListView videosListView = (ListView) getView().findViewById(R.id.videos_list);
                VideosAdapter videosAdapter = new VideosAdapter(getActivity(), data, false);
                videosListView.setAdapter(videosAdapter);

                Utility.setListViewHeightBasedOnChildren(videosListView);

                videosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // open the video with youtube

                        Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                        if (cursor != null) {

                            String key = cursor.getString(COL_VIDEO_KEY);
                            String site = cursor.getString(COL_VIDEO_SITE);

                            //if (site == "YouTube"){

                                Uri videoLocation = Uri.parse("https://www.youtube.com/watch").buildUpon()
                                        .appendQueryParameter("v", key)
                                        .build();
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(videoLocation);

                                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivity(intent);
                                } else {
                                    Log.d(LOG_TAG, "Couldn't call " + videoLocation + ", no receiving apps installed!");
                                }

                           // }else{
                         //       Log.d(LOG_TAG, "Video website "+ site +" is not supported in this version!");
                         //   }
                        }
                    }
                });
                break;
            case REVIEWS_LOADER:
                ListView reviewsListView = (ListView) getView().findViewById(R.id.reviews_list);
                ReviewsAdapter reviewsAdapter = new ReviewsAdapter(getActivity(), data, false);
                reviewsListView.setAdapter(reviewsAdapter);
                Utility.setListViewHeightBasedOnChildren(reviewsListView);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
