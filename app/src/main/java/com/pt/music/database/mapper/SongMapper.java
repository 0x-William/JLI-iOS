package com.pt.music.database.mapper;

import android.database.Cursor;

import com.pt.music.config.DatabaseConfig;
import com.pt.music.database.CursorParseUtility;
import com.pt.music.database.IRowMapper;
import com.pt.music.object.Song;

public class SongMapper implements IRowMapper<Song> {
	@Override
	public Song mapRow(Cursor row, int rowNum) {
		Song song = new Song();
		song.setId(CursorParseUtility.getString(row, DatabaseConfig.KEY_ID));
		song.setName(CursorParseUtility.getString(row, DatabaseConfig.KEY_NAME));
		song.setUrl(CursorParseUtility.getString(row, DatabaseConfig.KEY_URL));
		song.setImage(CursorParseUtility.getString(row, DatabaseConfig.KEY_IMAGE));
		song.setArtist(CursorParseUtility.getString(row, DatabaseConfig.KEY_ARTIST));
		song.setPosition(CursorParseUtility.getInt(row, DatabaseConfig.KEY_POSITION));
		return song;
	}
}