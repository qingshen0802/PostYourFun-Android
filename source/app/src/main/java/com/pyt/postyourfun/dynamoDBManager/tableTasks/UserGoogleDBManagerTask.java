package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import android.os.AsyncTask;

import com.pyt.postyourfun.dynamoDBManager.DynamoDBManagerTaskResult;

public class UserGoogleDBManagerTask extends AsyncTask<String, Void, DynamoDBManagerTaskResult> {

	@Override
	protected DynamoDBManagerTaskResult doInBackground(String... params) {

		DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
		return result;
	}

	@Override
	protected void onPostExecute(DynamoDBManagerTaskResult dynamoDBManagerTaskResult) {

	}
}
