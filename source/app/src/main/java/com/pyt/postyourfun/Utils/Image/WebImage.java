package com.pyt.postyourfun.Utils.Image;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

public class WebImage implements SmartImage {
	private static final int CONNECT_TIMEOUT = 5000;
	private static final int READ_TIMEOUT = 10000;

	private static WebImageCache webImageCache;

	private String url;

	public WebImage(String url) {
		this.url = url;
	}

	public Bitmap getBitmap(Context context) {
		// Don't leak context
		if (webImageCache == null) {
			webImageCache = new WebImageCache(context);
		}

		// Try getting bitmap from cache first
		Bitmap bitmap = null;
		if (url != null) {
			bitmap = webImageCache.get(url);
			if (bitmap == null) {
				bitmap = getBitmapFromUrl(url);
				if (bitmap != null) {
					webImageCache.put(url, bitmap);
				}
			}
		}

		return bitmap;
	}

	private Bitmap getBitmapFromUrl(String url) {
		Bitmap bitmap = null;

		try {
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			conn.connect();
			if (conn.getResponseCode() != 200) {
				String redirectionUrl = conn.getHeaderField("Location");
				if (redirectionUrl == null || redirectionUrl.isEmpty()) return null;
				else return getBitmapFromUrl(redirectionUrl);
			}
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 8;
				bitmap = BitmapFactory.decodeStream((InputStream) conn.getContent(), null, options);
			} else {
				bitmap = BitmapFactory.decodeStream((InputStream) conn.getContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public static void removeFromCache(String url) {
		if (webImageCache != null) {
			webImageCache.remove(url);
		}
	}
}
