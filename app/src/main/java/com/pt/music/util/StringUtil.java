package com.pt.music.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

import com.pt.music.config.GlobalValue;

public class StringUtil {
	public static boolean checkUrl(String s) {
		return s.startsWith("http://") || s.startsWith("https://");
	}

	public static String unAccent(String s) {
		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(temp).replaceAll("").replaceAll(GlobalValue.DD, "D").replace(GlobalValue.dd, "d");
	}

	public static boolean checkEndNextPage(String nextPage) {
		if (nextPage == null) {
			return false;
		}
		return nextPage.equals("end");
	}
}
