package com.pt.music.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.pt.music.BaseFragment;
import com.pt.music.R;
import com.pt.music.adapter.SongPlayingAdapter;
import com.pt.music.config.GlobalValue;

public class PlayerListPlayingFragment extends BaseFragment {
	private ListView lsvSongPlaying;
	private SongPlayingAdapter songPlayingAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_player_list_playing, container, false);
		songPlayingAdapter = new SongPlayingAdapter(getActivity(), GlobalValue.listSongPlay);
		lsvSongPlaying = (ListView) view.findViewById(R.id.lsvSongPlaying);
		lsvSongPlaying.setAdapter(songPlayingAdapter);
		lsvSongPlaying.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long l) {
				getMainActivity().mService.startMusic(position);
			}
		});
		//setButtonMenu(view);
		return view;
	}

	public void refreshListPlaying() {
		if (songPlayingAdapter != null) {
			songPlayingAdapter.setIndex(GlobalValue.currentSongPlay);
			songPlayingAdapter.notifyDataSetChanged();
			lsvSongPlaying.smoothScrollToPosition(GlobalValue.currentSongPlay);
		} else {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					refreshListPlaying();
				}
			}, 500);
		}
	}
}
