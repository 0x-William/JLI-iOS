package com.pt.music.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pt.music.BaseFragment;
import com.pt.music.R;
import com.pt.music.slidingmenu.SlidingMenu;
import com.pt.music.util.LanguageUtil;
import com.pt.music.util.MySharedPreferences;

public class SettingsFragment extends BaseFragment {
    private View layoutLanguage;
    private TextView lblLanguage, lblLanguageSelect, lblAbout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initUIBase(view);
        setButtonMenu(view);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getMainActivity().menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            getMainActivity().setVisibilityFooter();
        }
    }

    @Override
    protected void initUIBase(View view) {
        super.initUIBase(view);
        layoutLanguage = view.findViewById(R.id.layoutLanguage);
        lblLanguage = (TextView) view.findViewById(R.id.lblLanguage);
        lblLanguageSelect = (TextView) view.findViewById(R.id.lblLanguageSelect);
        lblAbout = (TextView) view.findViewById(R.id.lblAbout);

        lblLanguageSelect.setText(getResources().getStringArray(R.array.arrayLanguage)[new MySharedPreferences(
                getActivity()).getLanguage()]);

        layoutLanguage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.language).setItems(R.array.arrayLanguage,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                LanguageUtil.setLocale(which, getActivity());
                                lblLanguage.setText(R.string.language);
                                lblAbout.setText(R.string.about);
                                lblLanguageSelect.setText(getResources().getStringArray(R.array.arrayLanguage)[which]);
                                showToast(R.string.msgRestartApp);
                                new MySharedPreferences(getActivity()).putLanguage(which);
                            }
                        });
                builder.create().show();
            }
        });

        lblAbout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse("http://projectemplate.com/");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
    }
}
