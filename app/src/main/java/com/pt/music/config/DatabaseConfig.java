package com.pt.music.config;

import android.annotation.SuppressLint;

import com.pt.music.PacketUtility;

public final class DatabaseConfig {
	private final int DB_VERSION = 3;
	private final String DB_NAME = "DbMusicOnline1.sqlite";

	// --------------------TABLE FAVORITE----------------------
	public static String TABLE_FAVORITE = "tbFavorite";
	public static String TABLE_PLAYLIST = "tbPlaylist";
	public static String KEY_ID = "id";
	public static String KEY_NAME = "name";
	public static String KEY_URL = "url";
	public static String KEY_IMAGE = "image";
	public static String KEY_ARTIST = "artist";
	public static String KEY_POSITION = "position";
	public static String KEY_LIST_SONG = "list_song";

	/**
	 * Get database version
	 * 
	 * @return
	 */
	public int getDatabaseVersion() {
		return DB_VERSION;
	}

	/**
	 * Get database name
	 * 
	 * @return
	 */
	public String getDatabaseName() {
		return DB_NAME;
	}

	/**
	 * Get database path
	 * 
	 * @return
	 */
	@SuppressLint("SdCardPath")
	public String getDatabasePath() {
		return "/data/data/" + PacketUtility.getPacketName() + "/databases/";
	}

	/**
	 * Get database path
	 * 
	 * @return
	 */
	public String getDatabaseFullPath() {
		return getDatabasePath() + DB_NAME;
	}
}
