package com.pt.music.database;

import java.util.List;

import android.content.Context;

import com.pt.music.config.DatabaseConfig;
import com.pt.music.database.binder.PlaylistBinder;
import com.pt.music.database.binder.SongBinder;
import com.pt.music.database.mapper.PlaylistMapper;
import com.pt.music.database.mapper.SongMapper;
import com.pt.music.object.Playlist;
import com.pt.music.object.Song;

public final class DatabaseUtility {
	private PrepareStatement statement;

	public DatabaseUtility(Context context) {
		statement = new PrepareStatement(context);
	}

	private static String STRING_SQL_INSERT_INTO_TABLE_FAVORITE = "INSERT OR REPLACE INTO "
			+ DatabaseConfig.TABLE_FAVORITE
			+ "("
			+ DatabaseConfig.KEY_ID
			+ ","
			+ DatabaseConfig.KEY_NAME
			+ " ,"
			+ DatabaseConfig.KEY_URL
			+ " ,"
			+ DatabaseConfig.KEY_IMAGE
			+ " ,"
			+ DatabaseConfig.KEY_ARTIST
			+ " ,"
			+ DatabaseConfig.KEY_POSITION
			+ ") VALUES (?, ?, ?, ?, ?, ?)";

	public List<Song> getAllFavorite() {
		return statement.select(DatabaseConfig.TABLE_FAVORITE, "*", "",
				new SongMapper());
	}

	public boolean insertFavorite(Song song) {
		return statement.insert(STRING_SQL_INSERT_INTO_TABLE_FAVORITE, song,
				new SongBinder());
	}

	public boolean deleteFavorite(Song song) {
		return statement.query(
				"DELETE FROM " + DatabaseConfig.TABLE_FAVORITE + " where "
						+ DatabaseConfig.KEY_ID + "='" + song.getId() + "'"
						+ "and" + " " + DatabaseConfig.KEY_NAME + "='"
						+ song.getName() + "'" + "and" + " "
						+ DatabaseConfig.KEY_ARTIST + "='" + song.getArtist()
						+ "'", null);
	}
	public boolean deleteAllFavorite() {
		return statement.query(
				"DELETE FROM " + DatabaseConfig.TABLE_FAVORITE, null);
	}
	// We use Favorite table to save List Songs
	public void insertFavorite(List<Song> listSongs){
		int i = 0;
		for(i = 0; i <= listSongs.size() -1; i ++){
			insertFavorite(listSongs.get(i));			
		}
		
	}

	private static String STRING_SQL_INSERT_INTO_TABLE_PLAYLIST = "INSERT OR REPLACE INTO "
			+ DatabaseConfig.TABLE_PLAYLIST
			+ "("
			+ DatabaseConfig.KEY_ID
			+ ","
			+ DatabaseConfig.KEY_NAME
			+ " ,"
			+ DatabaseConfig.KEY_LIST_SONG
			+ ") VALUES (?, ?, ?)";

	public List<Playlist> getAllPlaylist() {
		return statement.select(DatabaseConfig.TABLE_PLAYLIST, "*", "",
				new PlaylistMapper());
	}

	public Playlist getAPlaylist(String id) {
		List<Playlist> list = statement.select(DatabaseConfig.TABLE_PLAYLIST,
				"*",DatabaseConfig.KEY_ID + "='" + id + "'",
				new PlaylistMapper());
		if (list.size() > 0)
			return list.get(0);
		return null;
	}

	public boolean insertPlaylist(Playlist playlist) {
		return statement.insert(STRING_SQL_INSERT_INTO_TABLE_PLAYLIST,
				playlist, new PlaylistBinder());
	}

	public boolean deletePlaylist(Playlist playlist) {
		return statement.query("DELETE FROM " + DatabaseConfig.TABLE_PLAYLIST
				+ " where " + DatabaseConfig.KEY_ID + "='" + playlist.getId()
				+ "'", null);
	}

	public boolean updatePlaylist(Playlist playlist) {
		return statement
				.query("UPDATE " + DatabaseConfig.TABLE_PLAYLIST + " SET "
						+ DatabaseConfig.KEY_LIST_SONG + "='"
						+ playlist.getJsonArraySong() + "' where "
						+ DatabaseConfig.KEY_ID + "='" + playlist.getId() + "'",
						null);
	}

}
