package com.baurine.instamaterial.ui.manager;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogManager {

    private static ProgressDialogManager mInstance;

    private ProgressDialog mProgresssDialog;

    public static ProgressDialogManager getInstance() {
        if (mInstance == null) {
            mInstance = new ProgressDialogManager();
        }
        return mInstance;
    }

    private ProgressDialogManager() {
    }

    public void showProgressDialog(Context context, String content) {
        if (mProgresssDialog != null) {
            return;
        }
        mProgresssDialog = new ProgressDialog(context);
        mProgresssDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgresssDialog.setMessage(content);
        mProgresssDialog.setIndeterminate(false);
        mProgresssDialog.setCancelable(false);
        mProgresssDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgresssDialog != null) {
            mProgresssDialog.cancel();
            mProgresssDialog = null;
        }
    }
}
