package nga.ngamedia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    private RecyclerView mRecyclerView2;
    private MoviesAdapter mAdapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, 0, false));
        mAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(this, 0, false));
        mAdapter2 = new MoviesAdapter(this);
        mRecyclerView2.setAdapter(mAdapter2);

        getPopularMedia();

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent movieActivityIntent;

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_settings:
                return true;
            case R.id.menu_item_search:
                movieActivityIntent = new Intent(this, MovieSubActivity.class);
                movieActivityIntent.putExtra("EXTRA_CLASS","Search");
                startActivity(movieActivityIntent);
                return true;
            case R.id.menu_item_movie:
                movieActivityIntent = new Intent(this, MovieSubActivity.class);
                movieActivityIntent.putExtra("EXTRA_CLASS","Movie");
                startActivity(movieActivityIntent);
                return true;
            case R.id.menu_item_tv:
                movieActivityIntent = new Intent(this, MovieSubActivity.class);
                movieActivityIntent.putExtra("EXTRA_CLASS","Television");
                startActivity(movieActivityIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //int id = item.getItemId();
        Intent navIntent;
        switch(item.getItemId()){
            case R.id.nav_home:
                navIntent = new Intent(this, MainActivity.class);
                //movieActivityIntent.putExtra("EXTRA_CLASS","Movie");
                startActivity(navIntent);
                // return true;
                break;
            case R.id.nav_television:
                navIntent = new Intent(this, MovieSubActivity.class);
                navIntent.putExtra("EXTRA_CLASS","Television");
                startActivity(navIntent);
                //return true;
                break;
            case R.id.nav_movie:
                navIntent = new Intent(this, MovieSubActivity.class);
                navIntent.putExtra("EXTRA_CLASS","Movie");
                startActivity(navIntent);
                //return true;
                break;
            case R.id.nav_aboutus:
                navIntent = new Intent(this, AboutUs.class);
               // aboutusActivityIntent.putExtra("EXTRA_CLASS","Movie");
                startActivity(navIntent);
                //return true;
                break;
            case R.id.nav_share:
                navIntent = new Intent();
                setSendIntent(navIntent);
                setShareIntent(navIntent);
                //return true;
                break;
            case R.id.nav_user:
                navIntent = new Intent(this, LoginActivity.class);
                startActivity(navIntent);
                break;
            case R.id.nav_favorite:
                // add auth condition
                if(true){
                    //navIntent = new Intent(this, FavoriteActivity.class);
                    //startActivity(navIntent);
                }
                else {
                    navIntent = new Intent(this, LoginActivity.class);
                    startActivity(navIntent);
                }
                break;
            default:
                navIntent = new Intent(this, MainActivity.class);
                //movieActivityIntent.putExtra("EXTRA_CLASS","Movie");
                startActivity(navIntent);
                // return true;

        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getPopularMedia() {
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
                mAdapter.setMovieList(movieResult.getResults());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

        service.getPopularTV(new Callback<Movie.MovieResult>() {
            @Override
            public void success(Movie.MovieResult movieResult, Response response) {
                mAdapter2.setMovieList(movieResult.getResults());
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
                    .load(movie.getPoster())
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
    // Call to set up the intent to share
    private void setSendIntent(Intent sendIntent) {
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
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
