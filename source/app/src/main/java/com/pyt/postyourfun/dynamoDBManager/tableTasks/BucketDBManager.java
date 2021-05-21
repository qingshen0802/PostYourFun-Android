package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.pyt.postyourfun.dynamoDBClass.BucketMapper;
import com.pyt.postyourfun.dynamoDBClass.DeviceMapper;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManager;

import java.util.ArrayList;

/**
 * Created by Simon on 7/13/2015.
 */
public class BucketDBManager extends DynamoDBManager {

	private String TAG = "BucketDBManager";

	public static BucketMapper get_Bucket(String bucketID) {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);
		try {
			BucketMapper result = mapper.load(BucketMapper.class, bucketID);
			return result;
		} catch (AmazonServiceException e) {
			clientManager.wipeCredentialsOnAuthError(e);
		}
		return null;
	}

	public static ArrayList<BucketMapper> get_All_Buckets() {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		try {
			PaginatedScanList<BucketMapper> result = mapper.scan(BucketMapper.class, scanExpression);

			ArrayList<BucketMapper> resultList = new ArrayList<>();
			for (BucketMapper up : result) {
				resultList.add(up);
			}
			return resultList;
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}
		return null;
	}
}
