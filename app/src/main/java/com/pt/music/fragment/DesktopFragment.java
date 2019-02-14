package com.pt.music.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.pt.music.BaseFragment;
import com.pt.music.R;
import com.pt.music.activity.MainActivity;
import com.pt.music.activity.SplashActivity;
import com.pt.music.config.Args;
import com.pt.music.config.GlobalValue;
import com.pt.music.config.WebserviceApi;
import com.pt.music.slidingmenu.SlidingMenu;
import com.pt.music.util.MySharedPreferences;
import com.pt.music.util.NetworkUtil;

import java.net.URI;
import java.net.URL;

public class DesktopFragment extends BaseFragment {
    private WebView mWvDashboard;
    private ProgressDialog progressBar;
    private Button prevBtn, fwdBtn, homeBtn;
    private View headerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_desktop, container, false);

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
        mWvDashboard = (WebView) view.findViewById(R.id.webAboutView);
        headerView = (View) view.findViewById(R.id.desktop_header);
        headerView.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            mWvDashboard.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            mWvDashboard.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mWvDashboard.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        prevBtn = (Button) view.findViewById(R.id.prevDBtn);
        fwdBtn = (Button) view.findViewById(R.id.fwdDBtn);
        homeBtn = (Button) view.findViewById(R.id.homeDBtn);

        prevBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mWvDashboard.canGoBack())
                    mWvDashboard.goBack();
            }
        });

        fwdBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mWvDashboard.canGoForward())
                    mWvDashboard.goForward();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String url = WebserviceApi.GET_DESKTOP + "?userid=" + GlobalValue.pref.getStringValue(Args.USER_ID) + "&v=2";
                progressBar = ProgressDialog.show(getActivity(), "Please wait", "Loading...");
                mWvDashboard.loadUrl(url);
            }
        });

        progressBar = ProgressDialog.show(getActivity(), "Please wait", "Loading...");

        setHeaderTitle(R.string.desktop);

            if (GlobalValue.pref == null) {
                GlobalValue.pref = new MySharedPreferences(this.getActivity());
            }
            //String url = WebserviceApi.GET_DESKTOP + "?userid=" + GlobalValue.pref.getStringValue(Args.USER_ID) + "&v=2";
            //mWvDashboard.loadUrl(url);

            WebSettings settings = mWvDashboard.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setBuiltInZoomControls(true);
            mWvDashboard.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

//            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();


            mWvDashboard.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

                    if (url.startsWith("jliapp:")) {


                        String host = Uri.parse(url).getHost();
                        MainActivity ma = (MainActivity) getActivity();

                        if (host.equals("") || host.equals("desktop"))
                            ma.setSelect(MainActivity.DESKTOP, false);
                        if (host.equals("forum")) {
                            ma.setSelect(MainActivity.FORUM, false);
                            String param = GlobalValue.pref.getStringValue(Args.PARAM);
                            if (!param.equals("")) {
                                String urlstr = WebserviceApi.SERVER_DOMAIN + param.substring(5) + "&userid=" + GlobalValue.pref.getStringValue(Args.USER_ID);
                                ((ForumFragment) ma.arrayFragments[MainActivity.FORUM_FRAGMENT]).loadURL(urlstr);
                            }

                        }
                        if (host.equals("calendar"))
                            ma.setSelect(MainActivity.DASHBOARD, false);
                        if (host.equals("catalog"))
                            ma.setSelect(MainActivity.CATALOG, false);
                        if (host.equals("recordings"))
                            ma.setSelect(MainActivity.CATEGORY_MUSIC, false);
                        if (host.equals("chat"))
                            ma.setSelect(MainActivity.CHATJLI, false);
                        if (host.equals("intercom"))
                            ma.setSelect(MainActivity.INTERCOM, false);

                    } else{
//                        if (url.startsWith("https:")) {
//                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                            startActivity(browserIntent);
//                        } else {
//                            view.loadUrl(url);
//                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            mWvDashboard.evaluateJavascript("(function() { arrs = document.getElementsByTagName('a'); for(i = 0; i < arrs.length; i++){if(arrs[i].href == '" + url + "') return arrs[i].className; } return ''; })();", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    if(value.contains("external-link")){
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        startActivity(browserIntent);
                                    } else{
                                        view.loadUrl(url);
                                    }
                                }
                            });
                        }

                    }
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                    if (view.canGoBack()) {
                        prevBtn.setAlpha((float) 0.65);
                    } else {
                        prevBtn.setAlpha((float) 0.1);
                    }
                    if (view.canGoForward()) {
                        fwdBtn.setAlpha((float) 0.65);
                    } else {
                        fwdBtn.setAlpha((float) 0.1);
                    }

                    if(view.canGoBack())
                        headerView.setVisibility(View.VISIBLE);
                    else
                        headerView.setVisibility(View.GONE);
                    super.onPageFinished(view, url);
                }

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    //Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
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
            //mWvDashboard.loadUrl(url);
    }

    public void loadURL(){
        progressBar.show();
        String url = WebserviceApi.GET_DESKTOP + "?userid=" + GlobalValue.pref.getStringValue(Args.USER_ID) + "&v=2";
//        String url = "http://interbuild.co/test/test.php";
        mWvDashboard.loadUrl(url);
    }
}
