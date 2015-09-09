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

    public static class ViewHolder{
        public final ImageView posterView;
        public ViewHolder(View view){
            posterView = (ImageView) view.findViewById(R.id.move_poster_image);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_view_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int poster_idx = cursor.getColumnIndex(MovieContract.Movie.POSTER);
        String poster_path = cursor.getString(poster_idx);
        String full_poster_path = Utility.getPosterPath("w500" , poster_path);

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder != null){
            Picasso.with(context).load(full_poster_path).into(viewHolder.posterView);
        }else{
            // A single row/item view in the grid layout
            ImageView imageView = (ImageView) view.findViewById(R.id.move_poster_image);
            Picasso.with(context).load(full_poster_path).into(imageView);
        }
    }
}
