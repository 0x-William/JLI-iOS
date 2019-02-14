package com.pt.music.modelmanager;

import com.android.volley.VolleyError;

public interface ModelManagerListener {
	public void onError(VolleyError error);
	public void onSuccess(String json);
}


