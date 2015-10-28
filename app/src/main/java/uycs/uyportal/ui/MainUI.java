package uycs.uyportal.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import uycs.uyportal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AungKo on 10/25/2015.
 */
public class MainUI extends AppCompatActivity {
    @Bind(R.id.app_bar)    Toolbar toolbar;
    @Bind(R.id.fab)    FloatingActionButton fab;
    @Bind(R.id.main_drawer)    NavigationView mDrawer;
    @Bind(R.id.drawer_layout)    DrawerLayout mDrawerLayout;

    private static final String FIRST_TIME = "first_time";
    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private int mSelectedId;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mUserSawDrawer = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This is Snack Bar", Snackbar.LENGTH_LONG).show();
            }
        });

        mDrawer.setNavigationItemSelectedListener(navItemListener);mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        if (!didUserSeeDrawer()) {
            showDrawer();
            markDrawerSeen();
        } else {
            hideDrawer();
        }
        mSelectedId = savedInstanceState == null ? R.id.navigation_item_1 : savedInstanceState.getInt(SELECTED_ITEM_ID);
        navigate(mSelectedId);

    }

    NavigationView.OnNavigationItemSelectedListener navItemListener = new NavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            menuItem.setChecked(true);
            mSelectedId = menuItem.getItemId();

            navigate(mSelectedId);
            return true;
        }
    };

    private boolean didUserSeeDrawer() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = sharedPreferences.getBoolean(FIRST_TIME, false);
        return mUserSawDrawer;
    }

    private void markDrawerSeen() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = true;
        sharedPreferences.edit().putBoolean(FIRST_TIME, mUserSawDrawer).apply();
    }

    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void navigate(int mSelectedId) {
        switch(mSelectedId) {
            case R.id.navigation_item_2:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, TabLayoutFrag.newInstance(), TabLayoutFrag.TAG).commit();
                Toast.makeText(MainUI.this, "selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navigation_item_3:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, TabLayoutFrag.newInstance(), TabLayoutFrag.TAG).commit();
                Toast.makeText(MainUI.this, "selected", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_ID, mSelectedId);
    }
}
