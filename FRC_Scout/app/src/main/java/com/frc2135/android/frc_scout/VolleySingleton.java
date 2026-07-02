package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton
{
    private static final String TAG = "VolleySingleton";
    private static volatile VolleySingleton sInstance;
    private RequestQueue mRequestQueue;
    private final Context mContext;

    private VolleySingleton(Context context)
    {
        Log.d(TAG, "VolleySingleton constructor");
        mContext = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }

    public static VolleySingleton getInstance(Context context)
    {
        Log.d(TAG, "getInstance()");
        if (sInstance == null)
        {
            synchronized (VolleySingleton.class)
            {
                if (sInstance == null)
                {
                    sInstance = new VolleySingleton(context);
                }
            }
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null)
        {
            // getApplicationContext() is key, it keeps the VolleySingleton from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }
}
