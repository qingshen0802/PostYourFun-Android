package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.pyt.postyourfun.dynamoDBClass.ImageQueryMapper;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Simon on 7/14/2015.
 */
public class ImageQueryDBManager extends DynamoDBManager {
	private static String TAG = "ImageQueryDBManager";

	public static List<ImageQueryMapper> getImage(String device_id) {

		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		try {
			DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

			Map<String, Condition> scanFilter = new HashMap<String, Condition>();

			// Condition1: DeviceId
			Condition scanCondition =
					new Condition().withComparisonOperator(ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(device_id));
			scanFilter.put("DeviceId", scanCondition);
			scanExpression.setScanFilter(scanFilter);

			return mapper.scan(ImageQueryMapper.class, scanExpression);
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}
		return null;
	}
}
