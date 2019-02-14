package com.pt.music.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.pt.music.BaseFragment;
import com.pt.music.R;
import com.pt.music.activity.DownloadUpdateActivity;
import com.pt.music.activity.MainActivity;
import com.pt.music.config.Args;
import com.pt.music.config.GlobalValue;
import com.pt.music.object.Playlist;
import com.pt.music.object.Song;
import com.pt.music.slidingmenu.SlidingMenu;
import com.pt.music.util.ShareUtility;
import com.pt.music.widget.AutoBgButton;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

public class PlayerFragment extends BaseFragment implements OnClickListener {
    public static final int LIST_PLAYING = 0;
    public static final int THUMB_PLAYING = 1;

    public float rate = (float) 1.0;

    private AutoBgButton btnBackward, btnForward;
    public static AutoBgButton btnPlay;
    private ToggleButton btnShuffle, btnRepeat;
    private Button btnSpeedNormal, btnSpeedPlus, btnSpeedMinus, btnToggleSpeed;
    private TextView lblSpeed;
    private SeekBar speedSeeker;
    private ViewPager viewPager;
    private SeekBar seekBarLength;
    private TextView lblTimeCurrent, lblTimeLength;
    private View viewIndicatorList, viewIndicatorThumb;
    private RelativeLayout speedLayout;
    public static TextView lblTopHeader;
    private View btnAction;

    private String rootFolder, ringtoneFolder, alarmFolder, notifyFolder;

    public PlayerListPlayingFragment playerListPlayingFragment;
    public PlayerThumbFragment playerThumbFragment;
    private List<Playlist> listPlaylists;
    String[] arrayPlaylistName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_player, container, false);
        try {
            initUIBase(view);
            initControl(view);
        } catch (Exception e) {
            getMainActivity().cancelNotification();
        }
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            ((MainActivity) self).setVisibilityFooter();
            ((MainActivity) self).showBannerAd();
            initPlayList();
            getMainActivity().menu
                    .setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

            switch (getMainActivity().toMusicPlayer) {
                case MainActivity.FROM_LIST_SONG:
                case MainActivity.FROM_SEARCH:
                    try {
                        getMainActivity().mService
                                .setListSongs(GlobalValue.listSongPlay);
                        if (!getMainActivity().isTapOnFooter)// re-set song only when not from footer
                        {
                            setCurrentSong(GlobalValue.currentSongPlay);
                        }
                        playerListPlayingFragment.refreshListPlaying();
                        playerThumbFragment.refreshData();
                        setSelectTab(THUMB_PLAYING);
                        viewPager.setCurrentItem(THUMB_PLAYING);
                        getMainActivity().setButtonPlay();
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                    break;

                case MainActivity.FROM_NOTICATION:
                    try {
                        playerListPlayingFragment.refreshListPlaying();
                        playerThumbFragment.refreshData();
                    } catch (Exception e) {
                        getMainActivity().cancelNotification();
                    }
                    break;

                case MainActivity.FROM_OTHER:
                    break;
            }
        }
    }

    @Override
    protected void initUIBase(View view) {
        btnAction = view.findViewById(R.id.btnRightButton);
        btnAction.setBackgroundResource(R.drawable.ic_action);
        btnAction.setVisibility(View.VISIBLE);
        lblTopHeader = (TextView) view.findViewById(R.id.lblHeader);
        btnBackward = (AutoBgButton) view.findViewById(R.id.btnBackward);
        btnPlay = (AutoBgButton) view.findViewById(R.id.btnPlay);
        btnForward = (AutoBgButton) view.findViewById(R.id.btnForward);
        //btnShuffle = (ToggleButton) view.findViewById(R.id.btnShuffle);
        btnRepeat = (ToggleButton) view.findViewById(R.id.btnRepeat);
        seekBarLength = (SeekBar) view.findViewById(R.id.seekBarLength);
        seekBarLength.setMax(100);
        lblTimeCurrent = (TextView) view.findViewById(R.id.lblTimeCurrent);
        lblTimeLength = (TextView) view.findViewById(R.id.lblTimeLength);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewIndicatorList = view.findViewById(R.id.viewIndicatorList);
        viewIndicatorThumb = view.findViewById(R.id.viewIndicatorThumb);

        btnSpeedPlus =(Button) view.findViewById(R.id.speedPlus);
        btnSpeedNormal =(Button) view.findViewById(R.id.speedNormal);
        btnSpeedMinus =(Button) view.findViewById(R.id.speedMinus);
        lblSpeed = (TextView) view.findViewById(R.id.lblSpeed);
        speedSeeker = (SeekBar) view.findViewById(R.id.speedSeeker);
        speedSeeker.setMax(15);
        speedLayout = (RelativeLayout) view.findViewById(R.id.speedLayout);
        btnToggleSpeed = (Button) view.findViewById(R.id.btnToggleSpeed);
    }

    private void initControl(View view) {
        setButtonBack(view);
        // make root folder
        rootFolder = Environment.getExternalStorageDirectory() + "/"
                + getString(R.string.app_name) + "/";
        File folder = new File(rootFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // make folder ringtone
        ringtoneFolder = rootFolder + "ringtone/";
        File ringtonefolder = new File(ringtoneFolder);
        if (!ringtonefolder.exists()) {
            ringtonefolder.mkdirs();
        }

        alarmFolder = rootFolder + "alarm/";
        File alarmfolder = new File(alarmFolder);
        if (!alarmfolder.exists()) {
            alarmfolder.mkdirs();
        }

        notifyFolder = rootFolder + "notify/";
        File notifyfolder = new File(notifyFolder);
        if (!notifyfolder.exists()) {
            notifyfolder.mkdirs();
        }

        btnAction.setOnClickListener(this);
        //btnShuffle.setOnClickListener(this);
        btnBackward.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnToggleSpeed.setOnClickListener(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Build.MANUFACTURER.toLowerCase().contains("motorola")) {
            btnToggleSpeed.setVisibility(View.INVISIBLE);
        }

        seekBarLength.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (getMainActivity().mService.isPlay() == false)
                    return true;
                return false;
            }
        });
        seekBarLength.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getMainActivity().mService.seekTo(seekBar.getProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
            }
        });

        btnSpeedNormal.setOnClickListener(this);
        btnSpeedPlus.setOnClickListener(this);
        btnSpeedMinus.setOnClickListener(this);
        speedSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rate = (float)0.5 + (float)progress / 10;
                lblSpeed.setText(Float.toString(rate) + "x");
                btnToggleSpeed.setText(Float.toString(rate) + "x");
                getMainActivity().mService.setSpeed(rate);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playerListPlayingFragment = new PlayerListPlayingFragment();
        playerThumbFragment = new PlayerThumbFragment();

        viewPager.setAdapter(new MyFragmentPagerAdapter(getFragmentManager()));
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setSelectTab(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private void setSelectTab(int tab) {
        if (tab == LIST_PLAYING) {
            viewIndicatorList.setBackgroundResource(R.drawable.indicator_cyan);
            viewIndicatorThumb
                    .setBackgroundResource(R.drawable.indicator_white);
        } else {
            viewIndicatorList.setBackgroundResource(R.drawable.indicator_white);
            viewIndicatorThumb.setBackgroundResource(R.drawable.indicator_cyan);
        }
    }

    private void setCurrentSong(int position) {
        playerListPlayingFragment.refreshListPlaying();
        playerThumbFragment.refreshData();
        getMainActivity().mService.startMusic(position);
    }

    public void seekChanged(int maxprogress, String lengthTime,
                            String currentTime, int progress) {
        lblTimeLength.setText(lengthTime);
        lblTimeCurrent.setText(currentTime);
        seekBarLength.setMax(maxprogress);
        seekBarLength.setProgress(progress);

    }

    public void changeSong(int indexSong) {
        try {
            lblTimeCurrent.setText(getString(R.string.timeStart));
            lblTimeLength.setText(getMainActivity().mService.getLengSong());
            playerListPlayingFragment.refreshListPlaying();
            playerThumbFragment.refreshData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setButtonPlay() {
        if (getMainActivity().mService.isPause()) {
            btnPlay.setBackgroundResource(R.drawable.btn_play);
        } else {
            btnPlay.setBackgroundResource(R.drawable.btn_pause);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btnShuffle:
//                onClickShuffle();
//                break;

            case R.id.btnBackward:
                onClickBackward();
                break;

            case R.id.btnPlay:
                onClickPlay();
                break;

            case R.id.btnForward:
                onClickForward();
                break;

            case R.id.btnRepeat:
                onClickRepeat();
                break;

            case R.id.btnRightButton:
                showMenuAction(btnAction);
                break;

            case R.id.speedNormal:
                speedNormal();
                break;

            case R.id.speedMinus:
                speedMinus();
                break;

            case R.id.speedPlus:
                speedPlus();
                break;

            case R.id.btnToggleSpeed:
                toggleSpeedLayout();
                break;
        }
    }

    private void toggleSpeedLayout(){
        if(speedLayout.getVisibility() == View.VISIBLE){
            speedLayout.setVisibility(View.INVISIBLE);
            btnToggleSpeed.setTextColor(Color.WHITE);
        } else {
            speedLayout.setVisibility(View.VISIBLE);
            btnToggleSpeed.setTextColor(Color.WHITE);
        }
    }

    private void speedPlus(){
        if(rate < 2)
            speedSeeker.setProgress(speedSeeker.getProgress() + 1);
    }

    private void speedNormal(){
        speedSeeker.setProgress(5);
    }

    private void speedMinus(){
        if(rate > 0.5)
            speedSeeker.setProgress(speedSeeker.getProgress() - 1);
    }

    private void showMenuAction(View v) {
        PopupMenu popupMenu = new PopupMenu(getMainActivity(), v);
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO Auto-generated method stub
                if (item.getTitle().equals(
                        getMainActivity().getString(R.string.download))) {
                    onClickDownload();
                } else if (item.getTitle().equals(
                        getMainActivity().getString(R.string.Share))) {
                    onClickShare();
                } else if (item.getTitle().equals(
                        getMainActivity().getString(R.string.set_as_ringtone))) {
                    Toast.makeText(getActivity(),
                            "Set as ringtone is processing ... !", Toast.LENGTH_SHORT).show();
                    ShareUtility.setRingtone(getMainActivity(), GlobalValue
                            .getCurrentSong().getUrl(), ringtoneFolder);
                } else if (item.getTitle().equals(
                        getMainActivity().getString(R.string.set_as_alarm))) {
                    Toast.makeText(getActivity(),
                            "Set as Alarm is processing ... !", Toast.LENGTH_SHORT).show();
                    ShareUtility.setAlarm(getMainActivity(), GlobalValue
                            .getCurrentSong().getUrl(), alarmFolder);
                } else if (item.getTitle().equals(
                        getMainActivity().getString(
                                R.string.set_as_notification))) {
                    Toast.makeText(getActivity(),
                            "Set as Notification is processing ... !", Toast.LENGTH_SHORT)
                            .show();
                    ShareUtility.setNotification(getMainActivity(), GlobalValue
                            .getCurrentSong().getUrl(), notifyFolder);
                } else if (item.getTitle().equals(
                        getMainActivity().getString(
                                R.string.set_as_contact_ringtone))) {
                    getMainActivity().chooseContacts();
                } else if (item.getTitle().equals(
                        getMainActivity().getString(R.string.add_to_playlist))) {
                    showPlayList();
                }
                return false;
            }
        });
        popupMenu.inflate(R.menu.popup_audio_action);
        popupMenu.show();
    }

    private void onClickShuffle() {
        getMainActivity().mService.setShuffle(btnShuffle.isChecked());
    }

    private void onClickBackward() {
        getMainActivity().mService.backSongByOnClick();
    }

    private void onClickPlay() {
        if(getMainActivity().mService != null) {
            getMainActivity().mService.playOrPauseMusic();
            getMainActivity().setButtonPlay();
        }
    }

    private void onClickForward() {
        getMainActivity().mService.nextSongByOnClick();
    }

    private void onClickRepeat() {
        getMainActivity().mService.setRepeat(btnRepeat.isChecked());
        if (getMainActivity().mService.isRepeat()) {
            showToast(R.string.enableRepeat);
        } else {
            showToast(R.string.offRepeat);
        }
    }

    private void onClickDownload() {
        Song currentSong = GlobalValue.getCurrentSong();
        File file = new File(rootFolder, currentSong.getName() + " - "
                + currentSong.getArtist() + ".mp3");
        if (file.exists()) {
            Toast.makeText(getActivity(), R.string.songExisted,
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getActivity(),
                    DownloadUpdateActivity.class);
            intent.putExtra("url_song", currentSong.getUrl());
            intent.putExtra("file_name", currentSong.getName() + " - "
                    + currentSong.getArtist() + ".mp3");
            startActivity(intent);
        }
    }

    private void onClickShare() {
        Song currentSong = GlobalValue.getCurrentSong();
        String shareBody = currentSong.getUrl();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                currentSong.getName() + " - " + currentSong.getArtist());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources()
                .getString(R.string.share)));

    }

    private void initPlayList() {
        listPlaylists = getMainActivity().databaseUtility.getAllPlaylist();
    }

    private void showPlayList() {

        if (listPlaylists.size() > 0) {
            arrayPlaylistName = new String[listPlaylists.size()];
            for (int i = 0; i < arrayPlaylistName.length; i++) {
                arrayPlaylistName[i] = listPlaylists.get(i).getName();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.choosePlaylist).setSingleChoiceItems(
                    arrayPlaylistName, 0,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                            int selectedPosition = ((AlertDialog) dialog)
                                    .getListView().getCheckedItemPosition();
                            // Do something useful withe the position of the

                            Playlist playlist = listPlaylists
                                    .get(selectedPosition);

                            // check existed file music on this play list
                            boolean isExisited = false;
                            for (Song song : playlist.getListSongs()) {
                                if (song.getId().equals(
                                        GlobalValue.getCurrentSong().getId())) {
                                    isExisited = true;
                                    break;
                                }
                            }
                            if (isExisited) {
                                Toast.makeText(
                                        getMainActivity(),
                                        "This song is existed on "
                                                + playlist.getName(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                playlist.addSong(GlobalValue.getCurrentSong());
                                // update play list
                                if (getMainActivity().databaseUtility
                                        .updatePlaylist(playlist)) {
                                    Toast.makeText(
                                            getMainActivity(),
                                            "Add to " + playlist.getName()
                                                    + " successfully!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

            builder.create().show();
        } else {
//            Toast.makeText(getActivity(), "Please add a new playlist!",
//                    Toast.LENGTH_SHORT).show();
            confirmAddNewPlaylist();
        }
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return playerListPlayingFragment;
            }
            return playerThumbFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @SuppressLint("DefaultLocale")
    private String getTime(int millis) {
        long second = (millis / 1000) % 60;
        long minute = millis / (1000 * 60);
        return String.format("%02d:%02d", minute, second);
    }

    private void confirmAddNewPlaylist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add new playlist");
        builder.setMessage("Do you want to create a new playlist?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                // Open playlist page
                getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(Args.ACTION, MainActivity.PLAYLIST);
                getActivity().startActivity(intent);
            }
        });

        builder.create().show();
    }
}
