package com.icedcap.itbookfinder.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.icedcap.itbookfinder.R;
import com.icedcap.itbookfinder.adapter.LinearContentAdapter;
import com.icedcap.itbookfinder.help.BookJsonHelper;
import com.icedcap.itbookfinder.help.FastBlurUtil;
import com.icedcap.itbookfinder.help.SharedPreferencesHelper;
import com.icedcap.itbookfinder.help.Utils;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.model.CacheBook;
import com.icedcap.itbookfinder.network.FileDownloadManager;
import com.icedcap.itbookfinder.network.RequestManager;
import com.icedcap.itbookfinder.persistence.BookDatabaseHelper;
import com.icedcap.itbookfinder.persistence.Constants;
import com.icedcap.itbookfinder.widget.blur.BlurHelper;

/**
 * Created by shuqi on 16-3-27.
 */
public class BookDetailActivity extends AppCompatActivity {
    private static final String TAG = "BookDetailActivity";
    public static final String EXTRA_BOOK = "book";
    private FloatingActionButton mFab;
    private Book mBook;
    private ViewGroup mAppBarLayout;
    private ViewGroup mDownViewGroup;
    //    private RecyclerView mRecyclerView;
//    private DetailBookAdapter mAdapter;
    private boolean isFavorite;

    public static Intent getStartIntent(Context context, Book book) {
        Intent starter = new Intent(context, BookDetailActivity.class);
        starter.putExtra(EXTRA_BOOK, book);
        return starter;
    }

    public static void start(Context context, Book book) {
        context.startActivity(getStartIntent(context, book));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mBook = getIntent().getParcelableExtra(EXTRA_BOOK);

        initToolLayout();

        initHeader();
        initView();
    }


    private void initToolLayout() {
        mAppBarLayout = (ViewGroup) findViewById(R.id.detail_appbar);
        mAppBarLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mAppBarLayout.removeOnLayoutChangeListener(this);
                animRevealShowAppBar(mAppBarLayout, 2f);
            }
        });
        final Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(null == mBook ? "Book" : mBook.getBookTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                final int totalOffset = appBarLayout.getHeight() - getSupportActionBar().getHeight();
                if (Math.abs(verticalOffset) > (totalOffset - (totalOffset / 10))) {
                    getSupportActionBar().setTitle(null == mBook ? "Book" : mBook.getBookTitle());
                } else {
                    getSupportActionBar().setTitle("");
                }
            }
        });


    }

    private void initHeader() {
        final View header = findViewById(R.id.detail_header);
        final ImageView imageView = (ImageView) findViewById(R.id.detail_book_icon);

        imageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                final Drawable drawable = imageView.getDrawable();

                if (drawable != null) {
                    new AsyncTask<Void, Void, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(Void... params) {
                            Bitmap blurBitmap = FastBlurUtil.doBlur(Utils.drawableToBitamp(drawable), 100, false);
                            return blurBitmap;
                        }

                        @Override
                        protected void onPostExecute(Bitmap blurBitmap) {
                            mAppBarLayout.setBackground(new BitmapDrawable(BookDetailActivity.this.getResources(), blurBitmap));
                        }
                    }.execute();
                    imageView.removeOnLayoutChangeListener(this);
                }
            }
        });

        imageView.setTransitionName(LinearContentAdapter.SHARE_ELEMENT_NAME);

        Glide.with(this).load(mBook.getIconUrl()).centerCrop().crossFade().into(imageView);
    }

    private void initView() {
        mDownViewGroup = (ViewGroup) findViewById(R.id.detail_book_content);
//        mRecyclerView = (RecyclerView) mDownViewGroup.findViewById(R.id.detail_recycleView);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setHasFixedSize(true);
    }

    private void initFabWithAnim(boolean anim) {
        mFab = (FloatingActionButton) findViewById(R.id.detail_fab);
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
        p.setAnchorId(R.id.appbar);
        mFab.setLayoutParams(p);

        mFab.getDrawable().setLevel(isFavorite ? 1 : 0);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorite();
            }
        });
        mFab.setVisibility(View.VISIBLE);
        if (anim) {
            final Animation animation = AnimationUtils.loadAnimation(this, R.anim.fab_fade_expose_in);
            mFab.startAnimation(animation);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animRevealShowAppBar(final View view, float startRadius) {
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;
        final float endRadius = (float) Math.hypot(view.getWidth(), view.getHeight()) / 2;
        Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius, endRadius);
        animator.setDuration(500);
        animator.setInterpolator(new FastOutLinearInInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAppBarLayout.setBackgroundColor(ContextCompat.getColor(BookDetailActivity.this, R.color.colorPrimary));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                initData();
            }
        });
        animator.start();
    }

    private void animExit(ViewGroup up, ViewGroup down) {
        if (mFab != null) {
            mFab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_fade_expose_out));
        }
        final Animation upOut = AnimationUtils.loadAnimation(this, R.anim.transition_up_out);
        final Animation downOut = AnimationUtils.loadAnimation(this, R.anim.transition_down_out);
        up.startAnimation(upOut);
        down.startAnimation(downOut);
    }

    private void favorite() {
        isFavorite = !isFavorite;
        LevelListDrawable drawable = (LevelListDrawable) mFab.getDrawable();
        drawable.setLevel(isFavorite ? 1 : 0);
        BookDatabaseHelper.favorite(this, mBook, isFavorite);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mAppBarLayout != null && mDownViewGroup != null) {
            animExit(mAppBarLayout, mDownViewGroup);
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initData() {
        if (mBook == null) {
            return;
        }

        if (getBookFromDatabase()) {
            return;
        }

        new AsyncTask<Void, Void, Book>() {
            @Override
            protected Book doInBackground(Void... params) {
                Book book = null;
                try {
                    String response = new RequestManager.Builder()
                            .req(String.valueOf(mBook.getId()))
                            .reqType(RequestManager.ReqType.BOOK)
                            .build()
                            .getResponse();
                    book = BookJsonHelper.getBookFromJson(response.replace("\\", "\\\\"), mBook);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                return book;
            }

            @Override
            protected void onPostExecute(Book book) {
                if (null == book) {
                    return;
                }
                if (SharedPreferencesHelper.getAutoCache(BookDetailActivity.this)) {
                    BookDatabaseHelper.insert(BookDetailActivity.this, book, false);
                }
//                setDataForRecyclerView(book);
                fillDataToUi(book);
            }
        }.execute();
    }

    private boolean getBookFromDatabase() {
        CacheBook cacheBook = BookDatabaseHelper.getCacheBook(this, mBook);
        if (cacheBook == null) {
            return false;
        } else {
            mBook = cacheBook.mBook;
            isFavorite = cacheBook.isFavorite;
//            setDataForRecyclerView(mBook);
            fillDataToUi(mBook);
            return true;
        }
    }

//    private void setDataForRecyclerView(Book book) {
//        mAdapter = new DetailBookAdapter(book.getBookStates(), book);
//        mRecyclerView.setAdapter(mAdapter);
//        initFabWithAnim(true);
//    }

    private void fillDataToUi(final Book book) {
        Utils.setTextViewCharSequence(this, R.id.book_title, book.getBookTitle());
        Utils.setTextViewCharSequence(this, R.id.book_subtitle, book.getBookSubtitle());
        Utils.setTextViewCharSequence(this, R.id.book_description, book.getDescription());
        Utils.setTextViewCharSequence(this, R.id.book_author, book.getAuthor());
        Utils.setTextViewCharSequence(this, R.id.book_isbn, book.getIsbn());
        Utils.setTextViewCharSequence(this, R.id.book_year, book.getYear());
        Utils.setTextViewCharSequence(this, R.id.book_page, book.getPage());
        Utils.setTextViewCharSequence(this, R.id.book_publisher, book.getPublisher());
        findViewById(R.id.book_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(book);
            }
        });
        mDownViewGroup.setVisibility(View.VISIBLE);
        mDownViewGroup.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_item_enter));


        initFabWithAnim(true);
    }

    private synchronized void download(Book book) {
        if (Constants.DOWNLOAD_IDS.containsKey(book.getDownloadUrl())) {
            return;
        }
        long downloadId = BookDatabaseHelper.getDownloadId(this, book);

        final String filename = book.getBookTitle() + ".pdf";
        final FileDownloadManager manager = FileDownloadManager.getManager(this);
        if (manager.isDownloaded(this, downloadId)
                && Utils.isFileExists(Utils.getAppExternalFolder(this), filename)
                ) {
            Toast.makeText(this, "This file has already exists!", Toast.LENGTH_SHORT).show();
            // TODO: 16-4-7 direct open file

            return;
        }

        long id = manager.downloadFile(this, book.getDownloadUrl(), filename);
        manager.setFlagToDatabase(this, mBook.getId(), id);
    }

}
