package com.pt.music.service;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pt.music.config.GlobalValue;


public class NotificationDismissedReceiver extends BroadcastReceiver {
	 @Override
	  public void onReceive(Context context, Intent intent) {
	      int notificationId = intent.getExtras().getInt("notificationId");
	      /* Your code to handle the event here */	    
	      try
	      {
	    	  if (notificationId == GlobalValue.PAUSE_ACTION_ID)
		    	  GlobalValue.currentMusicService.pauseMusic();
		      else  if (notificationId == GlobalValue.BACK_ACTION_ID)
		    	  GlobalValue.currentMusicService.backSong();
		      else  if (notificationId == GlobalValue.NEXT_ACTION_ID)
		    	  GlobalValue.currentMusicService.nextSong();
		      else  if (notificationId == GlobalValue.PLAY_OR_ACTION_ID)
	    	  {	    	  
		    	  if (GlobalValue.currentMusicService.isPause) 	    	  
		    		  GlobalValue.currentMusicService.resumeMusic();
		    	   else 
		    		  GlobalValue.currentMusicService.pauseMusic(false);	    	  
	    	  }
	      }
	      catch (Exception e){
	    	  e.printStackTrace();
	    	  int NOTIFICATION_ID = 231109;// = MainActivity.NOTIFICATION_ID
	    	  NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	  		  nMgr.cancel(NOTIFICATION_ID);
	      }
	  }
}
