package com.pyt.postyourfun.dynamoDBClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by einar_000 on 23.6.2015.
 */
@DynamoDBTable(tableName = "DeviceRating")
public class DeviceRatingMapper {

	private String ratingId;
	private String userId;
	private String deviceId;
	private String speed;
	private String g_force;
	private String adrenalineKick;
	private String comment;

	@DynamoDBHashKey(attributeName = "RatingId")
	public String getRatingId() {
		return ratingId;
	}

	public void setRatingId(String ratingId) {
		this.ratingId = ratingId;
	}

	@DynamoDBRangeKey(attributeName = "UserId")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@DynamoDBAttribute(attributeName = "DeviceId")
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@DynamoDBAttribute(attributeName = "Speed")
	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	@DynamoDBAttribute(attributeName = "G_force")
	public String getG_force() {
		return g_force;
	}

	public void setG_force(String g_force) {
		this.g_force = g_force;
	}

	@DynamoDBAttribute(attributeName = "AdrenalineKick")
	public String getAdrenalineKick() {
		return adrenalineKick;
	}

	public void setAdrenalineKick(String adrenalineKick) {
		this.adrenalineKick = adrenalineKick;
	}

	@DynamoDBAttribute(attributeName = "Comment")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
