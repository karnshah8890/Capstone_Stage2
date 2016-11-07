package com.ks.redditreader.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.ks.redditreader.RedditReaderApplication;
import com.ks.redditreader.appwidget.MyAppWidgetProvider;
import com.ks.redditreader.model.SubmissionModel;
import com.ks.redditreader.model.SubmissionsTable;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {


        RedditClient reddit = RedditReaderApplication.getRedditClient();
        if (reddit.isAuthenticated()) {
            SubredditPaginator mListings = new SubredditPaginator(reddit);
            if (mListings.hasNext()) {
                getContext().getContentResolver()
                        .delete(SubmissionsTable.CONTENT_URI, null, null);
            }

            int id = 0;
            Listing<Submission> submissions = mListings.next();
            for (Submission submission : submissions) {
                if (!submission.isNsfw()) {
                    getContext().getContentResolver().insert(SubmissionsTable.CONTENT_URI,
                            SubmissionsTable
                                    .getContentValues(new SubmissionModel(id, submission),
                                            false));
                    id++;
                }
            }

            MyAppWidgetProvider.updateAppWidgets(getContext());
        }
    }


}