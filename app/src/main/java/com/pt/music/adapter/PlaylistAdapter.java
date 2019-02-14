package com.pt.music.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pt.music.R;
import com.pt.music.object.Playlist;

public class PlaylistAdapter extends BaseAdapter {
	private List<Playlist> listPlaylists;
	private LayoutInflater layoutInflater;

	public PlaylistAdapter(Context context, List<Playlist> listPlaylists) {
		this.listPlaylists = listPlaylists;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return listPlaylists.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_playlist, null);
		}

		Playlist item = listPlaylists.get(position);
		if (item != null) {
			((TextView) convertView.findViewById(R.id.lblNamePlaylist)).setText(item.getName());
		}
		return convertView;
	}
}
