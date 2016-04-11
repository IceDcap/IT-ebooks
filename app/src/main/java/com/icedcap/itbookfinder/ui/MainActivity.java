package com.icedcap.itbookfinder.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RawRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import com.icedcap.itbookfinder.R;
import com.icedcap.itbookfinder.adapter.GridContentAdapter;
import com.icedcap.itbookfinder.adapter.LinearContentAdapter;
import com.icedcap.itbookfinder.help.BookJsonHelper;
import com.icedcap.itbookfinder.help.SharedPreferencesHelper;
import com.icedcap.itbookfinder.help.Utils;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.persistence.Constants;
import com.icedcap.itbookfinder.model.SearchResult;
import com.icedcap.itbookfinder.persistence.DefaultBookCategory;
import com.icedcap.itbookfinder.presenters.LoadMorePresenter;
import com.icedcap.itbookfinder.ui.fragments.FavoriteFragment;
import com.icedcap.itbookfinder.ui.fragments.SearchFragment;
import com.icedcap.itbookfinder.ui.fragments.TabFragment;
import com.icedcap.itbookfinder.ui.interfaces.OnHomepageLayoutChangedListener;
import com.icedcap.itbookfinder.ui.interfaces.RecyclerViewScrollListener;
import com.icedcap.itbookfinder.ui.interfaces.RefreshInterface;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import fr.castorflex.android.smoothprogressbar.ContentLoadingSmoothProgressBar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    private static final String LOG_TAG = "MainActivity";
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private MaterialSearchView mSearchView;
    private int mHomepageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_title);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setNavigation(navigationView);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewPage);
        if (null != mViewPager) {
            setupViewPager(mViewPager);
            tabLayout.setupWithViewPager(mViewPager);
        }

        initSearch();
        mHomepageLayout = Integer.decode(SharedPreferencesHelper.getHomepageLayout(this));
    }


    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }


    private void initSearch() {
        mSearchView = (MaterialSearchView) findViewById(R.id.searchView);
        mSearchView.setVoiceSearch(false);
        mSearchView.setEllipsize(true);
        mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.closeSearch();
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    private void search(final String query) {
        if (query.length() > 50) {
            Toast.makeText(this, "query character is limited to 50!", Toast.LENGTH_SHORT).show();
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SearchFragment.start(MainActivity.this, query);
                overridePendingTransition(R.anim.transition_right_in, R.anim.transition_left_out);
            }
        }, 100l);
    }

    private void setNavigation(NavigationView navigationView) {
        View view = navigationView.getHeaderView(0);
        view.findViewById(R.id.settings).setOnClickListener(this);
        view.findViewById(R.id.favorites).setOnClickListener(this);
        view.findViewById(R.id.downloads).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(Constants.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START) || mSearchView.isSearchOpen()) {
            drawer.closeDrawer(GravityCompat.START);
            mSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_android) {
            mViewPager.setCurrentItem(0);
        } else if (id == R.id.nav_ios) {
            mViewPager.setCurrentItem(1);
        } else if (id == R.id.nav_http) {
            mViewPager.setCurrentItem(2);
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(final View v) {
        mDrawerLayout.closeDrawers();
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (v.getId()) {
                    case R.id.settings:
                        SettingsActivity.start(MainActivity.this);
                        overridePendingTransition(R.anim.transition_right_in, R.anim.transition_left_out);
                        break;
                    case R.id.favorites:
                        FavoriteFragment.start(MainActivity.this);
                        overridePendingTransition(R.anim.transition_right_in, R.anim.transition_left_out);
                        break;
                    case R.id.downloads:
                        String downloadPath = Utils.getAppExternalFolder(MainActivity.this);
                        Utils.openFolder(MainActivity.this, downloadPath);
                        break;
                }

            }
        }, 300l);

    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int layoutCode = intent.getIntExtra(Constants.BROADCAST_ACTION, mHomepageLayout);
//            ((TabFragment) ((FragmentPagerAdapter) mViewPager.getAdapter()).getItem(0)).onHomepageLayoutChanged(layoutCode);

        }
    };


    static class Adapter extends FragmentStatePagerAdapter {

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TabFragment.newInstance(DefaultBookCategory.values()[position].getJson(),
                    DefaultBookCategory.values()[position].getName().toString());
        }

        @Override
        public int getCount() {
            return DefaultBookCategory.values().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return DefaultBookCategory.values()[position].getName();
        }
    }
}
