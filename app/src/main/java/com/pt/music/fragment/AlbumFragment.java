package com.pt.music.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.android.volley.NetworkError;
import com.android.volley.VolleyError;
import com.pt.music.BaseFragment;
import com.pt.music.R;
import com.pt.music.activity.MainActivity;
import com.pt.music.adapter.AlbumAdapter;
import com.pt.music.config.Args;
import com.pt.music.config.GlobalValue;
import com.pt.music.config.WebserviceApi;
import com.pt.music.modelmanager.ModelManager;
import com.pt.music.modelmanager.ModelManagerListener;
import com.pt.music.object.Album;
import com.pt.music.slidingmenu.SlidingMenu;
import com.pt.music.util.AppUtil;
import com.pt.music.modelmanager.CommonParser;
import com.pt.music.widget.pulltorefresh.PullToRefreshBase;
import com.pt.music.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.pt.music.widget.pulltorefresh.PullToRefreshGridView;

public class AlbumFragment extends BaseFragment {

    private PullToRefreshGridView grvAlbum;
    private GridView grvActually;
    private AlbumAdapter albumAdapter;
    private List<Album> arrAlbum = new ArrayList<Album>();
    public static boolean isShowing = false;
    private int page = 0;
    private boolean isLoadMore = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_album, container, false);
        initUIBase(view);
        setButtonMenu(view);
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // loadAlbum();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            page = 0;
            loadAlbum(true, false);
            getMainActivity().menu
                    .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            getMainActivity().setVisibilityFooter();
        }
    }

    @Override
    protected void initUIBase(View view) {
        // TODO Auto-generated method stub
        super.initUIBase(view);
        setHeaderTitle(R.string.collections);
        grvAlbum = (PullToRefreshGridView) view.findViewById(R.id.grvAlbum);
        grvActually = grvAlbum.getRefreshableView();

        albumAdapter = new AlbumAdapter(self, arrAlbum);
        grvActually.setAdapter(albumAdapter);

        initControl(view);

    }

    private void initControl(View view) {
        grvAlbum.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position,
                                    long l) {
                ListSongsFragment.isShowing = false;
                GlobalValue.currentAlbumId = arrAlbum.get(position).getId();
                GlobalValue.currentAlbumName = arrAlbum.get(position).getName();
                getMainActivity().gotoFragment(MainActivity.LIST_SONG_FRAGMENT);
            }
        });
        grvAlbum.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // TODO Auto-generated method stub
                return true;
            }
        });

        grvAlbum.setOnRefreshListener(new OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<GridView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(),
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);
                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                loadAlbum(true, true);
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<GridView> refreshView) {
                loadAlbum(false, true);
            }
        });
    }

    private void loadAlbum(final boolean isRefresh, final boolean isPull) {

        if (isRefresh) {
            isLoadMore = true;
            arrAlbum.clear();
            page = 0;
        }

        if (!isLoadMore) {
            showNoMoreData();
        } else {
            page++;

            String url = WebserviceApi.GET_ALBUM_API + "?page=" + page + "&user_id=" + GlobalValue.pref.getStringValue(Args.USER_ID);
            ModelManager.sendGetRequest(self, url, null, !isPull, new ModelManagerListener() {
                @Override
                public void onError(VolleyError error) {
                    if (error instanceof NetworkError) {
                        AppUtil.alertNetworkUnavailableCommon(getActivity());
                        return;
                    } else {
                        AppUtil.alert(getActivity(),
                                getString(R.string.server_error));
                    }

                }

                @Override
                public void onSuccess(String json) {
                    processAlbumResponse(json
                                    .substring(json.indexOf("{")),
                            isRefresh);
                }
            });
        }
    }

    private void showNoMoreData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showToast(R.string.endPage);
                grvAlbum.onRefreshComplete();
            }
        }, 100);
    }

    private void processAlbumResponse(String json, boolean isRefresh) {
        try {
            if (json == null) {
                AppUtil.alert(getActivity(),
                        getString(R.string.json_server_error));
                return;
            }

            JSONObject entry = new JSONObject(json);

            if (entry.getString(WebserviceApi.KEY_STATUS).equalsIgnoreCase(
                    WebserviceApi.KEY_SUCCESS)) {

                List<Album> tempList = CommonParser.parseAlbumFromServer(json);
                if (tempList.size() > 0) {
                    arrAlbum.addAll(tempList);
                    if (!isRefresh)
                        showToast(R.string.loadmore_success);
                    isLoadMore = true;
                } else {
                    isLoadMore = false;
                    showToast(R.string.endPage);
                }

                albumAdapter.notifyDataSetChanged();
                grvActually.setSelection(arrAlbum.size() - 1);
                grvAlbum.onRefreshComplete();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
