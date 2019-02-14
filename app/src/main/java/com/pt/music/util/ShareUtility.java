package com.pt.music.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.widget.Toast;

import com.pt.music.R;

public class ShareUtility {

	// SDcard folder for saving photo and music

	public static void shareText(Activity act, String text) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, text.trim());
		act.startActivity(Intent.createChooser(shareIntent, "Share via..."));

	}

	// save image to galery before share
	public static void shareImageFromUrl(Activity act, String link) {
		Intent share = new Intent(Intent.ACTION_SEND);
		URL url = null;
		Uri uri = null;
		try {
			url = new URL(link);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);

			connection.connect();

			InputStream input = connection.getInputStream();

			Bitmap immutableBpm = BitmapFactory.decodeStream(input);

			Bitmap mutableBitmap = immutableBpm.copy(Bitmap.Config.ARGB_8888,
					true);

			String path = Images.Media.insertImage(act.getContentResolver(),
					mutableBitmap, "Nur", null);

			uri = Uri.parse(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		share.setType("image/*");
		share.putExtra(Intent.EXTRA_STREAM, uri);

		act.startActivity(Intent.createChooser(share, "Share Image!"));
	}

	// save image to SDcard before share
	public static void shareImageInSdCardFromUrl(Activity act, String link,
			String localUrl) {
		Uri uri = null;
		URL url = null;
		// create folder
		File file = new File(localUrl);
		if (!file.exists()) {
			file.mkdirs();
		}

		try {
			url = new URL(link);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();

			// check save to sd card :
			int nameMusicIndex = url.toString().lastIndexOf("/");
			String filename = url.toString().substring(nameMusicIndex,
					url.toString().length());
			File f = new File(localUrl, filename);
			if (!f.exists()) {

				// check format file

				// get input data
				InputStream input = connection.getInputStream();
				Bitmap immutableBpm = BitmapFactory.decodeStream(input);
				Bitmap mutableBitmap = immutableBpm.copy(
						Bitmap.Config.ARGB_8888, true);
				// set file output
				FileOutputStream fileOutputStream = new FileOutputStream(f);
				BufferedOutputStream bos = new BufferedOutputStream(
						fileOutputStream);
				mutableBitmap.compress(CompressFormat.PNG, 100, bos);
			}

			uri = Uri.parse(f.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/*");
		share.putExtra(Intent.EXTRA_STREAM, uri);

		act.startActivity(Intent.createChooser(share, "Share Image!"));
	}

	public static void setAsImageIntent(Activity act, String link) {
		URL url = null;
		Uri uri = null;
		try {
			url = new URL(link);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);

			connection.connect();

			InputStream input = connection.getInputStream();

			Bitmap immutableBpm = BitmapFactory.decodeStream(input);

			Bitmap mutableBitmap = immutableBpm.copy(Bitmap.Config.ARGB_8888,
					true);

			String path = Images.Media.insertImage(act.getContentResolver(),
					mutableBitmap, "Nur", null);

			uri = Uri.parse(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
		intent.setDataAndType(uri, "image/jpg");
		intent.putExtra("mimeType", "image/jpg");
		act.startActivityForResult(Intent.createChooser(intent, "Set As"), 200);
	}

	public static boolean setRingtone(Activity act, String link, String localUrl) {
		File file = new File(localUrl);
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			URL url = new URL(link);
			URLConnection conection = url.openConnection();
			conection.connect();
			// input stream to read file - with 8k buffer
			int nameMusicIndex = url.toString().lastIndexOf("/");
			String filename = url.toString().substring(nameMusicIndex + 1,
					url.toString().length());
			//Log.e("DEBUG-Cuong", "localUrl: " + localUrl  + filename);

			File f = new File(localUrl, filename);
			if (!f.exists()) {
				InputStream input = new BufferedInputStream(url.openStream(),
						8192);

				OutputStream output = new FileOutputStream(localUrl  + filename);
				byte data[] = new byte[1024];
				int count;
				while ((count = input.read(data)) != -1) {
					// writing data to file
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			}
			//Uri defaultRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(act.getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
			
			String key = filename.substring(0, filename.lastIndexOf("."));
			Log.e("CUONG-Debug", "key: " + key);
			Uri uri = MediaStore.Audio.Media.getContentUriForPath(f
					.getAbsolutePath());
			/*
			Cursor localCursor = act.getContentResolver().query(
					uri, null, MediaStore.MediaColumns.DATA + " = ?", new String[] { f.getAbsolutePath() }, null);
			
			
			if (localCursor.moveToFirst())
			{// debug URI
				
				for (int i = 0; i < localCursor.getColumnCount(); i++) {				
					Log.e("CUONG-Debug",
							"value " + localCursor.getColumnName(i) + ": "
									+ localCursor.getString(i));
				}
			}
			localCursor.close();*/
			
			ContentValues values = new ContentValues();
			values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
			values.put(MediaStore.MediaColumns.TITLE, key);
			values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
			values.put(MediaStore.MediaColumns.SIZE, f.length());
			values.put(AudioColumns.ARTIST, act.getString(R.string.app_name));
			values.put(AudioColumns.IS_RINGTONE, true);
			values.put(AudioColumns.IS_NOTIFICATION, false);
			values.put(AudioColumns.IS_ALARM, false);
			values.put(AudioColumns.IS_MUSIC, false);			
			
			// remove the old ContentValues
			int temp = act.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + " = ?", new String[] { f.getAbsolutePath() });
			Log.e("CUONG-Debug", "temp: " + temp);
			Uri newUri = act.getContentResolver().insert(uri, values);
			// debug new URI
			/*
			localCursor = act.getContentResolver().query(
					newUri, null, null, null, null);
			if (localCursor.moveToFirst())
			{
				for (int i = 0; i < localCursor.getColumnCount(); i++) {				
					Log.e("CUONG-Debug",
							"new value " + localCursor.getColumnName(i) + ": "
									+ localCursor.getString(i));
				}
			}
			localCursor.close();*/
			RingtoneManager.setActualDefaultRingtoneUri(act,
					RingtoneManager.TYPE_RINGTONE, newUri);
			Toast.makeText(act, "Set as ringtone successfully !", 3000).show();

		} catch (Exception e) {
			Toast.makeText(act, "Set as ringtone failed !", 3000).show();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean setNotification(Activity act, String link,
			String localUrl) {
		File file = new File(localUrl);
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			URL url = new URL(link);
			URLConnection conection = url.openConnection();
			conection.connect();
			// input stream to read file - with 8k buffer
			int nameMusicIndex = url.toString().lastIndexOf("/");
			String filename = url.toString().substring(nameMusicIndex + 1,
					url.toString().length());
			
			File f = new File(localUrl, filename);
			if (!f.exists()) {
				InputStream input = new BufferedInputStream(url.openStream(),
						8192);

				OutputStream output = new FileOutputStream(localUrl + filename);
				byte data[] = new byte[1024];
				int count;
				while ((count = input.read(data)) != -1) {
					// writing data to file
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			}
			Uri uri = MediaStore.Audio.Media.getContentUriForPath(f
					.getAbsolutePath());
			String key = filename.substring(0, filename.lastIndexOf("."));
			/*Log.e("CUONG-Debug", "key: " + key);
			// debug uri
			Cursor localCursor = act.getContentResolver().query(
					uri, null, MediaStore.MediaColumns.DATA + " = ?", new String[] { f.getAbsolutePath() }, null);			
			
			if (localCursor.moveToFirst())
			{
				// debug URI
				
				for (int i = 0; i < localCursor.getColumnCount(); i++) {				
					Log.e("CUONG-Debug",
							"value " + localCursor.getColumnName(i) + ": "
									+ localCursor.getString(i));
				}
			}*/
			
			ContentValues values = new ContentValues();
			values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
			values.put(MediaStore.MediaColumns.TITLE, key);
			values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
			values.put(MediaStore.MediaColumns.SIZE, f.length());
			values.put(AudioColumns.ARTIST, act.getString(R.string.app_name));
			values.put(AudioColumns.IS_RINGTONE, false);
			values.put(AudioColumns.IS_NOTIFICATION, true);
			values.put(AudioColumns.IS_ALARM, false);
			values.put(AudioColumns.IS_MUSIC, false);
			
			// remove the old ContentValues
			int temp = act.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + " = ?", new String[] { f.getAbsolutePath() });
			Log.e("CUONG-Debug", "temp: " + temp);
			
			Uri newUri = act.getContentResolver().insert(uri, values);
			/*
			localCursor = act.getContentResolver().query(newUri, null, null, null, null);
			if (localCursor.moveToFirst())
			{
				for (int i = 0; i < localCursor.getColumnCount(); i++) {				
					Log.e("CUONG-Debug",
							"new value " + localCursor.getColumnName(i) + ": "
									+ localCursor.getString(i));
				}
			}*/
			RingtoneManager.setActualDefaultRingtoneUri(act,
					RingtoneManager.TYPE_NOTIFICATION, newUri);
			Toast.makeText(act, "Set as notification successfully !", 3000)
					.show();
		} catch (Exception e) {
			Toast.makeText(act, "Set as notification failed !", 3000).show();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean setAlarm(Activity act, String link, String localUrl) {
		File file = new File(localUrl);
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			URL url = new URL(link);
			URLConnection conection = url.openConnection();
			conection.connect();
			// input stream to read file - with 8k buffer
			int nameMusicIndex = url.toString().lastIndexOf("/");
			String filename = url.toString().substring(nameMusicIndex + 1,
					url.toString().length());
			File f = new File(localUrl, filename);
			//Log.e("Cuong-Debug", "url: " + localUrl + filename + "; f:" + f.getAbsolutePath());
			
			if (!f.exists()) {
				InputStream input = new BufferedInputStream(url.openStream(),
						8192);

				OutputStream output = new FileOutputStream(localUrl  + filename);
				byte data[] = new byte[1024];
				int count;
				while ((count = input.read(data)) != -1) {
					// writing data to file
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			}
			Uri uri = MediaStore.Audio.Media.getContentUriForPath(f
					.getAbsolutePath());
			String key = filename.substring(0, filename.lastIndexOf("."));
			// debug uri
			/*
			Cursor localCursor = act.getContentResolver().query(
					uri, null, MediaStore.MediaColumns.DATA + " = ?", new String[] { f.getAbsolutePath() }, null);
			
			if (localCursor.moveToFirst())
			{
				// debug URI
				for (int i = 0; i < localCursor.getColumnCount(); i++) {				
					Log.e("CUONG-Debug",
							"value " + localCursor.getColumnName(i) + ": "
									+ localCursor.getString(i));
				}
			}*/
			// prepare new states
			ContentValues values = new ContentValues();
			values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
			values.put(MediaStore.MediaColumns.TITLE, key);
			values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
			values.put(MediaStore.MediaColumns.SIZE, f.length());
			values.put(AudioColumns.ARTIST, act.getString(R.string.app_name));
			values.put(AudioColumns.IS_RINGTONE, false);
			values.put(AudioColumns.IS_NOTIFICATION, false);
			values.put(AudioColumns.IS_ALARM, true);
			values.put(AudioColumns.IS_MUSIC, false);
			
			// remove the old ContentValues
			int temp = act.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + " = ?", new String[] { f.getAbsolutePath() });
			Log.e("CUONG-Debug", "temp: " + temp);
			Uri newUri = act.getContentResolver().insert(uri, values);
			// debug new URI
			
			Log.e("CUONG-Debug",
					"insert URI " );
			/*
			localCursor = act.getContentResolver().query(
					newUri, null, MediaStore.MediaColumns.TITLE + " = ?", new String[] { key }, null);
			if (localCursor.moveToFirst())
			{
				for (int i = 0; i < localCursor.getColumnCount(); i++) {				
					Log.e("CUONG-Debug",
							"new value " + localCursor.getColumnName(i) + ": "
									+ localCursor.getString(i));
				}
			}*/
			RingtoneManager.setActualDefaultRingtoneUri(act,
					RingtoneManager.TYPE_ALARM, newUri);
			Toast.makeText(act, "Set as alarm successfully !", 3000).show();
		} catch (Exception e) {
			Toast.makeText(act, "Set as alarm failed !", 3000).show();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean setRingtoneContact(Activity act, String contactId,
			String link, String localUrl, Uri contactUri) {
		File file = new File(localUrl);
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			URL url = new URL(link);
			URLConnection conection = url.openConnection();
			conection.connect();
			int nameMusicIndex = url.toString().lastIndexOf(".");
			String filename = "rt"
					+ url.toString().substring(nameMusicIndex,
							url.toString().length());

			File f = new File(localUrl, filename);
			if (f.exists())
				f.delete();
			InputStream input = new BufferedInputStream(url.openStream(), 8192);

			OutputStream output = new FileOutputStream(localUrl + filename);
			byte data[] = new byte[1024];
			int count;
			while ((count = input.read(data)) != -1) { // writing data to
														// file
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();

			// Log.e("ShareUtility", "contactID: " + contactId);
			Uri localUri = Uri.withAppendedPath(
					ContactsContract.Contacts.CONTENT_URI, contactId);

			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			ops.add(ContentProviderOperation
					.newUpdate(ContactsContract.Contacts.CONTENT_URI)
					.withSelection(ContactsContract.Contacts._ID + " = ?",
							new String[] { contactId })
					.withValue(ContactsContract.Contacts.CUSTOM_RINGTONE,
							Uri.fromFile(f).toString())
							.withValue("custom_ringtone_path", Uri.fromFile(f).toString()).build());
			//05-14 17:51:22.670: E/ShareUtility(29341): value custom_ringtone_path: null

			Log.e("ShareUtility", "CUSTOM_RINGTONE key: " + ContactsContract.Contacts.CUSTOM_RINGTONE);
			try {
				act.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
						ops);
			} catch (Exception e) {
				// Log.d("ShareUtility", "Set as contact ringtone failed !");
				e.printStackTrace();
			}

			// Log.d("ShareUtility", "Set as contact ringtone successfully !");
			Toast.makeText(act, "Set as contact ringtone successfully !", 3000)
					.show();

		} catch (Exception e) {
			Toast.makeText(act, "Set as contact ringtone failed !", 3000)
					.show();
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
