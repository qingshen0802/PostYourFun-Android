package com.pyt.postyourfun.dynamoDBClass;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Simon on 7/24/2015.
 */

@DynamoDBTable(tableName = "UserImages")
public class UserImageMapper {
    private String transactionId;
    private String userId;
    private String dateTime;
    private String imageId;
    private String imageUrl;
    private boolean owned;
    private String imageThumbUrl;

    @DynamoDBHashKey(attributeName = "TransactionId")
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String tran_id) {
        transactionId = tran_id;
    }

    @DynamoDBRangeKey(attributeName = "UserId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String user_id) {
        userId = user_id;
    }

    @DynamoDBAttribute(attributeName = "ImageId")
    public String getImageId() {
        return imageId;
    }

    public void setImageId(String image_id) {
        imageId = image_id;
    }

    @DynamoDBAttribute(attributeName = "ImageUrl")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String image_url) {
        imageUrl = image_url;
    }

    @DynamoDBAttribute(attributeName = "DateTime")
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String date_time) {
        dateTime = date_time;
    }

    @DynamoDBAttribute(attributeName = "Owned")
    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean flag) {
        owned = flag;
    }

    @DynamoDBAttribute(attributeName = "ImageThumbUrl")
    public String getImageThumbUrl() {
        return imageThumbUrl;
    }

    public void setImageThumbUrl(String imageThumbUrl) {
        this.imageThumbUrl = imageThumbUrl;
    }
}