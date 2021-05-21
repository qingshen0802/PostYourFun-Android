package com.pyt.postyourfun.dynamoDBClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Simon on 7/14/2015.
 */
@DynamoDBTable(tableName = "ImageListQuery")
public class ImageQueryMapper {
	private String image_id;
	private String device_id;
	private long date_time;
	private String display_id;
	private String image_type;

	@DynamoDBAttribute(attributeName = "ImageId")
	public String getImage_id() {return image_id;}

	public void setImage_id(String id) {this.image_id = id;}

	@DynamoDBAttribute(attributeName = "DeviceId")
	public String getDevice_id() {return device_id;}

	public void setDevice_id(String device_id) {this.device_id = device_id;}

	@DynamoDBAttribute(attributeName = "DateTime")
	public long getDate_time() {return date_time;}

	public void setDate_time(long date_time) {this.date_time = date_time;}

	@DynamoDBAttribute(attributeName = "DisplayId")
	public String getDisplay_id() {return display_id;}

	public void setDisplay_id(String display_id) {this.display_id = display_id;}

	@DynamoDBAttribute(attributeName = "ImageType")
	public String getImage_type() {return image_type;}

	public void setImage_type(String image_type) {this.image_type = image_type;}
}
