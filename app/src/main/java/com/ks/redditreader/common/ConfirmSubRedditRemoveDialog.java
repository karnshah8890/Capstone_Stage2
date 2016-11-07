package com.ks.redditreader.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.ks.redditreader.R;
import com.ks.redditreader.activities.ManageSubredditsActivity;
import com.ks.redditreader.model.SubredditsTable;


public class ConfirmSubRedditRemoveDialog extends DialogFragment {

    public static final String SUBREDDIT_TITLE = "param1";

    public static final String SUBREDDIT_ID = "param2";

    public static final String ADAPTER_POSITION = "param3";

    private RemoveSubRedditsListener mRemoveSubredditsListener;

    public ConfirmSubRedditRemoveDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mRemoveSubredditsListener = ((ManageSubredditsActivity) getActivity());
        final String subredditName = getArguments().getString(SUBREDDIT_TITLE);
        final int subredditId = getArguments().getInt(SUBREDDIT_ID);
        final int adapterPosition = getArguments().getInt(ADAPTER_POSITION);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.remove_subreddit)
                .setMessage(subredditName)
                .setPositiveButton(R.string.remove,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                getActivity().getContentResolver()
                                        .delete(SubredditsTable.CONTENT_URI, "_id = ?", new String[]{
                                                String.valueOf(subredditId)});

                                mRemoveSubredditsListener.subredditRemoved(adapterPosition);
                            }
                        }
                )
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();

                            }
                        }
                )
                .create();
    }



}