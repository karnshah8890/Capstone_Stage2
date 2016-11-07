package com.ks.redditreader.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ks.redditreader.fragments.RedditPostsCursorFragment;
import com.ks.redditreader.model.SubredditsModel;
import com.ks.redditreader.model.SubredditsTable;

import java.util.List;

public class SubRedditsViewPagerAdapter extends FragmentStatePagerAdapter {

    private final Context mContext;

    private List<SubredditsModel> mList;

    public SubRedditsViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        loadSubreddits();
    }

    private void loadSubreddits() {
        Cursor cursor = mContext.getContentResolver()
                .query(SubredditsTable.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() == 0) {
            insertDefaultSubreddit(0, SubredditsModel.DEFAULT_SUB_ALL);
            insertDefaultSubreddit(1, SubredditsModel.DEFAULT_SUB_FUNNY);
            insertDefaultSubreddit(2, SubredditsModel.DEFAULT_SUB_PICS);
            insertDefaultSubreddit(3, SubredditsModel.DEFAULT_SUB_GIFS);
            insertDefaultSubreddit(4, SubredditsModel.DEFAULT_SUB_VIDEOS);
            insertDefaultSubreddit(5, SubredditsModel.DEFAULT_SUB_POLITICS);
            cursor.close();
            cursor = mContext.getContentResolver()
                    .query(SubredditsTable.CONTENT_URI, null, null, null, null);
        }

        mList = SubredditsTable.getRows(cursor, false);
        cursor.close();
    }

    public void addSubreddit(String newSubreddit) {
        Cursor cursor = mContext.getContentResolver()
                .query(SubredditsTable.CONTENT_URI, null, null, null, null);
        mContext.getContentResolver().insert(SubredditsTable.CONTENT_URI, SubredditsTable
                .getContentValues(
                        new SubredditsModel(cursor.getCount(), newSubreddit),
                        false));
        cursor.close();

        loadSubreddits();
    }

    private void insertDefaultSubreddit(int id, String subreddit) {
        mContext.getContentResolver().insert(SubredditsTable.CONTENT_URI, SubredditsTable
                .getContentValues(new SubredditsModel(id, subreddit), false));
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new RedditPostsCursorFragment();
        Bundle args = new Bundle();
        args.putString(RedditPostsCursorFragment.SUBREDDIT, mList.get(i).mSubreddit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position).mSubreddit;
    }
}
