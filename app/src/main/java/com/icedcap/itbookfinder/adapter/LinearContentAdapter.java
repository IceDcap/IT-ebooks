package com.icedcap.itbookfinder.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icedcap.itbookfinder.help.Utils;
import com.icedcap.itbookfinder.ui.BookDetailActivity;
import com.icedcap.itbookfinder.R;
import com.icedcap.itbookfinder.model.Book;
import com.icedcap.itbookfinder.widget.MyTextView;

import java.util.List;

/**
 * Created by shuqi on 16-3-27.
 */
public class LinearContentAdapter extends RecyclerView.Adapter<LinearContentAdapter.ContentViewHolder> {

    public static final String SHARE_ELEMENT_NAME = "book_icon";
    private List<Book> mBooks;
    private Activity mContext;

    public LinearContentAdapter(Activity c, List<Book> book) {
        mBooks = book;
        mContext = c;
    }

    public void update(List<Book> books) {
        this.mBooks = books;
        notifyDataSetChanged();
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
                .inflate(R.layout.item_vertical_content, parent, false));
    }

    @Override
    public void onBindViewHolder(final ContentViewHolder holder, int position) {
        if (mBooks == null || mBooks.size() == 0) {
            return;
        }
        final Book book = mBooks.get(position);
        Glide.with(mContext)
                .load(book.getIconUrl())
                .centerCrop()
                .placeholder(R.drawable.ic_book_image)
                .crossFade()
                .into(holder.mBookIcon);
        holder.mBookTitle.setText(book.getBookTitle());
        holder.mBookSubtitle.setText(book.getBookSubtitle());
        holder.mDescription.setText(book.getDescription());
        holder.mDescription.setText(book.getDescription());
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
        private ViewGroup mBookContent;
        private TextView mBookTitle;
        private TextView mBookSubtitle;
        private MyTextView mDescription;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mBookIcon = (ImageView) itemView.findViewById(R.id.content_book_icon);
            mBookTitle = (TextView) itemView.findViewById(R.id.content_book_title);
            mBookSubtitle = (TextView) itemView.findViewById(R.id.content_book_subtitle);
            mDescription = (MyTextView) itemView.findViewById(R.id.content_book_description);
            mBookContent = (ViewGroup) itemView.findViewById(R.id.content_book_content);
        }
    }

}
