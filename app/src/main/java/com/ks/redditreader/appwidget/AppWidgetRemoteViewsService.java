package com.ks.redditreader.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ks.redditreader.R;
import com.ks.redditreader.model.SubmissionModel;
import com.ks.redditreader.model.SubmissionsTable;
import com.ks.redditreader.utils.Utils;


public class AppWidgetRemoteViewsService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ListProvider(this.getApplicationContext(), intent));
    }

    public static class ListProvider implements RemoteViewsFactory {

        private Cursor mCursor;

        private Context context = null;

        private int appWidgetId;

        public ListProvider(Context context, Intent intent) {
            this.context = context;
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (mCursor != null) {
                mCursor.close();
            }

            mCursor = context.getContentResolver()
                    .query(SubmissionsTable.CONTENT_URI, null, null, null, null);

        }

        @Override
        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


        @Override
        public RemoteViews getViewAt(int position) {
            final RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.list_item_appwidget);

            if (mCursor.moveToPosition(position)) {
                String title = mCursor
                        .getString(SubmissionModel.getColumnIndex(SubmissionModel.TITLE));
                rv.setTextViewText(R.id.title, title);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("r/")
                        .append(mCursor.getString(
                                SubmissionModel.getColumnIndex(SubmissionModel.SUBREDDIT_NAME)))
                        .append(Utils.SPACE).append(context.getString(R.string.bullet_point)).append(
                        Utils.SPACE)
                        .append(Utils.timeElapsed(mCursor.getLong(
                                SubmissionModel.getColumnIndex(SubmissionModel.CREATED_TIME))))
                        .append(Utils.SPACE).append(context.getString(R.string.bullet_point)).append(
                        Utils.SPACE)
                        .append("u/")
                        .append(mCursor
                                .getString(SubmissionModel.getColumnIndex(SubmissionModel.AUTHOR)));
                rv.setTextViewText(R.id.subtext, stringBuilder.toString());

                rv.setTextViewText(R.id.upvotes_text, mCursor
                        .getString(SubmissionModel.getColumnIndex(SubmissionModel.SCORE)));
                rv.setTextViewText(R.id.comments_text, mCursor
                        .getString(SubmissionModel.getColumnIndex(SubmissionModel.COMMENT_COUNT)));

//                rv.setViewVisibility(R.id.thumbnail, View.VISIBLE);
//                String url = mCursor
//                        .getString(SubmissionModel.getColumnIndex(SubmissionModel.THUMBNAIL));
//                if (url != null) {
//                    Picasso.with(context)
//                            .load(url)
//                            .into(rv, R.id.thumbnail, new int[]{appWidgetId});
//                } else {
                rv.setViewVisibility(R.id.thumbnail, View.GONE);
//                }

//Fill in intent
                Bundle extras = new Bundle();
                extras.putString(Utils.EXTRA_URL, mCursor.getString(SubmissionModel.getColumnIndex(SubmissionModel.SHORT_URL)));
                extras.putString(Utils.EXTRA_TITLE, mCursor.getString(
                        SubmissionModel.getColumnIndex(SubmissionModel.TITLE)));
                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                // Make it possible to distinguish the individual on-click
                // action of a given item
                rv.setOnClickFillInIntent(R.id.appwidget_item, fillInIntent);
            }

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }


    }
}