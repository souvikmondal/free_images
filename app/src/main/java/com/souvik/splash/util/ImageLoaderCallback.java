package com.souvik.splash.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class ImageLoaderCallback implements BitmapCache.Callback {

    private WeakReference<ImageView> imageViewWeakReference;
    public String url;

    public ImageLoaderCallback(ImageView imageView, String url) {
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
        this.url = url;
    }

    @Override
    public void success(String url, final Bitmap bitmap) {
        if (imageViewWeakReference != null) {
            final ImageView imageView = imageViewWeakReference.get();
            ImageLoaderCallback imageLoaderCallback = getImageLoaderCallback(imageView);
            if ((this == imageLoaderCallback)) {
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }

    @Override
    public void error(String url, Exception ex) {

    }

    public static ImageLoaderCallback getImageLoaderCallback(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getImageLoaderCallback();
            }
        }
        return null;
    }

}