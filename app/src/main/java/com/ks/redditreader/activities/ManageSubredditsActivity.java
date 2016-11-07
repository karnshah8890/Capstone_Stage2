package com.ks.redditreader.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devbrackets.android.recyclerext.adapter.RecyclerCursorAdapter;
import com.ks.redditreader.R;
import com.ks.redditreader.common.ConfirmSubRedditRemoveDialog;
import com.ks.redditreader.common.RemoveSubRedditsListener;
import com.ks.redditreader.model.SubredditsModel;
import com.ks.redditreader.model.SubredditsTable;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ManageSubredditsActivity extends AppCompatActivity implements
        RemoveSubRedditsListener {

    private static final String REMOVE_SUBREDDITS = "remove_subreddits";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subreddits);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ManageSubredditsActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        initAdapter();

    }

    private void initAdapter() {
        mAdapter = new MyAdapter(
                this,
                getContentResolver().query(SubredditsTable.CONTENT_URI, null, null, null, null)
        );
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        To update subreddits in MainActivity viewpager
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void subredditRemoved(int position) {
        mAdapter.notifyItemRemoved(position);
        mAdapter.changeCursor(getContentResolver().query(SubredditsTable.CONTENT_URI, null, null, null, null));
    }

    private void showConfirmSubredditRemoveDialog(int position, int subredditId,
            String subredditName) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(REMOVE_SUBREDDITS);
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putInt(ConfirmSubRedditRemoveDialog.ADAPTER_POSITION, position);
        bundle.putInt(ConfirmSubRedditRemoveDialog.SUBREDDIT_ID, subredditId);
        bundle.putString(ConfirmSubRedditRemoveDialog.SUBREDDIT_TITLE, subredditName);

        ConfirmSubRedditRemoveDialog
                removeDialog = new ConfirmSubRedditRemoveDialog();
        removeDialog.setArguments(bundle);
        removeDialog.show(ft, REMOVE_SUBREDDITS);
    }

    static class MyAdapter extends RecyclerCursorAdapter<MyAdapter.MyViewHolder> {

        private Activity mContext;

        public MyAdapter(Activity context, Cursor cursor) {
            super(cursor);
            mContext = context;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = mContext.getLayoutInflater()
                    .inflate(R.layout.list_item_manage_subreddits, parent, false);
            itemView.findViewById(R.id.delete_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final int position = (int) view.getTag();
                            Cursor cursor = getCursor(position);

                            int subredditId = cursor
                                    .getInt(SubredditsModel.getColumnIndex(SubredditsModel.ID));
                            String subredditName = cursor.getString(
                                    SubredditsModel.getColumnIndex(SubredditsModel.SUBREDDITS));

                            ((ManageSubredditsActivity) mContext)
                                    .showConfirmSubredditRemoveDialog(position, subredditId,
                                            subredditName);
                        }
                    });
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, Cursor cursor, int position) {
            holder.deleteButton.setTag(position);
            holder.textView.setText(
                    cursor.getString(SubredditsModel.getColumnIndex(SubredditsModel.SUBREDDITS)));

        }


        public static class MyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.text)
            TextView textView;

            @BindView(R.id.delete_button)
            ImageView deleteButton;

            public MyViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }


}
