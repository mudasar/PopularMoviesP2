package uk.appinvent.popularmovies;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DetailsActivity extends ActionBarActivity {

    private static final String LOG_TAG = DetailsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
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
    }
}
