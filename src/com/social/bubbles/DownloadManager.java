package com.social.bubbles;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import com.bubbles.location.MyLocation;
import com.facebook.android.Facebook;
import com.facebook.android.Util;
import com.social.bubbles.facebook.Session;
import com.social.bubbles.fbquery.FacebookObject;
import com.social.bubbles.fbquery.FacebookQuery;
import com.social.bubbles.fsquery.FoursquareObject;
import com.social.bubbles.fsquery.FoursquareQuery;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class DownloadManager extends Service {
	private static final String TAG = "		Bubbles Download Manager: ";
	
	
	
	private static HashMap<String, Bitmap> imageDB;
	
	public static Service mService;
	
	protected static FacebookQuery fbQuery;
	protected static FoursquareQuery fsQuery;

	
	//Handlers
	public static DownloadSignalHandler downloadSignalHandler; 	//starts new set of FB/FS downloads
	public static Handler refreshBubbleHandler;					//communicates data to UI
	public static Handler progressBarHandler;

	//Fetching threads
	protected Thread fetchInfoThread;

	
	//Bubble Manager
	private static BubbleManager bubbleManager;

	
	@Override
	public void onCreate() {
		mService = this;

		bubbleManager = new BubbleManager(mService);

		Toast.makeText(this, "Background Downloader Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		downloadSignalHandler = new DownloadSignalHandler();
		MyLocation.setHandler(downloadSignalHandler);
		//imageDB = new HashMap<String, Bitmap>();
		
		fetchInfoThread = new FetchInfoThread();
	}
	
	public static void showToast(String text, boolean longDuration){
		if(mService!=null)
			Toast.makeText(mService, text, (longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
	}
	
	
	int progress = 0;
	private void startProgressBar(){
		 // create a thread for updating the progress bar
	    Thread background = new Thread (new Runnable() {
	       public void run() {
	           try {
	               // enter the code to be run while displaying the progressbar.
	               //
	        	   progress=0;
	               // This example is just going to increment the progress bar:
	               // So keep running until the progress value reaches maximum value
	               while (progress<= 1000) {
	                   // wait 500ms between each update
	                   Thread.sleep(200);

	     		          Message updateMessage = Message.obtain();
	     		          updateMessage.obj = progress++;
	     		          
		                   if(progressBarHandler!=null){
		     		          updateMessage.setTarget(progressBarHandler);
		     		          updateMessage.sendToTarget();
		                   }
	                
	                   // active the update handler
	                     // Update the progress bar
	               }
	           } catch (java.lang.InterruptedException e) {
	               // if something fails do something smart
	           }
	       }
	    });
	    background.start();
	}
	void incrementBarTo(int a){
		progress = a;
		
        Message updateMessage = Message.obtain();

		if(progressBarHandler!=null){
	 	   Log.d("PROGRESS BAR UPDATE ", ""+progress);
	        updateMessage.obj = a;
	        updateMessage.setTarget(progressBarHandler);
	        updateMessage.sendToTarget();
		}
		
		if(refreshBubbleHandler!=null){
	        updateMessage = Message.obtain();
	        updateMessage.setTarget(refreshBubbleHandler);
	        updateMessage.sendToTarget();
		}
        
	}
//downloads relevant facebook info
	private class FetchInfoThread extends Thread {
	
	      public void run() {
	    	  this.setPriority(MIN_PRIORITY);
    		  
	    	  progress=0;
	    	  startProgressBar();
	    	  int steps = 1;
	    	  int initialSteps = 5;
	    	  
    		  boolean needLogin = false;
	    	  if(fsQuery!=null && Settings.Bubble.DISPLAY_FOURSQUARE_BUBBLES){
	        	  if(fsQuery.isSessionValid()){
	        		  //retrieve all foursquare objects
		    		  Log.d("Download Manager: ", "retrieve all relevant Foursquare objects");

		    		  LinkedList<Bubble> bubbles = new LinkedList<Bubble>();
		    		  bubbles.addAll(fsQuery.getFoursquareUsers());
	        		  BubbleManager.add(bubbles);
	        		  incrementBarTo(1000/initialSteps*steps);
	        		  steps++;

	        		  bubbles.clear();
		    		  bubbles.addAll(fsQuery.getFoursquareVenues());
	        		  BubbleManager.add(bubbles);	
	        		  incrementBarTo(1000/initialSteps*steps);
	        		  steps++;

	        		  Log.d("Download Manager: ", "send all fresh Foursquare objects to BubbleManager");
	        	  }
                  else{
                	  if(needLogin){
                  		Intent listIntent = new Intent(getBaseContext(), com.social.bubbles.Main.class);
                	  	getBaseContext().startActivity(listIntent);
                	  }
                	  //return to login screen
                  }
	    	  }
	    	  
	    	  if(fbQuery!=null && Settings.Bubble.DISPLAY_FACEBOOK_BUBBLES){
	        	  if(Session.restore(getBaseContext())!=null){
	        		  //retrieve all facebook objects
		    		  Log.d("Download Manager: ", "retrieve all relevant Facebook objects");
	        		   
		    		  LinkedList<Bubble> bubbles = new LinkedList<Bubble>();
		    		  bubbles.addAll((fbQuery.getFacebookMyEvents()));
	        		  BubbleManager.add(bubbles);
	        		  incrementBarTo(1000/initialSteps*steps);
	        		  steps++;

	        		  
	        		  bubbles.clear();
		    		  bubbles.addAll((fbQuery.getFacebookRecentCheckins()));
	        		  BubbleManager.add(bubbles);
	        		  incrementBarTo(1000/initialSteps*steps);
	        		  steps++;


	        		  bubbles.clear();
		    		  bubbles.addAll((fbQuery.getFacebookNearbyData()));
	        		  BubbleManager.add(bubbles);
	        		  incrementBarTo(1000/initialSteps*steps);
	        		  steps++;
	        		  steps = 0;


	        		  Log.d("Download Manager: ", "send all fresh Facebook objects to BubbleManager");
	        	  }
                  else{
                	  needLogin = true;
                	  //Toast.makeText(mService, "Please Login to Facebook", Toast.LENGTH_LONG).show();
                	  //return to login screen
                  }
	    	  }
	    	  BubbleManager.queueEverythingElse();

    		  //BubbleManager.add(freshBubbleData);
  
//		          Message updateMessage = Message.obtain();
//		          updateMessage.what = 1;
//		          updateMessage.obj = BubbleManager.getSortedActiveList();
//		          updateMessage.setTarget(updateHandler);
//		          updateMessage.sendToTarget();
//		        	Log.d("           SENT MESSAGE TO UPDATE UI : FIRST IS ", bubbleManager.getSortedActiveList().getFirst().title);
//		
		    	  //
		    	  //FOR TESTING PURPOSES ONLY
		    	  //
	    	  		LinkedList<Bubble> alist = BubbleManager.getSortedFreshList();
	    		  Log.d("Download Manager: Displaying Fresh List of size : ", String.valueOf(alist.size()));

	    		  
		    	  for(Bubble b : alist){
		    		  Log.d("Description: ", " "+b.getDescription());
		    		  Log.d("			ID: ", " "+b.id);
		    		  Log.d("			Type: ", " "+b.getType());
		    		  Log.d("			Rank: ", " "+Long.toString(b.rank));
		    		  Log.d("			Picture: ", " "+b.getImageUrl());
		    		  Log.d("			Last Activity: ", " "+b.getLastActivity().toString());
		    	  }
//		    	  SortedSet<Bubble> blist = BubbleManager.getSortedCacheList();
//	    		  Log.d("Download Manager: Displaying Cache List of size : ", String.valueOf(blist.size()));
//
//		    	  for(Bubble b : blist){
//		    		  Log.d("Description: ", " "+b.getDescription());
//
//		    		  Log.d("			ID: ", " "+b.id);
//		    		  Log.d("			Type: ", " "+b.getType());
//		    		  Log.d("			Rank: ", " "+Long.toString(b.rank));
//		    		  Log.d("			Picture: ", " "+b.getImageUrl());
//		    		  Log.d("			Last Activity: ", " "+b.getLastActivity().toString());
//		    	  }  			        	  
					      
	      	}
	}
	    	  
    private class DownloadSignalHandler extends Handler {
        private static final int LOC_UPDATE = 0;
		private static final int BUBBLE_UPDATE = 1;

		public void handleMessage(Message msg) {
                switch (msg.what) {
                case LOC_UPDATE:
                	forceDownloadStart();
				case BUBBLE_UPDATE:
                	forceDownloadStart();
                }  
			
		}
    }
    
    public void forceDownloadStart(){
        if(!fetchInfoThread.isAlive()){
        	fetchInfoThread  = new FetchInfoThread();
        	fetchInfoThread.start();
        }
    }

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Bubble Downloader Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");

	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Log.d(TAG, "onStart");

	}
	
	public static void setFacebookQuery(FacebookQuery fbQ){
		fbQuery = fbQ;
	}
	public static void setFoursquareQuery(FoursquareQuery fsQ){
		fsQuery = fsQ;
	}
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


}