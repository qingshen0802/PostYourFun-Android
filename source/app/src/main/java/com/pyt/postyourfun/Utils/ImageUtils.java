package com.pyt.postyourfun.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by r8tin on 2/5/16.
 */
public class ImageUtils {

    public static Bitmap decodeFile(File f, int WIDTH, int HEIGHT) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            int scale = 1;
            while (o.outWidth / scale >= WIDTH && o.outHeight / scale >= HEIGHT) scale *= 2;
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            try {
                return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            } catch (NullPointerException e) {
                return decodeFile(f, (int) (WIDTH / 2f), (int) (HEIGHT / 2f));
            }
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
