package com.pyt.postyourfun.Utils;

/**
 * Created by Simon on 7/24/2015.
 */
public class UsersImageModel {

    private int db_id;
    private String transactionId;
    private String userId;
    private String imageId;
    private String imageUrl;
    private String thumbImageUrl;
    private String dateTime;
    private String localPath;

    public UsersImageModel() {

    }

    public void setDb_id(int id) {
        db_id = id;
    }

    public int getDb_id() {
        return db_id;
    }

    public void setTransactionId(String id) {
        transactionId = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setUserId(String u_id) {
        userId = u_id;
    }

    public String getUserId() {
        return userId;
    }

    public void setImageId(String img_id) {
        imageId = img_id;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageUrl(String img_url) {
        imageUrl = img_url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setDateTime(String date_time) {
        dateTime = date_time;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setThumbImageUrl(String url) {
        thumbImageUrl = url;
    }

    public String getThumbImageUrl() {
        return thumbImageUrl;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}