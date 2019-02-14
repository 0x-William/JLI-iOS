package com.pt.music.network;


import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.util.Map;

public abstract class HttpRequest {
    public static final int SOCKET_TIME_OUT = 3000;
    public static final int METHOD_GET = Request.Method.GET;
    public static final int METHOD_POST = Request.Method.POST;
    public static final int REQUEST_STRING_PARAMS = 0;
    public static final int REQUEST_JSON_PARAMS = 1;

    protected String url;
    protected HttpListener httpListener;
    protected HttpError httpError;
    protected Map<String, String> params;
    protected int requestDataType;
    private Context context;
    protected int requestMethod;
    protected ProgressDialog pDialog;
    protected boolean isShowDialog = true;
    private RetryPolicy policy = null;
    protected Request request;

    public HttpRequest(Context context, int requestMethod, String url, boolean isShowDialog, HttpListener httpListener, HttpError httpError) {
        this.context = context;
        this.httpListener = httpListener;
        this.httpError = httpError;
        this.requestMethod = requestMethod;
        this.url = url;
        this.isShowDialog = isShowDialog;
        pDialog = new ProgressDialog(context);
        policy = new DefaultRetryPolicy(SOCKET_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    }

    protected void sendRequest()
    {
        request.setRetryPolicy(policy);
        ControllerRequest.getInstance().addToRequestQueue(request);
    }

    protected void showDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(this.context);
        }
        pDialog.show();
    }

    protected void closeDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.hide();
        }
    }

    protected void hanhdleErrorRequest(VolleyError volleyError) {
        NetworkResponse networkResponse = volleyError.networkResponse;
        if (networkResponse != null) {
            Log.d("Error", "Status Code: " + networkResponse.statusCode + "  " + networkResponse.data.toString());
        }
        if (volleyError instanceof TimeoutError) {
            Log.d("Error", "Time out error");
        } else if (volleyError instanceof NoConnectionError) {
            Log.d("Error", "NoConnectionError");
        } else if (volleyError instanceof AuthFailureError) {
            Log.d("Error", "AuthFailureError");
        } else if (volleyError instanceof ServerError) {
            Log.d("Error", "ServerError");
        } else if (volleyError instanceof NetworkError) {
            Log.d("Error", "NetworkError");
        } else if (volleyError instanceof ParseError) {
            Log.d("Error", "ParseError");
        }
    }


}
