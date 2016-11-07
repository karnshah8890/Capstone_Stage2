package com.ks.redditreader.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.recyclerext.adapter.RecyclerCursorAdapter;
import com.ks.redditreader.R;
import com.ks.redditreader.activities.BaseActivity;
import com.ks.redditreader.common.EndlessRecyclerViewScrollListener;
import com.ks.redditreader.common.OnRVItemClickListener;
import com.ks.redditreader.common.SubmissionsCursorLoader;
import com.ks.redditreader.model.SubmissionModel;
import com.ks.redditreader.model.SubmissionsTable;
import com.ks.redditreader.model.SubredditsModel;
import com.ks.redditreader.utils.Utils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RedditPostsCursorFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String SUBREDDIT = "subreddit_text";

    private String mSubreddit;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeView;

    private MyAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mSubreddit = args.getString(SUBREDDIT);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reddit_posts, container, false);
        ButterKnife.bind(this, view);
        mSwipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader(0, null, RedditPostsCursorFragment.this);
            }
        });

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount,
                        StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(sglm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getLoaderManager().getLoader(0).onContentChanged();
            }
        });
        mAdapter = new MyAdapter(new MatrixCursor(SubmissionModel.COLUMNS), getActivity(),
                (BaseActivity) getActivity());
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (SubredditsModel.DEFAULT_SUB_ALL.equals(mSubreddit.toLowerCase())) {
            Cursor cursor = getActivity().getContentResolver()
                    .query(SubmissionsTable.CONTENT_URI, null, null, null, null);
            swapCursor(cursor);
        }

        if (isConnected()) {
            showRefreshing(true);
        } else {
            showRefreshing(false);
            Toast.makeText(getActivity(), R.string.network_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showRefreshing(final boolean refreshing) {
        mSwipeView.post(new Runnable() {
            @Override
            public void run() {
                mSwipeView.setRefreshing(refreshing);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeView != null) {
            mSwipeView.setRefreshing(false);
            mSwipeView.destroyDrawingCache();
            mSwipeView.clearAnimation();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        showRefreshing(true);
        return new SubmissionsCursorLoader(getActivity(), mSubreddit);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swapCursor(data);
        showRefreshing(false);
    }

    private void swapCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }

        mAdapter.changeCursor(cursor);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private static class MyAdapter extends RecyclerCursorAdapter<ViewHolder> {

        private Activity mContext;

        private OnRVItemClickListener mOnRVItemClickListener;

        public MyAdapter(Cursor cursor, Activity context,
                         OnRVItemClickListener onRVItemClickListener) {
            super(cursor);
            mContext = context;
            mOnRVItemClickListener = onRVItemClickListener;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mContext.getLayoutInflater()
                    .inflate(R.layout.list_item_submission, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getCursor().moveToPosition((Integer) view.getTag());
                    String url = getCursor().getString(
                            SubmissionModel.getColumnIndex(SubmissionModel.SHORT_URL));
                    String title = getCursor().getString(
                            SubmissionModel.getColumnIndex(SubmissionModel.TITLE));
                    String subRedditeName = getCursor().getString(
                            SubmissionModel.getColumnIndex(SubmissionModel.SUBREDDIT_NAME));
                    mOnRVItemClickListener.onItemClicked(title, url,subRedditeName);
                }
            });
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, Cursor cursor,
                                     int position) {
            holder.itemView.setTag(position);
            holder.titleView.setText(
                    cursor.getString(SubmissionModel.getColumnIndex(SubmissionModel.TITLE)));

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("r/")
                    .append(cursor.getString(
                            SubmissionModel.getColumnIndex(SubmissionModel.SUBREDDIT_NAME)))
                    .append(Utils.SPACE).append(mContext.getString(R.string.bullet_point))
                    .append(Utils.SPACE)
                    .append(Utils.timeElapsed(cursor.getLong(
                            SubmissionModel.getColumnIndex(SubmissionModel.CREATED_TIME))))
                    .append(Utils.SPACE).append(mContext.getString(R.string.bullet_point))
                    .append(Utils.SPACE)
                    .append("u/")
                    .append(cursor
                            .getString(SubmissionModel.getColumnIndex(SubmissionModel.AUTHOR)));
            holder.subtextView.setText(stringBuilder.toString());

            holder.upvotesView.setText(cursor
                    .getString(SubmissionModel.getColumnIndex(SubmissionModel.SCORE)));
            holder.commentsView.setText(cursor
                    .getString(SubmissionModel.getColumnIndex(SubmissionModel.COMMENT_COUNT)));

            holder.thumbnailView.setVisibility(View.VISIBLE);
            String url = cursor
                    .getString(SubmissionModel.getColumnIndex(SubmissionModel.THUMBNAIL));
            if (url != null) {
                Picasso.with(mContext)
                        .load(url)
                        .into(holder.thumbnailView);
            } else {
                holder.thumbnailView.setVisibility(View.GONE);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumbnail)
        ImageView thumbnailView;

        @BindView(R.id.title)
        TextView titleView;

        @BindView(R.id.subtext)
        TextView subtextView;

        @BindView(R.id.upvotes_text)
        TextView upvotesView;

        @BindView(R.id.comments_text)
        TextView commentsView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
