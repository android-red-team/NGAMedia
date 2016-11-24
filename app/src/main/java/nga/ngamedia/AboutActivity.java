package nga.ngamedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by kwanc on 2016-11-03.
 */
public class AboutActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private ShareActionProvider mShareActionProvider;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
                finish();
                startActivity(movieActivityIntent);
                break;
            case R.id.nav_television:
                Intent televisionActivityIntent = new Intent(this, MovieSubActivity.class);
                televisionActivityIntent.putExtra("EXTRA_CLASS","Television");
                finish();
                startActivity(televisionActivityIntent);
                break;
            case R.id.nav_aboutus:
                Intent aboutusActivityIntent = new Intent(this, AboutActivity.class);
                finish();
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
                    finish();
                    startActivity(favoriteIntent);
                }
                else {
                    Intent navIntent = new Intent(this, MovieSubActivity.class);
                    navIntent.putExtra("EXTRA_CLASS","Favorite");
                    finish();
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
