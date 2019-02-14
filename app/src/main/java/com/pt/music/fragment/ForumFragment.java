package com.pt.music.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.pt.music.BaseFragment;
import com.pt.music.R;
import com.pt.music.activity.MainActivity;
import com.pt.music.config.Args;
import com.pt.music.config.GlobalValue;
import com.pt.music.config.WebserviceApi;
import com.pt.music.slidingmenu.SlidingMenu;
import com.pt.music.util.NetworkUtil;

/**
 * Created by pro on 1/14/16.
 */
public class ForumFragment extends BaseFragment {
    private ProgressDialog progressBar;
    public WebView mWvDashboard;
    private Button prevBtn, fwdBtn, homeBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        initUIBase(view);
        setButtonMenu(view);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getMainActivity().menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            getMainActivity().setVisibilityFooter();
        }
    }

    @Override
    protected void initUIBase(View view) {
        super.initUIBase(view);

        prevBtn = (Button) view.findViewById(R.id.prevBtn);
        fwdBtn = (Button) view.findViewById(R.id.fwdBtn);
        homeBtn = (Button) view.findViewById(R.id.homeBtn);

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

        homeBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String url = WebserviceApi.GET_FORUM + "?userid=" + GlobalValue.pref.getStringValue(Args.USER_ID);
                progressBar = ProgressDialog.show(getActivity(), "Please wait", "Loading...");
                mWvDashboard.loadUrl(url);
            }
        });

        mWvDashboard = (WebView) view.findViewById(R.id.webForumView);

        setHeaderTitle(R.string.forum);


            //mWvDashboard.loadUrl(url);

            WebSettings settings = mWvDashboard.getSettings();
            settings.setJavaScriptEnabled(true);

            mWvDashboard.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

            mWvDashboard.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Do what you want
                    return false;
                }
            });

//            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

            //progressBar = ProgressDialog.show(getActivity(), "Please wait", "Loading...");


            mWvDashboard.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //view.loadUrl(url);
                    return false;
                }

                public void onPageFinished(WebView view, String url) {
                    if (progressBar != null && progressBar.isShowing()) {
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
                    super.onPageFinished(view, url);

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
                            String furl = WebserviceApi.GET_FORUM + "?userid=" + GlobalValue.pref.getStringValue(Args.USER_ID);
                            loadURL(furl);
                            return;
                        }
                    });
                    alertDialog.show();
                }
            });
    }

    public void loadURL(String url){

        progressBar = ProgressDialog.show(getActivity(), "Please wait", "Loading...");
        mWvDashboard.loadUrl(url);
    }
}
