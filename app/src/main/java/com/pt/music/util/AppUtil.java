/*
 * Name: $RCSfile: AppUtil.java,v $
 * Version: $Revision: 1.1 $
 * Date: $Date: Apr 19, 2011 11:05:43 AM $
 *
 * Copyright (C) 2011 COMPANY NAME, Inc. All rights reserved.
 */

package com.pt.music.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.pt.music.R;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author cuongvm6037
 * 
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class AppUtil {

	/**
	 * Check if external storage exists such as SD card
	 * 
	 * @return
	 */
	public static boolean hasExternalStorage() {
		try {
			return android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Show an alert dialog box
	 * 
	 * @param context
	 * @param s
	 */
	public static void alert(Context context, String s) {
		if (context != null) {
			// AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
			// alertbox.setTitle(context.getString(R.string.app_full_name));
			// alertbox.setMessage(s);
			// alertbox.setNeutralButton("OK",
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface arg0, int arg1)
			// {}
			// });
			// alertbox.show();
			Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Show an alert dialog box
	 * 
	 * @param context
	 * @param s
	 */
	public static void alert(Context context, String s,
			DialogInterface.OnClickListener okOnclick) {
		if (context != null) {
			AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
			alertbox.setTitle(context.getString(R.string.app_name));
			alertbox.setMessage(s);
			alertbox.setNeutralButton("OK", okOnclick);
			alertbox.show();
		}
	}

	/**
	 * Show an alert dialog box
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param okOnclick
	 */
	public static void alert(Context context, String title, String message,
			DialogInterface.OnClickListener okOnclick) {
		if (context != null) {
			AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
			alertbox.setTitle(title);
			alertbox.setMessage(message);
			alertbox.setNeutralButton("OK", okOnclick);
			alertbox.show();
		}
	}

	/**
	 * Show an alert dialog box
	 * 
	 * @param context
	 * @param s
	 */
	public static void alert(Context context, CharSequence s) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
		alertbox.setTitle(context.getString(R.string.app_name));
		alertbox.setMessage(s);
		alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		alertbox.show();
	}

	/**
	 * Notify network unavailable
	 * 
	 * @param context
	 */
	public static void alertNetworkUnavailable(Context context) {
		if (context != null) {
			alert(context, "Network unavailable. Please turn it on.");
		}
	}

	/**
	 * Notify network unavailable
	 * 
	 * @param context
	 */
	public static void alertNetworkUnavailableCommon(Context context) {
		if (context != null) {

			alert(context, "Network is unavailable. Please try again later.");
		}
	}

	/**
	 * Notify network unavailable
	 * 
	 * @param context
	 */
	public static void alertNetworkUnavailableShare(Context context) {
		if (context != null) {
			alert(context,
					"Network is unavailable. Unsuccessful sharing, please try again.");
		}
	}

	public static void alertNoOfflineData(Context context) {
		if (context != null) {

			alert(context, "No offline data found!");
		}
	}

	public static void alertViewOfflineData(Context context) {
		if (context != null) {

			alert(context, "You are viewing offline data.");
		}
	}

	public static void alertNoDataFound(Context context) {
		if (context != null) {
			alert(context, "No data found.");
		}
	}

	/**
	 * Notify network unavailable
	 * 
	 * @param context
	 */
	public static void alertNetworkUnavailableNormal(Context context) {
		if (context != null) {
			alert(context, "Network is unavailable.");
		}
	}

	public static void alertNetworkUnavailableNormal(Context context,
			DialogInterface.OnClickListener okOnclick) {
		if (context != null) {
			alert(context, "Network is unavailable.", okOnclick);
		}
	}

	public static String convertMinutesToHoursMinuteSecond(long minute) {

		long millis = minute * 60 * 1000;

		String hms = String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						% TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						% TimeUnit.MINUTES.toSeconds(1));

		return hms;
	}

	public static String convertMillisToHoursMinuteSecond(long millis) {

		String hms = String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						% TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						% TimeUnit.MINUTES.toSeconds(1));

		return hms;
	}

	public static Date convertStringToDate(String strDate, String pattern) {
		Date convertDate = null;
		SimpleDateFormat sf = new SimpleDateFormat(pattern);
		try {
			convertDate = sf.parse(strDate);
			String newDateString = sf.format(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return convertDate;
	}

	/**
	 * Notify server error
	 * 
	 * @param context
	 */
	public static void alertServerError(Context context) {
		if (context != null) {
			alert(context, "There is an error from server, please try again.");
		}
	}

	public static boolean isMyServiceRunning(Class<?> serviceClass,
			Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// ListAdapter listAdapter = listView.getAdapter();
		// if (listAdapter == null)
		// {
		// // pre-condition
		// return;
		// }
		//
		// int totalHeight = 0;
		// for (int i = 0; i < listAdapter.getCount(); i++)
		// {
		// View listItem = listAdapter.getView(i, null, listView);
		// listItem.measure(0, 0);
		// totalHeight += listItem.getMeasuredHeight();
		// }
		//
		// ViewGroup.LayoutParams params = listView.getLayoutParams();
		// params.height = totalHeight
		// + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.setLayoutParams(params);
		// listView.requestLayout();

		ListAdapter mAdapter = listView.getAdapter();

		int totalHeight = 0;

		for (int i = 0; i < mAdapter.getCount(); i++) {
			View mView = mAdapter.getView(i, null, listView);

			mView.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),

					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

			totalHeight += mView.getMeasuredHeight();
			Log.w("LISTVIEW_HEIGHT" + i, String.valueOf(totalHeight));

		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (mAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();

	}

	public static byte[] object2Bytes(Object o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		return baos.toByteArray();
	}

	public static Object bytes2Object(byte raw[]) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(raw);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object o = ois.readObject();
		return o;
	}
}
