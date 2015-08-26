package uk.appinvent.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by mudasar on 25/08/15.
 */

public class Utility {

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.sort_order_key),
                context.getString(R.string.sort_order_default));
    }

}
