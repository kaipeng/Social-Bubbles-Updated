package com.social.bubbles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.bubbles.location.MyLocation;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.frublin.androidoauth2.AndroidOAuth;
import com.frublin.androidoauth2.AsyncFoursquareRunner;
import com.frublin.androidoauth2.FoursquareError;
import com.frublin.androidoauth2.R;
import com.frublin.androidoauth2.AndroidOAuth.FSDialogListener;
import com.social.bubbles.facebook.AsyncRequestListener;
import com.social.bubbles.foursquare.FSAsyncRequestListener;
import com.social.bubbles.fsquery.FoursquareQuery;

import com.social.bubbles.facebook.Session;
import com.social.bubbles.fbquery.FacebookQuery;
import com.social.bubbles.socialbubbles3.SocialBubblesSetting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Main extends Activity implements OnClickListener{
	
	//Location Variables
	MyLocation mLoc;

	//Facebook APP ID
    public static final String FB_APP_ID = "184168524953485";

    //Foursquare Session
    private AndroidOAuth fs;
    
    //Facebook Session
    public Facebook fb;
    // The permissions that the app should request from the user
    // when the user authorizes the app.
    private final String[] PERMISSIONS = 
        new String[] { "offline_access", "read_stream", "publish_stream", "user_checkins", "friends_checkins", "publish_checkins", "user_events", "friends_events", "rsvp_event",
    					"email", "user_location", "friends_location", "read_stream", "friends_photos", "user_photos"  /*"read_friendslists", "manage_friendslists",*/};
    private ProgressDialog mFBLogOutSpinner;

    
    private boolean FSsignedin = false;
    private boolean FBsignedin = false;
    
    private FacebookQuery fbQuery;
    private FoursquareQuery fsQuery;


	private Context mContext;
	public static Activity mActivity;
	
	private String name;
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	isFullscreen = false;
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
                
        mContext = getBaseContext();
        mActivity = this;
                
        //Start Location Timer Thread in OAUTH Library
		Log.d("My Location Timer: ", "Timer Start");
        mLoc = new MyLocation(this);
        
        
        fs = new AndroidOAuth(getBaseContext());
        fb = new Facebook(FB_APP_ID);
        
        mFBLogOutSpinner = new ProgressDialog(mContext);
        mFBLogOutSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mFBLogOutSpinner.setMessage("Logging Out...");
        
        fbQuery = new FacebookQuery(mActivity, mContext);
        fsQuery = new FoursquareQuery(mActivity, fs);        
    }
    
    @Override
    public void onResume() {
            super.onResume();
            
            setupView();
            setupDownloader();

    }
    

    Set<View> viewObjects = new HashSet<View>(); 
    private void setupView() {
       //if Foursquare is not logged in
        // We want to see the unauthenticated layout
        final ImageButton FSsigninButton = (ImageButton) findViewById(R.id.foursquare_button);
        FSsigninButton.setOnClickListener(this);
        viewObjects.add(FSsigninButton);
      
        final ImageButton FBsigninButton = (ImageButton) findViewById(R.id.facebook_button);
        FBsigninButton.setOnClickListener(this);
        viewObjects.add(FBsigninButton);

        final ImageButton FullScreenButton = (ImageButton) findViewById(R.id.FullscreenButton);
        FullScreenButton.setOnClickListener(this);
        
        //Launch View Bubble List
        final ImageButton LaunchViewerButton = (ImageButton) findViewById(R.id.LaunchViewerButton);
        LaunchViewerButton.setOnClickListener(this);
        viewObjects.add(LaunchViewerButton);

        
        
        //Check for existing Facebook session
        Session session = Session.restore(this);
        if (session != null) {
            //Display "logged in"
            fb = session.getFb();
        	FBsigninButton.setImageResource(R.drawable.facebook_banner_sign_out);
        	FBsignedin = true;
        } else {
            //Display "log in"
        	FBsigninButton.setImageResource(R.drawable.facebook_banner_sign_in);
        	FBsignedin = false;
        }
        
        //Check for existing Foursquare session
        String accessToken = fs.getSavedAccessToken(PreferenceManager
                .getDefaultSharedPreferences(this));
        if (accessToken != null) {
            //Display "logged in"
        	FSsigninButton.setImageResource(R.drawable.foursquare_banner_sign_out);
        	FSsignedin = true;
        } else {
            //Display "log in"
        	FSsigninButton.setImageResource(R.drawable.foursquare_banner_sign_in);
        	FSsignedin = false;
        }
        
        //Set Launch Button visibility based on FS or FB session validity
        if(FSsignedin||FBsignedin){
        	LaunchViewerButton.setVisibility(View.VISIBLE);
        }
        else{
        	LaunchViewerButton.setVisibility(View.INVISIBLE);
        }
        setFullscreenMode();


    }
    //after setupView()
    private void setupDownloader(){
		if(FBsignedin)
			DownloadManager.setFacebookQuery(fbQuery);
		if(FSsignedin)
			DownloadManager.setFoursquareQuery(fsQuery);
		Intent downloaderIntent = new Intent(mContext, DownloadManager.class);
		this.startService(downloaderIntent);
    }

    private void setFullscreenMode(){
    	if(!isFullscreen){
    		for(View v : viewObjects){
    			v.setVisibility(View.VISIBLE);
    		}
    	}else{
    		for(View v : viewObjects){
    			v.setVisibility(View.INVISIBLE);
    		}
    	}
    }
    boolean isFullscreen;
	@Override
	public void onClick(View view) {
        switch (view.getId()) {
        case R.id.FullscreenButton:
        	isFullscreen=!isFullscreen;
        	setFullscreenMode();
        	break;
        case R.id.foursquare_button:
        	if(FSsignedin){
        		FSlogout();
                setupView();
        	}else{
        		FSlogin();
        		setupView();
        	}
        	break;
        case R.id.LaunchViewerButton:
        	if(FBsignedin){
        		//testing
        		if(FBsignedin)
        			DownloadManager.setFacebookQuery(fbQuery);
        		if(FSsignedin)
        			DownloadManager.setFoursquareQuery(fsQuery);
        		Log.d("Social Bubbles Main: ", "DownloadManager Started");
        		
        		//Initialize download from current Location
        		MyLocation.go();
        		
        		Intent listIntent = new Intent(mContext, com.social.lazylist.BubbleList.class);
        		this.startActivity(listIntent);
        		Log.d("Social Bubbles Main: ", "BubbleList Started");

        		//fbQuery.getReleventFacebookObjects();
        		setupView();
        	}else{
        	}
        	break;
        case R.id.facebook_button:
        	if(FBsignedin){
        		FBlogout();
                setupView();
        	}else{
        		FBlogin();
                setupView();
        	}
        	break;
        default:
            return;        
            }		
	}
	
	private void FSlogin(){			      
		fs.authorize(this, PERMISSIONS, new FSAppLoginListener(fs));
	}
	
	private void FSlogout(){
		fs.logout(this);
        Toast.makeText(mContext, "Successfully signed out of FS", Toast.LENGTH_SHORT)
      	.show();
	}
	
	private void FBlogin(){
        Session.waitForAuthCallback(fb);
		fb.authorize(this, PERMISSIONS, new AppLoginListener(fb));
	}
	
	private void FBlogout(){
		Facebook fb = Session.restore(mContext).getFb();
		Log.d("Social Bubbles: ", "Attempting to log out of Facebook session " + Session.restore(mContext).getName());

        //mFBLogOutSpinner.show();

        // clear the local session data
        Session.clearSavedSession(mContext);
        
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
            	setupView();
                Toast.makeText(mContext, "Logging out...", Toast.LENGTH_SHORT)
              	.show();
                //mFBLogOutSpinner.dismiss();
            }
        });
        new AsyncFacebookRunner(fb).logout(mContext, 
                new AsyncRequestListener() {

            public void onComplete(JSONObject response, final Object state) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                    	setupView();
                        Toast.makeText(mContext, "Deleted saved access tokens.", Toast.LENGTH_SHORT)
                      	.show();
                        Toast.makeText(mContext, "Succesfully logged out of Facebook!", Toast.LENGTH_SHORT)
                      	.show();
                        //mFBLogOutSpinner.dismiss();
                    }
                });

            }

			@Override
			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub
				
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
        });
    
	}
	
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
    	Facebook fb = Session.wakeupForAuthCallback();
    	fb.authorizeCallback(requestCode, resultCode, data);
    }
    
    private class AppLoginListener implements DialogListener {

        private Facebook fb;

        public AppLoginListener(Facebook fb) {
            this.fb = fb;
        }

        public void onCancel() {
            Log.d("app", "login canceled");
        }

        public void onComplete(Bundle values) {
            /**
             * We request the user's info so we can cache it locally and
             * use it to render the new html snippets
             * when the user updates her status or comments on a post. 
             */
            new AsyncFacebookRunner(fb).request("me", 
                    new AsyncRequestListener() {
                public void onComplete(JSONObject obj, final Object state) {
                    // save the session data
                    String uid = obj.optString("id");
                    name = obj.optString("name");
                    new Session(fb, uid, name).save(mContext);
                    
                    // render the Stream page in the UI thread
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                        	setupView(); //refresh UI screen
                            
      	                  Toast.makeText(mContext, "Signed into FB as: " + name, Toast.LENGTH_LONG)
  	                  	.show();
                        }
                    });
                    //
                    //Launch NEXT UI PAGE if we want to...
                    // 
                  
                  //setupView();
                }
            }, null);
        }

        public void onError(DialogError e) {
            Log.d("app", "dialog error: " + e);               
        }

        public void onFacebookError(FacebookError e) {
            Log.d("app", "facebook error: " + e);
        }
    }
    
    private class FSAppLoginListener implements FSDialogListener {

        private AndroidOAuth fs;

        public FSAppLoginListener(AndroidOAuth fs) {
            this.fs = fs;
        }

        public void onCancel() {
            Log.d("app", "login canceled");
        }

        public void onComplete() {
            new AsyncFoursquareRunner(fs).request("users/self", 
                    new FSAsyncRequestListener() {
                public void onComplete(JSONObject obj, final Object state) throws JSONException {
                    // save the session data
                	Log.d("Foursquare Self: ", obj.toString());
                	
                	JSONObject mobj = obj.getJSONObject("response").getJSONObject("user");

                    String firstname = mobj.getString("firstName");

                    String lastname = mobj.getString("lastName");

                    name = firstname+" "+lastname;
                    
                    // render the Stream page in the UI thread
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                        	setupView(); //refresh UI 
                            
      	                  Toast.makeText(mContext, "Signed into FS as: " + name, Toast.LENGTH_LONG)
  	                  	.show();
                        }
                    });
                    //
                    //Launch NEXT UI PAGE if we want to...
                    // 
                  
                  //setupView();
                }
            }, null);
        }

        public void onFoursquareError(FoursquareError e) {
            Log.d("app", "foursquare error: " + e);
        }

		@Override
		public void onError(com.frublin.androidoauth2.DialogError e) {
			// TODO Auto-generated method stub
			
		}
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	mLoc.stopUpdates();
    }
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	mLoc.stopUpdates();
    }
    
	public static void showToast(String text, boolean longDuration){
		if(mActivity!=null)
			mActivity.runOnUiThread(new Toaster(text, longDuration));
	}
    //Used to display bitmap in the UI thread
    static class Toaster implements Runnable
    {
    	String text;
    	boolean longDuration;
        public Toaster(String text, boolean longDuration){this.text = text; this.longDuration = longDuration;}
        public void run()
        {
			Toast.makeText(mActivity, text, (longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//    	Intent intent = new Intent(this.getBaseContext(), SocialBubblesSetting.class);
//    	startActivity(intent);
//
//        return true;
//    }
}
