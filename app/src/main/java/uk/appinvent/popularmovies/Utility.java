package uk.appinvent.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mudasar on 25/08/15.
 */

public class Utility {

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String PREF_FAV_KEY = "favourites";

    public static List<String> getFavouriteMovies(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> favourites = prefs.getStringSet(PREF_FAV_KEY, null);

        if (favourites == null){
            return new ArrayList<String>();
        }
        return new ArrayList<String >(favourites);
    }

    public static void addFavorite(Context context, long movieId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> favourites = prefs.getStringSet(PREF_FAV_KEY, null);

        if (favourites == null){
            favourites = new HashSet<String>();
        }

        if (!favourites.contains(Long.toString(movieId))){
            favourites.add(Long.toString(movieId));

            editor.putStringSet(PREF_FAV_KEY, favourites);
            editor.commit();
            Toast.makeText(context, "Added to favorite list", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isFavourite(Context context, long movieId){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> favourites = prefs.getStringSet(PREF_FAV_KEY, null);
        if (favourites != null){
            return favourites.contains(Long.toString(movieId));
        }
        return false;
    }

    public static void removeFavorite(Context context, long movieId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> favourites = prefs.getStringSet(PREF_FAV_KEY, null);
        if (favourites == null){
            favourites = new HashSet<String>();
        }
        if (!favourites.contains(Long.toString(movieId))){
            favourites.remove(Long.toString(movieId));
            editor.putStringSet(PREF_FAV_KEY, favourites);
            editor.commit();
            Toast.makeText(context, "Removed to favorite list", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.sort_order_key),
                context.getString(R.string.sort_order_default));
    }

    public static String getPosterPath(String size, String path){
            return IMAGE_BASE_URL + size + path;
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
