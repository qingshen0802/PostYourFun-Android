package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.pyt.postyourfun.dynamoDBClass.ParkInformationMapper;
import com.pyt.postyourfun.dynamoDBClass.ParkSocialMediaMapper;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Simon on 7/13/2015.
 */
public class ParkSocialMediaDBManager extends DynamoDBManager {

	private String TAG = "ParkSocialMediaDBManager";

	public static ParkSocialMediaMapper get_Park_Social_Media(String parkId) {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		try {
			Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("ParkId", new AttributeValue().withS(parkId));
			GetItemRequest getItemRequest = new GetItemRequest("ParkSocialMedia", key);
			GetItemResult getItemResult = ddb.getItem(getItemRequest);

			ParkSocialMediaMapper result = new ParkSocialMediaMapper();
			result.setFacebook(getItemResult.getItem().get("Facebook").getS());
			result.setPark_ID(parkId);
			return result;
		} catch (AmazonServiceException e) {
			clientManager.wipeCredentialsOnAuthError(e);
		}
		return null;
	}

	public static ArrayList<ParkSocialMediaMapper> get_All_Park_Social_Media() {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		try {
			PaginatedScanList<ParkSocialMediaMapper> result = mapper.scan(ParkSocialMediaMapper.class, scanExpression);
			ArrayList<ParkSocialMediaMapper> resultList = new ArrayList<>();
			for (ParkSocialMediaMapper up : result) {
				resultList.add(up);
			}
			return resultList;
		} catch (AmazonServiceException e) {
			clientManager.wipeCredentialsOnAuthError(e);
		}
		return null;
	}
}
