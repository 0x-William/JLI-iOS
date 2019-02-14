package com.pt.music.object;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class Playlist {
	private String id;
	private String name;
	private List<Song> listSongs;

	public Playlist() {
		listSongs = new ArrayList<Song>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Song> getListSongs() {
		if (listSongs == null) {
			return new ArrayList<Song>();
		}
		return listSongs;
	}

	public void setListSongs(List<Song> listSongs) {
		this.listSongs = listSongs;
	}

	public void setListSongs(String jsonSong) {
		if (listSongs == null) {
			listSongs = new ArrayList<Song>();
		}
		try {
			JSONArray array = new JSONArray(jsonSong);
			for (int i = 0; i < array.length(); i++) {
				listSongs.add(new Song(array.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public boolean removeSong(int position)
	{
		if (listSongs != null){
			return listSongs.remove(position) != null ? true: false;			
		}
		return false;		
	}

	public void addSong(Song song) {
		if (listSongs == null) {
			listSongs = new ArrayList<Song>();
		}
		listSongs.add(song);
	}

	public String getJsonArraySong() {
		JSONArray array = new JSONArray();
		for (Song song : listSongs) {
			array.put(song.getJsonObject());
		}
		return array.toString();
	}
}
