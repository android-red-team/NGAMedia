package nga.ngamedia;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieSubActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ShareActionProvider mShareActionProvider;
    private MoviesAdapter mAdapter;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Get the intent, verify the action and get the query
        handleIntent(getIntent());
    }

    // If a user start a new search from within your search activity, Android would recycle the instance
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    // Handles incoming intent/s including search queries
    private void handleIntent(Intent intent) {
        // Get the intent, verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            setTitle(query);
            getMedia(1, query);
        } else {
            // Code and null (no query)
            Intent myIntent = getIntent();
            setTitle(myIntent.getStringExtra("EXTRA_CLASS"));
            if(myIntent.getStringExtra("EXTRA_CLASS").equals("Movies")) {
                getMedia(1, null);
            } else if (myIntent.getStringExtra("EXTRA_CLASS").equals("TVShows")) {
                getMedia(5, null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
                //movieActivityIntent.putExtra("EXTRA_CLASS","Movie");
                finish();
                startActivity(homeActivityIntent);
                break;
            case R.id.nav_movie:
                Intent movieActivityIntent = new Intent(this, MovieSubActivity.class);
                movieActivityIntent.putExtra("EXTRA_CLASS","Movies");
                finish();
                startActivity(movieActivityIntent);
                break;
            case R.id.nav_television:
                Intent televisionActivityIntent = new Intent(this, MovieSubActivity.class);
                televisionActivityIntent.putExtra("EXTRA_CLASS", "Television");
                startActivity(televisionActivityIntent);
                //return true;
                break;

            case R.id.nav_aboutus:
                Intent aboutusActivityIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutusActivityIntent);
                break;
            case R.id.nav_share:
                Intent sendIntent = new Intent();
                setSendIntent(sendIntent);
                setShareIntent(sendIntent);
                //return true;
                break;
            case R.id.nav_favorite:
                // add auth condition
                if(true){
                    //navIntent = new Intent(this, FavorityActivity.class);
                    //startActivity(navIntent);
                }
                else {
                    Intent navIntent = new Intent(this, SigninActivity.class);
                    startActivity(navIntent);
                }
                break;
            case R.id.nav_user:
                if(true){
                    // Signout or user profile.
                    // MenuItem uname = (MenuItem) findViewById(R.id.nav_user);
                    // uname.setTitle(username);
                }
                else {
                    Intent navIntent = new Intent(this, SigninActivity.class);
                    startActivity(navIntent);
                }
                break;
            default:
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Determines the API call to make
    private void getMedia(int code, String query) {
        // If a query parameter is passed, automatically make it a search-based API call
        if(query != null && query.length() > 2) {
            code = 4;
        }
        // Initialize service
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

        switch (code) {
            //popular movies
            case (1): {
                service.getPopularMovies(new Callback<Movie.MovieResult>() {
                    @Override
                    public void success(Movie.MovieResult movieResult, Response response) {
                        mAdapter.setMovieList(movieResult.getResults());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
                break;
            }

            // recent movies
            case (2): {
                service.getUpcomingMovies(new Callback<Movie.MovieResult>() {
                    @Override
                    public void success(Movie.MovieResult movieResult, Response response) {
                        mAdapter.setMovieList(movieResult.getResults());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
                break;
            }

            // best movies
            case (3): {
                service.getPopularTV(new Callback<Movie.MovieResult>() {
                    @Override
                    public void success(Movie.MovieResult movieResult, Response response) {
                        mAdapter.setMovieList(movieResult.getResults());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
                break;
            }

            // search results
            case (4): {
                service.getSearchResults(query, new Callback<Movie.MovieResult>() {
                    @Override
                    public void success(Movie.MovieResult movieResult, Response response) {
                        mAdapter.setMovieList(movieResult.getResults());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
                break;
            }

            //popular television
            case (5): {
                service.getPopularTV(new Callback<Movie.MovieResult>() {
                    @Override
                    public void success(Movie.MovieResult movieResult, Response response) {
                        mAdapter.setMovieList(movieResult.getResults());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
                break;
            }

            // recent television
            case (6): {
                service.getRecentTV(new Callback<Movie.MovieResult>() {
                    @Override
                    public void success(Movie.MovieResult movieResult, Response response) {
                        mAdapter.setMovieList(movieResult.getResults());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
                break;
            }

            // best television
            case (7): {
                service.getBestTV(new Callback<Movie.MovieResult>() {
                    @Override
                    public void success(Movie.MovieResult movieResult, Response response) {
                        mAdapter.setMovieList(movieResult.getResults());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
                break;
            }
        }

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
            if(movie.getPoster().length() > 35) {
                Picasso.with(mContext)
                        .load(movie.getPoster())
                        .placeholder(R.color.colorAccent)
                        .into(holder.imageView);
            } else {
                Picasso.with(mContext)
                        .load("https://thumbs.gfycat.com/UnfoldedEmotionalEquine-size_restricted.gif")
                        .placeholder(R.color.colorAccent)
                        .into(holder.imageView);
            }
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

