package com.icedcap.itbookfinder.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.icedcap.itbookfinder.R;
import com.icedcap.itbookfinder.adapter.LinearContentAdapter;
import com.icedcap.itbookfinder.help.Utils;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.model.SearchResult;
import com.icedcap.itbookfinder.persistence.Constants;
import com.icedcap.itbookfinder.presenters.LoadMorePresenter;
import com.icedcap.itbookfinder.presenters.SearchPresenter;
import com.icedcap.itbookfinder.ui.interfaces.RecyclerViewScrollListener;
import com.icedcap.itbookfinder.ui.interfaces.SearchInterface;

import java.util.List;

/**
 * Author: doushuqi
 * Date: 16-4-1
 * Email: shuqi.dou@singuloid.com
 * LastUpdateTime:
 * LastUpdateBy:
 */
public class SearchActivity extends AppCompatActivity implements SearchInterface {
    private static final String LOG_TAG = "SearchActivity";
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearContentAdapter mAdapter;

    private static final String EXTRA_KEY = "keyword";
    private String mKeywords;
    private List<Book> mBooks;
    private SearchPresenter mSearchPresenter;
    private LoadMorePresenter mLoadMorePresenter;
    private SearchResult mSearchResult;

    public static void start(Activity activity, String keywords) {
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putExtra(EXTRA_KEY, keywords);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search);

        initToolBar();
        initRecyclerView();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();

    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.orange),
                ContextCompat.getColor(this, R.color.green),
                ContextCompat.getColor(this, R.color.blue));
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

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.search_recyclerview);
        final LinearLayoutManager lm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            protected boolean onScrolledToBottom() {
                if (Constants.DEBUG) {
                    Log.d(LOG_TAG, "onScrolledToBottom");
                }
                if (!Utils.isWifiConnected(SearchActivity.this)) {
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

        mAdapter = new LinearContentAdapter(this, mBooks);
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
        Intent intent = getIntent();
        if (intent != null) {
            mKeywords = intent.getStringExtra(EXTRA_KEY);
            getSupportActionBar().setTitle(mKeywords);
            mSearchPresenter = new SearchPresenter(this, mKeywords, this);
            mLoadMorePresenter = new LoadMorePresenter(this);
        } else {
            showEmptyView(true);
        }
    }

    private void showEmptyView(boolean show) {
        findViewById(R.id.empty_view).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add_tab:
                mSearchPresenter.saveToLocal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.transition_left_in, R.anim.transition_right_out);
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
        findViewById(R.id.loading_more).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        findViewById(R.id.loading_more).setVisibility(View.GONE);
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
