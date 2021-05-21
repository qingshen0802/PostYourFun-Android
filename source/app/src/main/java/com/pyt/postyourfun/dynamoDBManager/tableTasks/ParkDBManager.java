package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.pyt.postyourfun.dynamoDBClass.ParkMapper;
import com.pyt.postyourfun.dynamoDBClass.UserMapper;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManager;

import java.util.ArrayList;

/**
 * Created by Administrator on 7/10/2015.
 */
public class ParkDBManager extends DynamoDBManager {
	private String TAG = "ParkDBManager";

	public static ParkMapper getPark(String park_id) {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			ParkMapper user = mapper.load(ParkMapper.class, park_id);
			return user;
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}
		return null;
	}

	public static ArrayList<ParkMapper> get_all_Parks() {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		try {
			PaginatedScanList<ParkMapper> result = mapper.scan(ParkMapper.class, scanExpression);

			ArrayList<ParkMapper> resultList = new ArrayList<ParkMapper>();
			for (ParkMapper up : result) {
				resultList.add(up);
			}
			return resultList;
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}
		return null;
	}
}
