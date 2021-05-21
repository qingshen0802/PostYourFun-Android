package com.pyt.postyourfun.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Simon on 7/24/2015.
 */

public class UserImageSQLiteHelper extends SQLiteOpenHelper {

    //DB version
    private static final int DB_VERSION = 1;
    //DB name
    private static final String DB_NAME = "UserImageDataBase";
    private static final String TABLE_NAME = "User_Images";

    private static final String KEY_ID = "db_id";
    private static final String KEY_TRANSACTION_ID = "transactionId";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_IMAGE_ID = "imageId";
    private static final String KEY_IMAGE_URL = "imageUrl";
    private static final String KEY_DATE_TIME = "dateTime";
    private static final String KEY_THUMBNAIL_IMAGE_URL = "thumbnailImageUrl";
    private static final String KEY_PATH = "image_path";

    private static final String[] COLUMNS = {KEY_ID, KEY_TRANSACTION_ID, KEY_USER_ID, KEY_IMAGE_ID, KEY_IMAGE_URL, KEY_THUMBNAIL_IMAGE_URL, KEY_DATE_TIME, KEY_PATH};

    public UserImageSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_IMAGE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "( " + "db_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "transactionId TEXT, " + "userId TEXT, " +
                        "imageId TEXT, " + "imageUrl TEXT, " + "thumbnailImageUrl TEXT, " + "dateTime TEXT, " + "image_path TEXT" + " )";
        db.execSQL(CREATE_USER_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS User_Images");
        this.onCreate(db);
    }

    public void addImage(UsersImageModel imageModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRANSACTION_ID, imageModel.getTransactionId());
        values.put(KEY_USER_ID, imageModel.getUserId());
        values.put(KEY_IMAGE_ID, imageModel.getImageId());
        values.put(KEY_IMAGE_URL, imageModel.getImageUrl());
        values.put(KEY_THUMBNAIL_IMAGE_URL, imageModel.getThumbImageUrl());
        values.put(KEY_DATE_TIME, imageModel.getDateTime());
        values.put(KEY_PATH, imageModel.getLocalPath());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public UsersImageModel getImageModel(int db_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, COLUMNS, " db_id = ?", new String[]{String.valueOf(db_id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        UsersImageModel temp = new UsersImageModel();
        temp.setDb_id(db_id);
        temp.setTransactionId(cursor.getString(1));
        temp.setUserId(cursor.getString(2));
        temp.setImageId(cursor.getString(3));
        temp.setImageUrl(cursor.getString(4));
        temp.setThumbImageUrl(cursor.getString(5));
        temp.setDateTime(cursor.getString(6));
        temp.setLocalPath(cursor.getString(7));
        db.close();

        return temp;
    }

    public ArrayList<UsersImageModel> getAllImages() {
        ArrayList<UsersImageModel> imageModels = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        UsersImageModel temp = null;
        if (cursor.moveToFirst()) {
            do {
                temp = new UsersImageModel();
                temp.setDb_id(Integer.parseInt(cursor.getString(0)));
                temp.setTransactionId(cursor.getString(1));
                temp.setUserId(cursor.getString(2));
                temp.setImageId(cursor.getString(3));
                temp.setImageUrl(cursor.getString(4));
                temp.setThumbImageUrl(cursor.getString(5));
                temp.setDateTime(cursor.getString(6));
                temp.setLocalPath(cursor.getString(7));
                imageModels.add(temp);
            } while (cursor.moveToNext());
        }
        db.close();
        return imageModels;
    }

    public void updateImage(UsersImageModel imageModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, imageModel.getDb_id());
        values.put(KEY_TRANSACTION_ID, imageModel.getTransactionId());
        values.put(KEY_USER_ID, imageModel.getUserId());
        values.put(KEY_IMAGE_ID, imageModel.getImageId());
        values.put(KEY_IMAGE_URL, imageModel.getImageUrl());
        values.put(KEY_THUMBNAIL_IMAGE_URL, imageModel.getThumbImageUrl());
        values.put(KEY_DATE_TIME, imageModel.getDateTime());
        values.put(KEY_PATH, imageModel.getLocalPath());
        int i = db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[]{String.valueOf(imageModel.getDb_id())});
    }

    public void delete_ImageModel(UsersImageModel imageModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[]{String.valueOf(imageModel.getDb_id())});
        db.close();
    }

    public void deleteAllImages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}