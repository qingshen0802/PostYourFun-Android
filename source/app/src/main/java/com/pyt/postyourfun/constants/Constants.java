package com.pyt.postyourfun.constants;

/**
 * Created by Administrator on 6/25/2015.
 */
public class Constants {
	public static final String PREF_KEY_TWITTER_LOGIN = "twitter_login";
	public static final String CONSUMER_KEY = "MtJkfTEi597EuorEmScSrzH9q";
	public static final String CONSUMER_SECRET = "41g7AKjGJVVuGTJthjz7a986tZdEcnWVhDDZeNGwXNdlS7OV7R";
	public static final String CALLBACK_URL = "oauth://testing";
	public static String PREFERENCE_TWITTER_OAUTH_TOKEN = "TWITTER_OAUTH_TOKEN";
	public static String PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET = "TWITTER_OAUTH_TOKEN_SECRET";
	public static String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";

	public static final String ACCOUNT_ID = "748089263593";
	public static final String IDENTITY_POOL_ID = "eu-west-1:e194b9df-e036-41a3-8d48-289ed42177bc";
	public static final String UNAUTH_ROLE_ARN = "arn:aws:iam::748089263593:role/Cognito_PostYourFunUnauth_Role";

	// Table Names
	public static final String USER_TABLE_NAME = "User";
	public static final String USER_FACEBOOK_TABLE_NAME = "UserFacebookDetails";
	public static final String USER_TWITTER_TABLE_NAME = "UserTwitterDetails";
//    public static final String USER_TABLE_NAME = "User";
//    public static final String USER_TABLE_NAME = "User";
//    public static final String USER_TABLE_NAME = "User";

	// GET_TABLE_STATUS, CREATE_TABLE, INSERT_USER, LIST_USERS, CLEAN_UP
	public static final String DDB_CREATE_TABLE = "CREATE TABLE";
	public static final String DDB_INSERT_USER = "INSERT USER";
	public static final String DDB_GET_TABLE_STATUS = "GET_TABLE_STATUS";
	public static final String DDB_GET_USER = "GET_USER";
	public static final String DDB_LIST_USERS = "LIST USERS";
	public static final String DDB_CLEAN_UP = "CLEAN UP";

	public static final String DDB_INSERT_PAYMENT_TRANSACTION = "INSERT_TRANSACTION";
	public static final String DDB_GET_TRANSACTION_IMAGES = "GET_TRANSACTION_IMAGES";

	//Image Bucket URL
	public static final String IMAGE_CONSTANT_URL = "https://s3-eu-west-1.amazonaws.com/";

	//PayPal client ID
	public static final String PAYPAL_CLIENT_ID = "AZLEARoSJdJhGwICsP6f_BAmpoVroI7kzyaTemXuMAB5o7RH1ui9FIHdoRftb96VbUIJzC50tcewF_TB";
	public static final String PAYPAL_SECRET = "EAgsW_UelSrmKo2Mr-dEF5ycBj03beswOvnxRGB7I9kkvL1IaPxahX1XDlFMTHYoBvrdRKDln4421d9I";

	// Image download path
	public static final String IMAGE_FULL_PATH = android.os.Environment.getExternalStorageDirectory() + "/Post your Fun/Images";
}
