package nga.ngamedia;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "movie";

    private ShareActionProvider mShareActionProvider;
    /**
     *  Declare database reference
     */
    private DatabaseReference mDB;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FloatingActionButton mFloatActionBtn;
    private Movie mMovie;
    ImageView backdrop;
    ImageView poster;
    TextView title;
    TextView description;
    TextView voteAverage;
    TextView releaseDate;
    TextView popularity;

    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver networkReceiver = new NetworkReceiver();
    private Snackbar networkNotificationSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (getIntent().hasExtra(EXTRA_MOVIE)) {
            mMovie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        } else {
            throw new IllegalArgumentException("Detail activity must receive a movie parcelable");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(mMovie.getTitle());

        backdrop = (ImageView) findViewById(R.id.backdrop);
        title = (TextView) findViewById(R.id.movie_title);
        description = (TextView) findViewById(R.id.movie_description);
        poster = (ImageView) findViewById(R.id.movie_poster);
        voteAverage = (TextView) findViewById(R.id.vote_average);
        releaseDate = (TextView) findViewById(R.id.release_date);
        popularity = (TextView) findViewById(R.id.popularity);
        mFloatActionBtn = (FloatingActionButton) findViewById(R.id.fab);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String url = "http://image.tmdb.org/t/p/w500";

        // Movies contain a 'title' while TVShows contain a 'name'
        // Movies contain 'release_date' while TVShows contain 'first_air_date'
        if(mMovie.getTitle() != null) {
            setTitle(mMovie.getTitle());
            title.setText(mMovie.getTitle());
        } else {
            setTitle(mMovie.getName());
            title.setText(mMovie.getName());
        }
        description.setText(mMovie.getDescription());
        Picasso.with(this)
                .load(url + mMovie.getPoster())
                .into(poster);
        Picasso.with(this)
                .load(mMovie.getBackdrop())
                .into(backdrop);
        int ave = (int) Double.parseDouble(mMovie.getVoteAverage());
        voteAverage.setText("" + ave + " /10 "+" (out of " + mMovie.getVote_count() + " votes)");
        int pop = (int) Double.parseDouble(mMovie.getPopularity());
        popularity.setText("" + pop + " Popularity Rating");
        if(mMovie.getRelease_date() != null) {
            releaseDate.setText("" + mMovie.getRelease_date());
        } else {
            releaseDate.setText("First Airing Date: " + mMovie.getFirst_air_date());
        }

        // Floating add button
        mFloatActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUser == null){
                    Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                    startActivityForResult(intent, 0);
                    finish();
                }else {
                    //addFavorite(mMovie, mUser.getUid());
                    mDB = FirebaseDatabase.getInstance().getReference();
                    mDB.child(mUser.getUid()).push().setValue(mMovie);
                    Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter networkStatusFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkReceiver();
        this.registerReceiver(networkReceiver, networkStatusFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregisters BroadcastReceiver when app is paused.
        if (networkReceiver != null) {
            this.unregisterReceiver(networkReceiver);
        }
        if(networkNotificationSnackBar != null && networkNotificationSnackBar.isShown()) {
            networkNotificationSnackBar.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.menu_item_share) {
            Intent sendIntent = new Intent();
            setSendIntent(sendIntent);
            setShareIntent(sendIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Call to set up the intent to share
    private void setSendIntent(Intent sendIntent) {
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "I found " + mMovie.getTitle() + " using the NGAMedia App!" );
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
