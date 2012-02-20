package com.social.bubbles.fsquery;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.LinkedList;

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
import com.social.bubbles.DownloadManager;
import com.social.bubbles.Main;
import com.social.bubbles.fsquery.FoursquareVenue.FoursquareHereNowResponseClass.FoursquareHereNowResponse;
import com.social.bubbles.fsquery.FoursquareVenue.FoursquareHereNowResponseClass.FoursquareHereNowResponse.FoursquareHereNow;
import com.social.bubbles.fsquery.FoursquareVenue.FoursquareHereNowResponseClass.FoursquareHereNowResponse.FoursquareHereNow.FoursquareCheckin;
import com.social.bubbles.socialbubbles3.MySystem;


public class FoursquarePostResponseClass extends FoursquareObject{
	//Response: {"meta":{"code":200},"response":{"todo":{"id":"4dafc2f00cb6ead7fdd5ead7","createdAt":1303364336,"tip":{"id":"4dafc2f00cb6ead7fdd5ead6","createdAt":1303364336,"text":"Anyone want to join me at Zoo Bar? And they have a special: Mayor gets to take off his shoes and yell at anyone he wants. - Posted by SocialBubbles","status":"todo","todo":{"count":1},"done":{"count":0},"venue":{"id":"4cdb73bbd6656a31ace8fc3e","name":"Zoo Bar","contact":{},"location":{"address":"209 S 33rd St #3E5-7","crossStreet":"33rd and Walnut St","city":"Philadelphia","state":"PA","postalCode":"19104","country":"USA","lat":39.95193294760124,"lng":-75.18930315971375},"categories":[{"id":"4bf58dd8d48988d1a0941735","name":"College Classroom","pluralName":"College Classrooms","icon":"https://foursquare.com/img/categories/education/default.png","parents":["Colleges & Universities"],"primary":true},{"id":"4bf58dd8d48988d1d4941735","name":"Speakeasy","pluralName":"Speakeasies","icon":"https://foursquare.com/img/categories/nightlife/secretbar.png","parents":["Nightlife Spots"]}],"verified":true,"stats":{"checkinsCount":55,"usersCount":7},"todos":{"count":4}},"user":{"id":"6012607","firstName":"kai","lastName":"peng","photo":"https://playfoursquare.s3.amazonaws.com/userpix_thumbs/XES5E4WE2LF13PY5.jpg","gender":"male","homeCity":"Philadelphia, PA","relationship":"self"}}}}}
	
	FoursquarePostResponse response;

	public boolean isTodoResponse(){

		if(response!=null)
			return (response.todo!=null);
		return false;
	}
	public boolean isUserResponse(){
		if(response!=null)
			return (response.user!=null);
		return false;
	}
	public boolean isCheckinResponse(){
		if(response!=null)
			return (response.checkin!=null);
		return false;
	}
	public FoursquareTodo getTodo(){
		return response.todo;
	}
	public FoursquareUser getUser(){
		return response.user;
	}
	public FoursquareCheckin getCheckin(){
		return response.checkin;
	}
	
	@Override
	public String getDescription(){
		if(isTodoResponse()){
			return ("Todo at " + getTodo().getVenue().getName());
		}
		else if(isUserResponse()){
			return ("Request sent to " + getUser().getFullName());
		}
		else if(isCheckinResponse()){
			String response = "Checked into " + getCheckin().venue.getName();
			return (response);
		}
		return " ";
	}

	
	public static class FoursquarePostResponse extends FoursquareObject{
		FoursquareTodo todo;
		FoursquareUser user;
		FoursquareCheckin checkin;
	}
}
