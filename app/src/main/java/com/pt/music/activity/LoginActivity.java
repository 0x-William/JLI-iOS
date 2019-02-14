package com.pt.music.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.pt.music.R;
import com.pt.music.config.Args;
import com.pt.music.config.GlobalValue;
import com.pt.music.modelmanager.ModelManager;
import com.pt.music.modelmanager.ModelManagerListener;
import com.pt.music.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;

public class LoginActivity extends Activity {

    private EditText mTxtUserName, mTxtPassword;
    private Button mBtnLogin;
    private Button mBtnForgotpass;
    private String mStrUserName, mStrPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();
    }

    private void initUI() {
        mTxtPassword = (EditText) findViewById(R.id.txt_password);
        mTxtUserName = (EditText) findViewById(R.id.txt_username);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnForgotpass = (Button) findViewById(R.id.btn_forgotpass);

        // Should call this method at the end of declaring UI
        initControl();
    }

    private void initControl() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        mBtnForgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPass();
            }
        });
    }

    private void forgotPass(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://myjli.com/mobileapp/forgotpass"));
        startActivity(intent);
    }
    private void login() {
        if (validate()) {
            if (NetworkUtil.checkNetworkAvailable(this)) {
                ModelManager.login(this, mStrUserName, mStrPassword, new ModelManagerListener() {
                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Username or password is not correct",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String json) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            GlobalValue.pref.putStringValue(Args.USER_ID, jsonObject.getString("user_id"));
                            finish();
                            Intercom.client().registerIdentifiedUser(Registration.create().withUserId(jsonObject.getString("user_id")));
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Network is not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validate() {
        mStrUserName = mTxtUserName.getText().toString().trim();
        mStrPassword = mTxtPassword.getText().toString().trim();

        if (mStrUserName.equals("")) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_LONG).show();
            mTxtUserName.requestFocus();
            return false;
        } else if (mStrPassword.equals("")) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_LONG).show();
            mTxtPassword.requestFocus();
            return false;
        }

        return true;
    }
}
