package com.pt.music.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.VolleyError;
import com.pt.music.BaseFragment;
import com.pt.music.R;
import com.pt.music.activity.MainActivity;
import com.pt.music.adapter.SongAdapter;
import com.pt.music.config.Args;
import com.pt.music.config.GlobalValue;
import com.pt.music.config.WebserviceApi;
import com.pt.music.modelmanager.CommonParser;
import com.pt.music.modelmanager.ModelManager;
import com.pt.music.modelmanager.ModelManagerListener;
import com.pt.music.object.Playlist;
import com.pt.music.object.Song;
import com.pt.music.slidingmenu.SlidingMenu;
import com.pt.music.util.AppUtil;
import com.pt.music.util.Logger;
import com.pt.music.util.SmartLog;
import com.pt.music.widget.pulltorefresh.PullToRefreshBase;
import com.pt.music.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.pt.music.widget.pulltorefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListSongsFragment extends BaseFragment {
    public static final String SORT_BY_VIEWS = "listen";
    public static final String SORT_BY_DOWNLOAD = "download";

    private PullToRefreshListView lsvSong;
    private ListView lsvActually;
    private List<Song> arrSong = new ArrayList<Song>();
    private SongAdapter songAdapter;
    private View view, btnSortBy;
    private int page;
    private int totalPage;
    private static String currentSortBy = "";
    private boolean isFromPlaylist = false;
    public static boolean isShowing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_song, container, false);
        initUIBase(view);
        initControl(view);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (!isShowing) {
                page = 0;
                totalPage = 0;
                initData();
                isShowing = true;
            }
            // getData(true);
            getMainActivity().menu
                    .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            getMainActivity().setVisibilityFooter();
        }
    }

    @Override
    protected void initUIBase(View view) {
        super.initUIBase(view);
        lsvSong = (PullToRefreshListView) view.findViewById(R.id.lsvSong);
        lsvActually = lsvSong.getRefreshableView();

        songAdapter = new SongAdapter(getActivity(), arrSong);
        lsvActually.setAdapter(songAdapter);

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    private void initControl(View view) {
        setButtonMenu(view);
        initRightButton(view);
        lsvSong.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position,
                                    long l) {
                Logger.e("currentFragment: "
                        + getMainActivity().currentFragment);
                getMainActivity().toMusicPlayer = MainActivity.FROM_LIST_SONG;
                GlobalValue.currentSongPlay = (int) l;
                GlobalValue.listSongPlay.clear();
                GlobalValue.listSongPlay.addAll(arrSong);
                getMainActivity().isTapOnFooter = false;
                getMainActivity().gotoFragment(MainActivity.PLAYER_FRAGMENT);
            }
        });
        lsvSong.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // TODO Auto-generated method stub
                showConfirmDeleteItemFromPlaylistDialog(position);
                return true;
            }
        });

        lsvSong.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(),
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);
                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                page = 0;
                arrSong.clear();
                getData(true, true);
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(),
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);
                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getData(false, true);
            }
        });
    }

    private void showConfirmDeleteItemFromPlaylistDialog(final int index) {
        if (GlobalValue.currentMenu == getMainActivity().PLAYLIST) {
            final Playlist playlist = getMainActivity().currentPlaylist;

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Remove Song")
                    .setMessage(
                            "Do you want to remove this song from '"
                                    + playlist.getName() + " ' ?")
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    boolean result = playlist
                                            .removeSong(index - 1);
                                    // update play list
                                    if (getMainActivity().databaseUtility
                                            .updatePlaylist(playlist)) {
                                        Toast.makeText(
                                                getMainActivity(),
                                                "Remove song from "
                                                        + playlist.getName()
                                                        + " successfully!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    if (result == true) {
                                        arrSong.clear();
                                        arrSong.addAll(getMainActivity().currentPlaylist
                                                .getListSongs());
                                        songAdapter.notifyDataSetChanged();
                                    }
                                }
                            }).setNegativeButton(android.R.string.cancel, null);
            builder.create().show();
        }
    }

    private void initRightButton(View view) {
        btnSortBy = view.findViewById(R.id.btnRightButton);
        btnSortBy.setVisibility(View.INVISIBLE);
        btnSortBy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showPopupMenu(btnSortBy);
            }
        });
    }

    private void getData(boolean isRefresh, boolean isPull) {
        //
        switch (GlobalValue.currentMenu) {
            case MainActivity.TOP_CHART:
                getTopWeekMusic(isRefresh, isPull);
                break;
            case MainActivity.ALL_SONG:
                getAllMusic(isRefresh, isPull);
                break;

            case MainActivity.CATEGORY_MUSIC:
                getSongByCategory(isRefresh, isPull);
                break;
            case MainActivity.ALBUM:
                getSongByAlbum(isRefresh, isPull);
                break;
            case MainActivity.PLAYLIST:
                arrSong.clear();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        arrSong.addAll(getMainActivity().currentPlaylist
                                .getListSongs());
                        songAdapter.notifyDataSetChanged();
                        lsvSong.onRefreshComplete();
                    }
                }, 500);

                break;
        }
    }

    private void getTopWeekMusic(final boolean isRefresh, final boolean isPull) {
        if (++page > totalPage && totalPage > 0 && !isRefresh) {
            showNoMoreData();
        } else {
            String getUrl = WebserviceApi.TOP_SONG + "?page=" + page + "&user_id=" + GlobalValue.pref.getStringValue(Args.USER_ID);
            Logger.e(TAG, "URL : " + getUrl);

            ModelManager.sendGetRequest(self, getUrl, null, !isPull, new ModelManagerListener() {
                @Override
                public void onError(VolleyError error) {
                    if (error instanceof NetworkError) {
                        AppUtil.alertNetworkUnavailableCommon(getActivity());
                    } else {
                        AppUtil.alert(getActivity(),
                                getString(R.string.server_error));
                    }
                    lsvSong.onRefreshComplete();

                }

                @Override
                public void onSuccess(String json) {
                    processListSongResponse(json.substring(json
                            .indexOf("{")));
                    lsvSong.onRefreshComplete();
                }
            });

        }
    }

    private void getAllMusic(final boolean isRefresh, final boolean isPull) {
        if (++page > totalPage && totalPage > 0 && !isRefresh) {
            showNoMoreData();
        } else {
            String getUrl = WebserviceApi.GET_SONGS + "?page=" + page
                    + "&type=" + currentSortBy;
            Logger.e(TAG, "URL : " + getUrl);

            ModelManager.sendGetRequest(self, getUrl, null, !isPull, new ModelManagerListener() {
                @Override
                public void onError(VolleyError error) {
                    if (error instanceof NetworkError) {
                        AppUtil.alertNetworkUnavailableCommon(getActivity());
                    } else {
                        AppUtil.alert(getActivity(),
                                getString(R.string.server_error));
                    }
                    lsvSong.onRefreshComplete();
                }

                @Override
                public void onSuccess(String json) {
                    processListSongResponse(json.substring(json
                            .indexOf("{")));
                    lsvSong.onRefreshComplete();
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

                List<Song> tempList = CommonParser.parseSongFromServer(json);
                arrSong.addAll(tempList);
                songAdapter.notifyDataSetChanged();
                lsvSong.onRefreshComplete();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showToast(R.string.endPage);
                lsvSong.onRefreshComplete();
            }
        }, 100);
    }

    private void initData() {
        getMainActivity().currentFragment = MainActivity.LIST_SONG_FRAGMENT;
        if (arrSong != null) {
            arrSong.clear();
            refreshList();
        }
        switch (GlobalValue.currentMenu) {
            case MainActivity.TOP_CHART:
                setHeaderTitle(R.string.suggested_tracks);
                if (getMainActivity().listTopWeek.size() == 0) {
                    getData(true, false);
                } else {
                    getData(false, false);
                }
                btnSortBy.setVisibility(View.INVISIBLE);
                setButtonMenu(view);
                break;
            case MainActivity.ALL_SONG:
                setHeaderTitle(R.string.allSong);
                getData(true, false);
                btnSortBy.setVisibility(View.VISIBLE);
                setButtonMenu(view);

                break;

            case MainActivity.ALBUM:
                setHeaderTitle(GlobalValue.currentAlbumName);
                getData(true, false);
                btnSortBy.setVisibility(View.INVISIBLE);
                setButtonBack(view);
                break;

            case MainActivity.CATEGORY_MUSIC:
                setHeaderTitle(GlobalValue.currentCategoryName);
                getData(true, false);
                setButtonBack(view);
                btnSortBy.setVisibility(View.INVISIBLE);
                break;

            case MainActivity.PLAYLIST:
                setHeaderTitle(getMainActivity().currentPlaylist.getName());
                arrSong.clear();
                arrSong.addAll(getMainActivity().currentPlaylist.getListSongs());
                songAdapter.notifyDataSetChanged();
                btnSortBy.setVisibility(View.INVISIBLE);
                setButtonBack(view);
                break;
        }
    }

    private void getSongByCategory(boolean isRefresh, final boolean isPull) {
        if (++page > totalPage && totalPage > 0 && !isRefresh) {
            showNoMoreData();
        } else {

            String getUrl = WebserviceApi.GET_SONG_BY_CATEGORY + "?categoryId="
                    + GlobalValue.currentCategoryId + "&page=" + page + "&user_id=" + GlobalValue.pref.getStringValue(Args.USER_ID);

            ModelManager.sendGetRequest(self, getUrl, null, !isPull, new ModelManagerListener() {
                @Override
                public void onError(VolleyError error) {
                    if (error instanceof NetworkError) {
                        AppUtil.alertNetworkUnavailableCommon(getActivity());
                    } else {
                        AppUtil.alert(getActivity(),
                                getString(R.string.server_error));
                    }
                    lsvSong.onRefreshComplete();
                }

                @Override
                public void onSuccess(String json) {
                    processListSongResponse(json.substring(json
                            .indexOf("{")));
                    lsvSong.onRefreshComplete();
                }
            });

        }
    }

    private void getSongByAlbum(boolean isRefresh, final boolean isPull) {

        if (++page > totalPage && totalPage > 0 && !isRefresh) {
            showNoMoreData();
        } else {

            String getUrl = WebserviceApi.GET_SONG_BY_ALBUM + "?albumId="
                    + GlobalValue.currentAlbumId + "&page=" + page + "&user_id=" + GlobalValue.pref.getStringValue(Args.USER_ID);

            ModelManager.sendGetRequest(self, getUrl, null, !isPull, new ModelManagerListener() {
                @Override
                public void onError(VolleyError error) {
                    if (error instanceof NetworkError) {
                        AppUtil.alertNetworkUnavailableCommon(getActivity());
                    } else {
                        AppUtil.alert(getActivity(),
                                getString(R.string.server_error));
                    }
                    lsvSong.onRefreshComplete();
                }

                @Override
                public void onSuccess(String json) {
                    processListSongResponse(json.substring(json
                            .indexOf("{")));
                    lsvSong.onRefreshComplete();
                }
            });

        }
    }

    private void refreshList() {
        songAdapter.notifyDataSetChanged();
        lsvSong.onRefreshComplete();
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(getMainActivity(), v);
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO Auto-generated method stub
                if (item.getTitle().equals(
                        getMainActivity().getString(R.string.sort_by_views))) {
                    currentSortBy = SORT_BY_VIEWS;
                } else if (item.getTitle().equals(
                        getMainActivity().getString(R.string.sort_by_download))) {
                    currentSortBy = SORT_BY_DOWNLOAD;
                } else {
                    currentSortBy = "";
                }
                page = 0;
                arrSong.clear();
                getAllMusic(true, false);
                return false;
            }
        });
        popupMenu.inflate(R.menu.popupmenu);
        popupMenu.show();
    }
}
