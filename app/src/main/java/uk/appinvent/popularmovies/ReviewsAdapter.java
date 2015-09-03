package uk.appinvent.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import uk.appinvent.popularmovies.data.MovieContract;

/**
 * Created by mudasar on 31/08/15.
 */
public class ReviewsAdapter extends CursorAdapter {

    public ReviewsAdapter(Context context, Cursor c, boolean flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_view_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (view != null){

            TextView reviewText = (TextView) view.findViewById(R.id.review_text_view);
            int content_idx = cursor.getColumnIndex(MovieContract.Review.CONTENT);
            String reviewTextContent = cursor.getString(content_idx);
            reviewText.setText(reviewTextContent);

            TextView authorTextView = (TextView) view.findViewById(R.id.author_text_view);
            int auth_idx = cursor.getColumnIndex(MovieContract.Review.AUTHOR);
            String reviewAuthor = cursor.getString(auth_idx);
            authorTextView.setText(reviewAuthor);
        }

    }
}
