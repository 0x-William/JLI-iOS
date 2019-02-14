package com.pt.music.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.pt.music.config.DatabaseConfig;
import com.pt.music.util.Logger;

public class PrepareStatement {
	private final static String TAG = "PrepareStatement";

	private final static String MESSAGE_ERROR_WHEN_IMPLEMENT_QUERY = "ERROR WHEN IMPLEMENT QUERY";

	private final static String MESSAGE_INSERT_AN_OBJECT_SUCCESSFULLY = "INSERTED AN OBJECT SUCCESSFULLY";
	private final static String MESSAGE_UPDATE_AN_OBJECT_SUCCESSFULLY = "UPDATED AN OBJECT SUCCESSFULLY";

	private final static String MESSAGE_INSERT_A_LIST_OBJECT_SUCCESSFULLY = "INSERTED A LIST OBJECT SUCCESSFULLY";

	private final static String MESSAGE_NO_OBJECT_INSERT = "NO OBJECT INSERT";
	private final static String MESSAGE_NO_OBJECT_UPDATED = "NO OBJECT UPDATED";

	private final static String MESSAGE_LINE = "============================";

	private Context context = null;

	private SQLiteDatabase db = null;

	/**
	 * Constructor
	 */
	public PrepareStatement(Context context) {
		this.context = context;
	}

	// ==================== SQLITE CORE FUNCTION ====================

	/**
	 * Implement a query string using for delete, update command
	 * 
	 * @param sql
	 * @return
	 */
	public boolean query(String sql, Object[] params) {
		boolean success = false;
		db = openDatabaseConnection();
		if (db != null) {
			SQLiteStatement statement = null;
			try {
				statement = db.compileStatement(sql);
				if (params != null) {
					for (int i = 0; i < params.length; i++) {
						statement.bindString(i + 1, params[i].toString());
					}
				}
				statement.execute();
				success = true;
			} catch (Exception ex) {
				Logger.e(TAG, "Error when implement query: " + sql);
				ex.printStackTrace();
			} finally {
				statement.close();
				closeDatabaseConnection();
			}
		}

		return success;
	}

	/**
	 * Implement a select query string
	 * 
	 * @param sql
	 * @return
	 */
	public <T> ArrayList<T> select(String table, String columns, String where, IRowMapper<T> IRowMapper) {
		ArrayList<T> modelList = new ArrayList<T>();

		db = openDatabaseConnection();
		if (db != null) {
			Cursor cursor = execSelect(db, table, columns, where);
			cursor.moveToFirst();
			int rowNum = 0;
			while (cursor.isAfterLast() == false) {
				modelList.add(IRowMapper.mapRow(cursor, rowNum));
				cursor.moveToNext();
				rowNum++;
			}
			cursor.close();
			closeDatabaseConnection();
		}

		return modelList;
	}

	/**
	 * Implement a select query string with limit row
	 * 
	 * @param sql
	 * @return
	 */

	public <T> ArrayList<T> select(String table, String columns, String where, int limit, IRowMapper<T> IRowMapper) {
		ArrayList<T> modelList = new ArrayList<T>();

		db = openDatabaseConnection();
		if (db != null) {
			Cursor cursor = execSelect(db, table, columns, where, limit);
			cursor.moveToFirst();
			int rowNum = 0;
			while (cursor.isAfterLast() == false) {
				modelList.add(IRowMapper.mapRow(cursor, rowNum));
				cursor.moveToNext();
				rowNum++;
			}
			cursor.close();
			closeDatabaseConnection();
		}

		return modelList;
	}

	public <T> ArrayList<T> runRawQuery(String sql, IRowMapper<T> IRowMapper) {
		ArrayList<T> modelList = new ArrayList<T>();

		db = openDatabaseConnection();
		if (db != null) {
			Cursor cursor = execRawQuery(db, sql);
			cursor.moveToFirst();
			int rowNum = 0;
			while (cursor.isAfterLast() == false) {
				modelList.add(IRowMapper.mapRow(cursor, rowNum));
				cursor.moveToNext();
				rowNum++;
			}
			cursor.close();
			closeDatabaseConnection();
		}

		return modelList;
	}

	/**
	 * Implement a insert a list object to local database
	 * 
	 * @param sql
	 * @return
	 */
	public boolean insertlist(String sql, ArrayList<Object> objectList, ParameterBinder parameterBinder) {
		if (objectList == null || objectList.size() == 0) {
			Logger.e(TAG, MESSAGE_NO_OBJECT_INSERT + MESSAGE_LINE);
			return false;
		}

		boolean success = false;
		db = openDatabaseConnection();
		if (db != null) {
			db.beginTransaction();
			SQLiteStatement statement = null;
			try {
				for (int i = 0; i < objectList.size(); i++) {
					statement = db.compileStatement(sql);
					Object object = objectList.get(i);
					parameterBinder.bind(statement, object);
					statement.execute();
				}

				success = true;
				db.setTransactionSuccessful();
				Logger.e(TAG, MESSAGE_INSERT_A_LIST_OBJECT_SUCCESSFULLY + MESSAGE_LINE);
			} catch (Exception ex) {
				Logger.e(TAG, MESSAGE_ERROR_WHEN_IMPLEMENT_QUERY + " : " + sql);
				ex.printStackTrace();
			} finally {
				db.endTransaction();
				statement.close();
				closeDatabaseConnection();
			}
		}

		return success;
	}

	/**
	 * Insert an object to local database
	 * 
	 * @param sql
	 * @return
	 */
	public boolean insert(String sql, Object object, ParameterBinder parameterBinder) {
		if (object == null) {
			Logger.e(TAG, MESSAGE_NO_OBJECT_INSERT + MESSAGE_LINE);
			return false;
		}

		boolean success = false;
		db = openDatabaseConnection();
		if (db != null) {
			SQLiteStatement statement = null;
			try {
				statement = db.compileStatement(sql);
				parameterBinder.bind(statement, object);
				statement.execute();
				success = true;
				Logger.e(TAG, MESSAGE_INSERT_AN_OBJECT_SUCCESSFULLY + MESSAGE_LINE);
			} catch (Exception ex) {
				Logger.e(TAG, MESSAGE_ERROR_WHEN_IMPLEMENT_QUERY + " : " + sql);
				ex.printStackTrace();
			} finally {
				statement.close();
				closeDatabaseConnection();
			}
		}

		return success;
	}

	/**
	 * Update a row in table
	 * 
	 * @param sql
	 * @return
	 */
	public boolean update(String tableName, String set, String where) {
		if (tableName == null || tableName.equalsIgnoreCase("") || where == null || where.equalsIgnoreCase("")) {
			Logger.e(TAG, MESSAGE_NO_OBJECT_UPDATED + MESSAGE_LINE);
			return false;
		}
		StringBuffer sql = null;
		boolean success = false;
		db = openDatabaseConnection();
		if (db != null) {
			SQLiteStatement statement = null;
			try {

				sql = new StringBuffer();
				sql.append("Update " + tableName);
				sql.append(" Set " + set);
				sql.append(" Where " + where);
				Logger.d(TAG, "Exec SQL : " + sql.toString());
				statement = db.compileStatement(sql.toString());
				statement.execute();
				success = true;
				Logger.e(TAG, MESSAGE_UPDATE_AN_OBJECT_SUCCESSFULLY + MESSAGE_LINE);
			} catch (Exception ex) {
				Logger.e(TAG, MESSAGE_ERROR_WHEN_IMPLEMENT_QUERY + sql.toString());
				ex.printStackTrace();
			} finally {
				statement.close();
				closeDatabaseConnection();
			}
		}

		return success;
	}

	// =================================================================

	/**
	 * Run SELECT command
	 * 
	 * @param table
	 * @param columns
	 * @param where
	 * @return
	 */
	protected Cursor execSelect(SQLiteDatabase db, String table, String columns, String where) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		if (columns == null || columns.equals("") || columns.trim().equalsIgnoreCase("*")) {
			sql.append("*");
		} else {
			String[] st = columns.split(" ");
			String[] fields = new String[st.length];
			int n = 0;
			for (String c : st) {
				if ("".equals(c) == false) {
					fields[n] = c;
					n++;
				}
			}
			sql.append(join(fields, ", "));
		}
		sql.append(" FROM ");
		sql.append(table);
		if (where != null && !where.equalsIgnoreCase("")) {
			sql.append(" WHERE ");
			sql.append(where);
		}
		String s = sql.toString();
		Logger.d(TAG, "Exect SQL :" + s);
		return db.rawQuery(s, null);
	}

	/**
	 * Run SELECT command with limit
	 * 
	 * @param table
	 * @param columns
	 * @param where
	 * @return
	 */
	protected Cursor execSelect(SQLiteDatabase db, String table, String columns, String where, int limit) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		if (columns == null || columns.equals("") || columns.trim().equalsIgnoreCase("*")) {
			sql.append("*");
		} else {
			String[] st = columns.split(" ");
			String[] fields = new String[st.length];
			int n = 0;
			for (String c : st) {
				if ("".equals(c) == false) {
					fields[n] = c;
					n++;
				}
			}
			sql.append(join(fields, ", "));
		}
		sql.append(" FROM ");
		sql.append(table);
		if (where != null && !where.equalsIgnoreCase("")) {
			sql.append(" WHERE ");
			sql.append(where);
		}
		if (!(limit == 0)) {
			sql.append(" Limit ");
			sql.append(limit);
		}
		String s = sql.toString();
		Logger.d(TAG, "Exect SQL :" + s);
		return db.rawQuery(s, null);
	}

	/**
	 * Merge all elements of a string array into a string
	 * 
	 * @param strings
	 * @param separator
	 * @return
	 */
	public static String join(String[] strings, String separator) {
		StringBuffer sb = new StringBuffer();
		int max = strings.length;
		for (int i = 0; i < max; i++) {
			if (i != 0)
				sb.append(separator);
			sb.append(strings[i]);
		}
		return sb.toString();
	}

	/**
	 * Run Raw query sql
	 * 
	 * @return
	 */
	public Cursor execRawQuery(SQLiteDatabase db, String sql) {
		Logger.d("Database ", "Exec SQL :" + sql);
		return db.rawQuery(sql, null);
	}

	// ==================== SQLITE UTILITY FUNCTION ====================

	/**
	 * Open database connection
	 */
	private SQLiteDatabase openDatabaseConnection() {
		// Open database connection
		return (new OpenDBHelper(context, new DatabaseConfig())).getWritableDatabase();
	}

	/**
	 * Close database connection
	 */
	private void closeDatabaseConnection() {
		if (db != null) {
			db.close();
		}
	}
}
