package com.ks.redditreader.model;

import java.util.Arrays;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

@SimpleSQLTable(table = "subreddits", provider = "SubmissionsProvider")
public class SubredditsModel {

    public static final String DEFAULT_SUB_ALL = "all";
    public static final String DEFAULT_SUB_POLITICS = "politics";
    public static final String DEFAULT_SUB_FUNNY = "funny";
    public static final String DEFAULT_SUB_PICS = "pics";
    public static final String DEFAULT_SUB_GIFS = "gifs";
    public static final String DEFAULT_SUB_VIDEOS = "videos";

    public static final String ID = "_id";

    public static final String SUBREDDITS = "subreddits";

    public static final String[] COLUMNS = new String[]{ID, SUBREDDITS};

    @SimpleSQLColumn(ID)
    public int _id;

    @SimpleSQLColumn(SUBREDDITS)
    public String mSubreddit;

    public SubredditsModel() {

    }

    public SubredditsModel(int id, String subreddit) {
        _id = id;
        mSubreddit = subreddit;
    }

    public static int getColumnIndex(String columnName) {
        return Arrays.asList(COLUMNS).indexOf(columnName);
    }
}
