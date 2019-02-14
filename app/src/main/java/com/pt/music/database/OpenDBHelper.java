package com.pt.music.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pt.music.config.DatabaseConfig;

public class OpenDBHelper extends SQLiteOpenHelper {
	private final String TAG = "OpenDBHelper";
	private Context context;
	private DatabaseConfig dbConfig;

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public OpenDBHelper(Context context, DatabaseConfig dbConfig) {
		super(context, dbConfig.getDatabaseName(), null, dbConfig.getDatabaseVersion());
		this.context = context;
		this.dbConfig = dbConfig;
		if (!isDatabaseExist()) {
			// Create blank file
			getReadableDatabase();
			close();
			try {
				copyDatabase();
			} catch (IOException e) {
				Log.e(TAG, "Error to init database");
			}
		}
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}

	/**
	 * Copy database to application directory on SD card
	 * 
	 * @throws IOException
	 */
	private void copyDatabase() throws IOException {
		Log.i(TAG, "Copy database into application directory");
		InputStream is = context.getAssets().open(dbConfig.getDatabaseName());
		OutputStream os = new FileOutputStream(dbConfig.getDatabaseFullPath());
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}
		os.flush();
		os.close();
		is.close();
	}

	/**
	 * Check database is exist
	 * 
	 * @return
	 */
	private boolean isDatabaseExist() {
		SQLiteDatabase checkDB = null;
		try {
			checkDB = SQLiteDatabase.openDatabase(dbConfig.getDatabaseFullPath(), null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			Log.e(TAG, "Database is not exist! " + dbConfig.getDatabaseFullPath() + " ======================");
			e.printStackTrace();
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return (checkDB != null) ? true : false;
	}
}
