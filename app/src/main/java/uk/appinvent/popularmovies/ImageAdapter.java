package uk.appinvent.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uk.appinvent.popularmovies.data.MovieContract;

/**
 * Created by mudasar on 10/07/2015.
 */
public class ImageAdapter extends CursorAdapter {

    Context context;


    public ImageAdapter(Context context, Cursor c, int flags) {
        super(context,c,flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_view_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // A single row/item view in the grid layout
        ImageView imageView = (ImageView) view.findViewById(R.id.move_poster_image);
        int poster = cursor.getColumnIndex(MovieContract.Movie.POSTER);
        Picasso.with(context).load(poster).into(imageView);
    }
}
