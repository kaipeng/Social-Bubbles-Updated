package com.social.bubbles;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import android.util.Log;

public class Settings {
    

        public static class Bubble{
        	
            public static double BUBBLE_DIMENSION = 0.4;
            public static double BUBBLE_SPEED = 1.6;

            
            //Bubble Manager Settings
        	public static int FRESH_QUALIFIER = 10;//minutes
        	public static int DISPLAY_QUEUE_SIZE = 10;
        	public static int ONSCREEN_QUEUE_SIZE = 10;

        	public static int CACHE_SIZE = 100;

        	//settings displaying of bubble types
        	public static boolean DISPLAY_ONLY_FRESH_BUBBLES = false;
	        	public static boolean DISPLAY_FACEBOOK_BUBBLES = true;
	        	public static boolean DISPLAY_FACEBOOK_EVENTS = true;
	        	public static boolean DISPLAY_FACEBOOK_FRIENDSEVENTS = true;
	        	public static boolean DISPLAY_FACEBOOK_RECENTCHECKINS = true;
	        	public static boolean DISPLAY_FACEBOOK_NEARBYCHECKINS = true;
	        	public static boolean DISPLAY_FACEBOOK_TRENDINGPLACES = true;
        	public static boolean DISPLAY_FOURSQUARE_BUBBLES = true;
	        	public 	static boolean DISPLAY_FOURSQUARE_NEARBYCHECKINS = true;
	        	public static boolean DISPLAY_FOURSQUARE_RECENTCHECKINS = true;
	        	public static boolean DISPLAY_FOURSQUARE_TRENDINGPLACES = true;
	        	public static boolean DISPLAY_FOURSQUARE_SPECIALS = true;
	        	public static boolean DISPLAY_FOURSQUARE_TODOS = true;
        		public static boolean DISPLAY_FOURSQUARE_FRIENDSONLY = true;
        }
    public static class Wallpaper{
    }
    public static class Foursquare{
    	
    }
    public static class Facebook{
    	
    }
}