package com.morion.clabki;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by morion on 11/07/17.
 */

public class NetworkSingleton {

    private static NetworkSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private NetworkSingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized NetworkSingleton getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new NetworkSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
