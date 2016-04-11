package com.icedcap.itbookfinder.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.icedcap.itbookfinder.R;
import com.icedcap.itbookfinder.adapter.GridContentAdapter;
import com.icedcap.itbookfinder.adapter.LinearContentAdapter;
import com.icedcap.itbookfinder.help.BookJsonHelper;
import com.icedcap.itbookfinder.help.SharedPreferencesHelper;
import com.icedcap.itbookfinder.help.Utils;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.model.SearchResult;
import com.icedcap.itbookfinder.persistence.Constants;
import com.icedcap.itbookfinder.persistence.DefaultBookCategory;
import com.icedcap.itbookfinder.presenters.LoadMorePresenter;
import com.icedcap.itbookfinder.ui.interfaces.OnHomepageLayoutChangedListener;
import com.icedcap.itbookfinder.ui.interfaces.RecyclerViewScrollListener;
import com.icedcap.itbookfinder.ui.interfaces.RefreshInterface;

import java.util.List;

import fr.castorflex.android.smoothprogressbar.ContentLoadingSmoothProgressBar;

/**
 * Created by shuqi on 16-4-4.
 */
public class TabFragment extends Fragment implements RefreshInterface {
    private static final String LOG_TAG = "TabFragment";

    private static final String EXTRA_JSON_RES_ID = Constants.SUFFIX_PKG + "jsonResIdExtra";
    private static final String EXTRA_HOMEPAGE_LAYOUT = Constants.SUFFIX_PKG + "homepageLayoutExtra";
    private static final String EXTRA_SEARCH_KEY = Constants.SUFFIX_PKG + "searchKeyExtra";

    private Activity mActivity;
    private View mRootView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ContentLoadingSmoothProgressBar mProgressBar;
    private String mSearchKey;
    private SearchResult mSearchResult;
    private List<Book> mBooks;
    private LoadMorePresenter mLoadMorePresenter;
    @RawRes
    private int mJsonRawRes;
    private int mHomepageLayout;

    public static TabFragment newInstance(int jsonRawRes, String searchKey) {
        TabFragment fragment = new TabFragment();
        final Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_JSON_RES_ID, jsonRawRes);
        bundle.putString(EXTRA_SEARCH_KEY, searchKey);
        fragment.setArguments(bundle);
        return fragment;
    }


    private void initView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.content_container);
        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            protected boolean onScrolledToBottom() {
                if (Constants.DEBUG) {
                    Log.d(LOG_TAG, "onScrolledToBottom");
                }
                if (!Utils.isWifiConnected(mActivity)) {
                    Toast.makeText(mActivity, "please check network!", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (mLoadMorePresenter != null) {
                    int total = (int) Math.ceil(Integer.decode(mSearchResult.getTotal()) / 10d);
                    mLoadMorePresenter.fetchMoreData(mSearchKey, total, mBooks);
                    return true;
                }
                return false;
            }
        });

        mProgressBar = (ContentLoadingSmoothProgressBar) mRootView.findViewById(R.id.loading_more);

    }

    private void initData() {
        setHomepageLayout();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mLoadMorePresenter = new LoadMorePresenter(this);
    }

    private List<Book> loadLocalData(@RawRes int json) {
        try {
            mSearchResult = BookJsonHelper.getSearchFromResource(mActivity, json);
            mBooks = mSearchResult.getBooks();
            return mBooks;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            return null;
        }
    }


    private void setHomepageLayout() {
        switch (mHomepageLayout) {
            case 1:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                mAdapter = new LinearContentAdapter(mActivity, loadLocalData(mJsonRawRes));
                break;
            case 2:
                final Configuration configuration = mActivity.getResources().getConfiguration();
                final int spanCount = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ? 6 : 3;
                mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, spanCount));
                mAdapter = new GridContentAdapter(mActivity, loadLocalData(mJsonRawRes), spanCount);
                break;
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mLoadMorePresenter = new LoadMorePresenter(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            mJsonRawRes = savedInstanceState.getInt(EXTRA_JSON_RES_ID);
            mSearchKey = savedInstanceState.getString(EXTRA_SEARCH_KEY);
            mHomepageLayout = savedInstanceState.getInt(EXTRA_HOMEPAGE_LAYOUT);
        } else {
            mJsonRawRes = getArguments().getInt(EXTRA_JSON_RES_ID);
            mSearchKey = getArguments().getString(EXTRA_SEARCH_KEY);
            mHomepageLayout = Integer.decode(SharedPreferencesHelper.getHomepageLayout(mActivity));
        }

        return inflater.inflate(R.layout.content_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRootView = view;
        initView();
        initData();
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        final int homepage = Integer.decode(SharedPreferencesHelper.getHomepageLayout(mActivity));
        if (mHomepageLayout != homepage) {
            mHomepageLayout = homepage;
            setHomepageLayout();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_JSON_RES_ID, mJsonRawRes);
        outState.putString(EXTRA_SEARCH_KEY, mSearchKey);
        outState.putInt(EXTRA_HOMEPAGE_LAYOUT, mHomepageLayout);
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void fetchedData(Object result) {
        List<Book> newResult = (List<Book>) result;
        int count = newResult.size() - mBooks.size();
        mBooks = newResult;
        if (mAdapter instanceof LinearContentAdapter) {
            ((LinearContentAdapter) mAdapter).loadMoreItems(newResult, count);
        } else {
            ((GridContentAdapter) mAdapter).loadMoreItems(newResult, count);
        }

    }

    @Override
    public void toast(CharSequence charSequence) {
        Toast.makeText(mActivity, charSequence, Toast.LENGTH_SHORT).show();
    }


    public void onHomepageLayoutChanged(int homepageLayout) {
        if (mHomepageLayout != homepageLayout) {
            mHomepageLayout = homepageLayout;
//            setHomepageLayout();
        }
    }
}
