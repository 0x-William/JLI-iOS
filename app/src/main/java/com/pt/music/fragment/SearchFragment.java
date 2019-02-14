package com.pt.music.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.NetworkError;
import com.android.volley.VolleyError;
import com.pt.music.BaseFragment;
import com.pt.music.R;
import com.pt.music.activity.MainActivity;
import com.pt.music.adapter.SongAdapter;
import com.pt.music.config.Args;
import com.pt.music.config.GlobalValue;
import com.pt.music.config.WebserviceApi;
import com.pt.music.modelmanager.ModelManager;
import com.pt.music.modelmanager.ModelManagerListener;
import com.pt.music.object.Song;
import com.pt.music.slidingmenu.SlidingMenu;
import com.pt.music.util.AppUtil;
import com.pt.music.modelmanager.CommonParser;
import com.pt.music.util.SmartLog;
import com.pt.music.widget.AutoBgButton;
import com.pt.music.widget.pulltorefresh.PullToRefreshBase;
import com.pt.music.widget.pulltorefresh.PullToRefreshListView;
import com.pt.music.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;

public class SearchFragment extends BaseFragment {
	private AutoBgButton btnSearch;
	private EditText txtKeyword;
	// private ListView lsvResult;
	private PullToRefreshListView lsvResult;
	private ListView lsvActually;
	private View lblNoResult;
	private List<Song> listResult;
	private SongAdapter songAdapter;
	private ProgressDialog progressDialog;
	private int page=0, totalPage;
	public String keyword = "";
	private boolean isSearch = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_search, container, false);
		initUIBase(view);
		initControl(view);
		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			getMainActivity().menu
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			getMainActivity().setVisibilityFooter();
			if (keyword.equals("")) {

				page = 0;
				totalPage = 0;
				clearList();
			}
			txtKeyword.setText(keyword);
		}
	}

	@Override
	protected void initUIBase(View view) {
		super.initUIBase(view);
		btnSearch = (AutoBgButton) view.findViewById(R.id.btnSearch);
		txtKeyword = (EditText) view.findViewById(R.id.txtKeyword);
		lsvResult = (PullToRefreshListView) view.findViewById(R.id.lsvResult);
		lsvActually = lsvResult.getRefreshableView();
		lblNoResult = view.findViewById(R.id.lblNoResult);
	}

	private void initControl(View view) {
		setButtonMenu(view);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickSearch();
			}
		});

		txtKeyword.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					onClickSearch();
					return true;
				}
				return false;
			}
		});

		setHeaderTitle(R.string.search);
		listResult = new ArrayList<Song>();
		songAdapter = new SongAdapter(getActivity(), listResult);
		lsvActually.setAdapter(songAdapter);
		lsvResult.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long l) {
				// Log.e("SearchFragment", "position :"+position);
				ListSongsFragment.isShowing = false;
				GlobalValue.currentSongPlay = (int) l;
				GlobalValue.listSongPlay.clear();
				GlobalValue.listSongPlay.addAll(listResult);
				getMainActivity().toMusicPlayer = MainActivity.FROM_SEARCH;
				getMainActivity().isTapOnFooter = false;
				getMainActivity().gotoFragment(MainActivity.PLAYER_FRAGMENT);
			}
		});

		lsvResult.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				page = 1;
				getSongToSearch(true, true);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				getSongToSearch(false, true);
			}
		});
	}

	private void onClickSearch() {
		totalPage = 0;
		page = 0;
		clearList();
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(txtKeyword.getWindowToken(), 0);
		keyword = txtKeyword.getText().toString().trim();

		// showLoadingDialog();
		getSongToSearch(true, false);
	}

	private void getSongToSearch(boolean isRefresh, final boolean isPull) {
		Log.d("PAGE_FIRST", page + "-" + totalPage);
		if (++page > totalPage && totalPage > 0 && !isRefresh) {
			Log.d("PAGE", page + "-" + totalPage);
			showNoMoreData();
		} else {
			Log.d("PAGE", page + "-" + totalPage);
			String getUrl = WebserviceApi.SEARCH_SONG + "?song=" + keyword
					+ "&page=" + page + "&user_id=" + GlobalValue.pref.getStringValue(Args.USER_ID);

			ModelManager.sendGetRequest(self, getUrl, null, !isPull, new ModelManagerListener() {
				@Override
				public void onError(VolleyError error) {
					if (error instanceof NetworkError) {
						AppUtil.alertNetworkUnavailableCommon(getActivity());
					} else {
						AppUtil.alert(getActivity(),
								getString(R.string.server_error));
					}
					lsvResult.onRefreshComplete();
				}

				@Override
				public void onSuccess(String json) {
					processListSongResponse(json.substring(json
							.indexOf("{")));
					lsvResult.onRefreshComplete();
				}
			});
		}
	}

	private void processListSongResponse(String response) {
		String json = "";
		try {
			json = response;
			if (json == null) {
				AppUtil.alert(getActivity(),
						getString(R.string.json_server_error));
				return;
			}

			SmartLog.log("ListSongFragment", json);
			JSONObject entry = new JSONObject(json);

			if (CommonParser.isInteger(entry
					.getString(WebserviceApi.KEY_ALL_PAGE))) {
				totalPage = entry.getInt(WebserviceApi.KEY_ALL_PAGE);
			}

			if (entry.getString(WebserviceApi.KEY_STATUS).equalsIgnoreCase(
					WebserviceApi.KEY_SUCCESS)) {

				List<Song> arrSong = CommonParser.parseSongFromServer(json);
				for (Song song : arrSong) {
					addSongToListResult(song);
				}

				if (listResult.size() > 0) {
					lblNoResult.setVisibility(View.GONE);
					lsvResult.setVisibility(View.VISIBLE);
					songAdapter.notifyDataSetChanged();
				} else {
					lblNoResult.setVisibility(View.VISIBLE);
					lsvResult.setVisibility(View.GONE);
					songAdapter.notifyDataSetChanged();
				}

			} else {
				lblNoResult.setVisibility(View.VISIBLE);
				lsvResult.setVisibility(View.GONE);
				songAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// hideLoadingDialog();
	}

	private void showNoMoreData() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				showToast(R.string.endPage);
				lsvResult.onRefreshComplete();
			}
		}, 100);
	}

	private void addSongToListResult(Song song) {
		listResult.add(song);
	}

	private void clearList() {
		if (listResult != null) {
			listResult.clear();
		}
		if (songAdapter != null) {
			songAdapter.notifyDataSetChanged();
		}
	}
}
