package com.ks.redditreader.activities;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ks.redditreader.R;
import com.ks.redditreader.adapters.SubRedditsViewPagerAdapter;
import com.ks.redditreader.appwidget.MyAppWidgetProvider;
import com.ks.redditreader.common.AddSubredditDialog;
import com.ks.redditreader.common.OnRVItemClickListener;
import com.ks.redditreader.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ks.redditreader.utils.Utils.SYNC_INTERVAL;

/**
 * Created by karn.shah on 03-11-2016.
 */

public class BaseActivity extends AppCompatActivity implements OnRVItemClickListener {
    private static final String DIALOG_FRAGMENT_TAG = "dialog_fragment";

    @BindView(R.id.my_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    private Account mAccount;
    private SubRedditsViewPagerAdapter mViewPagerAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mViewPager.setOffscreenPageLimit(3);
        mViewPagerAdapter = new SubRedditsViewPagerAdapter(getSupportFragmentManager(),
                BaseActivity.this);
        mViewPager.setAdapter(mViewPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        mAccount = Utils.createSyncAccount(this);
        if (ContentResolver.isSyncPending(mAccount, Utils.CONTENT_PROVIDER_AUTHORITY) ||
                ContentResolver.isSyncActive(mAccount, Utils.CONTENT_PROVIDER_AUTHORITY)) {
            Log.i("ContentResolver", "SyncPending, canceling");
            ContentResolver.cancelSync(mAccount, Utils.CONTENT_PROVIDER_AUTHORITY);
        }
        ContentResolver
                .addPeriodicSync(mAccount, Utils.CONTENT_PROVIDER_AUTHORITY, Bundle.EMPTY,
                        SYNC_INTERVAL);
        ContentResolver.setSyncAutomatically(mAccount, Utils.CONTENT_PROVIDER_AUTHORITY, true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyAppWidgetProvider.updateAppWidgets(BaseActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_subreddit) {
            addSubReddit();
            return true;
        }
        if (id == R.id.action_manage_subreddits) {
            manageSubReddits();
        }
        return super.onOptionsItemSelected(item);
    }

    private void manageSubReddits() {
        Intent intent = new Intent(this, ManageSubredditsActivity.class);
        startActivity(intent);
    }

    private void addSubReddit() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        new AddSubredditDialog().show(fragmentTransaction, DIALOG_FRAGMENT_TAG);
    }

    public void reloadSubredditsList(String newSubreddit) {
        mViewPagerAdapter.addSubreddit(newSubreddit);
        mViewPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mViewPagerAdapter.getCount());
    }

    @Override
    public void onItemClicked(String title, String url, String subRedditeName) {
        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.VALUE, subRedditeName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM,
                payload);
        Utils.startWebViewActivity(BaseActivity.this, url, title);
    }


}
