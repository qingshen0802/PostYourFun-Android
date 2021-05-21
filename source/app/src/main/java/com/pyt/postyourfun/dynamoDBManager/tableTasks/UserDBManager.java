package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.pyt.postyourfun.dynamoDBClass.UserMapper;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManager;

import java.util.ArrayList;

public class UserDBManager extends DynamoDBManager {
	private static String TAG = "UserDBManager";

	/*
	 * Inserts users
	 */
	public static void insertUsers(String userId, String firstName, String middleName, String lastName, String email, String dateOfBirth, String country) {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {

			UserMapper user = new UserMapper();
			user.setUserId(userId);
			user.setFirstName(firstName);
			user.setMiddleName(middleName);
			user.setLastName(lastName);
			user.setEmail(email);
			user.setDateOfBirth(dateOfBirth);
			user.setCountry(country);

			Log.d(TAG, "Inserting users");
			mapper.save(user);
			Log.d(TAG, "Users inserted");
		} catch (AmazonServiceException ex) {
			Log.e(TAG, "Error inserting users");
			clientManager.wipeCredentialsOnAuthError(ex);
		}
	}

	/*
	 * Scans the table and returns the list of users.
	 */
	public static ArrayList<UserMapper> getUserList() {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		try {
			PaginatedScanList<UserMapper> result = mapper.scan(UserMapper.class, scanExpression);

			ArrayList<UserMapper> resultList = new ArrayList<UserMapper>();
			for (UserMapper up : result) {
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
	public static UserMapper getUser(String userId) {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			UserMapper user = mapper.load(UserMapper.class, userId);
			return user;
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}

		return null;
	}

	/*
	 * Updates one attribute/value pair for the specified user.
	 */
	public static void updateUser(UserMapper updateUser) {

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
	public static void deleteUser(UserMapper deleteUser) {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			mapper.delete(deleteUser);
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}
	}
}
