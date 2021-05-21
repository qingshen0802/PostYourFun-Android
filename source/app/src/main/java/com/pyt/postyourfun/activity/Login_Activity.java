package com.pyt.postyourfun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.pyt.postyourfun.R;
import com.pyt.postyourfun.social.SocialController;
import com.pyt.postyourfun.social.SocialControllerInterface;

public class Login_Activity extends BaseActivity implements SocialControllerInterface, View.OnClickListener {

	SocialController _socialController = null;

	private Button gplus_loginButton;
	private Button fb_loginButton;
	private Button twitter_login_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		_socialController = SocialController.sharedInstance(this, this);

		fb_loginButton = (Button) this.findViewById(R.id.fb_login_button);
		gplus_loginButton = (Button) this.findViewById(R.id.google_login_button);
		twitter_login_button = (Button) this.findViewById(R.id.twitter_login_button);

		gplus_loginButton.setOnClickListener(this);
		twitter_login_button.setOnClickListener(this);
		fb_loginButton.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (_socialController != null) {
			_socialController.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (_socialController != null) {
			_socialController.onPause();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (_socialController != null) {
			_socialController.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (_socialController != null) {
			_socialController.onDestroy();
		}
	}

	@Override
	public void onSuccess(int type, int action) {
		gotoNextActivity();
	}

	@Override
	public void onFailure(int type, int action) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fb_login_button:
			_socialController.loginWithFacebook(this);
			break;
		case R.id.google_login_button:
			// Google Signin button clicked
			break;
		case R.id.twitter_login_button:
			//Twitter sign in button clicked
			break;
		}
	}

	public void gotoNextActivity() {
		Intent intent = new Intent(Login_Activity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
