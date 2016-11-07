package com.ks.redditreader.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import com.ks.redditreader.BuildConfig;
import com.ks.redditreader.R;
import com.ks.redditreader.activities.WebViewActivity;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by karn.shah on 03-11-2016.
 */

public class Utils {
    public static final String CLIENT_ID = "mcuH2S28YOeJng";

    public static final String EXTRA_URL = "extra_url";

    public static final String EXTRA_TITLE = "extra_title";

    public static final String ACCOUNT = "Account";

    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;

    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;

    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;

    public static final String CONTENT_PROVIDER_AUTHORITY = BuildConfig.AUTHORITY /*+ ".submissions_provider.authority"*/;

    public static final String SPACE = "  ";
    public static final String NEW_LINE = "\n";

    public static String timeElapsed(long time) {

        long timeElapsedInMillis = System.currentTimeMillis() - time;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = timeElapsedInMillis / daysInMilli;
        if (elapsedDays > 0) {
            return elapsedDays + "d";
        }

        timeElapsedInMillis = timeElapsedInMillis % daysInMilli;
        long elapsedHours = timeElapsedInMillis / hoursInMilli;
        if (elapsedHours > 0) {
            return elapsedHours + "h";
        }

        timeElapsedInMillis = timeElapsedInMillis % hoursInMilli;
        long elapsedMinutes = timeElapsedInMillis / minutesInMilli;
        return elapsedMinutes + "m";
    }

    public static void startWebViewActivity(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Utils.EXTRA_URL, url);
        intent.putExtra(Utils.EXTRA_TITLE, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static Account createSyncAccount(Context context) {
        Account newAccount = new Account(ACCOUNT, context.getString(R.string.account_type));
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(newAccount, null, null);
        ContentResolver
                .setSyncAutomatically(newAccount, Utils.CONTENT_PROVIDER_AUTHORITY, true);
        return newAccount;
    }
}
