package com.pt.music.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.pt.music.R;
import com.pt.music.activity.MainActivity;
import com.pt.music.config.GlobalValue;
import com.pt.music.config.WebserviceApi;
import com.pt.music.fragment.PlayerFragment;
import com.pt.music.modelmanager.ModelManager;
import com.pt.music.modelmanager.ModelManagerListener;
import com.pt.music.object.Song;
import com.pt.music.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.intercom.android.sdk.Intercom;

public class MusicService extends Service {

	private static final String TAG = "MusicService";

	private final IBinder mBinder = new ServiceBinder();
	private List<Song> listSongs;
	private MediaPlayer mPlayer;
	private int length;
	private int lengthSong;
	private PlayerListener listener;
	public boolean isPause;
	private boolean isPreparing;
	private boolean isUpdatingSeek;
	private boolean isShuffle;
	private boolean isRepeat;
	private Handler mHandler;
	private boolean ringPhone = false;
	private boolean haveplayed;

	private Handler handler = new Handler();
	private static Runnable savedR = null;
	private static final int DELAY = 1000;
	private Runnable r = new Runnable() {
		@Override
		public void run() {
			// Log.e("MusicService", "handle.run");
			updateSeekProgress();
		}
	};

	public class ServiceBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		isPause = false;
		isPreparing = false;
		haveplayed = false;
		setNewPlayer();
		mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
			@Override
			public void onSeekComplete(MediaPlayer mp) {
				// Start the song 30 seconds in
				resumeMusic();
			}
		});
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		cancelNotification();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mHandler = new Handler();
		return START_STICKY;
	}

	private void updateSeekProgress() {
		try {
			listener.onSeekChanged(lengthSong, getLengSong(),
					getTime(mPlayer.getCurrentPosition()),
				mPlayer.getCurrentPosition());

			handler.postDelayed(r, DELAY);
		} catch (Exception e) {

			handler.postDelayed(r, DELAY);
		}
	}

	private void updateSeekProgressWithPlayingCheck() {
		try {
			listener.onSeekChanged(lengthSong, getLengSong(),
					getTime(mPlayer.getCurrentPosition()),
					mPlayer.getCurrentPosition());

			if (isPlay())
				handler.postDelayed(r, DELAY);
		} catch (Exception e) {
			if (isPlay())
				handler.postDelayed(r, DELAY);
		}
	}

	public boolean isPause() {
		return isPause;
	}

	public boolean isPreparing() {
		return isPreparing;
	}

	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}

	public void changeStatePause() {
		isPause = !isPause;
	}

	public boolean isPlay() {
		try {
			return mPlayer.isPlaying();
		} catch (Exception e) {
			return false;
		}
	}

	public void setSpeed(float speed){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			mPlayer.setPlaybackParams(mPlayer.getPlaybackParams().setSpeed(speed));
			if (PlayerFragment.btnPlay != null) {
				PlayerFragment.btnPlay
						.setBackgroundResource(R.drawable.btn_pause);
				MainActivity.btnPlayFooter
						.setBackgroundResource(R.drawable.bg_btn_pause_small);
			}
			isPause = false;
			sendNotification();
			if (!isUpdatingSeek) {
				isUpdatingSeek = true;
				updateSeekProgress();
			}
		}
	}

	public boolean isRepeat() {
		return isRepeat;
	}

	public void setRepeat(boolean isRepeat) {
		this.isRepeat = isRepeat;
	}

	public boolean isShuffle() {
		return isShuffle;
	}

	public void setShuffle(boolean isShuffle) {
		this.isShuffle = isShuffle;
	}

	public void setListener(PlayerListener listener) {
		this.listener = listener;
	}

	public void setListSongs(List<Song> listSongs) {
		if (this.listSongs == null) {
			this.listSongs = new ArrayList<Song>();
		}
		this.listSongs.clear();
		this.listSongs.addAll(listSongs);
	}

	public List<Song> getListSongs() {
		return listSongs;
	}

	public void addSong(Song song) {
		if (listSongs == null) {
			listSongs = new ArrayList<Song>();
		}
		listSongs.add(song);
	}

	public void startMusic() {

		try {
			if (!mPlayer.isPlaying()) {
				if (isPause) {
					resumeMusic();
				} else {
					try {
						mPlayer.setDataSource(listSongs.get(0).getUrl());
						isPreparing = true;
						mPlayer.prepareAsync();
					} catch (Exception e) {
						e.printStackTrace();
						startMusic(0);
						return;
					}
					lengthSong = mPlayer.getDuration();

					mPlayer.start();
					listener.onChangeSong(0);
					if (!isUpdatingSeek) {
						isUpdatingSeek = true;
						updateSeekProgress();
					}
					sendNotification();
				}
				isPause = false;
			}
		} catch (Exception e) {
			startMusic(0);
		}
	}

	private void plusNewListen() {
		String getUrl = WebserviceApi.ADD_NEW_VIEW + "?id="
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

	private void setNewPlayer() {
		mPlayer = new MediaPlayer();
		mPlayer.setVolume(100, 100);
		mPlayer.setWakeMode(getApplicationContext(),
				PowerManager.PARTIAL_WAKE_LOCK);
		checkCall();
		mPlayer.setOnErrorListener(new OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// onMediaPlayerError(mPlayer, what, extra);
				isPreparing = false;
				mHandler.post(new ToastRunnable(getString(R.string.song_error)));
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						nextSong();
					}
				}, 3000);
				return true;
			}
		});

		mPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.e("musicService", "progress : completed");
				addListen();
				nextSong();
			}
		});

		mPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				lengthSong = mPlayer.getDuration();
				isPreparing = false;
				mPlayer.start();
				plusNewListen();
				listener.OnMusicPrepared();
				if (!isUpdatingSeek) {
					isUpdatingSeek = true;
					updateSeekProgress();
				}
			}
		});

	}
	
	

	public void startMusic(int index) {
		Log.e("MusicService", "Start");
		haveplayed = true;
		if (PlayerFragment.btnPlay != null) {
			PlayerFragment.btnPlay.setBackgroundResource(R.drawable.btn_pause);
		}
		isPause = false;
		GlobalValue.currentSongPlay = index;
		PlayerFragment.lblTopHeader.setText(listSongs.get(index).getName());


		try {
			mPlayer.reset();
		} catch (Exception e) {
			// e.printStackTrace();
			setNewPlayer();
		}
		try {
			if (listSongs.get(index).getUrl() == null) {
				mPlayer.setDataSource(listSongs.get(index).getUrl());
			} else {
				mPlayer.setDataSource(listSongs.get(index).getUrl());
			}
			mPlayer.prepareAsync();
			listener.onChangeSong(index);
			sendNotification();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Map eventData = new HashMap();
		eventData.put("TITLE", listSongs.get(index).getName());
		eventData.put("AUTHOR", listSongs.get(index).getArtist());
		eventData.put("CATEGORY", listSongs.get(index).getCategoryId());
		Intercom.client().logEvent("listened to app audio", eventData);


	}

	public void playOrPauseMusic() {
		if (isPause) {
			resumeMusic();
		} else {
			pauseMusic();
		}
	}

	public void pauseMusic() {
		pauseMusic(true);
	}

	public void pauseMusic(boolean doCancelNotification) {
		//
		if (mPlayer.isPlaying()) {
			savedR = r;
			handler.removeCallbacks(r);

			isPause = true;
			length = mPlayer.getCurrentPosition();
			mPlayer.pause();
			isUpdatingSeek = false;
			PlayerFragment.btnPlay.setBackgroundResource(R.drawable.btn_play);
			MainActivity.btnPlayFooter
					.setBackgroundResource(R.drawable.bg_btn_play_small);
		}
		if (doCancelNotification)
			cancelNotification();
		else
			sendNotification();
		//
	}

	public void resumeMusic() {
		if (isPause) {
			r = savedR;
			handler.postDelayed(r, DELAY);
			mPlayer.seekTo(length);
			mPlayer.start();
			if (PlayerFragment.btnPlay != null) {
				PlayerFragment.btnPlay
						.setBackgroundResource(R.drawable.btn_pause);
				MainActivity.btnPlayFooter
						.setBackgroundResource(R.drawable.bg_btn_pause_small);
			}
			isPause = false;
			sendNotification();
			if (!isUpdatingSeek) {
				isUpdatingSeek = true;
				updateSeekProgress();
			}
		}
	}

	public void stopMusic() {
		try {
			PlayerFragment.btnPlay.setBackgroundResource(R.drawable.btn_play);
			mPlayer.seekTo(0);
			mPlayer.pause();
			handler.removeCallbacks(r);
			isUpdatingSeek = false;
			isPause = true;
			cancelNotification();
		} catch (Exception e) {
		}
	}

	public void seekTo(int progress) {
		int lSong = mPlayer.getDuration();
		pauseMusic();
		if(lengthSong>0 && isPreparing == false) {
			mPlayer.seekTo(progress);
			length = progress;
			updateSeekProgressWithPlayingCheck();
			if (progress >= lengthSong && lengthSong>0) {
				nextSongByOnClick();
			}
		}
	}

	public void backSong() {
		int newPosition;
		if (isShuffle) {
			newPosition = new Random().nextInt(listSongs.size());
		} else {
			if (isRepeat)
				newPosition = GlobalValue.currentSongPlay;
			else if (GlobalValue.currentSongPlay > 0) {
				newPosition = GlobalValue.currentSongPlay - 1;
			} else {
				newPosition = listSongs.size() - 1;
			}
		}
		startMusic(newPosition);
		listener.onChangeSong(newPosition);
	}

	public void backSongByOnClick() {
		backSong();
	}

	public void nextSong() {
		int newPosition;
		if(listSongs.size() > 0){
			if (isShuffle) {
				newPosition = new Random().nextInt(listSongs.size());
			} else {
				if (isRepeat)
					newPosition = GlobalValue.currentSongPlay;
				else if (GlobalValue.currentSongPlay < listSongs.size() - 1) {
					newPosition = GlobalValue.currentSongPlay + 1;
				} else {
					newPosition = 0;
				}
			}
			startMusic(newPosition);
			listener.onChangeSong(newPosition);
		}
	}

	public void nextSongByOnClick() {
		nextSong();
	}

	public String getLengSong() {
		return getTime(lengthSong);
	}

	@SuppressLint("DefaultLocale")
	private String getTime(int millis) {
		long second = (millis / 1000) % 60;
		long minute = millis / (1000 * 60);
		return String.format("%02d:%02d", minute, second);
	}

	private void checkCall() {
		PhoneStateListener phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				if (state == TelephonyManager.CALL_STATE_RINGING) {
					if (mPlayer != null)
						if (mPlayer.isPlaying())
							mPlayer.pause();
					ringPhone = true;

				} else if (state == TelephonyManager.CALL_STATE_IDLE) {
					if (ringPhone == true) {
						if (mPlayer != null)
							if (!mPlayer.isPlaying() && !isPause && haveplayed) {
								mPlayer.start();
							}
						ringPhone = false;
					}
				} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
					if (mPlayer != null)
						if (mPlayer.isPlaying())
							mPlayer.pause();
					ringPhone = true;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		};
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if (mgr != null) {
			mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}

	private void sendNotification() {

		Song song = GlobalValue.getCurrentSong();
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher);
		// load custom view
		RemoteViews rmView = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.layout_custom_notification);
		mBuilder.setContent(rmView);
		// set song name
		rmView.setTextViewText(R.id.lbl_song_name, song.getName());
		rmView.setTextViewText(R.id.lbl_singer, song.getArtist());
		// song controller
		rmView.setOnClickPendingIntent(R.id.btnBackward,
				createReceiverIntent(this, GlobalValue.BACK_ACTION_ID));
		rmView.setOnClickPendingIntent(R.id.btnForward,
				createReceiverIntent(this, GlobalValue.NEXT_ACTION_ID));
		rmView.setOnClickPendingIntent(R.id.btnPlay,
				createReceiverIntent(this, GlobalValue.PLAY_OR_ACTION_ID));
		if (GlobalValue.currentMusicService.isPause)
			rmView.setInt(R.id.btnPlay, "setBackgroundResource",
					R.drawable.btn_play);
		else
			rmView.setInt(R.id.btnPlay, "setBackgroundResource",
					R.drawable.btn_pause);

		// when swipe out the notification
		mBuilder.setDeleteIntent(createReceiverIntent(this,
				GlobalValue.PAUSE_ACTION_ID));
		// when tap on notification
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("notification", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(contentIntent);
		// mNotificationManager.notify(MainActivity.NOTIFICATION_ID,
		// mBuilder.build());
		startForeground(MainActivity.NOTIFICATION_ID, mBuilder.build()); // startForeGround
																			// so
																			// it
																			// will
																			// never
																			// be
																			// killed

	}

	private PendingIntent createReceiverIntent(Context context,
			int notificationId) {
		Intent intent = new Intent(context, NotificationDismissedReceiver.class);
		intent.putExtra("notificationId", notificationId);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context.getApplicationContext(), notificationId, intent, 0);
		return pendingIntent;
	}

	private void cancelNotification() {
		// NotificationManager nMgr = (NotificationManager)
		// getSystemService(Context.NOTIFICATION_SERVICE);
		// nMgr.cancel(MainActivity.NOTIFICATION_ID);
		stopForeground(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.e(TAG, "onDestroy");
		if (mPlayer != null) {
			try {
				mPlayer.stop();
				mPlayer.release();
			} finally {
				mPlayer = null;
			}
		}
	}

	public boolean onMediaPlayerError(MediaPlayer mp, int what, int extra) {
		if (PlayerFragment.btnPlay != null) {
			PlayerFragment.btnPlay.setBackgroundResource(R.drawable.btn_play);
		}
		mHandler.post(new ToastRunnable(getString(R.string.song_error)));

		if (mPlayer != null) {
			try {
				mPlayer.stop();
				mPlayer.release();
			} finally {
				mPlayer = null;
			}
		}
		return false;
	}

	private class ToastRunnable implements Runnable {
		String mText;

		public ToastRunnable(String text) {
			mText = text;
		}

		@Override
		public void run() {
			Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_LONG)
					.show();
		}
	}

	private void addListen() {
		String getUrl = WebserviceApi.ADD_NEW_VIEW + "?id="
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
