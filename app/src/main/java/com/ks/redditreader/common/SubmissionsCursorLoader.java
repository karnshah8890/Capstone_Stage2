package com.ks.redditreader.common;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v4.content.AsyncTaskLoader;

import com.ks.redditreader.RedditReaderApplication;
import com.ks.redditreader.model.SubmissionModel;
import com.ks.redditreader.model.SubmissionsTable;
import com.ks.redditreader.model.SubredditsModel;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;

public class SubmissionsCursorLoader extends AsyncTaskLoader<Cursor> {

    private static final int MAX_POSTS_TO_SHOW = 300;

    private String mSubreddit;

    private Cursor mList;

    private SubredditPaginator mListings;

    private MatrixCursor mMatrixCursor;


    public SubmissionsCursorLoader(Context context, String subreddit) {
        super(context);
        mSubreddit = subreddit;
        mMatrixCursor = new MatrixCursor(SubmissionModel.COLUMNS);
    }

    @Override
    public Cursor loadInBackground() {

        RedditClient reddit = RedditReaderApplication.getRedditClient();

        if (reddit.isAuthenticated()) {
            if (mListings == null) {
                if (mSubreddit.toLowerCase().equals(SubredditsModel.DEFAULT_SUB_ALL)) {
                    mListings = new SubredditPaginator(reddit);
                    getContext().getContentResolver()
                            .delete(SubmissionsTable.CONTENT_URI, null, null);
                } else {
                    mListings = new SubredditPaginator(reddit, mSubreddit);
                }
            }

            if (mMatrixCursor.getCount() < MAX_POSTS_TO_SHOW) {
                Listing<Submission> submissions = mListings.next();

                int id = mMatrixCursor.getCount();

                for (Submission submission : submissions) {
                    if (!submission.isNsfw()) {
                        mMatrixCursor.addRow(new Object[]{id, submission.getThumbnail(),
                                submission.getPostHint(),
                                submission.getDomain(), submission.getTitle(),
                                submission.getSubredditName(),
                                submission.getCreated().getTime(), submission.getAuthor(),
                                submission.getVote().getValue(),
                                submission.getScore(), submission.getCommentCount(),
                                submission.getShortURL()});

                        if (mSubreddit.toLowerCase().equals("all")) {
                            getContext().getContentResolver().insert(SubmissionsTable.CONTENT_URI,
                                    SubmissionsTable
                                            .getContentValues(new SubmissionModel(id, submission),
                                                    false));
                        }
                        id++;
                    }
                }
            }
        }
        return mMatrixCursor;
    }

    @Override
    public void deliverResult(Cursor list) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (list != null) {
                onReleaseResources(list);
            }
        }
        Cursor oldList = mList;
        mList = list;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(list);
        }

        // At this point we can release the resources associated with
        // 'oldList' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldList != null) {
            onReleaseResources(oldList);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mList != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mList);
        }

        if (takeContentChanged() || mList == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mList != null) {
            onReleaseResources(mList);
            mList = null;
        }

    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(Cursor apps) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}