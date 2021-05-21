package com.pyt.postyourfun.dynamoDBClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Simon on 7/13/2015.
 */
@DynamoDBTable(tableName = "ParkInformationMapper")
public class ParkInformationMapper {
	private String park_ID;
	private String park_email;
	private String image_Url;
	private String openingInformation;
	private String website;

	@DynamoDBHashKey(attributeName = "ParkId")
	public String getPark_ID() {return park_ID;}

	public void setPark_ID(String park_id) {this.park_ID = park_id;}

	@DynamoDBAttribute(attributeName = "Email")
	public String getPark_email() {return park_email;}

	public void setPark_email(String email) {this.park_email = email;}

	@DynamoDBAttribute(attributeName = "ImageUrl")
	public String getImage_Url() {return image_Url;}

	public void setImage_Url(String image_url) {this.image_Url = image_url;}

	@DynamoDBAttribute(attributeName = "OpeningInformation")
	public String getOpeningInformation() {return openingInformation;}

	public void setOpeningInformation(String openingInformation) {this.openingInformation = openingInformation;}

	@DynamoDBAttribute(attributeName = "WebSite")
	public String getWebsite() {return website;}

	public void setWebsite(String website) {this.website = website;}
}
