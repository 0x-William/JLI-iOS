package com.pt.music.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.pt.music.R;
import com.pt.music.config.GlobalValue;
import com.pt.music.config.WebserviceApi;
import com.pt.music.fragment.PlayerThumbFragment;
import com.pt.music.modelmanager.ModelManager;
import com.pt.music.modelmanager.ModelManagerListener;
import com.pt.music.util.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadUpdateActivity extends Activity {
    private final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private String downloadLink;
    private String localLink;
    private DownloadFileAsync downloadFileAsync;

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        downloadLink = extras.getString("url_song");
        String file = extras.getString("file_name");
        String rootFolder = Environment.getExternalStorageDirectory() + "/"
                + getString(R.string.app_name) + "/";

        File folder = new File(rootFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        localLink = rootFolder + file;
        Logger.e(localLink);
        Logger.e(downloadLink);

        downloadFileAsync = new DownloadFileAsync();
        downloadFileAsync.execute(downloadLink);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage(getString(R.string.downloading));
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                        "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadFileAsync.cancel(true);
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        deleteDownloadFile(localLink);
                                    }
                                }, 500);
                                DownloadUpdateActivity.this.finish();
                            }
                        });
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Logger.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(localLink);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            int value = Integer.parseInt(progress[0]);
            mProgressDialog.setProgress(value);
            if (value == 50) {
                addNewDownload();
            }
        }

        @Override
        protected void onPostExecute(String unused) {

            Toast.makeText(getBaseContext(), R.string.downloadComplete,
                    Toast.LENGTH_SHORT).show();
            GlobalValue.getCurrentSong().addMoreDownload();
            PlayerThumbFragment.lblNumberDownload.setText(GlobalValue
                    .getCurrentSong().getDownloadCount() + "");
            finish();
        }

    }

    private void deleteDownloadFile(String url) {
        File f = new File(url);
        f.deleteOnExit();
    }

    private void addNewDownload() {
        String getUrl = WebserviceApi.ADD_NEW_DOWN_LOAD + "?id="
                + GlobalValue.getCurrentSong().getId();
        ModelManager.sendGetRequest(getApplicationContext(), getUrl, null, false, new ModelManagerListener() {
            @Override
            public void onError(VolleyError error) {

            }

            @Override
            public void onSuccess(String json) {

            }
        });
    }

}