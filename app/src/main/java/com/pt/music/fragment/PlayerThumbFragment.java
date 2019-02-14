package com.pt.music.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.pt.music.BaseFragment;
import com.pt.music.R;
import com.pt.music.config.GlobalValue;

public class PlayerThumbFragment extends BaseFragment {

    private TextView lblNameSong, lblArtist;
    public static TextView lblNumberListen, lblNumberDownload;
    private View btnDownload, btnShare;
    private ImageView imgSong;
    private AQuery aq = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_thumb, container,
                false);
        aq = new AQuery(getActivity());
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        imgSong = (ImageView) view.findViewById(R.id.imgSong);
        lblNumberDownload = (TextView) view
                .findViewById(R.id.lblNumberDownload);
        lblNumberListen = (TextView) view.findViewById(R.id.lblNumberListen);
        lblNameSong = (TextView) view.findViewById(R.id.lblNameSong);
        lblArtist = (TextView) view.findViewById(R.id.lblArtist);
        btnShare = view.findViewById(R.id.btnShare);
        btnDownload = view.findViewById(R.id.btnDownload);
        lblNameSong.setSelected(true);
        lblArtist.setSelected(true);

    }

    public void refreshData() {
        if (lblNameSong != null && lblArtist != null) {
            lblNameSong.setText(GlobalValue.getCurrentSong().getName());
            lblArtist.setText(GlobalValue.getCurrentSong().getArtist());
            lblNumberDownload.setText(GlobalValue.getCurrentSong()
                    .getDownloadCount() + "");
            lblNumberListen.setText(GlobalValue.getCurrentSong()
                    .getListenCount() + "");
            aq.id(imgSong).image(GlobalValue.getCurrentSong().getImage(), true,
                    false, 0, R.drawable.ic_music_node);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshData();
                }
            }, 500);
        }
    }

}
