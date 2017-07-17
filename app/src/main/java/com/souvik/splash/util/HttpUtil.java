package com.souvik.splash.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Souvik on 13/07/17.
 */

public final class HttpUtil {

    public static final InputStream get(String path, Map<String, String> header) throws IOException{
        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        addHeader(connection, header);
        connection.setRequestMethod("GET");
        int code = connection.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            return connection.getInputStream();
        }
        throw new IOException();
    }

    private static void addHeader(HttpURLConnection connection, Map<String, String> header) {
        if (header == null)
            return;
        Iterator<String> keys = header.keySet().iterator();
        while (keys.hasNext()) {
            String k = keys.next();
            String v = header.get(k);
            connection.setRequestProperty(k, v);
        }
    }

    public static final String streamToString(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

}
