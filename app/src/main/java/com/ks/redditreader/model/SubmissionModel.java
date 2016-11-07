package com.ks.redditreader.model;

import net.dean.jraw.models.Submission;

import java.util.Arrays;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

@SimpleSQLTable(table = "submissions", provider = "SubmissionsProvider")
public class SubmissionModel {

    public static final String ID = "_id";

    public static final String THUMBNAIL = "thumbnail";

    public static final String POSTHINT = "posthint";

    public static final String DOMAIN = "domain";

    public static final String TITLE = "title";

    public static final String SUBREDDIT_NAME = "subreddit_name";

    public static final String CREATED_TIME = "created_time";

    public static final String AUTHOR = "author";

    public static final String VOTE_VALUE = "vote_value";

    public static final String SCORE = "score";

    public static final String COMMENT_COUNT = "comment_count";

    public static final String SHORT_URL = "short_url";

    public static final String[] COLUMNS = new String[]{ID, THUMBNAIL, POSTHINT, DOMAIN, TITLE,
            SUBREDDIT_NAME, CREATED_TIME, AUTHOR, VOTE_VALUE, SCORE,
            COMMENT_COUNT, SHORT_URL};

    @SimpleSQLColumn(ID)
    public int _id;

    @SimpleSQLColumn(THUMBNAIL)
    public String thumbnail;

    @SimpleSQLColumn(POSTHINT)
    public String postHint;

    @SimpleSQLColumn(DOMAIN)
    public String domain;

    @SimpleSQLColumn(TITLE)
    public String title;

    @SimpleSQLColumn(SUBREDDIT_NAME)
    public String subredditName;

    @SimpleSQLColumn(CREATED_TIME)
    public long createdTime;

    @SimpleSQLColumn(AUTHOR)
    public String author;

    @SimpleSQLColumn(VOTE_VALUE)
    public int voteValue;

    @SimpleSQLColumn(SCORE)
    public int score;

    @SimpleSQLColumn(COMMENT_COUNT)
    public int commentCount;

    @SimpleSQLColumn(SHORT_URL)
    public String shortUrl;

    public SubmissionModel() {

    }

    public SubmissionModel(int id, Submission submission) {
        _id = id;
        thumbnail = submission.getThumbnail();
        postHint = submission.getPostHint().name();
        domain = submission.getDomain();
        title = submission.getTitle();
        subredditName = submission.getSubredditName();
        createdTime = submission.getCreated().getTime();
        author = submission.getAuthor();
        voteValue = submission.getVote().getValue();
        score = submission.getScore();
        commentCount = submission.getCommentCount();
        shortUrl = submission.getShortURL();
    }

    public static int getColumnIndex(String columnName) {
        return Arrays.asList(COLUMNS).indexOf(columnName);
    }
}
