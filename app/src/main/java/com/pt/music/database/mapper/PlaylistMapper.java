package com.pt.music.database.mapper;

import android.database.Cursor;

import com.pt.music.config.DatabaseConfig;
import com.pt.music.database.CursorParseUtility;
import com.pt.music.database.IRowMapper;
import com.pt.music.object.Playlist;

public class PlaylistMapper implements IRowMapper<Playlist> {
	@Override
	public Playlist mapRow(Cursor row, int rowNum) {
		Playlist song = new Playlist();
		song.setId(CursorParseUtility.getString(row, DatabaseConfig.KEY_ID));
		song.setName(CursorParseUtility.getString(row, DatabaseConfig.KEY_NAME));
		song.setListSongs(CursorParseUtility.getString(row, DatabaseConfig.KEY_LIST_SONG));
		return song;
	}
}