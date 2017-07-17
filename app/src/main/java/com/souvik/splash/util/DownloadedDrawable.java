package com.souvik.splash.util;

import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

public class DownloadedDrawable extends BitmapDrawable {
    private WeakReference<ImageLoaderCallback> imageLoaderCallbackWeakReference;


    public DownloadedDrawable(ImageLoaderCallback callback) {
        imageLoaderCallbackWeakReference =
                new WeakReference<ImageLoaderCallback>(callback);
    }

    public ImageLoaderCallback getImageLoaderCallback() {
        return imageLoaderCallbackWeakReference.get();
    }




}