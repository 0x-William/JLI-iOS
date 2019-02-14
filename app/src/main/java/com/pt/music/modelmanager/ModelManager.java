package com.pt.music.modelmanager;

import android.content.Context;

import com.android.volley.VolleyError;
import com.pt.music.network.HttpError;
import com.pt.music.network.HttpGet;
import com.pt.music.network.HttpListener;

import java.util.HashMap;
import java.util.Map;

public class ModelManager {
    private static String TAG = "ModelManager";

    // demo
    // ==============================


    public static void sendGetRequest(Context context, String url, Map<String, String> params,
                                      boolean isProgress, final ModelManagerListener listener) {

        if (params == null)
            params = new HashMap<>();

        new HttpGet(context, url, params, isProgress, new HttpListener() {
            @Override
            public void onHttpResponse(Object respone) {
                if (respone != null) {
                    listener.onSuccess(respone.toString());
                } else {
                    listener.onError(null);
                }
            }
        }, new HttpError() {
            @Override
            public void onHttpError(VolleyError volleyError) {
                listener.onError(volleyError);
            }
        });
    }

    public static void login(Context context, String userName, String password, final ModelManagerListener listener) {

        String url = "http://myjli.com/crm/index.php/api/users/getUserId";

        Map<String, String> params = new HashMap<>();
        params.put("username", userName);
        params.put("password", password);

        new HttpGet(context, url, params, true, new HttpListener() {
            @Override
            public void onHttpResponse(Object respone) {
                if (respone != null) {
                    listener.onSuccess(respone.toString());
                } else {
                    listener.onError(null);
                }
            }
        }, new HttpError() {
            @Override
            public void onHttpError(VolleyError volleyError) {
                listener.onError(volleyError);
            }
        });
    }
}