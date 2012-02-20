/**
 * 
 */
package com.social.bubbles.socialbubbles3;


import com.social.bubbles.BubbleManager;
import com.social.bubbles.Settings;

import android.app.Service;
import android.content.res.Resources;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;

/**
 * @author Mike NTG
 *
 */

import android.content.Intent;
	import android.content.SharedPreferences;
import android.graphics.Rect;
import android.util.Log;
	import android.view.MotionEvent;
import android.view.SurfaceHolder;

	/**
	 * 
	 * A sample class that defines a LiveWallpaper an it's associated Engine.
	 * The Engine delegates all the Wallpaper painting stuff to a specialized Thread.
	 * 
	 * Sample from <a href="http://blog.androgames.net">Androgames tutorials blog</a>
	 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
	 * 
	 * @author antoine vianey
	 *
	 */
	public class SocialBubblesService extends WallpaperService {
			
			private final Handler mHandler = new Handler();
			
			private Resources res;

	        public static final String PREFERENCES = "com.socialbubbles.socialbubbles3.SocialBubblesService";
	        public static final String PREFERENCE_RADIUS = "preference_radius";
	        public static final String PREFERENCE_NUMBER_BUBBLES = "preference_number_bubbles";

	        public static Service service;
	        
	        @Override
	        public Engine onCreateEngine() {
	                return new SampleEngine();
	        }

	        @Override
	        public void onCreate() {
	        		this.service = this;
	                super.onCreate();
	        }

	        @Override
	        public void onDestroy() {
	                super.onDestroy();
	        }

	        public class SampleEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

	                private SocialBubblesService engine;
					private MySystem mSystem;
	                private SharedPreferences prefs;  
	                
	                SampleEngine() {
	                        SurfaceHolder holder = getSurfaceHolder();
	                        prefs = SocialBubblesService.this.getSharedPreferences(PREFERENCES, 0);
	                        prefs.registerOnSharedPreferenceChangeListener(this);
	                        
	                        engine= SocialBubblesService.this;
	                   
	                        res= getResources();
	                        	                        
	                        //Initialize System
	                        mSystem = new MySystem(holder, getApplicationContext(),res,engine);
	                }

	                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
	                		if(key == PREFERENCE_RADIUS){
	                			Settings.Bubble.BUBBLE_DIMENSION=Double.parseDouble(prefs.getString(PREFERENCE_RADIUS, ".4"));
                		    	BubbleManager.queueQualifyingBubbles();
                		    	
                		    	Log.d("Preferences Changed: ", "PREFERENCE_NUMBER_BUBBLES=" + Settings.Bubble.BUBBLE_DIMENSION);
	                		}
	                		
	                		if(key == PREFERENCE_NUMBER_BUBBLES){
	                			int num = (Integer.parseInt(prefs.getString(PREFERENCE_NUMBER_BUBBLES, "10")));
	                		    	Settings.Bubble.DISPLAY_QUEUE_SIZE = num;
	                		    	//Refresh Bubbles
	                		    	BubbleManager.queueQualifyingBubbles();
	                		    	
	                		    	Log.d("Preferences Changed: ", "PREFERENCE_NUMBER_BUBBLES=" + Settings.Bubble.DISPLAY_QUEUE_SIZE);
	                		}
	                }

	                @Override
	                public void onCreate(SurfaceHolder surfaceHolder) {
	                        super.onCreate(surfaceHolder);
	                        setTouchEventsEnabled(true);
	                }

	                @Override
	                public void onDestroy() {
	                        super.onDestroy();
	                        // remove listeners and callbacks here
	                      
	                        mSystem.stopPainting();
	                }

	                @Override
	                public void onVisibilityChanged(boolean visible) {
	                        if (visible) {
	                             
	                                mSystem.resumePainting();
	                        } else {
	                                // remove listeners and callbacks here
	                              
	                                mSystem.pausePainting();
	                        }
	                }

	                @Override
	                public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	                        super.onSurfaceChanged(holder, format, width, height);
	                   
	                        mSystem.setSurfaceSize(width, height);
	                }

	                @Override
	                public void onSurfaceCreated(SurfaceHolder holder) {
	                        super.onSurfaceCreated(holder);
	                   
	                        mSystem.start();
	                }

	                @Override
	                public void onSurfaceDestroyed(SurfaceHolder holder) {
	                        super.onSurfaceDestroyed(holder);
	                        boolean retry = true;
	                
	                        mSystem.stopPainting();
	                        while (retry) {
	                                try {
	                         
	                                        mSystem.join();
	                                        retry = false;
	                                } catch (InterruptedException e) {}
	                        }
	                }

	                @Override
	                public void onOffsetsChanged(float xOffset, float yOffset, 
	                                float xStep, float yStep, int xPixels, int yPixels) {
	                }

	                @Override
	                public void onTouchEvent(MotionEvent event) {
	                        super.onTouchEvent(event);
	                        mSystem.doTouchEvent(event);
	                        
	                }
	                
	        }
	        
	}