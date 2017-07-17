package com.souvik.splash.home;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.souvik.splash.R;
import com.souvik.splash.model.PinModel;
import com.souvik.splash.util.HttpUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

class ImageListLoader extends AsyncTaskLoader<ApiResult> {

        private int page;

        public ImageListLoader(Context context, int page) {
            super(context);
            this.page = page;
        }

        @Override
        public ApiResult loadInBackground() {
            ApiResult result = new ApiResult();
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                    .create();
            try {
                String path = getContext().getString(R.string.unsplash_api_endpoint) + page;
                String appKey = getContext().getString(R.string.unsplash_api_key);
                Map<String, String> header = new HashMap<>(1);
                header.put("Authorization:", "Client-ID " + appKey);
                InputStream inputStream = HttpUtil.get(path, header);
                String d = HttpUtil.streamToString(inputStream);
                result.model = gson.fromJson(d,
                        PinModel[].class);
            } catch (IOException e) {
                result.errorMessage = "Unable to connect. Please check your internet connection!!";
            }
            return result;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }