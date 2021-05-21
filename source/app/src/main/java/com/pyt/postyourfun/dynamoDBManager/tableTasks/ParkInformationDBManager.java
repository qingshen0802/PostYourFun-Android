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
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.pyt.postyourfun.dynamoDBClass.BucketMapper;
import com.pyt.postyourfun.dynamoDBClass.ParkInformationMapper;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Simon on 7/13/2015.
 */
public class ParkInformationDBManager extends DynamoDBManager {
	private String TAG = "ParkInformationDBManager";

	public static ParkInformationMapper get_ParkInformation(String ParkId) {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		try {
			Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("ParkId", new AttributeValue().withS(ParkId));
			GetItemRequest getItemRequest = new GetItemRequest("ParkInformation", key);
			GetItemResult getItemResult = ddb.getItem(getItemRequest);

			ParkInformationMapper result = new ParkInformationMapper();
			result.setImage_Url(getItemResult.getItem().get("ImageUrl").getS());
			result.setOpeningInformation(getItemResult.getItem().get("OpeningInformation").getS());
			result.setPark_email(getItemResult.getItem().get("Email").getS());
			result.setWebsite(getItemResult.getItem().get("WebSite").getS());
			result.setPark_ID(ParkId);
			return result;
		} catch (AmazonServiceException e) {
			clientManager.wipeCredentialsOnAuthError(e);
		}
		return null;
	}

	public static ArrayList<ParkInformationMapper> get_All_ParkInformation() {
		AmazonDynamoDBClient ddb = clientManager.ddb();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb);

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		try {
			PaginatedScanList<ParkInformationMapper> result = mapper.scan(ParkInformationMapper.class, scanExpression);

			ArrayList<ParkInformationMapper> resultList = new ArrayList<>();
			for (ParkInformationMapper up : result) {
				resultList.add(up);
			}
			return resultList;
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}
		return null;
	}
}
