package com.pyt.postyourfun.dynamoDBManager;

import android.content.Context;
import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cognitoidentity.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

public class DynamoDBManager {
	private static final String TAG = "DynamoDBManager";
	protected Context _context = null;
	public static AmazonClientManager clientManager = null;

	private static DynamoDBManager _sharedInstance = null;

	public static DynamoDBManager sharedInstance(Context context) {
		if (_sharedInstance == null) {
			_sharedInstance = new DynamoDBManager();
			_sharedInstance.init(context);
		}
		return _sharedInstance;
	}

	private void init(Context context) {
		_context = context;
		clientManager = new AmazonClientManager(context);
	}

	/*
	 * Creates a table with the following attributes: Table name: testTableName
	 * Hash key: userNo type N Read Capacity Units: 10 Write Capacity Units: 5
	 */
	public static void createTable(String tableName, String attributeName) {

		Log.d(TAG, "Create table called");

		AmazonDynamoDBClient ddb = clientManager.ddb();

		KeySchemaElement kse = new KeySchemaElement().withAttributeName(attributeName).withKeyType(KeyType.HASH);
		AttributeDefinition ad = new AttributeDefinition().withAttributeName(attributeName).withAttributeType(ScalarAttributeType.N);
		ProvisionedThroughput pt = new ProvisionedThroughput().withReadCapacityUnits(10l).withWriteCapacityUnits(5l);

		CreateTableRequest request =
				new CreateTableRequest().withTableName(tableName).withKeySchema(kse).withAttributeDefinitions(ad).withProvisionedThroughput(pt);

		try {
			Log.d(TAG, "Sending Create table request");
			ddb.createTable(request);
			Log.d(TAG, "Create request response successfully recieved");
		} catch (AmazonServiceException ex) {
			Log.e(TAG, "Error sending create table request", ex);
			clientManager.wipeCredentialsOnAuthError(ex);
		}
	}

	/*
	 * Retrieves the table description and returns the table status as a string.
	 */
	public String getTableStatus(String tableName) {

		try {
			AmazonDynamoDBClient ddb = clientManager.ddb();

			DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);
			DescribeTableResult result = ddb.describeTable(request);

			String status = result.getTable().getTableStatus();
			return status == null ? "" : status;
		} catch (ResourceNotFoundException e) {
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}

		return "";
	}

	/*
	 * Deletes the test table and all of its users and their attribute/value
	 * pairs.
	 */
	public static void cleanUp(String tableName) {

		AmazonDynamoDBClient ddb = clientManager.ddb();

		DeleteTableRequest request = new DeleteTableRequest().withTableName(tableName);
		try {
			ddb.deleteTable(request);
		} catch (AmazonServiceException ex) {
			clientManager.wipeCredentialsOnAuthError(ex);
		}
	}
}
