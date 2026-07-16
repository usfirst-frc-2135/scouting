package com.frc2135.android.frc_scout;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton class for managing the application's network request queue using the Volley library.
 * This pattern ensures that the network queue persists for the lifetime of the app, rather than being re-created on each activity change.
 */
public class VolleySingleton
{
    private static final String TAG = "VolleySingleton";
    private static volatile VolleySingleton sInstance;
    private RequestQueue mRequestQueue;
    private final Context mContext;

    /**
     * Initializes the Volley request queue using the application context.
     *
     * @param context the context used to create the request queue
     */
    private VolleySingleton(Context context)
    {
        Log.v(TAG, "VolleySingleton constructor");
        mContext = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }

    /**
     * Returns the thread-safe singleton instance of VolleySingleton.
     *
     * @param context the context used to initialize the instance if necessary
     * @return the singleton VolleySingleton instance
     */
    public static VolleySingleton getInstance(Context context)
    {
        Log.v(TAG, "getInstance");
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

    /**
     * Returns the Volley request queue, creating it if it does not already exist.
     * Uses the application context to prevent memory leaks.
     *
     * @return the {@link RequestQueue} for managing network operations
     */
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

    /**
     * Enqueues a network request to the Volley request queue.
     *
     * @param req the request to add to the queue
     * @param <T> the data type of the response expected
     */
    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }
}
