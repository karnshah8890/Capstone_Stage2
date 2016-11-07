package com.ks.redditreader.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.ks.redditreader.R;
import com.ks.redditreader.RedditReaderApplication;
import com.ks.redditreader.activities.BaseActivity;

import java.util.ArrayList;
import java.util.List;


public class AddSubredditDialog extends DialogFragment {

    private MyListAdapter mAdapter;

    public AddSubredditDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();

        LayoutInflater layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_add_subreddit, null);

        ListView listView = (ListView) view.findViewById(R.id.listView);
        mAdapter = new MyListAdapter(getActivity(), new ArrayList<String>());
        listView.setAdapter(mAdapter);

        EditText editText = (EditText) view.findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 2) {
                    if (mAdapter != null) {
                        mAdapter.getFilter().filter(charSequence);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view1, int position,
                    long id) {

                String newSubreddit = (String) mAdapter.getItem(position);
                ((BaseActivity) getActivity()).reloadSubredditsList(newSubreddit);
                AddSubredditDialog.this.dismiss();
            }
        });

        return new AlertDialog.Builder(activity)
                .setTitle(R.string.action_add_subreddit)
                .setView(view)
                .create();
    }

    static class MyListAdapter extends BaseAdapter implements Filterable {

        private List<String> mSubreddits;

        private LayoutInflater mInflater;

        private SubredditsFilter mFilter;

        MyListAdapter(Context context, List<String> subreddits) {
            mSubreddits = subreddits;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mSubreddits.size();
        }

        @Override
        public Object getItem(int i) {
            return mSubreddits.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(mSubreddits.get(position));

            return convertView;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new SubredditsFilter();
            }
            return mFilter;
        }

        static class ViewHolder {

            TextView text;
        }

        private class SubredditsFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                filterResults.values = new ArrayList<String>();
                filterResults.count = 0;

                if (RedditReaderApplication.getRedditClient().isAuthenticated()) {
                    List<String> subredditsByTopic = RedditReaderApplication.getRedditClient().getSubredditsByTopic(
                            String.valueOf(charSequence));
                    filterResults.values = subredditsByTopic;
                    filterResults.count = subredditsByTopic.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    mSubreddits = (List<String>) filterResults.values;
                    notifyDataSetChanged();
                }
            }
        }
    }

}