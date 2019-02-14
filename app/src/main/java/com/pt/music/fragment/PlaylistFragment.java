package com.pt.music.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.pt.music.BaseFragment;
import com.pt.music.R;
import com.pt.music.activity.MainActivity;
import com.pt.music.adapter.PlaylistAdapter;
import com.pt.music.config.WebserviceApi;
import com.pt.music.modelmanager.CommonParser;
import com.pt.music.object.Playlist;
import com.pt.music.object.Song;
import com.pt.music.slidingmenu.SlidingMenu;
import com.pt.music.util.AppUtil;
import com.pt.music.util.SmartLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends BaseFragment {
    private ListView lsvPlaylist;
    private List<Playlist> listPlaylists;
    private PlaylistAdapter playlistAdapter;
    private List<Song> listSongs;
    private String[] arraySongName;
    private String playlistName;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container,
                false);
        initUIBase(view);
        setButtonMenu(view);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getMainActivity().menu
                    .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            getMainActivity().setVisibilityFooter();
            // refresh list
            // listPlaylists =
            // getMainActivity().databaseUtility.getAllPlaylist();
            // playlistAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initUIBase(View view) {
        super.initUIBase(view);

        setHeaderTitle(R.string.playlist);

        view.findViewById(R.id.layoutCreatNewPlaylist).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogCreatNewPlaylist();
                    }
                });

        listSongs = new ArrayList<Song>();

        lsvPlaylist = (ListView) view.findViewById(R.id.lsvPlaylist);
        listPlaylists = getMainActivity().databaseUtility.getAllPlaylist();
        playlistAdapter = new PlaylistAdapter(getActivity(), listPlaylists);
        lsvPlaylist.setAdapter(playlistAdapter);
        lsvPlaylist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position,
                                    long l) {
                Playlist playlist = listPlaylists.get(position);
                getMainActivity().currentPlaylist = getMainActivity().databaseUtility
                        .getAPlaylist(playlist.getId());
                ListSongsFragment.isShowing = false;
                getMainActivity().gotoFragment(MainActivity.LIST_SONG_FRAGMENT);
            }
        });
        lsvPlaylist.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // TODO Auto-generated method stub
                showConfirmDeletePlaylistDialog(position);
                return true;
            }
        });
    }

    private void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(getActivity(),
                    getString(R.string.app_name),
                    getString(R.string.getListSongs), true);
        } else {
            progressDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    private void showConfirmDeletePlaylistDialog(final int index) {

        final Playlist playlist = listPlaylists.get(index);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Playlist :")
                .setMessage(
                        "Do you want to delete '"
                                + playlist.getName() + " ' ?")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (getMainActivity().databaseUtility
                                        .deletePlaylist(playlist)) {
                                    listPlaylists.remove(index);
                                    playlistAdapter.notifyDataSetChanged();
                                }
                            }
                        }).setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void showDialogCreatNewPlaylist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_new_playlist,
                null);
        builder.setView(dialogView)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText txtPlaylistName = (EditText) dialogView
                                        .findViewById(R.id.txtPlaylistName);
                                playlistName = txtPlaylistName.getText()
                                        .toString();
                                // showLoadingDialog();
                                // getSongToSearch();

                                Playlist playlist = new Playlist();
                                int temp = 0;
                                try {
                                    temp = Integer.parseInt(listPlaylists.get(
                                            listPlaylists.size() - 1).getId());
                                } catch (Exception e) {
                                }
                                playlist.setId((temp + 1) + "");
                                playlist.setName(playlistName);
                                // for (int integer : mSelectedItems) {
                                // playlist.addSong(listSongs.get(integer));
                                // }

                                if (getMainActivity().databaseUtility
                                        .insertPlaylist(playlist)) {
                                    listPlaylists.add(playlist);
                                    playlistAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(
                                            getMainActivity(),
                                            "Have error when add new playlist! Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void showListsongSelect() {
        final List<Integer> mSelectedItems = new ArrayList<Integer>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.chooseSong)
                .setMultiChoiceItems(arraySongName, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which, boolean isChecked) {
                                if (isChecked) {
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    mSelectedItems.remove(Integer
                                            .valueOf(which));
                                }
                            }
                        })
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (mSelectedItems.size() > 0) {
                                    Playlist playlist = new Playlist();
                                    int temp = 0;
                                    try {
                                        temp = Integer.parseInt(listPlaylists
                                                .get(listPlaylists.size() - 1)
                                                .getId());
                                    } catch (Exception e) {
                                    }
                                    playlist.setId((temp + 1) + "");
                                    playlist.setName(playlistName);
                                    for (int integer : mSelectedItems) {
                                        playlist.addSong(listSongs.get(integer));
                                    }
                                    listPlaylists.add(playlist);
                                    playlistAdapter.notifyDataSetChanged();
                                    getMainActivity().databaseUtility
                                            .insertPlaylist(playlist);
                                }
                            }
                        }).setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
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

            SmartLog.log(TAG, json);
            JSONObject entry = new JSONObject(json);

            if (entry.getString(WebserviceApi.KEY_STATUS).equalsIgnoreCase(
                    WebserviceApi.KEY_SUCCESS)) {

                List<Song> arrSong = CommonParser.parseSongFromServer(json);
                for (Song song : arrSong) {
                    addSongToListResult(song);
                }
                if (listSongs.size() > 0) {
                    arraySongName = new String[listSongs.size()];
                    for (int i = 0; i < arraySongName.length; i++) {
                        arraySongName[i] = listSongs.get(i).getName() + " - "
                                + listSongs.get(i).getArtist();
                    }
                    showListsongSelect();
                } else {
                    Toast.makeText(getActivity(), "No song found",
                            Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getActivity(), "No song found",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        hideLoadingDialog();
    }

    private void addSongToListResult(Song song) {
        for (Song song2 : listSongs) {
            if (song.compare(song2)) {
                return;
            }
        }
        listSongs.add(song);
    }
}
