package com.pyt.postyourfun.Utils;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSAbstractCognitoIdentityProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.regions.Regions;
import com.pyt.postyourfun.constants.Constants;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.UserDBManagerTask;
import com.pyt.postyourfun.dynamoDBManager.tableTasks.UserFacebookDBManagerTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Simon on 7/4/2015.
 */
public class CognitoSyncClientManager {
	private static final String TAG = "CognitoSyncManager";

	private static final Regions REGION = Regions.EU_WEST_1;

	private static CognitoSyncManager syncClient;
	protected static CognitoCachingCredentialsProvider credentialProvider = null;
	protected static AWSAbstractCognitoIdentityProvider developerIdentityProvider;

	private static boolean useDeveloperAuthenticatedIdentites = false;

	public static void init(Context context) {
		if (syncClient != null) return;

		credentialProvider = new CognitoCachingCredentialsProvider(context, Constants.IDENTITY_POOL_ID, REGION);
		syncClient = new CognitoSyncManager(context, REGION, credentialProvider);
	}

	public static void addLogins(String providerName, String token) {
		if (syncClient == null) {
			throw new IllegalStateException("CognitoSyncClientManager not initialized yet");
		}
		Map<String, String> logins = credentialProvider.getLogins();
		if (logins == null) {
			logins = new HashMap<String, String>();
		}
		logins.put(providerName, token);
		credentialProvider.withLogins(logins);

		Dataset dataset = syncClient.openOrCreateDataset("LoginData");
		dataset.put("graph.facebook.com", token);
		dataset.synchronize(new DefaultSyncCallback() {
			@Override
			public void onSuccess(Dataset dataset, List newRecords) {
				//Your handler code here
				Log.d("Sync FB client: ", "Success");
			}
		});
	}

	public static CognitoSyncManager getInstance() {
		if (syncClient == null) {
			throw new IllegalStateException("CognitoSyncClientManager not initialized yet");
		}
		return syncClient;
	}

	public CognitoCredentialsProvider getCredentialProvider() { return credentialProvider; }
}
