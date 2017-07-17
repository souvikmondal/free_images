package com.souvik.splash.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by souvik on 7/9/2016.
 */
public final class BitmapUtils {

    static final int BUFFER_SIZE = 128 * 1024;

    public static Bitmap decodeStreamToBitmap(InputStream inputStream, int reqWidth) {
        if (!(inputStream.markSupported())) {
            inputStream = new BufferedInputStream(inputStream);
        }
        inputStream.mark(BUFFER_SIZE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new BufferedInputStream(inputStream), null, options);

        try {
            inputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        options.inSampleSize = calculateInSampleSize(options.outWidth, reqWidth);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    public static Bitmap decodeStreamToBitmap(InputStream inputStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    public static Bitmap decodeStreamToBitmap(InputStream inputStream, int reqWidth, int reqHeight) {
        if (!(inputStream.markSupported())) {
            inputStream = new BufferedInputStream(inputStream);
        }
        inputStream.mark(BUFFER_SIZE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new BufferedInputStream(inputStream), null, options);

        try {
            inputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    public static int calculateInSampleSize(int actualWidth, int actualHeight,
                                            int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        if (actualHeight > reqHeight || actualWidth > reqWidth) {

            final int halfHeight = actualHeight / 2;
            final int halfWidth = actualWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static int calculateInSampleSize(int actualWidth, int reqWidth) {
        int inSampleSize = 1;

        if (actualWidth > reqWidth) {

            final int halfWidth = actualWidth / 2;

            while ((halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}