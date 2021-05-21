package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.pyt.postyourfun.dynamoDBClass.DeviceRatingMapper;
import com.pyt.postyourfun.dynamoDBClass.UserMapper;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManager;

/**
 * Created by Administrator on 7/10/2015.
 */
public class DeviceRateDBManager extends DynamoDBManager {
	private static String TAG = "DeviceRatingDBManager";

	public static void insertRatings(String userId, String device_id, String speed, String g_force, String adrenalineKick, String comment) {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {

			DeviceRatingMapper user = new DeviceRatingMapper();
			user.setUserId(userId);
			user.setAdrenalineKick(adrenalineKick);
			user.setComment(comment);
			user.setDeviceId(device_id);
			user.setG_force(g_force);
			user.setSpeed(speed);
			user.setRatingId(device_id + "_" + userId);

			Log.d(TAG, "Inserting rate");
			mapper.save(user);
			Log.d(TAG, "Rate inserted");
		} catch (AmazonServiceException ex) {
			Log.e(TAG, "Error inserting rate");
			clientManager.wipeCredentialsOnAuthError(ex);
		}
	}
}
