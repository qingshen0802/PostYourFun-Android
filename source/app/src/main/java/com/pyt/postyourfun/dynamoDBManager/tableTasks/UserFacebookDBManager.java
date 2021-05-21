package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.pyt.postyourfun.dynamoDBClass.ParkSocialMediaMapper;
import com.pyt.postyourfun.dynamoDBClass.UserFacebookDetailsMapper;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFacebookDBManager extends DynamoDBManager {
	private static String TAG = "UserFacebookDBManager";
	private String userId;
	private String facebookId;
	private String email;
	private String country;

	/*
	 * Inserts users
	 */
	public static void insertUsers(String userId, String facebookId, String email, String country) {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			UserFacebookDetailsMapper userFacebook = new UserFacebookDetailsMapper();

			userFacebook.setUserId(userId);
			userFacebook.setFacebookId(facebookId);
			userFacebook.setEmail(email);
			userFacebook.setCountry(country);

			Log.d(TAG, "Inserting users");
			mapper.save(userFacebook);
			Log.d(TAG, "Users inserted");
		} catch (AmazonServiceException ex) {
			Log.e(TAG, "Error inserting users");
			clientManager.wipeCredentialsOnAuthError(ex);
		}
	}

	/*
	 * Scans the table and returns the list of users.
	 */
	public static ArrayList<UserFacebookDetailsMapper> getUserFacebookList() {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		try {
			PaginatedScanList<UserFacebookDetailsMapper> result = mapper.scan(UserFacebookDetailsMapper.class, scanExpression);

			ArrayList<UserFacebookDetailsMapper> resultList = new ArrayList<UserFacebookDetailsMapper>();
			for (UserFacebookDetailsMapper up : result) {
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
	public static UserFacebookDetailsMapper getFacebookUser(String facebookId) {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
			Map<String, Condition> scanFilter = new HashMap<String, Condition>();

			// Condition1: DeviceId
			Condition scanCondition =
					new Condition().withComparisonOperator(ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(facebookId));
			scanFilter.put("FacebookId", scanCondition);
			scanExpression.setScanFilter(scanFilter);

			List<UserFacebookDetailsMapper> results = mapper.scan(UserFacebookDetailsMapper.class, scanExpression);
			if (results.size() > 0) {
				UserFacebookDetailsMapper fb_user = results.get(0);
				return fb_user;
			} else {
				return null;
			}
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}
		return null;
	}

	/*
	 * Updates one attribute/value pair for the specified user.
	 */
	public static void updateUserPreference(UserFacebookDetailsMapper updateUser) {

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
	public static void deleteUser(UserFacebookDetailsMapper deleteUser) {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			mapper.delete(deleteUser);
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}
	}
}
