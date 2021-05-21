package com.pyt.postyourfun.dynamoDBClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

/**
 * Created by einar_000 on 23.6.2015.
 */
@DynamoDBTable(tableName = "Device")
public class DeviceMapper {

	private String deviceId;
	private String parkId;
	private String name;
	private boolean HasMonitor;
	private boolean ImageSold;
	private int NumberOfColumns;
	private int NumberOfMinutes;

	@DynamoDBHashKey(attributeName = "DeviceId")
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@DynamoDBIndexRangeKey(attributeName = "ParkId")
	public String getParkId() {
		return parkId;
	}

	public void setParkId(String parkId) {
		this.parkId = parkId;
	}

	@DynamoDBAttribute(attributeName = "Name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@DynamoDBAttribute(attributeName = "HasMonitor")
	public Boolean getHasMonitor() {
		return HasMonitor;
	}

	public void setHasMonitor(Boolean hasMonitor) {
		HasMonitor = hasMonitor;
	}

	@DynamoDBAttribute(attributeName = "ImageSold")
	public Boolean getImageSold() {
		return ImageSold;
	}

	public void setImageSold(Boolean imageSold) {
		ImageSold = imageSold;
	}

	@DynamoDBAttribute(attributeName = "NumberOfColumns")
	public int getNumberOfColumns() {
		return NumberOfColumns;
	}

	public void setNumberOfColumns(int numberOfColumns) {
		NumberOfColumns = numberOfColumns;
	}

	@DynamoDBAttribute(attributeName = "NumberOfMinutes")
	public int getNumberOfMinutes() {
		return NumberOfMinutes;
	}

	public void setNumberOfMinutes(int numberOfMinutes) {
		NumberOfMinutes = numberOfMinutes;
	}
}