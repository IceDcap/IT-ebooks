package com.icedcap.itbookfinder.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.widget.NestedScrollView;
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
import android.view.animation.TranslateAnimation;
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
import com.icedcap.itbookfinder.presenters.LoadDetailPresenter;
import com.icedcap.itbookfinder.presenters.NetBasePresenter;
import com.icedcap.itbookfinder.ui.interfaces.DetailBookInterface;
import com.icedcap.itbookfinder.ui.interfaces.RefreshInterface;
import com.icedcap.itbookfinder.widget.blur.BlurHelper;

/**
 * Created by shuqi on 16-3-27.
 */
public class BookDetailActivity extends AppCompatActivity implements DetailBookInterface<Book> {
    private static final String TAG = "BookDetailActivity";
    public static final String EXTRA_BOOK = "book";
    private FloatingActionButton mFab;
    private Book mBook;
    private ViewGroup mAppBarLayout;
    private ViewGroup mDownViewGroup;
    private boolean isFavorite;
    private View mDownloadBtn;
    private int mScrollOffset;

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
                            return FastBlurUtil.doBlur(Utils.drawableToBitamp(drawable), 100, false);
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
        mDownloadBtn = findViewById(R.id.download);
        final NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

//        nestedScrollView.setOnScrollChangeListener(mListener);
    }

    private NestedScrollView.OnScrollChangeListener mListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(final NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            v.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    /* no-op */
                }
            });
            int direction = scrollY - oldScrollY;
            if (direction > 0) {
                //scroll up
                mScrollOffset = scrollY;
            }

            int offset = scrollY - oldScrollY;
            if (offset > 0) {
                Animation animation = AnimationUtils.loadAnimation(BookDetailActivity.this, R.anim.transition_footer_up);
                animation.setStartOffset(100);
                mDownloadBtn.startAnimation(animation);
            } else {
                Animation animation = AnimationUtils.loadAnimation(BookDetailActivity.this, R.anim.transition_footer_down);
                animation.setStartOffset(100);
                mDownloadBtn.startAnimation(animation);
            }
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    v.setOnScrollChangeListener(mListener);
                }
            }, 100);
        }
    };

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
                new LoadDetailPresenter(BookDetailActivity.this, BookDetailActivity.this, mBook).fetchData();
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

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void fetchedData(Book result) {
//            mDownloadBtn.setVisibility(View.VISIBLE);
        fillDataToUi(result);

    }

    @Override
    public void toast(final CharSequence charSequence) {
        Toast.makeText(BookDetailActivity.this, charSequence, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isFavorite(boolean is) {
        isFavorite = is;
    }

    @Override
    public void isDownloaded(boolean is) {

    }

    @Override
    public void forceExit() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BookDetailActivity.this.finish();
                overridePendingTransition(R.anim.transition_left_in, R.anim.transition_right_out);
            }
        }, 1000);
    }
}
