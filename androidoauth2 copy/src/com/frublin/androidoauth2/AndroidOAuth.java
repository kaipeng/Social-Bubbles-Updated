package com.frublin.androidoauth2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;


public class AndroidOAuth {

    public static final String TOKEN = "oauth_token";
    public static final String ACCESS_TOKEN = "access_token";


    protected static String BASE_URL = "https://api.foursquare.com/v2/";
	
	// Key and endpoints fields
	private String consumerKey;
	private String consumerSecret;
	private String accessTokenUrl;
	private String authenticationUrl;
	private boolean touchDisplay;

	// Callback field
	private Uri callbackUri;

	// Fields used for obtaining Android preferences
	private String accessTokenPreferenceName;

	// Log_CAT tag to be set
	private String LOG_TAG;
	private Context context;
	
	//Authorize pattern from Foursquare
    private FSDialogListener mAuthFSDialogListener;
    // Used as default activityCode by authorize(). See authorize() below.
    private static final int DEFAULT_AUTH_ACTIVITY_CODE = 32665;
    private static final String LOGIN = "oauth";
    public static final String CANCEL_URI = "androidoauth2://oauthcancel";
    public static final String REDIRECT_URI = "androidoauth2://oauthcallback";

    protected static String DIALOG_BASE_URL =
        "https://m.foursquare.com/";


	public AndroidOAuth(Context context) {
		this.context = context;
		setOAuthPreferences();
	}

	private void setOAuthPreferences() {

		Resources res = context.getResources();

		this.LOG_TAG = res.getString(R.string.androidOauthLogTag);
		this.consumerKey = res.getString(R.string.consumerKey);
		this.consumerSecret = res.getString(R.string.consumerSecret);
		this.accessTokenUrl = res.getString(R.string.accessTokenUrl);
		this.authenticationUrl = res.getString(R.string.authenticationUrl);
		this.callbackUri = Uri.parse(res.getString(R.string.oauthCallbackUri));
		this.touchDisplay = Boolean.parseBoolean(res.getString(R.string.touch));
		this.accessTokenPreferenceName = res
				.getString(R.string.accessTokenPreferenceName);
	}

	public Uri getCallbackUri() {
		return callbackUri;
	}

	public String getAuthenticationURL() {

		String touchParam = "";

		if (touchDisplay) {
			touchParam = "&display=touch";
		}

		return authenticationUrl + "?" + "client_id=" + consumerKey
				+ "&response_type=code" + "&redirect_uri=" + callbackUri
				+ touchParam;
	}

	public String getAccessTokenUrl(String code) {

		String touchParam = "";

		if (touchDisplay) {
			touchParam = "&display=touch";
		}

		return accessTokenUrl + "?client_id=" + consumerKey + "&client_secret="
				+ consumerSecret + "&grant_type=authorization_code"
				+ "&redirect_uri=" + callbackUri + "&code=" + code + touchParam;
	}

	public String getAccessTokenPreferenceName() {
		return accessTokenPreferenceName;
	}

	public void saveAccessToken(String accessToken) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		saveAccessToken(preferences, accessToken);
	}

	public void saveAccessToken(SharedPreferences preferences,
			String accessToken) {
		// null means to clear the old values
		SharedPreferences.Editor editor = preferences.edit();

		editor.putString(getAccessTokenPreferenceName(), accessToken);
		Log.d(LOG_TAG, "Saving OAuth Token: " + accessToken);

		editor.commit();
	}

	public String getSavedAccessToken(SharedPreferences preferences) {

		String savedAccessToken = preferences.getString(
				getAccessTokenPreferenceName(), null);

		if (savedAccessToken == null) {
			return null;
		}

		return savedAccessToken;
	}

	public String getSavedAccessToken() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return getSavedAccessToken(preferences);
	}

	public void removeSavedAccessToken() {

		Log.d(LOG_TAG, "Clearing OAuth Token");

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.Editor editor = preferences.edit();
		editor.remove(accessTokenPreferenceName);
		editor.commit();
	}
	
	public void logout(Context context){
        @SuppressWarnings("unused")
        CookieSyncManager cookieSyncMngr =
            CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        Log.d(LOG_TAG, "Removing tokens.");

        
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
        .edit();
        editor.remove(this.getAccessTokenPreferenceName());
        editor.commit();
        Log.d(LOG_TAG, "Clearing, exiting");
        Toast.makeText(context, "Deleted saved access tokens", Toast.LENGTH_SHORT)
        	.show();
	}

	public String authenticateUrl(String requestUrl) {

		String savedAccessToken = getSavedAccessToken();

		if (savedAccessToken == null) {

			Log.w(LOG_TAG,
					"Attempting to authenticate a URL without access token.  Using consumer info instead");
			return requestUrl + "&client_id=" + consumerKey + "&client_secret="
					+ consumerSecret;
		} else {
			if(requestUrl.contains("&")){
				Log.d("   -   -    -   -  ", "Using AMPERSAND");
				return requestUrl + "&oauth_token=" + savedAccessToken;
			}
			else
				return requestUrl + "?oauth_token=" + savedAccessToken;
		}
	}
	
	/**
     * Make a request to the Foursquare Graph API without any parameters.
     *
     * See http://developers.facebook.com/docs/api
     *
     * Note that this method blocks waiting for a network response, so do not
     * call it in a UI thread.
     *
     * @param graphPath
     *            Path to resource in the Foursquare graph, e.g., to fetch data
     *            about the currently logged authenticated user, provide "me",
     *            which will fetch http://graph.facebook.com/me
     * @throws IOException
     * @throws MalformedURLException
     * @return JSON string representation of the response
     */
    public String request(String graphPath)
            throws MalformedURLException, IOException {
        return request(graphPath, new Bundle(), "GET");
    }

    /**
     * Make a request to the Foursquare Graph API with the given string parameters
     * using an HTTP GET (default method).
     *
     * See http://developers.facebook.com/docs/api
     *
     * Note that this method blocks waiting for a network response, so do not
     * call it in a UI thread.
     *
     * @param graphPath
     *            Path to resource in the Foursquare graph, e.g., to fetch data
     *            about the currently logged authenticated user, provide "me",
     *            which will fetch http://graph.facebook.com/me
     * @param parameters
     *            key-value string parameters, e.g. the path "search" with
     *            parameters "q" : "facebook" would produce a query for the
     *            following graph resource:
     *            https://graph.facebook.com/search?q=facebook
     * @throws IOException
     * @throws MalformedURLException
     * @return JSON string representation of the response
     */
    public String request(String graphPath, Bundle parameters)
            throws MalformedURLException, IOException {
        return request(graphPath, parameters, "GET");
    }

    /**
     * Synchronously make a request to the Foursquare Graph API with the given
     * HTTP method and string parameters. Note that binary data parameters
     * (e.g. pictures) are not yet supported by this helper function.
     *
     * See http://developers.facebook.com/docs/api
     *
     * Note that this method blocks waiting for a network response, so do not
     * call it in a UI thread.
     *
     * @param graphPath
     *            Path to resource in the Foursquare graph, e.g., to fetch data
     *            about the currently logged authenticated user, provide "me",
     *            which will fetch http://graph.facebook.com/me
     * @param params
     *            Key-value string parameters, e.g. the path "search" with
     *            parameters {"q" : "facebook"} would produce a query for the
     *            following graph resource:
     *            https://graph.facebook.com/search?q=facebook
     * @param httpMethod
     *            http verb, e.g. "GET", "POST", "DELETE"
     * @throws IOException
     * @throws MalformedURLException
     * @return JSON string representation of the response
     */
    public String request(String graphPath, Bundle params, String httpMethod)
            throws FileNotFoundException, MalformedURLException, IOException {
        params.putString("format", "json");
        if (getSavedAccessToken() != null) {
            params.putString(TOKEN, getSavedAccessToken());
        }
        	
        String url = BASE_URL + graphPath;
        
        return Util.openUrl(url, httpMethod, params);
    }
    
    /**
     * Default authorize method. Grants only basic permissions.
     *
     * See authorize() below for @params.
     */
    public void authorize(Activity activity, final FSDialogListener listener) {
        authorize(activity, new String[] {}, DEFAULT_AUTH_ACTIVITY_CODE,
                listener);
    }

    /**
     * Authorize method that grants custom permissions.
     *
     * See authorize() below for @params.
     */
    public void authorize(Activity activity, String[] permissions,
            final FSDialogListener listener) {
        authorize(activity, permissions, DEFAULT_AUTH_ACTIVITY_CODE, listener);
    }

    /**
     * Full authorize method.
     *
     * Starts either an Activity or a dialog which prompts the user to log in to
     * Foursquare and grant the requested permissions to the given application.
     *
     * This method will, when possible, use Foursquare's single sign-on for
     * Android to obtain an access token. This involves proxying a call through
     * the Foursquare for Android stand-alone application, which will handle the
     * authentication flow, and return an OAuth access token for making API
     * calls.
     *
     * Because this process will not be available for all users, if single
     * sign-on is not possible, this method will automatically fall back to the
     * OAuth 2.0 User-Agent flow. In this flow, the user credentials are handled
     * by Foursquare in an embedded WebView, not by the client application. As
     * such, the dialog makes a network request and renders HTML content rather
     * than a native UI. The access token is retrieved from a redirect to a
     * special URL that the WebView handles.
     *
     * Note that User credentials could be handled natively using the OAuth 2.0
     * Username and Password Flow, but this is not supported by this SDK.
     *
     * See http://developers.facebook.com/docs/authentication/ and
     * http://wiki.oauth.net/OAuth-2 for more details.
     *
     * Note that this method is asynchronous and the callback will be invoked in
     * the original calling thread (not in a background thread).
     *
     * Also note that requests may be made to the API without calling authorize
     * first, in which case only public information is returned.
     *
     * IMPORTANT: Note that single sign-on authentication will not function
     * correctly if you do not include a call to the authorizeCallback() method
     * in your onActivityResult() function! Please see below for more
     * information. single sign-on may be disabled by passing FORCE_DIALOG_AUTH
     * as the activityCode parameter in your call to authorize().
     *
     * @param activity
     *            The Android activity in which we want to display the
     *            authorization dialog.
     * @param applicationId
     *            The Foursquare application identifier e.g. "350685531728"
     * @param permissions
     *            A list of permissions required for this application: e.g.
     *            "read_stream", "publish_stream", "offline_access", etc. see
     *            http://developers.facebook.com/docs/authentication/permissions
     *            This parameter should not be null -- if you do not require any
     *            permissions, then pass in an empty String array.
     * @param activityCode
     *            Single sign-on requires an activity result to be called back
     *            to the client application -- if you are waiting on other
     *            activities to return data, pass a custom activity code here to
     *            avoid collisions. If you would like to force the use of legacy
     *            dialog-based authorization, pass FORCE_DIALOG_AUTH for this
     *            parameter. Otherwise just omit this parameter and Foursquare
     *            will use a suitable default. See
     *            http://developer.android.com/reference/android/
     *              app/Activity.html for more information.
     * @param listener
     *            Callback interface for notifying the calling application when
     *            the authentication dialog has completed, failed, or been
     *            canceled.
     */
    public void authorize(Activity activity, String[] permissions,
            int activityCode, final FSDialogListener listener) {

        mAuthFSDialogListener = listener;

        startDialogAuth(activity, permissions);
    }
    
    /**
     * Internal method to handle dialog-based authentication backend for
     * authorize().
     *
     * @param activity
     *            The Android Activity that will parent the auth dialog.
     * @param applicationId
     *            The Foursquare application identifier.
     * @param permissions
     *            A list of permissions required for this application. If you do
     *            not require any permissions, pass an empty String array.
     */
	private void startDialogAuth(Activity activity, String[] permissions) {
        Bundle params = new Bundle();
        if (permissions.length > 0) {
            params.putString("scope", TextUtils.join(",", permissions));
        }
        CookieSyncManager.createInstance(activity);
        dialog(activity, LOGIN, params, new FSDialogListener() {

            public void onComplete() {
                // ensure any cookies set by the dialog are saved
                CookieSyncManager.getInstance().sync();

                if (isSessionValid()) {
                    Log.d("Foursquare-authorize", "Login Success! access_token="
                            + getSavedAccessToken());
                    mAuthFSDialogListener.onComplete();
                } else {
                    mAuthFSDialogListener.onFoursquareError(new FoursquareError(
                                    "Failed to receive access token."));
                }
            }

            public void onError(DialogError error) {
                Log.d("Foursquare-authorize", "Login failed: " + error);
                mAuthFSDialogListener.onError(error);
            }

            public void onFoursquareError(FoursquareError error) {
                Log.d("Foursquare-authorize", "Login failed: " + error);
                mAuthFSDialogListener.onFoursquareError(error);
            }

            public void onCancel() {
                Log.d("Foursquare-authorize", "Login canceled");
                mAuthFSDialogListener.onCancel();
            }
        });
    }

    
    /**
     * Generate a UI dialog for the request action in the given Android context.
     *
     * Note that this method is asynchronous and the callback will be invoked in
     * the original calling thread (not in a background thread).
     *
     * @param context
     *            The Android context in which we will generate this dialog.
     * @param action
     *            String representation of the desired method: e.g. "login",
     *            "stream.publish", ...
     * @param listener
     *            Callback interface to notify the application when the dialog
     *            has completed.
     */
    public void dialog(Context context, String action,
            FSDialogListener listener) {
        dialog(context, action, new Bundle(), listener);
    }

    /**
     * Generate a UI dialog for the request action in the given Android context
     * with the provided parameters.
     *
     * Note that this method is asynchronous and the callback will be invoked in
     * the original calling thread (not in a background thread).
     *
     * @param context
     *            The Android context in which we will generate this dialog.
     * @param action
     *            String representation of the desired method: e.g. "feed" ...
     * @param parameters
     *            String key-value pairs to be passed as URL parameters.
     * @param listener
     *            Callback interface to notify the application when the dialog
     *            has completed.
     */
    public void dialog(Context context, String action, Bundle parameters,
            final FSDialogListener listener) {

        String url;

        if (action.equals(LOGIN)) {
    		url = getAuthenticationURL();
        } else {
        	 url = DIALOG_BASE_URL;
        	 
//        	 + action;
//             
//             parameters.putString("display", "touch");
//             parameters.putString("redirect_uri", getCallbackUri());
//            if (isSessionValid()) {
//                parameters.putString(TOKEN, getSavedAccessToken());
//            }
             //String url = endpoint + "?" + Util.encodeUrl(parameters);

        }

        
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            Util.showAlert(context, "Error",
                    "Application requires permission to access the Internet");
        } else {
            new FsDialog(context, url, listener, this).show();
        }
    }
    
    /**
     * @return boolean - whether this object has an non-expired session token
     */
    public boolean isSessionValid() {
        return (getSavedAccessToken() != null);
    }


    /**
     * Callback interface for dialog requests.
     *
     */
    public static interface FSDialogListener {

        /**
         * Called when a dialog completes.
         *
         * Executed by the thread that initiated the dialog.
         *
         * @param values
         *            Key-value string pairs extracted from the response.
         */
        public void onComplete();

        /**
         * Called when a Foursquare responds to a dialog with an error.
         *
         * Executed by the thread that initiated the dialog.
         *
         */
        public void onFoursquareError(FoursquareError e);

        /**
         * Called when a dialog has an error.
         *
         * Executed by the thread that initiated the dialog.
         *
         */
        public void onError(DialogError e);

        /**
         * Called when a dialog is canceled by the user.
         *
         * Executed by the thread that initiated the dialog.
         *
         */
        public void onCancel();

    }
}
