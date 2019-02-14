package com.pt.music.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpPost extends HttpRequest {

    public HttpPost(Context context, String url, Map<String, String> params, int requestDataType, boolean isShowDialog, HttpListener httpListener, HttpError httpError) {
        super(context, HttpRequest.METHOD_POST, url, isShowDialog, httpListener, httpError);
        this.requestDataType = requestDataType;
        this.params = params;

        sendRequest();
    }
    
    protected void sendRequest() {
        if (isShowDialog)
            showDialog();
        if (requestDataType == REQUEST_JSON_PARAMS) {
            request = getJsonObjectRequest();
        } else {
            request = getStringRequest();
        }
        super.sendRequest();
    }

    private Request getJsonObjectRequest() {

        Response.Listener successResponse = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isShowDialog)
                    closeDialog();
                httpListener.onHttpResponse(response);

            }
        };
        Response.ErrorListener errorResponse = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isShowDialog)
                    closeDialog();
                httpError.onHttpError(error);

            }
        };
        JSONObject jsonParams = params != null ? new JSONObject(params) : null;
        JsonObjectRequest request = new JsonObjectRequest(requestMethod, url, jsonParams, successResponse, errorResponse) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("charset", "utf-8");
                return headers;
            }
        };
        return request;
    }

    private Request getStringRequest() {

        Response.Listener successResponse = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (isShowDialog)
                    closeDialog();
                httpListener.onHttpResponse(response);

            }
        };
        Response.ErrorListener errorResponse = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isShowDialog)
                    closeDialog();
                httpError.onHttpError(error);

            }
        };
        StringRequest request = new StringRequest(requestMethod, url, successResponse, errorResponse) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        return request;
    }

}
