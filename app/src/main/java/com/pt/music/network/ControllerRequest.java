package com.pt.music.network;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Objects;

import io.intercom.android.sdk.Intercom;
//import io.intercom.android.sdk.preview.IntercomPreviewPosition;

/**
 * Created by pham on 20/10/2015.
 */
public class ControllerRequest extends Application {
    private RequestQueue requestQueue;
    public static final String TAG = ControllerRequest.class.getSimpleName();
    private static ControllerRequest controller;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intercom.initialize(this, "android_sdk-3828686967c13615903f014e104430f21be1e99d", "joa9zqj3");
//        Intercom.client().setPreviewPosition(IntercomPreviewPosition.BOTTOM_RIGHT);

        controller = this;
    }

    /**
     * @return
     */

    public static ControllerRequest getInstance() {
        return controller;
    }

    /**
     * @return trả về một đối tượng của RequestQueue sử dụng để gửi request
     */
    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * @param request một request bất kì
     * @param tag     được sử dụng setTag cho request
     * @param <T>     tham số extends từ Object
     */
    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }

    /**
     * @param request
     * @param <T>     tham số extends từ Object
     */
    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        getRequestQueue().add(request);

    }

    /**
     * @param tag
     */
    public void cancelRequest(Objects tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
