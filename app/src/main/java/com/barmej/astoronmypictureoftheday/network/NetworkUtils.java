package com.barmej.astoronmypictureoftheday.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.barmej.astoronmypictureoftheday.R;

import java.net.MalformedURLException;
import java.net.URL;


public class NetworkUtils {
    private static String TAG = NetworkUtils.class.getSimpleName();
    private static final String BASE_URL = "https://api.nasa.gov";
    private static final String PICTURES_ENDPOINT = "/planetary/apod";
    private static final String API_KEY = "api_key";
    public static final String DATE = "date";
    private static Context mContext;
    private static NetworkUtils sInstance;
    private static final Object LOCK = new Object();
    private RequestQueue mRequestQueue;

    public static NetworkUtils getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) sInstance = new NetworkUtils(context);
            }
        }
        return sInstance;
    }

    private NetworkUtils(Context context) {
        mContext = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    public <T> void addRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public void cancelRequest(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public static URL getPictureUrl(Context context,String date) {
        return buildUrl(context,PICTURES_ENDPOINT,date);

    }

    private static URL buildUrl(Context context,String endPoint,String date) {
        Uri.Builder uriBuilder = Uri.parse(BASE_URL + endPoint).buildUpon();
        if (date != null) {
            uriBuilder.appendQueryParameter(DATE, date);
        }
        uriBuilder.appendQueryParameter(API_KEY,context.getString(R.string.api_key));
        Uri uri = uriBuilder.build();
        try {
            URL url = new URL(uri.toString());
            Log.d(TAG,"URL: " + url);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
