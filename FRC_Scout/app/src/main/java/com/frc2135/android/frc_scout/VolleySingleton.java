package com.frc2135.android.frc_scout;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton class for managing Volley {@link RequestQueue}.
 */
public class VolleySingleton
{
    private static volatile VolleySingleton sInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;
 
    private VolleySingleton(Context context)
    {
        mContext = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }

    public static VolleySingleton getInstance(Context context)
    {
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
            // getApplicationContext() is key, it keeps you from leaking the
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
