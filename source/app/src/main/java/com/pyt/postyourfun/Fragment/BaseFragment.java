package com.pyt.postyourfun.Fragment;

import android.support.v4.app.Fragment;

import com.pyt.postyourfun.activity.BaseActivity;

/**
 * Created by r8tin on 3/31/16.
 */
public class BaseFragment extends Fragment {
    protected void showProgressDialog() {
        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity != null) baseActivity.showProgressDialog();
    }

    protected void dismissProgressDialog() {
        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity != null) baseActivity.dismissProgressDialog();
    }

    protected BaseActivity getBaseActivity() {
        try {
            return (BaseActivity) getActivity();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}