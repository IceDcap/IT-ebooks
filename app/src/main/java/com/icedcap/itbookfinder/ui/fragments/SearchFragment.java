package com.icedcap.itbookfinder.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import com.icedcap.itbookfinder.R;
import com.icedcap.itbookfinder.adapter.LinearContentAdapter;
import com.icedcap.itbookfinder.help.Utils;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.model.SearchResult;
import com.icedcap.itbookfinder.persistence.Constants;
import com.icedcap.itbookfinder.presenters.LoadMorePresenter;
import com.icedcap.itbookfinder.presenters.SearchPresenter;
import com.icedcap.itbookfinder.ui.MagicActivity;
import com.icedcap.itbookfinder.ui.interfaces.RecyclerViewScrollListener;
import com.icedcap.itbookfinder.ui.interfaces.SearchInterface;

import java.util.List;

/**
 * Created by shuqi on 16-4-4.
 */
public class SearchFragment extends Fragment implements SearchInterface {
    private static final String LOG_TAG = "SearchFragment";

    private AppCompatActivity mActivity;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearContentAdapter mAdapter;

    private String mKeywords;
    private List<Book> mBooks;
    private SearchPresenter mSearchPresenter;
    private LoadMorePresenter mLoadMorePresenter;
    private SearchResult mSearchResult;

    public static void start(Activity activity, String keywords) {
        MagicActivity.start(activity, keywords, new SearchFragment());
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        mActivity.setSupportActionBar(toolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
    }


    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.search_recyclerview);
        final LinearLayoutManager lm = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            protected boolean onScrolledToBottom() {
                if (Constants.DEBUG) {
                    Log.d(LOG_TAG, "onScrolledToBottom");
                }
                if (!Utils.isWifiConnected(mActivity)) {
                    toast("please check network!");
                    return false;
                }
                if (mLoadMorePresenter != null) {
                    int total = (int) Math.ceil(Integer.decode(mSearchResult.getTotal()) / 10d);
                    mLoadMorePresenter.fetchMoreData(mKeywords, total, mBooks);
                    return true;
                }
                return false;
            }
        });

        mAdapter = new LinearContentAdapter(mActivity, mBooks);
        mRecyclerView.setAdapter(mAdapter);
        initSwipeRefreshLayout();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mSwipeRefreshLayout.setEnabled(lm.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
        mSearchPresenter.fetchSearchResult();
    }


    private void initData() {
        Intent intent = mActivity.getIntent();
        if (intent != null) {
            mKeywords = intent.getStringExtra(MagicActivity.EXTRA_KEY);
            mActivity.getSupportActionBar().setTitle(mKeywords);
            mSearchPresenter = new SearchPresenter(mActivity, mKeywords, this);
            mLoadMorePresenter = new LoadMorePresenter(this);
        } else {
            showEmptyView(true);
        }
    }


    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mActivity, R.color.orange),
                ContextCompat.getColor(mActivity, R.color.green),
                ContextCompat.getColor(mActivity, R.color.blue));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000l);
            }
        });


    }


    private void showEmptyView(boolean show) {
        mRootView.findViewById(R.id.empty_view).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRootView = view;
        initToolBar();
        initRecyclerView();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_result, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mActivity.onBackPressed();
                return true;
            case R.id.add_tab:
                mSearchPresenter.saveToLocal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showSwiperefreshlayout() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideSwiperefreshlayout() {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 0l);
    }

    @Override
    public void showLoading() {
        mRootView.findViewById(R.id.loading_more).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mRootView.findViewById(R.id.loading_more).setVisibility(View.GONE);
    }

    @Override
    public void fetchedData(Object result) {
        if (result instanceof List) {
            List<Book> newResult = (List<Book>) result;
            int count = newResult.size() - mBooks.size();
            mBooks = newResult;
            mAdapter.loadMoreItems(newResult, count);

        } else if (result instanceof SearchResult) {
            mSearchResult = (SearchResult) result;
            final boolean isDataValid = null != mSearchResult && (mBooks = mSearchResult.getBooks()) != null && mBooks.size() > 0;
            showEmptyView(!isDataValid);
            if (isDataValid) {
                mAdapter.loadMoreItems(mBooks, mBooks.size());//todo
            }
        } else if (null == result) {
            showEmptyView(true);
        }
    }

    @Override
    public void toast(CharSequence charSequence) {
        Snackbar.make(mRecyclerView, charSequence, Snackbar.LENGTH_SHORT).show();
    }
}
