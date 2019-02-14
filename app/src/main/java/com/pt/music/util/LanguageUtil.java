package com.pt.music.util;

import java.util.Locale;

import android.app.Activity;
import android.content.res.Configuration;

public class LanguageUtil {
	public static int ENGLISH = 0;
	public static int VIETNAMESE = 1;
	private static String arrLocalCode[] = new String[] { "en", "vi" };

	public static void setLocale(int localeCode, Activity activity) {
		Locale locale = new Locale(arrLocalCode[localeCode]);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		activity.onConfigurationChanged(config);
		activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
	}
}
