package com.pyt.postyourfun.social;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.pyt.postyourfun.R;
import com.pyt.postyourfun.Utils.CognitoSyncClientManager;

public class SocialController {

    private Context _context = null;
    private SocialControllerInterface _callcack = null;

    private FacebookController fbController = null;

    private static SocialController _sharedInstance = null;

    public static SocialController sharedInstance(Context context, SocialControllerInterface callcack) {
        if (_sharedInstance == null) {
            _sharedInstance = new SocialController();
            _sharedInstance._context = context;
            _sharedInstance._callcack = callcack;
        }
        return _sharedInstance;
    }

    public void loginWithFacebook(Activity activity) {
        initFacebookController();
        fbController.loginWithFacebook(activity, null);
    }

    public void loginWithFacebook(Fragment fragment) {
        initFacebookController();
        fbController.loginWithFacebook(fragment, null);
    }

    public void shareWithFaceBook(Activity activity, String description) {
        initFacebookController();
        fbController.shareWithFaceBook(activity, description);
    }

    public void shareWithFaceBook(Fragment fragment, String description, String park_name, String park_url, String image_url) {
        initFacebookController();
        fbController.shareWithFaceBook(fragment, description, park_name, park_url, image_url);
    }

    public void shareWithFaceBook(Fragment fragment, String description, String park_name, String park_url, String image_url, String placeId) {
        initFacebookController();
        fbController.shareWithFaceBook(fragment, description, park_name, park_url, image_url, placeId);
    }

    private void initFacebookController() {
        if (fbController == null) {
            fbController = FacebookController.sharedInstance();
            fbController.init(_context, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    if (_callcack != null) {
                        _callcack.onSuccess(SocialControllerInterface.FACEBOOK, SocialControllerInterface.ACTION.login.getValue());
                        Log.d("FB_TOKEN from Social: ", fbController.accessToken.getToken().toString());
                        CognitoSyncClientManager clientManager = new CognitoSyncClientManager();
                        clientManager.init(_context);
                        clientManager.addLogins("graph.facebook.com", fbController.accessToken.getToken());
                    }
                }

                @Override
                public void onCancel() {
                    showAlert();
                }

                @Override
                public void onError(FacebookException e) {
                    if (e instanceof FacebookAuthorizationException) {
                        showAlert();
                    }
                }

                private void showAlert() {
                    if (_callcack != null)
                        _callcack.onFailure(SocialControllerInterface.FACEBOOK, SocialControllerInterface.ACTION.login.getValue());
                    new AlertDialog.Builder(_context).setTitle(R.string.cancelled)
                            .setMessage(R.string.permission_not_granted)
                            .setPositiveButton(R.string.ok, null)
                            .show();
                }
            });
        }
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (fbController != null) {
            fbController.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onDestroy() {
        if (fbController != null) {
            fbController.onDestroy();
        }
    }
}
