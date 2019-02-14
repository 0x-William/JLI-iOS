package com.pt.music.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.pt.music.R;
import com.pt.music.config.Args;
import com.pt.music.config.GlobalValue;
import com.pt.music.config.WebserviceApi;
import com.pt.music.slidingmenu.SlidingMenu;
import com.pt.music.util.NetworkUtil;
import com.pt.music.widget.AutoBgButton;

public class DashboardActivity extends Activity {

    private AutoBgButton mBtnBack;
    private WebView mWvDashboard;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initUI();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initUI() {
        mBtnBack = (AutoBgButton) findViewById(R.id.btn_back);
        mWvDashboard = (WebView) findViewById(R.id.wv_dashboard);



        // should call this methods at the end of declaring UI.
        initControl();
        initData();
    }

    private void initData() {
        if (NetworkUtil.checkNetworkAvailable(this)) {
            String url = WebserviceApi.GET_DASHBOARD + "?userid=" + GlobalValue.pref.getStringValue(Args.USER_ID);
            WebSettings settings = mWvDashboard.getSettings();
            settings.setJavaScriptEnabled(true);
            mWvDashboard.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

            //final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

            progressBar = ProgressDialog.show(DashboardActivity.this, "Please wait", "Loading...");

            mWvDashboard.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(DashboardActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
//                    alertDialog.setTitle("Error");
//                    alertDialog.setMessage(description);
//                    alertDialog.setButton(0, "OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            return;
//                        }
//                    });
//                    alertDialog.show();
                }
            });
            mWvDashboard.loadUrl(url);
        } else {
            Toast.makeText(this, "Network is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void initControl() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
