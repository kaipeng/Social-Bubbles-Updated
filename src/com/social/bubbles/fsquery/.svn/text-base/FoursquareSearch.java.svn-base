package com.social.bubbles.fsquery;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.frublin.androidoauth2.AndroidOAuth;
import com.frublin.androidoauth2.AsyncFoursquareRunner;
import com.frublin.androidoauth2.FoursquareError;
import com.frublin.androidoauth2.Util;
import com.google.gson.Gson;
import com.social.bubbles.DownloadManager;
import com.social.bubbles.Main;
import com.social.bubbles.socialbubbles3.MySystem;


public class FoursquareSearch {
	
	public static AndroidOAuth fs;
	public static Context context;
	
	public static void initAndroidOAuth(AndroidOAuth fsa){
		fs=fsa;
		Log.d("Foursquare Search: Initialized", "Token = " + fs.getSavedAccessToken());
	}
	public FoursquareSearch(Context context){
		this.context = context;
		fs = new AndroidOAuth(context);
		String accessToken = fs.getSavedAccessToken(PreferenceManager.getDefaultSharedPreferences(context));
		Log.d("Foursquare Search: ", "Token = " + accessToken);
		if (accessToken == null){
			fs = null;
		}
	}
	
	public String queryFoursquare(String query) {
		String result;
		if (fs==null){
			return "";
		}
		try {
			result = fs.request(query);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			result = "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = "";
		}
		return result;
	}
	public String queryFoursquare(String query, Bundle parameters){
		String result;
		if (fs==null){
			return "";
		}
		try {
			result = fs.request(query, parameters);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			result = "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = "";
		}
		return result;
	}
	public void postToFoursquare(String query, Bundle parameters){
		String result;
		if (fs==null){
			return;
		}
		String accessToken = fs.getSavedAccessToken(PreferenceManager.getDefaultSharedPreferences(context));
		Log.d("Foursquare Search: ", "Token = " + accessToken);
		
		AsyncFoursquareRunner mAsyncRunner = new AsyncFoursquareRunner(this.fs);
		mAsyncRunner.request(query, parameters, "POST",
                 new UploadListener(), null);

		 return;
	}
	
    public class UploadListener implements AsyncFoursquareRunner.RequestListener {

        public void onComplete(final String response, final Object state) {
            try {
                // process the response here: (executed in background thread)
                Log.d("Foursquare-Upload", "Response: " + response.toString());
                FoursquarePostResponseClass fpsc = new Gson().fromJson(response, FoursquarePostResponseClass.class);

                String text = fpsc.getDescription();
                Log.d("Foursquare-Upload", "Text: " + text);

                if(text == null)
                	Main.showToast("Post Unsuccessful:", false);
                else
                	Main.showToast("Post Successful: "+text, false);
                
            } catch (Exception e) {
                Log.w("Foursquare-Upload", "GSON Error in response");
            }
        }

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}
    }
}
