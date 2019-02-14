package com.pt.music.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pt.music.R;
import com.pt.music.config.Args;
import com.pt.music.config.GlobalValue;
import com.pt.music.gcm.QuickstartPreferences;
import com.pt.music.gcm.RegistrationIntentService;
import com.pt.music.util.LanguageUtil;
import com.pt.music.util.MySharedPreferences;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //intercom old
        //Intercom.client().setVisibility(Intercom.GONE);
        Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE);
        LanguageUtil.setLocale(new MySharedPreferences(this).getLanguage(),
                this);
        GlobalValue.constructor(this);
        Intent intent = getIntent();
        Uri data = intent.getData();
        GlobalValue.pref.putStringValue(Args.HOST, "");
        GlobalValue.pref.putStringValue(Args.PARAM, "");
        if(data != null) {
            String host = data.getHost();
            GlobalValue.pref.putStringValue(Args.HOST, host);
            String param = data.getQuery();
            if(param != null)
                GlobalValue.pref.putStringValue(Args.PARAM, param);
        }
        startMainActivity();

    }

    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            if (!GlobalValue.pref.getStringValue(Args.USER_ID).equals("")) {
                Intercom.client().registerIdentifiedUser(Registration.create().withUserId(GlobalValue.pref.getStringValue(Args.USER_ID)));
                startActivity(new Intent(SplashActivity.this,
                        MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_out_left);
            finish();
            //intercom old
            //Intercom.client().openGCMMessage(getIntent());
            Intercom.client().openGcmMessage();
            }
        }, 2000);

    }

    @Override
    public void onBackPressed() {
    }

}
