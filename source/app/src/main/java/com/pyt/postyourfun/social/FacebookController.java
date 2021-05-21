package com.pyt.postyourfun.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.Arrays;
import java.util.Collection;

public class FacebookController {

    private Context context = null;
    private ShareDialog shareDialog = null;

    // Facebook Login callback
    public CallbackManager callbackManager = null;

    // Facebook Default Permissions
    Collection<String> _defaultPermissions = Arrays.asList("public_profile", "user_friends", "email");

    // Facebook AccessToken
    public AccessToken accessToken = null;
    AccessTokenTracker accessTokenTracker = null;

    // Facebook Profile
    ProfileTracker profileTracker = null;
    public Profile profile = null;

    // FacebookController shared instance
    private static FacebookController _sharedInstance = null;

    public static FacebookController sharedInstance() {
        if (_sharedInstance == null) {
            _sharedInstance = new FacebookController();
        }
        return _sharedInstance;
    }

    public void init(Context context, FacebookCallback<LoginResult> callback) {
        this.context = context;

        FacebookSdk.sdkInitialize(context);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, callback);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken1) {
                FacebookController.this.accessToken = AccessToken.getCurrentAccessToken();
            }
        };
        accessToken = AccessToken.getCurrentAccessToken();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                FacebookController.this.profile = Profile.getCurrentProfile();
            }
        };
        profile = Profile.getCurrentProfile();
    }

    public void loginWithFacebook(Fragment fragment, Collection<String> permissions) {
        if (permissions == null) permissions = _defaultPermissions;
        LoginManager.getInstance().logInWithReadPermissions(fragment, permissions);
    }

    public void loginWithFacebook(Activity activity, Collection<String> permissions) {
        if (permissions == null) permissions = _defaultPermissions;
        LoginManager.getInstance().logInWithReadPermissions(activity, permissions);
    }

    public void shareWithFaceBook(Activity activity, String description) {
        shareDialog = new ShareDialog(activity);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder().setContentDescription(description).setContentTitle("PostYourFun Share").build();
            shareDialog.show(content);
        }
    }

    public void shareWithFaceBook(Fragment fragment, String description, String park_name, String park_url, String image_url) {
        shareDialog = new ShareDialog(fragment);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder().setContentDescription(description)
                    .setContentTitle(park_name)
                    .setContentUrl(Uri.parse(park_url))
                    .setImageUrl(Uri.parse(image_url))
                    .build();
            shareDialog.show(content);
        }
    }

    public void shareWithFaceBook(Fragment fragment, String description, String park_name, String park_url, String image_url, String placeId) {
        shareDialog = new ShareDialog(fragment);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder().setContentDescription(description)
                    .setContentTitle(park_name)
                    .setContentUrl(Uri.parse(park_url))
                    .setImageUrl(Uri.parse(image_url))
                    .setPlaceId(placeId)
                    .build();
            shareDialog.show(content);
        }
    }

    /*
    public void shareWithFaceBook(Fragment fragment, String description){
        shareDialog = new ShareDialog(fragment);
        if (ShareDialog.canShow(ShareLinkContent.class)){
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentDescription("Post Your Fun FB share Test,but not working now")
                    .setContentTitle("PostYourFun Share")
                    .build();
            shareDialog.show(content);
        }
    }
*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void onDestroy() {
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
}