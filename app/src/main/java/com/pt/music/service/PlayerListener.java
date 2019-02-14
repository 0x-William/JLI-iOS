package com.pt.music.service;

public interface PlayerListener {
	public void onSeekChanged(int maxProgress,String lengthTime, String currentTime, int progress);
	public void onChangeSong(int indexSong);	
	public void OnMusicPrepared();
}
