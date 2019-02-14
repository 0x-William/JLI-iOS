package com.pt.music.object;

import java.util.ArrayList;
import java.util.List;

public class CategoryMusic {
	private int id;
	private String title;
	private String image;
	private List<Song> listSongs;
	private String nextPage;
	private boolean isParent;
	private boolean hasChild;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public void addListSongs(List<Song> listSongs) {
		this.listSongs.addAll(listSongs);
	}

	public void addSong(Song song) {
		if (listSongs == null) {
			listSongs = new ArrayList<Song>();
		}
		listSongs.add(song);
	}

	public void clearSong() {
		if (listSongs == null) {
			listSongs = new ArrayList<Song>();
		} else {
			listSongs.clear();
		}
	}

	public String getNextPage() {
		return nextPage;
	}

	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}

	public boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(boolean isParent) {
		this.isParent = isParent;
	}

	public boolean getHasChild() {
		return hasChild;
	}

	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}
}
