package com.souvik.splash.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.LruCache;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Souvik on 14/07/17.
 */

public final class BitmapCache {

    private static BitmapCache cache;
    private static final Lock lock = new ReentrantLock();

    public static final BitmapCache getCache() {
        try {
            lock.lock();
            if (cache == null) {
                cache = new BitmapCache();
            }
        } finally {
            lock.unlock();
        }

        return cache;
    }

    private LruCache<String, Bitmap> lruCache;
    private ThreadPoolExecutor threadPoolExecutor;
    private Map<String, Callback> callbackMap;

    private BitmapCache() {
        initCache();
        initThreadPool();
        callbackMap = new LinkedHashMap<>(5, .75f, true);
    }

    private void initCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 16;
        this.lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024 / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key,
                                        Bitmap oldValue, Bitmap newValue) {
                if (evicted) {
                    synchronized (callbackMap) {
                        callbackMap.remove(key);
                    }
                }
            }
        };
    }

    private void initThreadPool() {
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
    }

    public void get(@NonNull String url,
                    @NonNull Callback callback,
                    boolean refresh) {
        if (!refresh) {
            Bitmap bitmap = get(url);
            if (bitmap != null) {
                callback.success(url, bitmap);
                return;
            }
        }
        putCallback(url, callback);
        threadPoolExecutor.execute(new Downloader(url));
    }

    public void cancel(String url) {
        synchronized (callbackMap) {
            callbackMap.remove(url);
        }
    }

    private Bitmap get(String key) {
        synchronized (lruCache) {
            return lruCache.get(key);
        }
    }

    private void putCallback(String key, Callback callback) {
        synchronized (callbackMap) {
            callbackMap.put(key, callback);
        }
    }

    public Callback getCallback(String key) {
        synchronized (callbackMap) {
            return callbackMap.get(key);
        }
    }

    private class Downloader implements Runnable {

        String url;

        Downloader(String url) {
            this.url = url;
        }


        @Override
        public void run() {
            try {
                Bitmap bitmap = BitmapUtils.decodeStreamToBitmap(
                        HttpUtil.get(url, null));
                getCallback(url).success(url, bitmap);
            } catch (IOException e) {
                getCallback(url).error(url, e);
                e.printStackTrace();
            }
        }
    }

    public interface Callback {
        void success(String url, Bitmap bitmap);
        void error(String url, Exception e);
    }


}
