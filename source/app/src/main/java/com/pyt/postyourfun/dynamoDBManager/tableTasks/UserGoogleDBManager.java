package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.pyt.postyourfun.dynamoDBClass.UserFacebookDetailsMapper;
import com.pyt.postyourfun.dynamoDBClass.UserGoogleDetails;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManager;

import java.util.ArrayList;

public class UserGoogleDBManager extends DynamoDBManager {
	private static String TAG = "UserFacebookDBManager";
	private String userId;
	private String googleId;
	private String email;
	private String country;

	/*
	 * Inserts users
	 */
	public static void insertUsers(String userId, String googleId, String email, String country) {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			for (int i = 1; i <= 10; i++) {
				UserGoogleDetails userFacebook = new UserGoogleDetails();

				userFacebook.setUserId(userId);
				userFacebook.setGoogleId(googleId);
				userFacebook.setEmail(email);
				userFacebook.setCountry(country);

				Log.d(TAG, "Inserting users");
				mapper.save(userFacebook);
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
	public static ArrayList<UserGoogleDetails> getUserGoolgeList() {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		try {
			PaginatedScanList<UserGoogleDetails> result = mapper.scan(UserGoogleDetails.class, scanExpression);

			ArrayList<UserGoogleDetails> resultList = new ArrayList<UserGoogleDetails>();
			for (UserGoogleDetails up : result) {
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
	public static UserGoogleDetails getUserPreference(int userId) {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			UserGoogleDetails user = mapper.load(UserGoogleDetails.class, userId);

			return user;
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}

		return null;
	}

	/*
	 * Updates one attribute/value pair for the specified user.
	 */
	public static void updateUserPreference(UserGoogleDetails updateUser) {

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
	public static void deleteUser(UserGoogleDetails deleteUser) {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			mapper.delete(deleteUser);
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}
	}
}
