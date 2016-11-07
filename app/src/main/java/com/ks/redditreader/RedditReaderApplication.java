package com.ks.redditreader;

import android.app.Application;

import com.ks.redditreader.utils.Utils;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.LoggingMode;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;

import java.util.UUID;

public class RedditReaderApplication extends Application {

    private static RedditClient mReddit;

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getRedditClient();
            }
        }).start();

    }


    public synchronized static RedditClient getRedditClient() {
        if (mReddit != null && mReddit.isAuthenticated()) {
            return mReddit;
        }

        mReddit = new RedditClient(
                UserAgent.of("android:com.ks.redditreader:v0.1"));
        if (BuildConfig.DEBUG) {
            mReddit.setLoggingMode(LoggingMode.ALWAYS);
        }

        try {
            Credentials credentials = Credentials
                    .userlessApp(Utils.CLIENT_ID, UUID
                            .randomUUID());
            OAuthData authData = mReddit.getOAuthHelper().easyAuth(credentials);
            mReddit.authenticate(authData);
            return mReddit;
        } catch (OAuthException e) {
            e.printStackTrace();
        } catch (NetworkException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mReddit;
    }
}
