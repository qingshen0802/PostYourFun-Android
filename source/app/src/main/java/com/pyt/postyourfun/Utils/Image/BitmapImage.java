package com.pyt.postyourfun.Utils.Image;

import android.content.Context;
import android.graphics.Bitmap;

import com.pyt.postyourfun.Utils.ImageUtils;

import java.io.File;

public class BitmapImage implements SmartImage {
    private File file;
    private int width, height;

    public BitmapImage(File file, int width, int height) {
        this.file = file;
        this.width = width;
        this.height = height;
    }

    public Bitmap getBitmap(Context context) {
        return ImageUtils.decodeFile(file, width, height);
    }
}