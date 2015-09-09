package uk.appinvent.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uk.appinvent.popularmovies.data.MovieContract;

/**
 * Created by mudasar on 30/08/15.
 */
public class VideosAdapter extends CursorAdapter {


    public VideosAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public static  class ViewHolder{

        final TextView videoTextView ;

        public ViewHolder(View view) {
            videoTextView =(TextView)  view.findViewById(R.id.trailer_title_text);
        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list_view_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int video_name_idx = cursor.getColumnIndex(MovieContract.Video.NAME);
        String videoName = cursor.getString(video_name_idx);
        if (viewHolder != null){
            viewHolder.videoTextView.setText(videoName);
        }else if(view != null) {
            TextView textView = (TextView) view.findViewById(R.id.trailer_title_text);
            textView.setText(videoName);
        }

    }
}
