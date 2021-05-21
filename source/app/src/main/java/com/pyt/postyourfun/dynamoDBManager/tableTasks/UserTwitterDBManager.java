package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.pyt.postyourfun.dynamoDBClass.UserFacebookDetailsMapper;
import com.pyt.postyourfun.dynamoDBClass.UserTwitterDetails;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManager;

import java.util.ArrayList;

public class UserTwitterDBManager extends DynamoDBManager {
	private static String TAG = "UserTwitterDbManager";
	private String userId;
	private String twitterId;
	private String email;
	private String country;

	/*
	 * Inserts users
	 */
	public static void insertUsers(String userId, String twitterId, String email, String country) {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			for (int i = 1; i <= 10; i++) {
				UserTwitterDetails userTwitter = new UserTwitterDetails();

				userTwitter.setUserId(userId);
				userTwitter.setTwitterId(twitterId);
				userTwitter.setEmail(email);
				userTwitter.setCountry(country);

				Log.d(TAG, "Inserting users");
				mapper.save(userTwitter);
				Log.d(TAG, "Users inserted");
			}
		} catch (AmazonServiceException ex) {
			Log.e(TAG, "Error inserting users");
			clientManager.wipeCredentialsOnAuthError(ex);
		}
	}

	/*
	 * Scans the table and returns the list of users.
	 */
	public static ArrayList<UserTwitterDetails> getUserTwitterList() {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		try {
			PaginatedScanList<UserTwitterDetails> result = mapper.scan(UserTwitterDetails.class, scanExpression);

			ArrayList<UserTwitterDetails> resultList = new ArrayList<UserTwitterDetails>();
			for (UserTwitterDetails up : result) {
				resultList.add(up);
			}
			return resultList;
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}

		return null;
	}

	/*
	 * Retrieves all of the attribute/value pairs for the specified user.
	 */
	public static UserTwitterDetails getUserPreference(int userId) {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			UserTwitterDetails user = mapper.load(UserTwitterDetails.class, userId);

			return user;
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}

		return null;
	}

	/*
	 * Updates one attribute/value pair for the specified user.
	 */
	public static void updateUserPreference(UserTwitterDetails updateUser) {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			mapper.save(updateUser);
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}
	}

	/*
	 * Deletes the specified user and all of its attribute/value pairs.
	 */
	public static void deleteUser(UserTwitterDetails deleteUser) {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			mapper.delete(deleteUser);
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}
	}
}
