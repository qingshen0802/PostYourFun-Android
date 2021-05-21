package com.pyt.postyourfun.dynamoDBClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Simon on 7/13/2015.
 */

@DynamoDBTable(tableName = "ParkSocialMedia")
public class ParkSocialMediaMapper {
	private String park_ID;
	private String facebook;

	@DynamoDBHashKey(attributeName = "ParkId")
	public String getPark_ID() {return park_ID;}

	public void setPark_ID(String park_id) {this.park_ID = park_id;}

	@DynamoDBAttribute(attributeName = "Facebook")
	public String getFacebook() {return facebook;}

	public void setFacebook(String facebook) {this.facebook = facebook;}
}
