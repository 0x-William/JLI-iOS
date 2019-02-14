package com.pt.music.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.pt.music.BaseFragment;
import com.pt.music.R;
import com.pt.music.config.Args;
import com.pt.music.config.GlobalValue;
import com.pt.music.config.WebserviceApi;
import com.pt.music.slidingmenu.SlidingMenu;
import com.pt.music.util.MySharedPreferences;
import com.pt.music.util.NetworkUtil;

public class AboutFragment extends BaseFragment {
    private ProgressDialog progressBar;
    private WebView mWvDashboard;
    private View mview;
    private Button prevBtn, fwdBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        mview = view;
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

        prevBtn = (Button) view.findViewById(R.id.prevMBtn);
        fwdBtn = (Button) view.findViewById(R.id.fwdMBtn);

        prevBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mWvDashboard.canGoBack())
                    mWvDashboard.goBack();
            }
        });

        fwdBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mWvDashboard.canGoForward())
                    mWvDashboard.goForward();
            }
        });

        mWvDashboard = (WebView) view.findViewById(R.id.webAboutView);

        setHeaderTitle(R.string.dashboard);

        if (NetworkUtil.checkNetworkAvailable(getActivity())) {
            if (GlobalValue.pref == null) {
                GlobalValue.pref = new MySharedPreferences(this.getActivity());
            }



            WebSettings settings = mWvDashboard.getSettings();
            settings.setJavaScriptEnabled(true);


            mWvDashboard.setWebViewClient(new WebViewClient() {

                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    view.loadUrl(url);
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }

                    if(view.canGoBack()){
                        prevBtn.setAlpha((float) 0.65);
                    } else {
                        prevBtn.setAlpha((float) 0.1);
                    }
                    if(view.canGoForward()){
                        fwdBtn.setAlpha((float) 0.65);
                    } else {
                        fwdBtn.setAlpha((float) 0.1);
                    }
                }

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Internet Unavailable");
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Something went wrong \n" +
                            "The app can't access the Internet");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadURL();
                            return;
                        }
                    });
                    alertDialog.show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "Network is not available", Toast.LENGTH_SHORT).show();
        }

    }
    public void loadURL(){
        progressBar = ProgressDialog.show(getActivity(), "Please wait", "Loading...");
        String url = WebserviceApi.GET_DASHBOARD + "?userid=" + GlobalValue.pref.getStringValue(Args.USER_ID);
        mWvDashboard.loadUrl(url);
    }
}
