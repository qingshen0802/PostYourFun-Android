package com.pyt.postyourfun.dynamoDBClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Simon on 7/13/2015.
 */

@DynamoDBTable(tableName = "BucketList")
public class BucketMapper {
	private String bucket_ID;
	private String bucket_Name;
	private String bucket_Url;

	@DynamoDBAttribute(attributeName = "BucketID")
	public String getBucket_ID() {return bucket_ID;}

	public void setBucket_ID(String id) {this.bucket_ID = id;}

	@DynamoDBAttribute(attributeName = "BucketName")
	public String getBucket() {return bucket_Name;}

	public void setBucket_Name(String bucket_name) {this.bucket_Name = bucket_name;}

	@DynamoDBAttribute(attributeName = "BucketURL")
	public String getBucket_Url() {return bucket_Url;}

	public void setBucket_Url(String b_url) {this.bucket_Url = b_url;}
}
