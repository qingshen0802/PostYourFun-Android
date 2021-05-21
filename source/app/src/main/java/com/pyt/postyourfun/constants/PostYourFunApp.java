package com.pyt.postyourfun.constants;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.pyt.postyourfun.dynamoDBClass.DeviceMapper;
import com.pyt.postyourfun.dynamoDBClass.ParkMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Administrator on 7/10/2015.
 */
public class PostYourFunApp extends Application {
	private static PostYourFunApp instance;

	public static ArrayList<ParkMapper> all_parks = new ArrayList<>();
	public static ArrayList<DeviceMapper> all_rides = new ArrayList<>();

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			Class.forName("android.os.AsyncTask");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		instance = this;
	}

	public static Context getContext() {
		return instance.getApplicationContext();
	}

	public static String getCurrentTimDate(long miliseconds, String dateFormat) {
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(miliseconds);
		return formatter.format(calendar.getTime());
	}

	//Create GUID to store Transaction history ID
	public static String createGUID() {
		String gUID = "";
		UUID uuid = UUID.randomUUID();
		gUID = uuid.toString();
		Log.d("new GUID: ", gUID);
		return gUID;
	}
}
