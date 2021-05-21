package com.pyt.postyourfun.dynamoDBClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.regions.Region;

/**
 * Created by Simon on 7/13/2015.
 */
@DynamoDBTable(tableName = "ImageList")
public class ImageMapper {

	private String imageID;
	private String device_ID;
	private long imageDate_Time;
	private String imageDisplayID;
	private String imageName;
	private String region;

	@DynamoDBHashKey(attributeName = "ImageId")
	public String getImageID() {return imageID;}

	public void setImageID(String image_id) {this.imageID = image_id;}

	@DynamoDBAttribute(attributeName = "DeviceId")
	public String getDevice_ID() {return device_ID;}

	public void setDevice_ID(String device_id) {this.device_ID = device_id;}

	@DynamoDBAttribute(attributeName = "DateTime")
	public long getImageDate_Time() {return imageDate_Time;}

	public void setImageDate_Time(long date_time) {this.imageDate_Time = date_time;}

	@DynamoDBAttribute(attributeName = "DisplayId")
	public String getImageDisplayID() {return imageDisplayID;}

	public void setImageDisplayID(String image_display_id) {this.imageDisplayID = image_display_id;}

	@DynamoDBAttribute(attributeName = "Name")
	public String getImageName() {return imageName;}

	public void setImageName(String image_name) {this.imageName = image_name;}

	@DynamoDBAttribute(attributeName = "Region")
	public String getRegion() {return region;}

	public void setRegion(String region) {this.region = region;}
}
