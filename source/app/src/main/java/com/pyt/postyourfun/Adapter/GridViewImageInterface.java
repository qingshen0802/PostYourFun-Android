package com.pyt.postyourfun.Adapter;

import android.view.View;

import com.pyt.postyourfun.Utils.Image.SmartImage;

/**
 * Created by Simon on 7/30/2015.
 */
public interface GridViewImageInterface {
	public void onClickedImage(View view, int index);

	public void onChangeCheckbox(boolean isChecked);
}
