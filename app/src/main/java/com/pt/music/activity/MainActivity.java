package com.pt.music.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pt.music.R;
import com.pt.music.config.Args;
import com.pt.music.config.GlobalValue;
import com.pt.music.config.WebserviceApi;
import com.pt.music.database.DatabaseUtility;
import com.pt.music.fragment.AboutFragment;
import com.pt.music.fragment.AlbumFragment;
import com.pt.music.fragment.CategoryMusicFragment;
import com.pt.music.fragment.DesktopFragment;
import com.pt.music.fragment.ForumFragment;
import com.pt.music.fragment.ListSongsFragment;
import com.pt.music.fragment.PlayerFragment;
import com.pt.music.fragment.SearchFragment;
import com.pt.music.gcm.QuickstartPreferences;
import com.pt.music.gcm.RegistrationIntentService;
import com.pt.music.object.Playlist;
import com.pt.music.object.Song;
import com.pt.music.service.MusicService;
import com.pt.music.service.MusicService.ServiceBinder;
import com.pt.music.service.PlayerListener;
import com.pt.music.slidingmenu.SlidingMenu;
import com.pt.music.util.Logger;
import com.pt.music.util.NetworkUtil;
import com.pt.music.util.ShareUtility;
import com.pt.music.widget.AutoBgButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import io.intercom.android.sdk.Intercom;

public class MainActivity extends FragmentActivity implements OnClickListener {
    public static final int TOP_CHART = 0;
    public static final int ALL_SONG = 1;
    public static final int ALBUM = 2;
    public static final int CATEGORY_MUSIC = 3;
    public static final int PLAYLIST = 4;
    public static final int SEARCH = 5;
    public static final int GOOD_APP = 6;
    public static final int ABOUT = 7;          //Marketing Calendar
    public static final int EXIT_APP = 8;
    public static final int DASHBOARD = 9;      //Marketing Calendar
    public static final int FORUM = 10;
    public static final int CHATJLI = 11;
    public static final int DESKTOP = 12;
    public static final int INTERCOM = 13;
    public static final int CATALOG = 14;

    public static final int LIST_SONG_FRAGMENT = 0;
    public static final int CATEGORY_MUSIC_FRAGMENT = 1;
    public static final int PLAYLIST_FRAGMENT = 2;
    public static final int SEARCH_FRAGMENT = 3;
    public static final int SETTING_FRAGMENT = 4;
    public static final int PLAYER_FRAGMENT = 5;
    public static final int ABOUT_FRAGMENT = 6;
    public static final int ALBUM_FRAGMENT = 7;
    public static final int FORUM_FRAGMENT = 8;
    public static final int DESKTOP_FRAGMENT = 9;

    public static final int FROM_LIST_SONG = 0;
    public static final int FROM_NOTICATION = 1;
    public static final int FROM_SEARCH = 2;
    public static final int FROM_OTHER = 3;

    public static final int NOTIFICATION_ID = 231109;
    public static final int PICK_CONTACT_REQUEST = 120;

    private FragmentManager fm;
    public Fragment[] arrayFragments;
    public SlidingMenu menu;
    public static AutoBgButton btnPlayFooter;
    private AutoBgButton btnPreviousFooter, btnNextFooter;
    // private ImageView imgSongFooter;
    private View layoutPlayerFooter;
    private TextView lblSongNameFooter, lblArtistFooter;

    private TextView lblTopChart, lblAllSongs, lblAlbum, lblCategoryMusic,
            lblPlaylist, lblSearch, lblGoodApp, lblAbout, lblChat, lblExitApp, lblDashboard, lblForum, lblDesktop;

    private LinearLayout llAdview;
//    private InterstitialAd interstitialAd;
//    private AdView adView;
//    AdRequest interstitialRequest;
//    AdRequest adRequest;

    private boolean doubleBackToExitPressedOnce;

    public int currentFragment;
    public int currentMusicType;
    public int toMusicPlayer;
    public boolean isTapOnFooter;
    public Playlist currentPlaylist;

    public String nextPageNomination;
    public String nextPageTopWeek;

    public List<Song> listNominations;
    public List<Song> listTopWeek;

    public DatabaseUtility databaseUtility;

    public MusicService mService;
    private Intent intentService;

    private boolean isReceiverRegistered;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Deque<Integer> fragment_stack;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceBinder binder = (ServiceBinder) service;
            mService = binder.getService();
            mService.setListSongs(GlobalValue.listSongPlay);
            mService.setListener(new PlayerListener() {
                @Override
                public void onSeekChanged(int maxProgress, String lengthTime,
                                          String currentTime, int progress) {
                    ((PlayerFragment) arrayFragments[PLAYER_FRAGMENT])
                            .seekChanged(maxProgress, lengthTime, currentTime,
                                    progress);
                }

                @Override
                public void onChangeSong(int indexSong) {
                    ((PlayerFragment) arrayFragments[PLAYER_FRAGMENT])
                            .changeSong(indexSong);
                    lblSongNameFooter.setText(GlobalValue.getCurrentSong()
                            .getName());
                    lblArtistFooter.setText(GlobalValue.getCurrentSong()
                            .getArtist());
                }

                @Override
                public void OnMusicPrepared() {
                    setVisibilityFooter();
                }
            });
            GlobalValue.currentMusicService = mService;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        fragment_stack = new ArrayDeque<Integer>();
        NetworkUtil.enableStrictMode();
        initList();
        databaseUtility = new DatabaseUtility(this);
        setContentView(R.layout.activity_main);

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        initService();
        initMenu();
        initUI();
        initControl();
        initFragment();

        isTapOnFooter = false;
        try {
            getIntent().getExtras().get("notification");
            toMusicPlayer = MainActivity.FROM_NOTICATION;
            isTapOnFooter = false;
            showFragment(PLAYER_FRAGMENT, false);

            ListSongsFragment.isShowing = false;
        } catch (Exception e) {

            String host = GlobalValue.pref.getStringValue(Args.HOST);
            GlobalValue.pref.putStringValue(Args.HOST, "");
            if(host.equals("") || host.equals("desktop"))
                setSelect(DESKTOP, false);
            if(host.equals("forum")) {
                setSelect(FORUM, false);
                String param = GlobalValue.pref.getStringValue(Args.PARAM);
                if(!param.equals("")){
                    String url = WebserviceApi.SERVER_DOMAIN + param.substring(5) + "&userid=" + GlobalValue.pref.getStringValue(Args.USER_ID);
                    ((ForumFragment) arrayFragments[FORUM_FRAGMENT]).loadURL(url);
                }

            }
            if(host.equals("calendar"))
                setSelect(DASHBOARD, false);
            if(host.equals("catalog"))
                setSelect(CATALOG, false);
            if(host.equals("recordings"))
                setSelect(CATEGORY_MUSIC, false);
            if(host.equals("chat"))
                setSelect(CHATJLI, false);
            if(host.equals("intercom"))
                setSelect(INTERCOM, false);
        }
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);

            }
        };

        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        //intercom old
        //Intercom.client().setVisibility(Intercom.VISIBLE);
        //Intercom.client().setLauncherVisibility(Intercom.Visibility.VISIBLE);
        // Exception handler
//		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    }

    private boolean checkPlayServices() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 0)
                        .show();
            } else {
                Log.i("Google Play", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.e("MainActivity", "Destroy");
        //mService.stopForeground(true);
        Intercom.client().reset();
        cancelNotification();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            e.printStackTrace();
            cancelNotification();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mService != null) {
            bindService(intentService, mConnection, Context.BIND_AUTO_CREATE);
            setVisibilityFooter();
            setButtonPlay();
        }

        String host = GlobalValue.pref.getStringValue(Args.HOST);
        GlobalValue.pref.putStringValue(Args.HOST, "");
        if(host.equals("desktop"))
            setSelect(DESKTOP, false);
        if(host.equals("forum")) {
            setSelect(FORUM, false);
            String param = GlobalValue.pref.getStringValue(Args.PARAM);
            Log.d("param",param);
            if(!param.equals("")){
                String url = WebserviceApi.SERVER_DOMAIN + param.substring(5) + "&userid=" + GlobalValue.pref.getStringValue(Args.USER_ID);
                Log.d("param",url);
                ((ForumFragment) arrayFragments[FORUM_FRAGMENT]).loadURL(url);
            }
        }
        if(host.equals("calendar"))
            setSelect(DASHBOARD, false);
        if(host.equals("catalog"))
            setSelect(CATALOG, false);
        if(host.equals("recordings"))
            setSelect(CATEGORY_MUSIC, false);
        if(host.equals("chat"))
            setSelect(CHATJLI, false);
        if(host.equals("intercom"))
            setSelect(INTERCOM, false);
        registerReceiver();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Static variable do not persist after long time of idle -> Store those
        try {
            databaseUtility.deleteAllFavorite();
            databaseUtility.insertFavorite(GlobalValue.listSongPlay);
            outState.putInt("currentSongPlay", GlobalValue.currentSongPlay);
            outState.putInt("currentMenu", GlobalValue.currentMenu);
            outState.putInt("currentCategoryId", GlobalValue.currentCategoryId);
            outState.putInt("currentParentCategoryId", GlobalValue.currentParentCategoryId);
            outState.putString("currentCategoryName",
                    GlobalValue.currentCategoryName);
            outState.putInt("currentAlbumId", GlobalValue.currentAlbumId);
            outState.putString("currentAlbumName", GlobalValue.currentAlbumName);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            try {

                GlobalValue.listSongPlay = databaseUtility.getAllFavorite();
                GlobalValue.currentSongPlay = (int) savedInstanceState
                        .getInt("currentSongPlay");
                GlobalValue.currentMenu = (int) savedInstanceState
                        .getInt("currentMenu");
                GlobalValue.currentCategoryId = (int) savedInstanceState
                        .getInt("currentCategoryId");
                GlobalValue.currentParentCategoryId = (int) savedInstanceState
                        .getInt("currentParentCategoryId");
                GlobalValue.currentCategoryName = (String) savedInstanceState
                        .getString("currentCategoryName");
                GlobalValue.currentAlbumId = (int) savedInstanceState
                        .getInt("currentAlbumId");
                GlobalValue.currentAlbumName = (String) savedInstanceState
                        .getString("currentAlbumName");
                cancelNotification();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        processAction(intent);
    }

    private void processAction(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Args.ACTION)) {
                if (bundle.getInt(Args.ACTION) == PLAYLIST) {
                    onClickPlaylist();
                }
            }
        }
    }

    public void setVisibilityFooter() {
        try {

            if (mService.isPause() || mService.isPlay()) {
                if (currentFragment == PLAYER_FRAGMENT) {
                    layoutPlayerFooter.setVisibility(View.GONE);
                } else {
                    layoutPlayerFooter.setVisibility(View.VISIBLE);
                }
            } else {
                layoutPlayerFooter.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            layoutPlayerFooter.setVisibility(View.GONE);
        }
    }

    private void initService() {
        intentService = new Intent(this, MusicService.class);
        startService(intentService);
        bindService(intentService, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void initUI() {
        btnPreviousFooter = (AutoBgButton) findViewById(R.id.btnPreviousFooter);
        btnPlayFooter = (AutoBgButton) findViewById(R.id.btnPlayFooter);
        btnNextFooter = (AutoBgButton) findViewById(R.id.btnNextFooter);
        //imgSongFooter = (ImageView) findViewById(R.id.imgSongFooter);
        layoutPlayerFooter = (LinearLayout) findViewById(R.id.layoutPlayerFooter);
        lblSongNameFooter = (TextView) findViewById(R.id.lblSongNameFooter);
        lblArtistFooter = (TextView) findViewById(R.id.lblArtistFooter);
        lblTopChart = (TextView) menu.findViewById(R.id.lblTopChart);
        lblAllSongs = (TextView) menu.findViewById(R.id.lblAllSong);
        lblAlbum = (TextView) menu.findViewById(R.id.lblNominations);
        lblForum = (TextView) menu.findViewById(R.id.lblForum);
        lblCategoryMusic = (TextView) menu.findViewById(R.id.lblCategoryMusic);
        lblPlaylist = (TextView) menu.findViewById(R.id.lblPlaylist);
        lblSearch = (TextView) menu.findViewById(R.id.lblSearch);
        lblGoodApp = (TextView) menu.findViewById(R.id.lblGoodApp);
        lblAbout = (TextView) menu.findViewById(R.id.lblAbout);
        lblChat = (TextView) menu.findViewById(R.id.lblChatJLI);
        lblExitApp = (TextView) menu.findViewById(R.id.lblExitApp);
        lblDashboard = (TextView) menu.findViewById(R.id.lblDashboard);
        lblDesktop = (TextView) menu.findViewById(R.id.lblDesktop);

        initBannerAd();
//        initInterstitialAd();
    }

    private void initControl() {
        btnPreviousFooter.setOnClickListener(this);
        btnPlayFooter.setOnClickListener(this);
        btnNextFooter.setOnClickListener(this);
        layoutPlayerFooter.setOnClickListener(this);
        lblTopChart.setOnClickListener(this);
        lblAllSongs.setOnClickListener(this);
        lblAlbum.setOnClickListener(this);
        lblCategoryMusic.setOnClickListener(this);
        lblPlaylist.setOnClickListener(this);
        lblSearch.setOnClickListener(this);
        lblGoodApp.setOnClickListener(this);
        lblAbout.setOnClickListener(this);
        lblChat.setOnClickListener(this);
        lblExitApp.setOnClickListener(this);
        lblSongNameFooter.setSelected(true);
        lblArtistFooter.setSelected(true);
        lblDashboard.setOnClickListener(this);
        lblForum.setOnClickListener(this);
        lblDesktop.setOnClickListener(this);

    }

    private void initBannerAd() {
        llAdview = (LinearLayout) findViewById(R.id.adView);

        // Load admob
//        adView = new AdView(this);
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId(getString(R.string.key_admob));
//        llAdview.addView(adView);
//
//        adRequest = new AdRequest.Builder().addTestDevice("EE03E01D57AA71B90A9D319DC56944F1").build();
//
//        adView.loadAd(adRequest);

        // Load ads from server
        new GetAdsFromServer().execute();
    }

    class GetAdsFromServer extends AsyncTask<Void, Void, String> {

        String urlAds = WebserviceApi.GET_ADS + "?userid=" + GlobalValue.pref.getStringValue(Args.USER_ID);

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            try {
                URL url = new URL(urlAds);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    result += inputLine;
                }

                in.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!s.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equalsIgnoreCase("SUCCESS")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (data != null) {
                            String urlAds = data.getString("thumb");

                            if (!urlAds.equals("")) {
                                AQuery aq = new AQuery(MainActivity.this);
                                aq.ajax(urlAds, Bitmap.class, new AjaxCallback<Bitmap>() {
                                    @Override
                                    public void callback(String url, Bitmap object, AjaxStatus status) {
                                        super.callback(url, object, status);

                                        Drawable bitmapDrawable = new BitmapDrawable(getResources(), object);
                                        llAdview.setBackgroundDrawable(bitmapDrawable);
                                    }
                                });
                            }

                            // Open home page when click on ads
                            final String urlHome = data.getString("url");
                            if (!urlHome.equals("")) {
                                llAdview.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlHome));
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initInterstitialAd() {
//        interstitialAd = new InterstitialAd(this);
//        interstitialAd.setAdUnitId(getString(R.string.key_admob_interstitial));
//        interstitialRequest = new AdRequest.Builder().addTestDevice("EE03E01D57AA71B90A9D319DC56944F1")
//                .build();
//        interstitialAd.loadAd(interstitialRequest);
//
//        interstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//
//                displayInterstitialAd();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//            }
//        });

    }

    public void displayInterstitialAd() {
//        if (interstitialAd.isLoaded()) {
//            interstitialAd.show();
//        } else {
//            interstitialAd.loadAd(interstitialRequest);
//            Log.d("INTERSTITIAL_AD",
//                    "Interstitial ad was not ready to be shown.");
//        }
    }

    private void initFragment() {
        fm = getSupportFragmentManager();
        arrayFragments = new Fragment[10];
        arrayFragments[LIST_SONG_FRAGMENT] = fm
                .findFragmentById(R.id.fragmentListSongs);
        arrayFragments[CATEGORY_MUSIC_FRAGMENT] = fm
                .findFragmentById(R.id.fragmentCategoryMusic);
        arrayFragments[PLAYLIST_FRAGMENT] = fm
                .findFragmentById(R.id.fragmentPlaylist);
        arrayFragments[SEARCH_FRAGMENT] = fm
                .findFragmentById(R.id.fragmentSearch);
        arrayFragments[SETTING_FRAGMENT] = fm
                .findFragmentById(R.id.fragmentSetting);
        arrayFragments[PLAYER_FRAGMENT] = fm
                .findFragmentById(R.id.fragmentPlayer);
        arrayFragments[ABOUT_FRAGMENT] = fm
                .findFragmentById(R.id.fragmentAbout);
        arrayFragments[ALBUM_FRAGMENT] = fm
                .findFragmentById(R.id.fragmentAlbum);
        arrayFragments[FORUM_FRAGMENT] = fm
            .findFragmentById(R.id.fragmentForum);
        arrayFragments[DESKTOP_FRAGMENT] = fm
                .findFragmentById(R.id.fragmentDesktop);

        FragmentTransaction transaction = fm.beginTransaction();
        for (Fragment fragment : arrayFragments) {
            transaction.hide(fragment);
        }
        transaction.commit();
    }

    private void showFragment(int fragmentIndex, boolean goback) {
        if(!goback)
            fragment_stack.push(currentFragment);

        currentFragment = fragmentIndex;

        FragmentTransaction transaction = fm.beginTransaction();
        for (Fragment fragment : arrayFragments) {
            transaction.hide(fragment);
        }
        transaction.show(arrayFragments[fragmentIndex]);
        transaction.commit();
        Logger.e(fragmentIndex);
    }

    private void initList() {
        listNominations = new ArrayList<Song>();
        listTopWeek = new ArrayList<Song>();
    }

    private void initMenu() {
        menu = new SlidingMenu(this);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.layout_menu);

    }

    public void hideBannerAd() {
        llAdview.setVisibility(View.GONE);
    }

    public void showBannerAd() {
        llAdview.setVisibility(View.VISIBLE);
    }

    public void gotoFragment(int fragment) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left,
                R.anim.slide_out_left);
        transaction.show(arrayFragments[fragment]);

        transaction.hide(arrayFragments[currentFragment]);
        transaction.commit();
        currentFragment = fragment;
    }

    public void gotoFragment(int fragment, Bundle args) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left,
                R.anim.slide_out_left);
        transaction.show(arrayFragments[fragment]);
        arrayFragments[fragment].getArguments().putAll(args);
        transaction.hide(arrayFragments[currentFragment]);
        transaction.commit();
        currentFragment = fragment;
    }

    public void backFragment(int fragment) {
        FragmentTransaction transaction = fm.beginTransaction();

        transaction.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_right);
        transaction.show(arrayFragments[fragment]);
        transaction.hide(arrayFragments[currentFragment]);
        transaction.commit();
        currentFragment = fragment;
    }

    public void setSelect(int select, boolean goback) {
        ListSongsFragment.isShowing = false;
        AlbumFragment.isShowing = false;

        GlobalValue.currentMenu = select;
        switch (select) {
            case TOP_CHART:
                lblDesktop.setBackgroundColor(Color.TRANSPARENT);
                lblTopChart.setBackgroundResource(R.drawable.bg_item_menu_select);
                lblAllSongs.setBackgroundColor(Color.TRANSPARENT);
                lblAlbum.setBackgroundColor(Color.TRANSPARENT);
                lblCategoryMusic.setBackgroundColor(Color.TRANSPARENT);
                lblPlaylist.setBackgroundColor(Color.TRANSPARENT);
                lblSearch.setBackgroundColor(Color.TRANSPARENT);
                lblGoodApp.setBackgroundColor(Color.TRANSPARENT);
                lblAbout.setBackgroundColor(Color.TRANSPARENT);
                lblExitApp.setBackgroundColor(Color.TRANSPARENT);
                lblDashboard.setBackgroundColor(Color.TRANSPARENT);
                lblForum.setBackgroundColor(Color.TRANSPARENT);
                showFragment(LIST_SONG_FRAGMENT, goback);
                break;

            case ALL_SONG:
                lblDesktop.setBackgroundColor(Color.TRANSPARENT);
                lblTopChart.setBackgroundResource(Color.TRANSPARENT);
                lblAllSongs.setBackgroundColor(R.drawable.bg_item_menu_select);
                lblAlbum.setBackgroundColor(Color.TRANSPARENT);
                lblCategoryMusic.setBackgroundColor(Color.TRANSPARENT);
                lblPlaylist.setBackgroundColor(Color.TRANSPARENT);
                lblSearch.setBackgroundColor(Color.TRANSPARENT);
                lblGoodApp.setBackgroundColor(Color.TRANSPARENT);
                lblAbout.setBackgroundColor(Color.TRANSPARENT);
                lblExitApp.setBackgroundColor(Color.TRANSPARENT);
                lblDashboard.setBackgroundColor(Color.TRANSPARENT);
                lblForum.setBackgroundColor(Color.TRANSPARENT);
                showFragment(LIST_SONG_FRAGMENT, goback);
                break;

            case ALBUM:
                lblDesktop.setBackgroundColor(Color.TRANSPARENT);
                lblTopChart.setBackgroundColor(Color.TRANSPARENT);
                lblAllSongs.setBackgroundColor(Color.TRANSPARENT);
                lblAlbum.setBackgroundResource(R.drawable.bg_item_menu_select);
                lblCategoryMusic.setBackgroundColor(Color.TRANSPARENT);
                lblPlaylist.setBackgroundColor(Color.TRANSPARENT);
                lblSearch.setBackgroundColor(Color.TRANSPARENT);
                lblGoodApp.setBackgroundColor(Color.TRANSPARENT);
                lblAbout.setBackgroundColor(Color.TRANSPARENT);
                lblExitApp.setBackgroundColor(Color.TRANSPARENT);
                lblDashboard.setBackgroundColor(Color.TRANSPARENT);
                lblForum.setBackgroundColor(Color.TRANSPARENT);
                showFragment(ALBUM_FRAGMENT, goback);
                break;

            case DESKTOP:
                lblDesktop.setBackgroundResource(R.drawable.bg_item_menu_select);
                lblTopChart.setBackgroundColor(Color.TRANSPARENT);
                lblAllSongs.setBackgroundColor(Color.TRANSPARENT);
                lblAlbum.setBackgroundColor(Color.TRANSPARENT);
                lblCategoryMusic.setBackgroundColor(Color.TRANSPARENT);
                lblPlaylist.setBackgroundColor(Color.TRANSPARENT);
                lblSearch.setBackgroundColor(Color.TRANSPARENT);
                lblGoodApp.setBackgroundColor(Color.TRANSPARENT);
                lblAbout.setBackgroundColor(Color.TRANSPARENT);
                lblExitApp.setBackgroundColor(Color.TRANSPARENT);
                lblDashboard.setBackgroundColor(Color.TRANSPARENT);
                lblForum.setBackgroundColor(Color.TRANSPARENT);
                showFragment(DESKTOP_FRAGMENT, goback);
                if(!goback)
                    ((DesktopFragment) arrayFragments[DESKTOP_FRAGMENT]).loadURL();
                break;

            case CATEGORY_MUSIC:
                lblDesktop.setBackgroundColor(Color.TRANSPARENT);
                lblTopChart.setBackgroundColor(Color.TRANSPARENT);
                lblAllSongs.setBackgroundColor(Color.TRANSPARENT);
                lblAlbum.setBackgroundColor(Color.TRANSPARENT);
                lblCategoryMusic.setBackgroundResource(R.drawable.bg_item_menu_select);
                lblPlaylist.setBackgroundColor(Color.TRANSPARENT);
                lblSearch.setBackgroundColor(Color.TRANSPARENT);
                lblGoodApp.setBackgroundColor(Color.TRANSPARENT);
                lblAbout.setBackgroundColor(Color.TRANSPARENT);
                lblExitApp.setBackgroundColor(Color.TRANSPARENT);
                lblDashboard.setBackgroundColor(Color.TRANSPARENT);
                lblForum.setBackgroundColor(Color.TRANSPARENT);
                showFragment(CATEGORY_MUSIC_FRAGMENT, goback);
                if(!goback)
                    ((CategoryMusicFragment) arrayFragments[CATEGORY_MUSIC_FRAGMENT]).setCurentParentCategory(0, "");

                break;

            case PLAYLIST:
                lblDesktop.setBackgroundColor(Color.TRANSPARENT);
                lblTopChart.setBackgroundColor(Color.TRANSPARENT);
                lblAllSongs.setBackgroundColor(Color.TRANSPARENT);
                lblAlbum.setBackgroundColor(Color.TRANSPARENT);
                lblCategoryMusic.setBackgroundColor(Color.TRANSPARENT);
                lblPlaylist.setBackgroundResource(R.drawable.bg_item_menu_select);
                lblSearch.setBackgroundColor(Color.TRANSPARENT);
                lblGoodApp.setBackgroundColor(Color.TRANSPARENT);
                lblAbout.setBackgroundColor(Color.TRANSPARENT);
                lblExitApp.setBackgroundColor(Color.TRANSPARENT);
                lblDashboard.setBackgroundColor(Color.TRANSPARENT);
                lblForum.setBackgroundColor(Color.TRANSPARENT);
                showFragment(PLAYLIST_FRAGMENT, goback);
                break;

            case SEARCH:
                lblDesktop.setBackgroundColor(Color.TRANSPARENT);
                lblTopChart.setBackgroundColor(Color.TRANSPARENT);
                lblAllSongs.setBackgroundColor(Color.TRANSPARENT);
                lblAlbum.setBackgroundColor(Color.TRANSPARENT);
                lblCategoryMusic.setBackgroundColor(Color.TRANSPARENT);
                lblPlaylist.setBackgroundColor(Color.TRANSPARENT);
                lblSearch.setBackgroundResource(R.drawable.bg_item_menu_select);
                lblGoodApp.setBackgroundColor(Color.TRANSPARENT);
                lblAbout.setBackgroundColor(Color.TRANSPARENT);
                lblExitApp.setBackgroundColor(Color.TRANSPARENT);
                lblDashboard.setBackgroundColor(Color.TRANSPARENT);
                lblForum.setBackgroundColor(Color.TRANSPARENT);
                ((SearchFragment) arrayFragments[SEARCH_FRAGMENT]).keyword = "";
                showFragment(SEARCH_FRAGMENT, goback);
                break;

            case CATALOG:
                String url = "http://myjli.com/catalog?source=app&userid="+GlobalValue.pref.getStringValue(Args.USER_ID);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
//                lblDesktop.setBackgroundColor(Color.TRANSPARENT);
//                lblTopChart.setBackgroundColor(Color.TRANSPARENT);
//                lblAllSongs.setBackgroundColor(Color.TRANSPARENT);
//                lblAlbum.setBackgroundColor(Color.TRANSPARENT);
//                lblCategoryMusic.setBackgroundColor(Color.TRANSPARENT);
//                lblPlaylist.setBackgroundColor(Color.TRANSPARENT);
//                lblSearch.setBackgroundColor(Color.TRANSPARENT);
//                lblGoodApp.setBackgroundColor(Color.TRANSPARENT);
//                lblAbout.setBackgroundResource(R.drawable.bg_item_menu_select);
//                lblExitApp.setBackgroundColor(Color.TRANSPARENT);
//                lblDashboard.setBackgroundColor(Color.TRANSPARENT);
//                lblForum.setBackgroundColor(Color.TRANSPARENT);
//                showFragment(ABOUT_FRAGMENT);
                break;

            case FORUM:
                lblDesktop.setBackgroundColor(Color.TRANSPARENT);
                lblTopChart.setBackgroundColor(Color.TRANSPARENT);
                lblForum.setBackgroundResource(R.drawable.bg_item_menu_select);
                lblAllSongs.setBackgroundColor(Color.TRANSPARENT);
                lblAlbum.setBackgroundColor(Color.TRANSPARENT);
                lblCategoryMusic.setBackgroundColor(Color.TRANSPARENT);
                lblPlaylist.setBackgroundColor(Color.TRANSPARENT);
                lblSearch.setBackgroundColor(Color.TRANSPARENT);
                lblGoodApp.setBackgroundColor(Color.TRANSPARENT);
                lblAbout.setBackgroundColor(Color.TRANSPARENT);
                lblExitApp.setBackgroundColor(Color.TRANSPARENT);
                lblDashboard.setBackgroundColor(Color.TRANSPARENT);
                showFragment(FORUM_FRAGMENT, goback);
                String furl = WebserviceApi.GET_FORUM + "?userid=" + GlobalValue.pref.getStringValue(Args.USER_ID);
                if(!goback)
                    ((ForumFragment) arrayFragments[FORUM_FRAGMENT]).loadURL(furl);
                break;

            case CHATJLI:
                Intercom.client().displayConversationsList();

                break;

            case INTERCOM:
                Intercom.client().displayMessageComposer();
                break;

            case DASHBOARD:
                lblDesktop.setBackgroundColor(Color.TRANSPARENT);
                lblTopChart.setBackgroundColor(Color.TRANSPARENT);
                lblAllSongs.setBackgroundColor(Color.TRANSPARENT);
                lblAlbum.setBackgroundColor(Color.TRANSPARENT);
                lblCategoryMusic.setBackgroundColor(Color.TRANSPARENT);
                lblPlaylist.setBackgroundColor(Color.TRANSPARENT);
                lblSearch.setBackgroundColor(Color.TRANSPARENT);
                lblGoodApp.setBackgroundColor(Color.TRANSPARENT);
                lblAbout.setBackgroundColor(Color.TRANSPARENT);
                lblExitApp.setBackgroundColor(Color.TRANSPARENT);
                lblDashboard.setBackgroundResource(R.drawable.bg_item_menu_select);
                lblForum.setBackgroundColor(Color.TRANSPARENT);
                showFragment(ABOUT_FRAGMENT, goback);
                if(!goback)
                    ((AboutFragment) arrayFragments[ABOUT_FRAGMENT]).loadURL();
                break;
            case GOOD_APP:
                break;
            case EXIT_APP:
                return;
        }
        menu.showContent();
    }

    private void openDashboardPage() {
        lblTopChart.setBackgroundColor(Color.TRANSPARENT);
        lblAllSongs.setBackgroundColor(Color.TRANSPARENT);
        lblAlbum.setBackgroundColor(Color.TRANSPARENT);
        lblCategoryMusic.setBackgroundColor(Color.TRANSPARENT);
        lblPlaylist.setBackgroundColor(Color.TRANSPARENT);
        lblSearch.setBackgroundColor(Color.TRANSPARENT);
        lblGoodApp.setBackgroundColor(Color.TRANSPARENT);
        lblAbout.setBackgroundColor(Color.TRANSPARENT);
        lblExitApp.setBackgroundColor(Color.TRANSPARENT);
        lblDashboard.setBackgroundResource(R.drawable.bg_item_menu_select);
        lblForum.setBackgroundColor(Color.TRANSPARENT);
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

    public void setButtonPlay() {
        if (mService.isPause()) {
            btnPlayFooter.setBackgroundResource(R.drawable.bg_btn_play_small);
        } else {
            btnPlayFooter.setBackgroundResource(R.drawable.bg_btn_pause_small);
        }
        ((PlayerFragment) arrayFragments[PLAYER_FRAGMENT]).setButtonPlay();
    }

    public void cancelNotification() {
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPreviousFooter:
                onClickPreviousFooter();
                break;

            case R.id.btnPlayFooter:
                onClickPlayFooter();
                break;

            case R.id.btnNextFooter:
                onClickNextFooter();
                break;

            case R.id.layoutPlayerFooter:
                onClickPlayerFooter();
                break;

            case R.id.lblTopChart:
                onClickTopChart();
                break;
            case R.id.lblAllSong:
                onClickAllSongs();
                break;

            case R.id.lblNominations:
                onClickAlbum();
                break;

            case R.id.lblForum:
                onClickForum();
                break;

            case R.id.lblCategoryMusic:
                onClickCategoryMusic();
                break;

            case R.id.lblPlaylist:
                onClickPlaylist();
                break;

            case R.id.lblSearch:
                onClickSearch();
                break;

            case R.id.lblGoodApp:
                onClickGoodApp();
                break;

            case R.id.lblAbout:
                onClickCatalog();
                break;

            case R.id.lblExitApp:
                displayInterstitialAd();
                onClickExitApp();
                break;

            case R.id.lblChatJLI:
                onClickChatJLI();
                break;

            case R.id.lblDashboard:
                onClickDashboard();
                break;

            case R.id.lblDesktop:
                onClickDesktop();
                break;
        }
    }

    private void onClickPreviousFooter() {
        mService.backSongByOnClick();
    }

    private void onClickDesktop() {
        setSelect(DESKTOP, false);

    }

    private void onClickPlayFooter() {
        mService.playOrPauseMusic();
        setButtonPlay();
    }

    private void onClickNextFooter() {
        mService.nextSongByOnClick();
    }

    private void onClickPlayerFooter() {
        // toMusicPlayer = FROM_OTHER;
        toMusicPlayer = currentFragment;
        isTapOnFooter = true;
        gotoFragment(PLAYER_FRAGMENT);
    }

    private void onClickTopChart() {
        setSelect(TOP_CHART, false);
    }

    private void onClickAllSongs() {
        setSelect(ALL_SONG, false);
    }

    private void onClickAlbum() {
        setSelect(ALBUM, false);
    }

    private void onClickCategoryMusic() {
        setSelect(CATEGORY_MUSIC, false);
    }

    private void onClickPlaylist() {
        setSelect(PLAYLIST, false);
    }

    private void onClickSearch() {
        setSelect(SEARCH, false);
    }

    private void onClickGoodApp() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.key_goodapp_link)));
        startActivity(browserIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private void onClickForum() {
        setSelect(FORUM, false);
    }

    private void onClickChatJLI() {
        setSelect(CHATJLI, false);

    }

    private void onClickDashboard() {
        setSelect(DASHBOARD, false);
    }

    private void onClickAbout() {
        setSelect(ABOUT, false);
    }

    private void onClickCatalog() {
        setSelect(CATALOG, false);
    }

    private void onClickExitApp() {
        showQuitDialog();
    }

    @Override
    public void onBackPressed() {
        if (menu.isMenuShowing()) {
            menu.showContent();
        } else {

            switch (currentFragment) {
                case FORUM_FRAGMENT:
                    ForumFragment fragment1 = (ForumFragment) arrayFragments[FORUM_FRAGMENT];
                    if(fragment1.mWvDashboard.canGoBack())
                        fragment1.mWvDashboard.goBack();
                    else{
                        if(fragment_stack.size() > 1) {
                            Integer prev = fragment_stack.pop();
                            switch(prev){
                                case DESKTOP_FRAGMENT:
                                    setSelect(DESKTOP, true);
                                    break;
                                case FORUM_FRAGMENT:
                                    setSelect(FORUM, true);
                                    break;
                                case ABOUT_FRAGMENT:
                                    setSelect(DASHBOARD, true);
                                    break;
                                case CATEGORY_MUSIC_FRAGMENT:
                                    setSelect(CATEGORY_MUSIC, true);
                                    break;
                            }
                            //backFragment(prev);
                        }

                    }
                    break;
                case CATEGORY_MUSIC_FRAGMENT:
                    CategoryMusicFragment fragment = (CategoryMusicFragment) arrayFragments[CATEGORY_MUSIC_FRAGMENT];
                    if (GlobalValue.currentParentCategoryId != 0) {
                        fragment.setCurentParentCategory(0, "");
                    } else{
                        if(fragment_stack.size() > 1) {
                            Integer prev = fragment_stack.pop();
                            switch(prev){
                                case DESKTOP_FRAGMENT:
                                    setSelect(DESKTOP, true);
                                    break;
                                case FORUM_FRAGMENT:
                                    setSelect(FORUM, true);
                                    break;
                                case ABOUT_FRAGMENT:
                                    setSelect(DASHBOARD, true);
                                    break;
                                case CATEGORY_MUSIC_FRAGMENT:
                                    setSelect(CATEGORY_MUSIC, true);
                                    break;
                            }
                        }
                    }
                    break;
                case PLAYER_FRAGMENT:
                /*
                 * if (toMusicPlayer == FROM_SEARCH) {
				 * backFragment(SEARCH_FRAGMENT); } else {
				 * backFragment(toMusicPlayer); }
				 */
                    backFragment(toMusicPlayer);
                    break;

                case LIST_SONG_FRAGMENT:
                    if (GlobalValue.currentMenu == CATEGORY_MUSIC) {
                        backFragment(CATEGORY_MUSIC_FRAGMENT);
                    } else if (GlobalValue.currentMenu == PLAYLIST) {
                        backFragment(PLAYLIST_FRAGMENT);
                    } else if (GlobalValue.currentMenu == ALBUM) {
                        backFragment(ALBUM_FRAGMENT);
                    } else {
                        super.onBackPressed();
                        quitApp();
                    }
                    break;

                default:
//                    super.onBackPressed();
//                    quitApp();
                    if(fragment_stack.size() > 1) {
                        Integer prev = fragment_stack.pop();
                        switch(prev){
                            case DESKTOP_FRAGMENT:
                                setSelect(DESKTOP, true);
                                break;
                            case FORUM_FRAGMENT:
                                setSelect(FORUM, true);
                                break;
                            case ABOUT_FRAGMENT:
                                setSelect(DASHBOARD, true);
                                break;
                            case CATEGORY_MUSIC_FRAGMENT:
                                setSelect(CATEGORY_MUSIC, true);
                                break;
                        }
                        //backFragment(prev);
                    }
                    break;
            }
        }
    }

    private void quitApp() {

//		displayInterstitialAd();
        //showQuitDialog();
//		if (doubleBackToExitPressedOnce) {
//			// finish();
//			// overridePendingTransition(R.anim.slide_in_right,
//			// R.anim.slide_out_right);
//			
//			// Stop music service
//
//			return;
//		}
//
//		this.doubleBackToExitPressedOnce = true;
//		Toast.makeText(this, R.string.doubleBackToExit, Toast.LENGTH_SHORT)
//				.show();
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				doubleBackToExitPressedOnce = false;
//
//				//
//			}
//		}, 2000);
    }

    private void showQuitDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.msgQuitApp)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.dismiss();
                                stopService(intentService);
                                cancelNotification();
                                finish();
                                System.exit(0);
                            }
                        }).setNegativeButton(android.R.string.cancel, null)
                .create().show();
    }

    public void chooseContacts() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        pickContactIntent
                .setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_CONTACT_REQUEST):
                if (resultCode == Activity.RESULT_OK) {

                    try {
                        Uri contactData = data.getData();
                        // String contactId = contactData.getLastPathSegment();
                        String[] PROJECTION = new String[]{
                                ContactsContract.Data.CONTACT_ID,
                                ContactsContract.Contacts.DISPLAY_NAME,
                                ContactsContract.Contacts.HAS_PHONE_NUMBER,};
                        Cursor localCursor = getContentResolver().query(
                                contactData, null, null, null, null);
                        localCursor.moveToFirst();

                        for (int i = 0; i < localCursor.getColumnCount(); i++) {
                            String value = localCursor.getString(i);
                            Log.e("ShareUtility",
                                    "value " + localCursor.getColumnName(i) + ": "
                                            + localCursor.getString(i));
                        }
                        // --> use moveToFirst instead of this:
                        // localCursor.move(Integer.valueOf(contactId)); /*CONTACT
                        // ID NUMBER*/

                        String contactID = localCursor
                                .getString(localCursor
                                        .getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
                        String contactDisplayName = localCursor
                                .getString(localCursor
                                        .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                        String localFolder = Environment
                                .getExternalStorageDirectory()
                                + "/"
                                + getString(R.string.app_name) + "/ringtone/";

                        // String localFolder = Environment
                        // .getExternalStorageDirectory()
                        // + "/Ringtones/";
                        Toast.makeText(this,
                                "Set as contact ringtone is processing ... !", 3000)
                                .show();
                        ShareUtility.setRingtoneContact(MainActivity.this,
                                contactID, GlobalValue.getCurrentSong().getUrl(),
                                localFolder, contactData);
                        Toast.makeText(this,
                                "Ringtone assigned to: " + contactDisplayName,
                                Toast.LENGTH_LONG).show();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(
                                this,
                                "Have error when Add contact ringtone. Try again please!",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
