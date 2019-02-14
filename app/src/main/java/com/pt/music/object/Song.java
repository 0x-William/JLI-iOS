package com.pt.music.object;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

import com.pt.music.config.WebserviceConfig;
import com.pt.music.util.Logger;
import com.pt.music.util.StringUtil;

public class Song {
	private String id;
	private String idType;
	private String name;
	private String url;
	private String image;
	private String artist;
	private String shareLink;
	private String categoryId;
	private int listenCount = 0, downloadCount = 0;
	private int position;
	private boolean isSelected;

	public Song() {
	}

	public Song(String name, String artist, String url) {
		this.name = name;
		this.artist = artist;
		this.url = url;
		image = "";
	}

	public Song(JSONObject object) {
		Logger.e(object);
		try {
			id = object.getString("id");
			name = object.getString("name");
			url = object.getString("url");
			image = object.getString("image");
			artist = object.getString("artist");
			position = object.getInt("position");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getShareLink() {
		return shareLink;
	}

	public void setShareLink(String shareLink) {
		this.shareLink = shareLink;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCategoryId(String categoryId){
		this.categoryId = categoryId;
	}

	public String getUrl() {
		return url;
	}

	public String getCategoryId(){ return categoryId;}

	public void setUrl(String url) {
		if (StringUtil.checkUrl(url)) {
			this.url = url;
		} else {
			this.url = WebserviceConfig.URL_SONG + url;
		}
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		if (StringUtil.checkUrl(image)) {
			this.image = image;
		} else {
			this.image = WebserviceConfig.URL_IMAGE + image;
		}
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getListenCount() {
		return listenCount;
	}

	public void setListenCount(int listenCount) {
		this.listenCount = listenCount;
	}

	public int getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	public void addMoreDownload() {
		this.downloadCount++;
	}

	public void addNewView() {
		this.listenCount++;
	}

	public boolean compare(Song otherSong) {
		return otherSong.getId().equals(id) && otherSong.getName().equals(name)
				&& otherSong.getArtist().equals(artist);
	}

	@SuppressLint("DefaultLocale")
	public boolean checkNameAndArtist(String keyword) {
		String key = StringUtil.unAccent(keyword.toLowerCase());
		return StringUtil.unAccent(name.toLowerCase()).contains(key)
				|| StringUtil.unAccent(artist.toLowerCase()).contains(key);
	}

	public boolean checkMusicType(String idMusicType) {
		try {
			return idMusicType.equals(idType);
		} catch (Exception e) {
			return false;
		}
	}

	public JSONObject getJsonObject() {
		JSONObject object = new JSONObject();
		try {
			object.put("id", id);
			object.put("id_type", idType);
			object.put("name", name);
			object.put("url", url);
			object.put("image", image);
			object.put("artist", artist);
			object.put("position", position);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
}
