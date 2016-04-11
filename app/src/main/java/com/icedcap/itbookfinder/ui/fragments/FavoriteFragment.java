package com.icedcap.itbookfinder.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.icedcap.itbookfinder.R;
import com.icedcap.itbookfinder.adapter.LinearContentAdapter;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.persistence.BookDatabaseHelper;
import com.icedcap.itbookfinder.persistence.Constants;
import com.icedcap.itbookfinder.presenters.LoadFavoritePresenter;
import com.icedcap.itbookfinder.ui.MagicActivity;
import com.icedcap.itbookfinder.ui.interfaces.LoadFavoritesInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: doushuqi
 * Date: 16-4-5
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class FavoriteFragment extends Fragment implements LoadFavoritesInterface<List<Book>> {
    private static final String EXTRA_KEY = Constants.SUFFIX_PKG + "keyExtra";
    private AppCompatActivity mActivity;
    private View mRootView;
    private SwipeRefreshLayout mRefreshLayout;
    private LoadFavoritePresenter mPresenter;
    private LinearContentAdapter mAdapter;
    private RecyclerView mRecyclerView;


    public static void start(Activity activity) {
        MagicActivity.start(activity, new FavoriteFragment());
    }

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.favorite_title);
        mActivity.setSupportActionBar(toolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initSwipeRefreshLayout() {
        mRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipeRefreshLayout);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mActivity, R.color.orange),
                ContextCompat.getColor(mActivity, R.color.green),
                ContextCompat.getColor(mActivity, R.color.blue));
        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.fetchFavorites();
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.favorites_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    private void setupData(List<Book> books) {
        if (null == books || books.size() == 0) {
            showEmptyPage();
        }

        if (null == mAdapter) {
            mAdapter = new LinearContentAdapter(mActivity, books);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.update(books);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
        mPresenter = new LoadFavoritePresenter(mActivity, this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRootView = view;
        initToolbar();
        initSwipeRefreshLayout();
        initRecyclerView();
        mPresenter.fetchFavorites();

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favorite, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mActivity.onBackPressed();
                return true;
//            case R.id.favorite_sort:
//
//                return true;
            case R.id.favorite_clear_all:
                BookDatabaseHelper.clearAllFavorites(mActivity);
                fetchedData(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showLoading() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideLoading() {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        }, 0l);
    }

    @Override
    public void showEmptyPage() {
        mRootView.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
    }

    @Override
    public void fetchedData(List<Book> result) {
        setupData(result);
    }

    @Override
    public void toast(CharSequence charSequence) {
        Toast.makeText(mActivity, charSequence, Toast.LENGTH_SHORT).show();
    }
}
