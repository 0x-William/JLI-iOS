package com.pt.music.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pt.music.R;
import com.pt.music.object.Song;

public class SongPlayingAdapter extends BaseAdapter {
	private List<Song> listSongs;
	private LayoutInflater layoutInflater;
	private int index;

	public SongPlayingAdapter(Context context, List<Song> listSongs) {
		this.listSongs = listSongs;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		index = -1;
	}

	// NullPointerException
	public int getCount() {
		return listSongs.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_song_playing, null);
		}

		View layoutSong = convertView.findViewById(R.id.layoutSong);
		TextView lblName = (TextView) convertView.findViewById(R.id.lblName);
		TextView lblArtist = (TextView) convertView.findViewById(R.id.lblArtist);

		Song item = listSongs.get(position);
		if (item != null) {
			lblName.setText(item.getName());
			lblArtist.setText(item.getArtist());
		}

		if (position == index) {
			layoutSong.setBackgroundResource(R.drawable.bg_current_song);
		} else {
			layoutSong.setBackgroundColor(Color.TRANSPARENT);
		}
		return convertView;
	}
}
