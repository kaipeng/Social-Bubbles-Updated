package com.social.bubbles.fsquery;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

/**
import android.content.Context;
import android.util.Log;
**/

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.social.bubbles.fsquery.FoursquareRecentResponseClass.FoursquareRecentResponse;
import com.social.bubbles.fsquery.FoursquareTodoResponseClass.FoursquareTodoResponse;
import com.social.bubbles.fsquery.FoursquareVenue.FoursquareHereNowResponseClass.FoursquareHereNowResponse.FoursquareHereNow.FoursquareCheckin;

public class FoursquareRecentSearch extends FoursquareSearch {
	private FoursquareRecentResponse response;
	
	
	public FoursquareRecentSearch(Context context){
		super(context);
		String query = "checkins/recent";
		Bundle params = new Bundle();
		params.putString("limit", "8");
		query = this.queryFoursquare(query, params);
		
		FoursquareRecentResponseClass responseClass = null;
		try{
		 responseClass = new Gson().fromJson(query,FoursquareRecentResponseClass.class);
		}catch(Exception e){e.printStackTrace();}
		Log.d("FoursquareQuery : FoursquareRecentsSearch Result ", query);

		if(responseClass!=null){
			response = responseClass.response;
		}
		else{
			response = null;
		}
		
	}
	
	
	public LinkedList<FoursquareUser> getRecents(){
		if(response!=null && response.recent!=null){
			LinkedList<FoursquareCheckin> checkins = new LinkedList<FoursquareCheckin>(Arrays.asList(response.recent));
			LinkedList<FoursquareUser> users = new LinkedList<FoursquareUser>();
			for(FoursquareCheckin aCheckin: checkins){
				FoursquareUser aUser = aCheckin.user;
				
				//Get Friend (wholistic) and replace to get Contact Info, etc
				String query2 = "users/"+aUser.getId();
				try {
					query2 = FoursquareSearch.fs.request(query2);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				FoursquareUser response2 = null;
				if (query2!=""){
					try{
					response2 = new Gson().fromJson(query2, FoursquareUserResponseClass.class).response.user;
					}catch(Exception e){};
				}
				Log.d("FoursquareQuery : FoursquareUser Contact Search Result ", query2);
				aUser=response2;
		
				if(aCheckin!=null && aUser!=null){
					aUser.setCurrentCheckin(aCheckin.venue);
					aUser.setLastActivity(new Date(aCheckin.getCreatedAt()));
				}
				
				users.add(aUser);
			}
			return users;
		}
		return new LinkedList<FoursquareUser>();
	}
	
	//not used
	public LinkedList<FoursquareTodo> getTodosOfRecents(){
		LinkedList<FoursquareTodo> todos = new LinkedList<FoursquareTodo>();
		
		Log.d("FoursquareRecentsSearch :  Parsing through list of Recents: ", ""+getRecents().size());

		for (FoursquareUser aRecent : getRecents()){
			String query = "users/" + aRecent.getId() + "/todos";
			Bundle params = new Bundle();
			params.putString("sort", "recent");
			params.putString("limit", "3");

			query = this.queryFoursquare(query, params);
			FoursquareTodoResponse response = null;
			try{
			response = new Gson().fromJson(query,FoursquareTodoResponseClass.class).response;
			}catch(Exception e){};
			
			Log.d("FoursquareQuery : FoursquareTodoSearch Result ", query);
			if(response!=null){
				for(FoursquareTodo todo: Arrays.asList(response.getTodos())){
					todos.add(todo);
				}
			}
		}
		return todos;
	}
	
	
}
