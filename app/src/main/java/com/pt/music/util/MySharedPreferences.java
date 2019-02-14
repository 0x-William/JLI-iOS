package com.pt.music.util;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
	private final String TEMPLATE_MUSIC_PRO = "TEMPLATE_MUSIC_PRO";
	private final String LANGUAGE = "LANGUAGE";

	// ================================================================
	private Context context;

	public MySharedPreferences(Context context) {
		this.context = context;
	}

	// ======================== UTILITY FUNCTIONS ========================

	public void putLanguage(int language) {
		putIntValue(LANGUAGE, language);
	}

	public int getLanguage() {
		return getIntValue(LANGUAGE);
	}

	// ======================== CORE FUNCTIONS ========================

	/**
	 * Save a long integer to SharedPreferences
	 * 
	 * @param key
	 * @param n
	 */
	public void putLongValue(String key, long n) {
		// SmartLogger.log(TAG, "Set long integer value");
		SharedPreferences pref = context.getSharedPreferences(TEMPLATE_MUSIC_PRO, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putLong(key, n);
		editor.commit();
	}

	/**
	 * Read a long integer to SharedPreferences
	 * 
	 * @param key
	 * @return
	 */
	public long getLongValue(String key) {
		// SmartLogger.log(TAG, "Get long integer value");
		SharedPreferences pref = context.getSharedPreferences(TEMPLATE_MUSIC_PRO, 0);
		return pref.getLong(key, 0);
	}

	/**
	 * Save an integer to SharedPreferences
	 * 
	 * @param key
	 * @param n
	 */
	public void putIntValue(String key, int n) {
		// SmartLogger.log(TAG, "Set integer value");
		SharedPreferences pref = context.getSharedPreferences(TEMPLATE_MUSIC_PRO, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key, n);
		editor.commit();
	}

	/**
	 * Read an integer to SharedPreferences
	 * 
	 * @param key
	 * @return
	 */
	public int getIntValue(String key) {
		// SmartLogger.log(TAG, "Get integer value");
		SharedPreferences pref = context.getSharedPreferences(TEMPLATE_MUSIC_PRO, 0);
		return pref.getInt(key, 0);
	}

	/**
	 * Save an string to SharedPreferences
	 * 
	 * @param key
	 * @param s
	 */
	public void putStringValue(String key, String s) {
		// SmartLogger.log(TAG, "Set string value");
		SharedPreferences pref = context.getSharedPreferences(TEMPLATE_MUSIC_PRO, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, s);
		editor.commit();
	}

	/**
	 * Read an string to SharedPreferences
	 * 
	 * @param key
	 * @return
	 */
	public String getStringValue(String key) {
		// SmartLogger.log(TAG, "Get string value");
		SharedPreferences pref = context.getSharedPreferences(TEMPLATE_MUSIC_PRO, 0);
		return pref.getString(key, "");
	}

	/**
	 * Read an string to SharedPreferences
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getStringValue(String key, String defaultValue) {
		// SmartLogger.log(TAG, "Get string value");
		SharedPreferences pref = context.getSharedPreferences(TEMPLATE_MUSIC_PRO, 0);
		return pref.getString(key, defaultValue);
	}

	/**
	 * Save an boolean to SharedPreferences
	 * 
	 * @param key
	 * @param s
	 */
	public void putBooleanValue(String key, Boolean b) {
		// SmartLogger.log(TAG, "Set boolean value");
		SharedPreferences pref = context.getSharedPreferences(TEMPLATE_MUSIC_PRO, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(key, b);
		editor.commit();
	}

	/**
	 * Read an boolean to SharedPreferences
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBooleanValue(String key) {
		// SmartLogger.log(TAG, "Get boolean value");
		SharedPreferences pref = context.getSharedPreferences(TEMPLATE_MUSIC_PRO, 0);
		return pref.getBoolean(key, false);
	}

	/**
	 * Save an float to SharedPreferences
	 * 
	 * @param key
	 * @param s
	 */
	public void putFloatValue(String key, float f) {
		SharedPreferences pref = context.getSharedPreferences(TEMPLATE_MUSIC_PRO, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putFloat(key, f);
		editor.commit();
	}

	/**
	 * Read an float to SharedPreferences
	 * 
	 * @param key
	 * @return
	 */
	public float getFloatValue(String key) {
		SharedPreferences pref = context.getSharedPreferences(TEMPLATE_MUSIC_PRO, 0);
		return pref.getFloat(key, 0.0f);
	}
}
