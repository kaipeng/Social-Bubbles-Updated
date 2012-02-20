package com.social.bubbles.fsquery;

import java.util.LinkedList;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import com.google.gson.Gson;
import com.social.bubbles.fsquery.FoursquareVenue.FoursquareHereNowResponseClass;

/**
 * FoursquareVenueSearch retrieves a lot of data with the venue search
 * operations include:
 * - getting venues around user
 * - getting trending venues around user
 * - getting people checked into venues around user
 * @author geoffrey
 *
 */
public class FoursquareVenueSearch extends FoursquareSearch {
	private FoursquareVenueResponse response;
	
	/**
	 * instantiate a search with only the current latitude and longitude 
	 * @param latitude, longitude
	 */
	
	
	public FoursquareVenueSearch(Context context, double latitude, double longitude){
		super(context);
		String query = "venues/search";
		Bundle params = new Bundle();
		params.putString("ll",latitude+","+longitude);
		params.putString("limit", "20");

		query = this.queryFoursquare(query, params);
		if (query!=""){
			try{
			response = new Gson().fromJson(query, FoursquareVenueResponseClass.class).response;
			}catch(Exception e){e.printStackTrace();}
		}
		Log.d("FoursquareQuery : FoursquareVenueSearch Result ", query);
	}
	
	
	
	//getters
	public LinkedList<FoursquareVenue> getVenuesForCheckin(){
		if (response==null){
			return new LinkedList<FoursquareVenue>();
		}
		return response.getCheckinVenues();
	}
	
	
	public LinkedList<FoursquareVenue> getVenuesAroundUser(){
		if (response==null){
			return new LinkedList<FoursquareVenue>();
		}
		return response.getAllVenues();
	}
	
	public LinkedList<FoursquareVenue> getTrendingVenuesAroundUser(){
		if (response==null){
			return new LinkedList<FoursquareVenue>();
		}
		return response.getTrendingVenues();
	}

	
	public LinkedList<FoursquareUser> getUsersAroundUser(){
		LinkedList<FoursquareVenue> venuesAroundUser = getVenuesAroundUser();
		if (venuesAroundUser==null){
			return new LinkedList<FoursquareUser>();
		}
		LinkedList<FoursquareUser> users = new LinkedList<FoursquareUser>();
		for (FoursquareVenue venue : venuesAroundUser){
			users.addAll(getUsersAtVenue(venue));
		}
		return users;
	}
	

	
	
	
	private LinkedList<FoursquareUser> getUsersAtVenue(FoursquareVenue venue){
		if (venue==null || venue.hereNow.count == 0){
			return new LinkedList<FoursquareUser>();
		}
		String query = "venues/"+venue.getId()+"/herenow";
		String response = this.queryFoursquare(query);
		Log.d("FoursquareQuery : GetUsersAtVenue Result ", response);

		LinkedList<FoursquareUser> users = new LinkedList<FoursquareUser>();
		try{
			FoursquareHereNowResponseClass fsHN = new Gson().fromJson(response, FoursquareHereNowResponseClass.class);
			if(fsHN==null || fsHN.response ==null || fsHN.response.hereNow==null)
				return users;
			LinkedList<FoursquareUser> hereNow = fsHN.response.hereNow.getUsers();
			
			Log.d("FoursquareQuery : GetUsersAtVenue numbers = ", ""+hereNow.size());
			if(fsHN != null && hereNow!=null){
				for(FoursquareUser aUser: hereNow){
					Log.d("FoursquareQuery : NEARBY USER ADDED ", ""+aUser.getFullName());
	
					aUser.setCurrentCheckin(venue);
					users.add(aUser);
				}}
		}catch(Exception e){
			Log.d("FoursquareQuery: ", "Error in getUsersAtVenue");
			e.printStackTrace();
		}
		return users;
	}
}
