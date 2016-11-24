package nga.ngamedia;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ShareActionProvider mShareActionProvider;
    private MoviesAdapter mMovieAdapter;
    private MoviesAdapter mMovieAdapter2;
    private MoviesAdapter mTVShowAdapter;

    // Firebase auth
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver networkReceiver = new NetworkReceiver();
    private Snackbar networkNotificationSnackBar;
    private static MainActivity mainActivityInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mainActivityInstance = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        //getSupportActionBar().setIcon(R.drawable.nga_logo);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        RecyclerView mMovieRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mMovieRecyclerView.setLayoutManager(new LinearLayoutManager(this, 0, false));
        mMovieAdapter = new MoviesAdapter(this);
        mMovieRecyclerView.setAdapter(mMovieAdapter);

        RecyclerView mTVShowRecyclerView = (RecyclerView) findViewById(R.id.recyclerView2);
        mTVShowRecyclerView.setLayoutManager(new LinearLayoutManager(this, 0, false));
        mTVShowAdapter = new MoviesAdapter(this);
        mTVShowRecyclerView.setAdapter(mTVShowAdapter);

        RecyclerView mMovieRecyclerView2 = (RecyclerView) findViewById(R.id.recyclerView3);
        mMovieRecyclerView2.setLayoutManager(new LinearLayoutManager(this, 0, false));
        mMovieAdapter2 = new MoviesAdapter(this);
        mMovieRecyclerView2.setAdapter(mMovieAdapter2);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter networkStatusFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkReceiver();
        this.registerReceiver(networkReceiver, networkStatusFilter);

        // Initialize UI resources to be updated
        TextView popularMovieHeaderTV = (TextView) findViewById(R.id.popularMovieHeader);
        String popular_movies_header = getString(R.string.popular_movies_header);

        TextView upcomingMovieHeaderTV = (TextView) findViewById(R.id.upcomingMoviesHeader);
        String upcoming_movies_header = getString(R.string.upcoming_movies_header);

        TextView popularTVHeader = (TextView) findViewById(R.id.popularTVHeader);
        String popular_tv_shows_header = getString(R.string.popular_tv_shows_header);

        popularMovieHeaderTV.setText(" ");
        upcomingMovieHeaderTV.setText(" ");
        popularTVHeader.setText(" ");
        loadMedia();
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Quit Application")
                    .setMessage("Are you sure you want to close NGAMedia app?")
                    .setIcon(R.drawable.alert_icon)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                            startActivity(intent);
                            finish();
                            System.exit(0);;
                        }
                    }).setNegativeButton("No", null).show();
        }
    }


    /*
    final MenuItem uName = (MenuItem) findViewById(R.id.nav_user);
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (mUser != null) {
                        uName.setTitle("Sign Out");
                    } else {
                        uName.setTitle("Sign In");
                    }
                }
            };

     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do iconify the widget; do not expand it by default, toggle with setIconified()
        searchView.setSubmitButtonEnabled(true); //add a "submit" button

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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch(item.getItemId()){
            case R.id.nav_home:
                Intent homeActivityIntent = new Intent(this, MainActivity.class);
                homeActivityIntent.putExtra("EXTRA_CLASS","Movie");
                finish();
                startActivity(homeActivityIntent);
                break;
            case R.id.nav_movie:
                Intent movieActivityIntent = new Intent(this, MovieSubActivity.class);
                movieActivityIntent.putExtra("EXTRA_CLASS","Movies");
                startActivity(movieActivityIntent);
                break;
            case R.id.nav_television:
                Intent televisionActivityIntent = new Intent(this, MovieSubActivity.class);
                televisionActivityIntent.putExtra("EXTRA_CLASS","Television");
                startActivity(televisionActivityIntent);
                break;
            case R.id.nav_aboutus:
                Intent aboutusActivityIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutusActivityIntent);
                break;
            case R.id.nav_share:
                Intent sendIntent = new Intent();
                setSendIntent(sendIntent);
                setShareIntent(sendIntent);
                break;
            case R.id.nav_favorite:
                // add auth condition
                if(mUser == null) {
                    // Not signed in, click redirect to sign in page
                    Intent favoriteIntent = new Intent(this, SigninActivity.class);
                    startActivity(favoriteIntent);
                }
                else {
                    Intent navIntent = new Intent(this, MovieSubActivity.class);
                    navIntent.putExtra("EXTRA_CLASS","Favorite");
                    startActivity(navIntent);
                }
                break;
            case R.id.nav_user:
                if(mUser == null){
                    // Not signed in, click redirect to sign in page
                    Intent signInIntent = new Intent(this, SigninActivity.class);
                    startActivity(signInIntent);
                } else {
                    mAuth.signOut();
                }
                break;
            default:
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadMedia() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.themoviedb.org/3")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addEncodedQueryParam("api_key", "b03774039f2c75ff84893e6e49f8a347");
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        TMDBApiService service = restAdapter.create(TMDBApiService.class);
        service.getPopularMovies(new Callback<Movie.MovieResult>() {
            @Override
            public void success(Movie.MovieResult movieResult, Response response) {
                mMovieAdapter.setMovieList(movieResult.getResults());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

        service.getUpcomingMovies(new Callback<Movie.MovieResult>() {
            @Override
            public void success(Movie.MovieResult movieResult, Response response) {
                mTVShowAdapter.setMovieList(movieResult.getResults());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

        service.getPopularTV(new Callback<Movie.MovieResult>() {
            @Override
            public void success(Movie.MovieResult movieResult, Response response) {
                mMovieAdapter2.setMovieList(movieResult.getResults());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.rowMovie);
        }
    }

    public static class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {
        private List<Movie> mMovieList;
        private LayoutInflater mInflater;
        private Context mContext;
        private String url = "http://image.tmdb.org/t/p/w500";

        public MoviesAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
            View view = mInflater.inflate(R.layout.row_movie, parent, false);
            final MovieViewHolder viewHolder = new MovieViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = viewHolder.getAdapterPosition();
                    Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, mMovieList.get(position));
                    mContext.startActivity(intent);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, int position) {
            Movie movie = mMovieList.get(position);
            Picasso.with(mContext)
                    .load(url + movie.getPoster())
                    .placeholder(R.color.colorAccent)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return (mMovieList == null) ? 0 : mMovieList.size();
        }

        public void setMovieList(List<Movie> movieList) {
            this.mMovieList = new ArrayList<>();
            this.mMovieList.addAll(movieList);
            notifyDataSetChanged();
        }
    }

    // Returns an instance of the current Main Activity running
    public static MainActivity  getMainActivityInstance(){
        return mainActivityInstance;
    }

    // Update
    public void updateMainActivityUI(final Boolean... params) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                // Initialize UI resources to be updated
                TextView popularMovieHeaderTV = (TextView) findViewById(R.id.popularMovieHeader);
                String popular_movies_header = getString(R.string.popular_movies_header);

                TextView upcomingMovieHeaderTV = (TextView) findViewById(R.id.upcomingMoviesHeader);
                String upcoming_movies_header = getString(R.string.upcoming_movies_header);

                TextView popularTVHeader = (TextView) findViewById(R.id.popularTVHeader);
                String popular_tv_shows_header = getString(R.string.popular_tv_shows_header);

                // params[0] refers to whether the device is connected to the internet
                if(params[0]) {
                    popularMovieHeaderTV.setText(popular_movies_header);
                    upcomingMovieHeaderTV.setText(upcoming_movies_header);
                    popularTVHeader.setText(popular_tv_shows_header);
                    // Hide Snackbar
                    if(networkNotificationSnackBar != null && networkNotificationSnackBar.isShown()) {
                        networkNotificationSnackBar.dismiss();
                    }
                    networkNotificationSnackBar = Snackbar.make(findViewById(android.R.id.content), "Loading media...", Snackbar.LENGTH_SHORT);
                    networkNotificationSnackBar.getView().setBackgroundColor(Color.BLUE);
                    networkNotificationSnackBar.show();
                    loadMedia();
                } else {
                    networkNotificationSnackBar = Snackbar.make(findViewById(android.R.id.content), "Warning: App cannot function without an internet connection!", Snackbar.LENGTH_INDEFINITE);
                    networkNotificationSnackBar.getView().setBackgroundColor(Color.RED);
                    networkNotificationSnackBar.show();
                }
            }
        });
    }

    // Call to set up the intent to share
    private void setSendIntent(Intent sendIntent) {
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "I'm using NGAMedia App to find the latest movies and tv shows! You can download it for free on the Google Playstore!");
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
