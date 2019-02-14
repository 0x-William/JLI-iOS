package com.pt.music;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.pt.music.activity.MainActivity;

public class BaseFragment extends Fragment {
    private TextView lblHeader;
    public Context self;
    public String TAG;

    public BaseFragment() {
        setArguments(new Bundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        self = getActivity();
        TAG = this.getClass().getSimpleName();
        // ((MainActivity)self).hideBannerAd();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden)
            ((MainActivity) self).hideBannerAd();
    }

    protected void initUIBase(View view) {

        lblHeader = (TextView) view.findViewById(R.id.lblHeader);
        view.findViewById(R.id.btnMenu).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
    }

    protected void setButtonBack(View view) {
        view.findViewById(R.id.btnMenu).setBackgroundResource(
                R.drawable.btn_back);
        view.findViewById(R.id.btnMenu).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
    }

    protected void setButtonMenu(View view) {
        view.findViewById(R.id.btnMenu).setBackgroundResource(
                R.drawable.btn_menu);
        view.findViewById(R.id.btnMenu).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getMainActivity().menu.showMenu();
                    }
                });
    }

    protected void setHeaderTitle(String header) {
        lblHeader.setText(header);
    }

    protected void setHeaderTitle(int header) {
        lblHeader.setText(header);
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    public void showDialogNoNetwork() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent settings = new Intent(
                                android.provider.Settings.ACTION_WIFI_SETTINGS);
                        settings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(settings);
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.app_name).setMessage(R.string.noNetwork)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    protected void showToast(int idString) {
        Toast.makeText(getActivity(), idString, Toast.LENGTH_SHORT).show();
    }
}
