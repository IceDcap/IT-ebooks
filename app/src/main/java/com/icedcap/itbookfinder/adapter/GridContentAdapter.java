package com.icedcap.itbookfinder.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icedcap.itbookfinder.help.TransitionHelper;
import com.icedcap.itbookfinder.help.Utils;
import com.icedcap.itbookfinder.ui.BookDetailActivity;
import com.icedcap.itbookfinder.R;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.widget.TypefaceTextView;

import java.util.List;

/**
 * Created by shuqi on 16-3-27.
 */
public class GridContentAdapter extends RecyclerView.Adapter<GridContentAdapter.ContentViewHolder> {

    public static final String SHARE_ELEMENT_NAME = "book_icon";
    private List<Book> mBooks;
    private Activity mContext;
    private final int mItemWidth;
    private final int mItemHeight;

    public GridContentAdapter(Activity c, List<Book> book, int spanCount) {
        mContext = c;
        mBooks = book;

        mItemWidth = Utils.getDisplayPoint(mContext).x / spanCount - Utils.dpToPx(mContext, 2f);
        mItemHeight = (int) (mItemWidth * 1.25f);
    }

    public void loadMoreItems(List<Book> books, int count) {
        if (books == null || books.size() == 0) {
            return;
        }
        this.mBooks = books;
        notifyItemRangeInserted(books.size() - count, count);
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid_content, parent, false));
    }

    @Override
    public void onBindViewHolder(final ContentViewHolder holder, int position) {
        if (mBooks == null || mBooks.size() == 0) {
            return;
        }
        final Book book = mBooks.get(position);
        holder.itemView.setMinimumWidth(mItemWidth);
        holder.itemView.setMinimumHeight(mItemHeight);
        Glide.with(mContext)
                .load(book.getIconUrl())
                .crossFade()
                .into(holder.mBookIcon);

        holder.mBookIcon.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);
                final int width = right - left;
                ((ImageView) v).setMaxWidth(width);
                ((ImageView) v).setMaxHeight((int) (1.25f * width));
            }
        });
        holder.mBookTitle.setText(book.getBookTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Utils.startBookDetailWithTransition(mContext, holder.mBookIcon, book);
                } else {
                    BookDetailActivity.start(mContext, book);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return null == mBooks ? 0 : mBooks.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        private ImageView mBookIcon;
        private TextView mBookTitle;
        private ImageView mMore;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mBookIcon = (ImageView) itemView.findViewById(R.id.content_book_icon);
            mBookTitle = (TextView) itemView.findViewById(R.id.content_book_title);
//            mMore = (ImageView) itemView.findViewById(R.id.more_vert);
        }
    }

}
