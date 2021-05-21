package com.pyt.postyourfun.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.regions.Regions;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.pyt.postyourfun.R;
import com.pyt.postyourfun.constants.Constants;
import com.pyt.postyourfun.dynamoDBClass.UserFacebookDetailsMapper;
import com.pyt.postyourfun.dynamoDBClass.UserMapper;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.UserDBManager;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.UserFacebookDBManager;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.UserFacebookDBManagerTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class LogInActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Post your Fun";
    private static SharedPreferences mSharedPreferences;

    //Controls
    private TextView infos;
    private Button gplus_loginButton;
    private Button fb_loginButton;
    private Button twitter_login_button;
    //FB callback manager
    private CallbackManager callbackManager;

    //G+ login values
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us from starting further intents.
     */
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    //Twitter Login Values
    private static Twitter twitter;
    private static RequestToken requestToken;
    private AccessToken accessToken;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private CognitoSyncManager syncClient;

    //FB user info
    private String FB_ID;
    private String FB_email;
    private String FB_first_name;
    private String FB_last_name;
    //G+ user info
    private String google_ID;
    private String google_email;
    //Twitter user info
    private String twitter_id;
    private String Twitter_first_Name;
    private String Twitter_last_name;
    private String twitter_email;

    private String User_ID;

    private boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //FaceBook LoginManager
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook Login Success", "Login");
                com.facebook.AccessToken accessToken = loginResult.getAccessToken();
                Log.d("Facebook AccessToken: ", accessToken.toString());

                getFBProfileInformation(accessToken);
//                gotoNextActivity();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LogInActivity.this, "Login Canceled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LogInActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        setContentView(R.layout.activity_login);

        mSharedPreferences = getApplicationContext().getSharedPreferences("twitter_login_pref", 0);
        fb_loginButton = (Button) this.findViewById(R.id.fb_login_button);
        gplus_loginButton = (Button) this.findViewById(R.id.google_login_button);
        twitter_login_button = (Button) this.findViewById(R.id.twitter_login_button);

        // Initialize the Amazon Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(LogInActivity.this, // Context
                Constants.IDENTITY_POOL_ID, // Identity Pool ID
                Regions.EU_WEST_1 // Region
        );
        // Initialize the Cognito Sync client
        syncClient = new CognitoSyncManager(LogInActivity.this, Regions.EU_WEST_1, // Region
                credentialsProvider);
        //Print hash key
//        printHashKey();

        //google API client
        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
//        gplus_loginButton.setOnClickListener(this);
//        twitter_login_button.setOnClickListener(this);
        fb_loginButton.setOnClickListener(this);

        // capture uri
        if (!isTwitterLoggedInAlready()) {

            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(Constants.CALLBACK_URL)) {
                // oAuth verifier
                final String verifier = uri.getQueryParameter(Constants.URL_TWITTER_OAUTH_VERIFIER);

                try {

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                // Get the access token
                                accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                                Log.v("accessToken", accessToken.getToken());
                                // Shared Preferences
                                mSharedPreferences = getApplicationContext().getSharedPreferences("twitter4j-sample", 0);
                                SharedPreferences.Editor e = mSharedPreferences.edit();

                                // After getting access token, access token secret
                                // store them in application preferences
                                e.putString(Constants.PREFERENCE_TWITTER_OAUTH_TOKEN, accessToken.getToken());
                                e.putString(Constants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, accessToken.getTokenSecret());
                                // Store login status - true
                                e.putBoolean(Constants.PREF_KEY_TWITTER_LOGIN, true);
                                e.commit(); // save changes

                                Log.e("Twitter OAuth Token1 ", "> " + accessToken.getToken());

                                //Get Twitter Profile Information
                                User user = twitter.showUser(accessToken.getUserId());
                                Log.d("Twitter Username: ", user.getName());
                                Log.d("Twitter Screen name: ", user.getScreenName());
                                Log.d("Twitter User ID: ", String.valueOf(user.getId()));
                                Log.d("Twitter User Location: ", user.getLocation());

                                twitter_id = String.valueOf(user.getId());
                                setTwitter_Token(accessToken.getToken(), accessToken.getTokenSecret());
                                gotoNextActivity();

                                // Hide login button
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                } catch (Exception e) {
                    // Check log for login errors
                    Log.e("Twitter Login Error", "> " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            gotoNextActivity();
        }
//        android_id = Settings.Secure.getString(LogInActivity.this.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//        Log.d("Device_ID: ", android_id);
//
        SharedPreferences sharedPreferences = LogInActivity.this.getSharedPreferences("user_info", 0);
        User_ID = sharedPreferences.getString("user_id", "");

        if (!User_ID.equals("")) {
            gotoNextActivity();
        }
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult != null && mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        // Get user's information
        getGoogleProfileInformation();

        gotoNextActivity();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    /**
     * Button on click listener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fb_login_button:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                break;
            case R.id.google_login_button:
                // Google Signin button clicked
                signInWithGplus();
                break;
            case R.id.twitter_login_button:
                //Twitter sign in button clicked
                loginToTwitter();
                break;
        }
    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_LOGIN, false);
    }

    //Twitter Login
    private void loginToTwitter() {
        // Check if already logged in
        if (!isTwitterLoggedInAlready()) {
            // Setup builder
            ConfigurationBuilder builder = new ConfigurationBuilder();
            // Get key and secret from Constants.java
            builder.setOAuthConsumerKey(Constants.CONSUMER_KEY);
            builder.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);

            // Build
            Configuration configuration = builder.build();
            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            // Start new thread for activity (you can't do too much work on the UI/Main thread.
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        requestToken = twitter.getOAuthRequestToken(Constants.CALLBACK_URL);
                        LogInActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
                        Log.e("Twitter OAuth Token2", "> " + accessToken.getToken());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else {
            gotoNextActivity();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    //Get Google Profile Information
    private void getGoogleProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String userId = currentPerson.getId();
                String personName = currentPerson.getDisplayName();
                String userName = currentPerson.getNickname();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String birthday = currentPerson.getBirthday();

                Log.e(TAG, "User ID: " + userId + ", Name: " + personName + ", Username: " + userName + ", email: " + email + ", Birthday: " + birthday);
                google_ID = userId;
                google_email = email;
            } else {
                Toast.makeText(getApplicationContext(), "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setGoogle_Token();
    }

    //Get FB Profile information
    private void getFBProfileInformation(com.facebook.AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                // Application code
                if (object != null) {
                    Log.d("JSONObject: ", object.toString());
                    JSONObject result = object;
                    try {
                        FB_ID = result.getString("id");
                        FB_email = result.getString("email");
                        FB_first_name = result.getString("first_name");
                        FB_last_name = result.getString("last_name");
                    } catch (JSONException e) {
                    }
                }
            }
        });
        Log.d("FB Token: ", accessToken.getToken());
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,first_name,last_name,middle_name");
        request.setParameters(parameters);
        request.executeAsync();

        setFB_Token(accessToken);
        showProgressDialog();
    }

    private void setFB_Token(com.facebook.AccessToken accessToken) {
        Map<String, String> login = new HashMap<String, String>();
        Log.d("FB token:", accessToken.getToken());
        login.put("graph.facebook.com", accessToken.getToken());
        credentialsProvider.withLogins(login);

        Dataset dataset = syncClient.openOrCreateDataset("LoginData");
        dataset.put("graph.facebook.com", accessToken.getToken());
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(Dataset dataset, List newRecords) {
                //Your handler code here
                Log.d("Sync FB client: ", "Success");
                new CheckUserFacebook().execute(FB_ID);
            }
        });
    }

    private void setTwitter_Token(String token, String secret) {
        Map<String, String> login = new HashMap<>();
        login.put("api.twitter.com", token + ";" + secret);
        credentialsProvider.withLogins(login);

        Dataset dataset = syncClient.openOrCreateDataset("TwitterLogin");
        dataset.put("api.twitter.com", token + ";" + secret);
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(Dataset dataset, List newRecords) {
                //Your handler code here
                Log.d("Sync Twitter client: ", "Success");
            }
        });
//        storeUserData(android_id, "", "", "", "", "", "");
//        UserTwitterDBManagerTask twitter_db_task = new UserTwitterDBManagerTask(getApplicationContext());
//        twitter_db_task.execute(Constants.DDB_INSERT_USER, android_id, twitter_id, "", "");
    }

    private void setGoogle_Token() {

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
                AccountManager am = AccountManager.get(LogInActivity.this);
                Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                String token = null;

                try {
                    token = GoogleAuthUtil.getToken(getApplicationContext(),
                            accounts[2].name,
                            "audience:server:client_id:1026246692199-34ju03icht4u0h5cg9clj5r0rjgu4rh6.apps.googleusercontent.com");
                } catch (IOException transientEx) {
                    // Network or server error, try later
                    Log.e(TAG, transientEx.toString());
                } catch (UserRecoverableAuthException e) {
                    // Recover (with e.getIntent())
                    Log.e(TAG, e.toString());
                } catch (GoogleAuthException authEx) {
                    // The call is not ever expected to succeed
                    // assuming you have already verified that
                    // Google Play services is installed.
                    Log.e(TAG, authEx.toString());
                }

                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                Log.i(TAG, "Access token retrieved:" + token);
            }
        };
//        task.execute();
//        Map<String, String> logins = new HashMap<String, String>();
//        logins.put("accounts.google.com", token);
//        credentialsProvider.withLogins(logins);
//
//        Dataset dataset = syncClient.openOrCreateDataset("GoogleLogin");
//        dataset.put("accounts.google.com", token);
//        dataset.synchronize(new DefaultSyncCallback() {
//            @Override
//            public void onSuccess(Dataset dataset, List newRecords) {
//                //Your handler code here
//                Log.d("Sync Google client: ", "Success");
//            }
//        });
    }

    private void storeFBInfo(String user_id, String FB_id, String email) {
//        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
//        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
//        UserFacebookDetailsMapper fbUser  = new UserFacebookDetailsMapper();
//        fbUser.setUserId(user_id);
//        fbUser.setEmail(email);
//        fbUser.setFacebookId(FB_id);
//        mapper.save(fbUser);
    }

    public class storeUserInfo extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return null;
        }
    }

    public void gotoNextActivity() {
        dismissProgressDialog();
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        if (mGoogleApiClient.isConnected()) {
            signOutFromGplus();
        }
    }

    //Print Hash key
    public void printHashKey() {

        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }

    protected class GetAllUser extends AsyncTask<Void, Void, ArrayList<UserMapper>> {
        @Override
        protected ArrayList<UserMapper> doInBackground(Void... params) {
            UserDBManager.sharedInstance(LogInActivity.this);
            ArrayList<UserMapper> users = UserDBManager.getUserList();
            return users;
        }

        @Override
        protected void onPostExecute(ArrayList<UserMapper> s) {
            super.onPostExecute(s);

            SharedPreferences sharedPreferences = LogInActivity.this.getSharedPreferences("user_info", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_id", String.valueOf(s.size() + 1));
            editor.commit();
            Log.d("current user_id: ", String.valueOf(s.size() + 1));

            new RegisterUser().execute(String.valueOf(s.size() + 1));
            UserFacebookDBManagerTask fb_db_task = new UserFacebookDBManagerTask(getApplicationContext());
            fb_db_task.execute(Constants.DDB_INSERT_USER, String.valueOf(s.size() + 1), FB_ID, FB_email, "");
        }
    }

    protected class RegisterUser extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            UserDBManager.sharedInstance(LogInActivity.this);
            UserDBManager.insertUsers(params[0], FB_first_name, "", FB_last_name, FB_email, "", "");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            gotoNextActivity();
        }
    }

    protected class CheckUserFacebook extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            UserFacebookDBManager.sharedInstance(LogInActivity.this);
            UserFacebookDetailsMapper user = UserFacebookDBManager.getFacebookUser(params[0]);
            if (user != null) {
                return user.getUserId();
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                isRegistered = true;
                SharedPreferences sharedPreferences = LogInActivity.this.getSharedPreferences("user_info", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_id", s);
                editor.commit();
                Log.d("current user_id: ", s);
                gotoNextActivity();
            } else {
                //Register new user
                new GetAllUser().execute();
            }
        }
    }
}
