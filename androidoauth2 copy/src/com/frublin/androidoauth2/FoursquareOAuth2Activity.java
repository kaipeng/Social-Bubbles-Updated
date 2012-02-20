package com.frublin.androidoauth2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//temp - will delete
import com.bubbles.location.MyLocation;

import com.frublin.androidoauth2.AsyncFoursquareRunner;
import com.frublin.androidoauth2.Util;
import com.frublin.androidoauth2.AsyncFoursquareRunner.RequestListener;

import com.frublin.androidoauth2.R;

public class FoursquareOAuth2Activity extends Activity implements
                OnClickListener {

        private AndroidOAuth androidOAuth;
        private String LOG_TAG;
        private Handler handler;
        private TextView nameValue;
        private TextView emailValue;
        private TextView cityValue;
        private TextView idValue;
        private TextView geoValue;


        private static final int USER_PROFILE = 1;

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                //LOG_TAG = "AndroidOAuth2";
                LOG_TAG = getString(R.string.androidOauthLogTag);
                androidOAuth = new AndroidOAuth(getBaseContext());
        }

        @Override
        public void onResume() {
                super.onResume();
                setupView();
        }

        private void setupView() {

                String accessToken = androidOAuth.getSavedAccessToken(PreferenceManager
                                .getDefaultSharedPreferences(this));

                if (accessToken == null) {
                        // We want to see the unauthenticated layout
                        setContentView(R.layout.foursquareoauthsignin);

                        Button signinButton = (Button) findViewById(R.id.SignInButton);
                        signinButton.setOnClickListener(this);

                } else {

                        // We want to see the authenticated layout
                        setContentView(R.layout.foursquareoauthsignedin);

                        nameValue = (TextView) findViewById(R.id.NameValue);
                        emailValue = (TextView) findViewById(R.id.EmailValue);
                        cityValue = (TextView) findViewById(R.id.CityValue);
                        idValue = (TextView) findViewById(R.id.IdValue);
                        geoValue = (TextView) findViewById(R.id.GeoValue);


                        //Handler needs to come in same thread view is defined ^^
                        handler = new FoursquareResponseHandler();

                        Button clearButton = (Button) findViewById(R.id.ClearButton);
                        clearButton.setOnClickListener(this);

                        Button getProfileButton = (Button) findViewById(R.id.GetProfileButton);
                        getProfileButton.setOnClickListener(this);
                }
        }

        // Updates the screen with new information
        protected void updateProfileUI(JSONObject jObject) {
                try {
                        jObject = jObject.getJSONObject("response").getJSONObject("user");

                        String firstname = jObject.getString("firstName");

                        String lastname = jObject.getString("lastName");
                        String email = jObject.getJSONObject("contact").getString("email");
                        String homecity = jObject.getString("homeCity");
                        String lastknown = jObject.getString("id");

                        nameValue.setText(firstname + " " + lastname);
                        emailValue.setText(email);
                        cityValue.setText(homecity);
                        idValue.setText(lastknown);

                } catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage());
                        e.printStackTrace();
                }

        }

        // Simply converts an http response to a string
        private String responseToString(HttpResponse resp)
                        throws IllegalStateException, IOException {
                BufferedReader in = new BufferedReader(new InputStreamReader(resp
                                .getEntity().getContent()));

                StringBuffer sb = new StringBuffer();

                String line = "";

                while ((line = in.readLine()) != null) {
                        sb.append(line);
                }
                in.close();

                return sb.toString();
        }

        public void onClick(View view) {
                switch (view.getId()) {
                case R.id.SignInButton:
                        this.startActivity(new Intent(this, AndroidOAuthActivity.class));
                        break;
                case R.id.ClearButton:
                		androidOAuth.logout(this);
//                        Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
//                                        .edit();
//                        editor.remove(androidOAuth.getAccessTokenPreferenceName());
//                        editor.commit();
//                        Log.d(LOG_TAG, "Clearing, exiting");
//                        Toast.makeText(this, "Deleted saved access tokens", Toast.LENGTH_LONG)
//                                        .show();
                        setupView();
                        break;
                case R.id.GetProfileButton:
                		
                		StreamRequestListener mSRL = new StreamRequestListener();
                		new AsyncFoursquareRunner(androidOAuth).request("users/self",
                				mSRL);
                		
                        Bundle params = new Bundle();
                        params.putString("ll", Double.toString(MyLocation.getLat()) + "," + Double.toString(MyLocation.getLon()));
                        params.putString("limit", "20");
                        new AsyncFoursquareRunner(androidOAuth).request("venues/search", params,
                                new JSONRequestListener());
                        
                        geoValue.setText(Double.toString(MyLocation.getLat()) + ", " + Double.toString(MyLocation.getLon()));
                        //updateProfileUI(mSRL.returnResponse);
                    
//                        Thread foursquareInfoThread = new FoursquareInfoThread();
//                        foursquareInfoThread.start();
                        break;
                default:
                        return;
                }

        }


        
        private class FoursquareResponseHandler extends Handler {
            public void handleMessage(Message msg) {
                    switch (msg.what) {
                    case USER_PROFILE:
				              try {
				                      Log.d(LOG_TAG, "Response Handler message: " + (String) msg.obj);
				                      updateProfileUI(new JSONObject((String) msg.obj));
				              } catch (JSONException e) {
				                      Log.e(LOG_TAG, e.getCause() + ": " + e.getMessage());
				                      e.printStackTrace();
				              }
                            break;
                    }
            }
    }
        
        public class StreamRequestListener implements RequestListener {
        	//public JSONObject returnResponse;
            public void onComplete(String response, final Object state) {
                
                Log.d(LOG_TAG, "StreamRequestListener Handler message: " + response);
            		
            		// Message and handler model to avoid error:
            		//android.view.ViewRoot$CalledFromWrongThreadException: 
            		//Only the original thread that created a view hierarchy
            		//can touch its views.

                    Message profileMessage = Message.obtain();
                    profileMessage.what = USER_PROFILE;
                    profileMessage.obj = response;
            		handler.obtainMessage(USER_PROFILE, response).sendToTarget();
            		//See FoursquareResponseHandler Above
            		
            		//updateProfileUI(new JSONObject(response));
            		
            }


            public void onFileNotFoundException(FileNotFoundException e,
                                                final Object state) {
                Log.e(LOG_TAG, e.getCause() + ": " + e.getMessage());
            }

            public void onIOException(IOException e, final Object state) {
                Log.e(LOG_TAG, e.getCause() + ": " + e.getMessage());
            }

            public void onMalformedURLException(MalformedURLException e,
                                                final Object state) {
                Log.e(LOG_TAG, e.getCause() + ": " + e.getMessage());
            }

        }
        
        
        public class JSONRequestListener implements RequestListener {

            public void onComplete(String response, final Object state) {
                
            	//******
            	//Handling it here:
            	Log.d("Printed JSON Response: ", response);
            	try {
                    JSONObject obj = Util.parseJson(response);
             
                    Log.w("hope this works for foursquare!!! - ", "dont die please");

                } catch (JSONException e) {
                    Log.e("stream", "JSON Error:" + e.getMessage());
                }
            }
 
            public void onFileNotFoundException(FileNotFoundException e,
                                                final Object state) {
                Log.e("stream", "Resource not found:" + e.getMessage());      
            }

            public void onIOException(IOException e, final Object state) {
                Log.e("stream", "Network Error:" + e.getMessage());      
            }

            public void onMalformedURLException(MalformedURLException e,
                                                final Object state) {
                Log.e("stream", "Invalid URL:" + e.getMessage());            
            }

        }
        
        
}




//// Retrieves foursquare profile information in a seperate thread
//private class FoursquareInfoThread extends Thread {
//
//      public void run() {
//              try {
//
//                      String requestUrl = androidOAuth
//                                      .authenticateUrl(getString(R.string.foursquarev2userself));
//
//                      Log.d(LOG_TAG, "Requesting url: " + requestUrl);
//
//                      HttpGet request = new HttpGet(requestUrl);
//
//                      // sign the request
//
//                      DefaultHttpClient client = new DefaultHttpClient();
//
//                      HttpResponse resp = client.execute(request);
//                      int responseCode = resp.getStatusLine().getStatusCode();
//                      Log.d(LOG_TAG, "Reponse code: " + responseCode);
//
//                      if (responseCode >= 200 && responseCode < 300) {
//
//                              String response = responseToString(resp);
//                              Log.d(LOG_TAG, response);
//
//                              Message profileMessage = Message.obtain();
//                              profileMessage.what = USER_PROFILE;
//                              profileMessage.obj = response;
//
//                              //what is this sending a message to??
//                              handler.obtainMessage(USER_PROFILE, response).sendToTarget();
//
//                      } else {
//                              Log.e(LOG_TAG, "Foursquare error: "
//                                              + responseToString(resp));
//                      }
//
//              } catch (IOException e) {
//                      Log.e(LOG_TAG, e.getMessage() + ": " + e.getCause());
//                      e.printStackTrace();
//              }
//      }
//}

//private class FoursquareResponseHandler extends Handler {
//      public void handleMessage(Message msg) {
//              switch (msg.what) {
//              case USER_PROFILE:
//                      try {
//                              Log.d(LOG_TAG, "Handler message: " + (String) msg.obj);
//                              updateProfileUI(new JSONObject((String) msg.obj));
//                      } catch (JSONException e) {
//                              Log.e(LOG_TAG, e.getCause() + ": " + e.getMessage());
//                              e.printStackTrace();
//                      }
//                      break;
//              }
//      }
//}