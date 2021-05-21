package com.pyt.postyourfun.activity;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;

/**
 * Created by r8tin on 3/31/16.
 */
public class BaseActivity extends FragmentActivity {
    private ProgressDialog progressDialog;

    public void showProgressDialog() {
        try {
            if (progressDialog != null) progressDialog.dismiss();
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
