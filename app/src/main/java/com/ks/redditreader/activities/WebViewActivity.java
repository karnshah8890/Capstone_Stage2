package com.ks.redditreader.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ks.redditreader.R;
import com.ks.redditreader.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.progressBar)
    ProgressBar mProgress;

    @BindView(R.id.my_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    private String mUrl;

    private Intent mShareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpProgressBar();
        setUpWebView();

        mUrl = getIntent().getStringExtra(Utils.EXTRA_URL);
        if (mUrl != null) {
            mWebView.loadUrl(mUrl);
        }

        String title = getIntent().getStringExtra(Utils.EXTRA_TITLE);
        setTitle(title);
        prepareShareIntent(title);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(mShareIntent);
            }
        });
    }

    private void setUpWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);       //Zoom Control on web (You don't need this
        //if ROM supports Multi-Touch
        webSettings.setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgress.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgress.setProgress(newProgress);
            }
        });
    }

    private void setUpProgressBar() {
        mProgress.setIndeterminate(false);
        mProgress.setMax(100);
    }

    private void prepareShareIntent(String title) {
        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        mShareIntent.removeExtra(Intent.EXTRA_TEXT);
        String textToShare = new StringBuilder()
                .append(getString(R.string.hey_checkout_reddit_post))
                .append(Utils.NEW_LINE)
                .append(title)
                .append(Utils.NEW_LINE)
                .append(mUrl).toString();
        mShareIntent
                .putExtra(Intent.EXTRA_TEXT,
                        textToShare);
        mShareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.reddit_post));
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

}
