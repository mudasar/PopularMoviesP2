package uk.appinvent.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    boolean mTwoPane = false;
    private static final String DETAILFRAGMENT_TAG = "MDTAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null){

            mTwoPane = true;

            if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, new DetailsFragment(), DETAILFRAGMENT_TAG)

                    .commit();
            }
        }else {
            mTwoPane = false;

        }

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_movies, new MoviesFragment())
//                    .commit();
//        }

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        DetailsFragment df = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
