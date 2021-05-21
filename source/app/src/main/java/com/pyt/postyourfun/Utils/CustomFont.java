package com.pyt.postyourfun.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by Devmania on 3/20/2015.
 */
public class CustomFont {
	public static String OPENSANS_BOLD = "OpenSans-Bold.ttf";
	public static String OPENSANS_BOLDITALIC = "OpenSans-BoldItalick.ttf";
	public static String OPENSANS_EXTRABOLD = "OpenSans-ExtraBold.ttf";
	public static String OPENSANS_EXTRABOLDITALIC = "OpenSans-ExtraBoldItalic.ttf";
	public static String OPENSANS_ITALIC = "OpenSans-Italic.ttf";
	public static String OPENSANS_LIGHT = "OpenSans-Light.ttf";
	public static String OPENSANS_LIGHTITALIC = "OpenSans-LightItalic.ttf";
	public static String OPENSANS_REGULAR = "OpenSans-Regular.ttf";
	public static String OPENSANS_SEMIBOLC = "OpenSans-Semibolc.ttf";
	public static String OPENSANS_SEMIBOLDITALIC = "OpenSans-SemiboldItalic.ttf";

	public static void setCustomFont(Context context, TextView textView, String fontName) {
		if (context != null && textView != null) {
			Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);
			textView.setTypeface(typeface);
		}
	}

	public static void setCustomFont(Context context, Button button, String fontName) {
		if (context != null && button != null) {
			Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);
			button.setTypeface(typeface);
		}
	}

	public static void setCustomFont(Context context, RadioButton button, String fontName) {
		if (context != null && button != null) {
			Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);
			button.setTypeface(typeface);
		}
	}
}
