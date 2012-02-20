package com.social.bubbles.fsquery;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.bubbles.location.MyLocation;
import com.frublin.androidoauth2.AndroidOAuth;
import com.social.bubbles.Bubble;
import com.social.bubbles.Settings;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * 
 * @author geoffrey
 * operations supported
 * - get todos of people in a wide radius
 * - get trending venues around user
 * - get people checked into places around user
 * - get specials around user
 */
public class FoursquareQuery {
	private static final String PICTURE_SIZE = "large";
	public static FoursquareSearch FOURSQUARE_SEARCH;
	
	private AndroidOAuth fs;
	private Context context;
	private FoursquareVenueSearch venueSearch;
	private FoursquareFriendSearch friendSearch;
	private FoursquareSpecialsSearch specialsSearch;
	public static Activity mainActivity;
	
	public FoursquareQuery(Activity context, AndroidOAuth fs){
		this.mainActivity = context;
		this.fs = fs;
		this.context = context;
		FoursquareSearch.initAndroidOAuth(fs);
		FoursquareQuery.FOURSQUARE_SEARCH = new FoursquareSearch(context);
	}
	
	
	public LinkedList<FoursquareVenue> getFoursquareVenues(){
		LinkedList<FoursquareVenue> fsVenues = new LinkedList<FoursquareVenue>();

		
		LinkedList<FoursquareSpecial> fsSpecials = new LinkedList<FoursquareSpecial>();
		LinkedList<FoursquareVenue> fsTrendingVenues = new LinkedList<FoursquareVenue>();
	
		if(Settings.Bubble.DISPLAY_FOURSQUARE_TRENDINGPLACES){
			fsTrendingVenues = getFoursquareTrendingVenues();
		}
		if(Settings.Bubble.DISPLAY_FOURSQUARE_SPECIALS){
			fsSpecials = getFoursquareSpecials();
		}
		
		for(FoursquareVenue venue: fsTrendingVenues){
			venue.setLastActivity(new Date());
			fsVenues.add(venue);
		}
		
		//consolidate specials into venues
		for(FoursquareSpecial special: fsSpecials){
			FoursquareVenue specialVenue = special.getVenue();
			boolean exists = false;
			for(FoursquareVenue existingVenue: fsVenues){
				//if it is already existing, remove special and add to existing venue
				if(existingVenue.getId().equals(specialVenue.getId())){
					existingVenue.addSpecial(special);
					existingVenue.setSpecialImageUrl("special_icon.png");
					existingVenue.setColor(Bubble.ORANGE);
					//existingVenue.setLastActivity(new Date(new Date().getTime()-1000*60*15 / existingVenue.getSpecials().size()));
					existingVenue.setLastActivity(new Date());

					exists = true;
					Log.d("FoursquareQuery : SpecialSearch - Venue and IS Trending: ", special.getDescription());

					break;
				}
			}
			//if it doesn't exist, 
			if(!exists){
				Log.d("FoursquareQuery : SpecialSearch - Venue Isn't Trending: ", special.getDescription());

				specialVenue.addSpecial(special);
				specialVenue.setType(Bubble.FS_SPECIAL);
				specialVenue.setTrending(false);
				specialVenue.setSpecialImageUrl("special_icon.png");
				specialVenue.setVenueImageUrl(specialVenue.getMainCategory().icon);
				specialVenue.setDescription("@" + specialVenue.getName() + ": " + special.getMessage().substring(0, Math.min(45, special.getMessage().length())) );
				specialVenue.setCoordinates(specialVenue.getLatitude(), specialVenue.getLongitude());
				specialVenue.setColor(Bubble.ORANGE);
				//specialVenue.setLastActivity(new Date(new Date().getTime()-1000*60*15 / specialVenue.getSpecials().size()));
				specialVenue.setLastActivity(new Date());

				fsVenues.add(specialVenue);
			}
		}
		for(FoursquareVenue av: fsVenues){
			Log.d("FoursquareQuery : TrendingSearch and SpecialSearch: ", av.getDescription());
			av.setLastActivity(new Date());
			av.setGeo();
		}

		return fsVenues;
	}

	public LinkedList<FoursquareUser> getFoursquareUsers(){
		venueSearch = new FoursquareVenueSearch(context, MyLocation.getLat(),MyLocation.getLon());

		HashMap<String, FoursquareUser> fsUsers = new HashMap<String, FoursquareUser>();
		FoursquareRecentSearch recentSearch = new FoursquareRecentSearch(context);
		
		LinkedList<FoursquareUser> recentCheckins = recentSearch.getRecents();
		
		
		LinkedList<FoursquareUser> usersAroundUser = venueSearch.getUsersAroundUser();
		for(FoursquareUser aUser : usersAroundUser){			//all nearbyFsUsers & set time to last checkin
			aUser.setType(Bubble.FS_NEARBY_CHECKIN);
			aUser.setImageUrl(aUser.getPhoto());
			aUser.setVenueImageUrl(aUser.getCurrentCheckin().getMainCategory().icon);
			aUser.setDescription(aUser.getFullName() + " @ " + aUser.getCurrentCheckin().getName());
			aUser.setCoordinates(aUser.getCurrentCheckin().getLatitude(), aUser.getCurrentCheckin().getLongitude());
			
			//aUser.setColor(Bubble.GREEN);
			fsUsers.put(aUser.getId(), aUser);
		}
		
		for(FoursquareUser aUser : recentCheckins){			//all nearbyFsUsers & set time to last checkin
			try{
			aUser.setType(Bubble.FS_RECENT_CHECKIN);
			aUser.setImageUrl(aUser.getPhoto());
			if(aUser.getCurrentCheckin()!=null && aUser.getCurrentCheckin().getMainCategory().icon!=null);
				aUser.setVenueImageUrl(aUser.getCurrentCheckin().getMainCategory().icon);
			aUser.setDescription(aUser.getFullName() + " @ " + aUser.getCurrentCheckin().getName());
			aUser.setCoordinates(aUser.getCurrentCheckin().getLatitude(), aUser.getCurrentCheckin().getLongitude());
			aUser.setColor(Bubble.GREEN);

			if(fsUsers.containsKey(aUser.getId()))
				fsUsers.remove(aUser.getId());
			fsUsers.put(aUser.getId(), aUser);
			}catch(NullPointerException e){}
		}
		
		LinkedList<FoursquareUser> getFoursquareTodos = getFoursquareTodos();
		for(FoursquareUser aTodoUser : getFoursquareTodos){
			if(fsUsers.containsKey(aTodoUser.getId())){
				fsUsers.get(aTodoUser.getId()).setTodos(aTodoUser.getTodos());
				fsUsers.get(aTodoUser.getId()).setTodoImageUrl(aTodoUser.getTodos().getFirst().getImageUrl());
				fsUsers.get(aTodoUser.getId()).setColor(Bubble.YELLOW);

				if(fsUsers.get(aTodoUser.getId()).getLastActivity().before(aTodoUser.getTodos().getFirst().getLastActivity()))
					fsUsers.get(aTodoUser.getId()).setLastActivity(aTodoUser.getTodos().getFirst().getLastActivity());
				Log.d("FoursquareQuery : TodoSearch: ", "A User with Todos Already Exists! Adding to original user: " + aTodoUser.getFullName());

			}else{
				Log.d("FoursquareQuery : TodoSearch: ", "A User with Todos DOESNOT Exists! Adding new user: "  + aTodoUser.getFullName());

				aTodoUser.setType(Bubble.FS_TODO);
				aTodoUser.setImageUrl(aTodoUser.getPhoto());
				aTodoUser.setDescription(aTodoUser.getFullName() + " Todo @ " + aTodoUser.getTodos().getFirst().getVenue().getName());
				aTodoUser.setCoordinates(aTodoUser.getTodos().getFirst().getVenue().getLatitude(), aTodoUser.getTodos().getFirst().getVenue().getLatitude());
				aTodoUser.setTodoImageUrl(aTodoUser.getTodos().getFirst().getVenue().getMainCategory().icon);
				aTodoUser.setLastActivity(aTodoUser.getTodos().getFirst().getLastActivity());
				aTodoUser.setColor(Bubble.YELLOW);

				fsUsers.put(aTodoUser.getId(), aTodoUser);
			}
		}
		
		for(FoursquareUser au : fsUsers.values()){
			au.setGeo();
		}
		return resizePictures(fsUsers.values());
	}

	public LinkedList<FoursquareVenue> getFoursquareTrendingVenues(){
		venueSearch = new FoursquareVenueSearch(context, MyLocation.getLat(),MyLocation.getLon());
		LinkedList<FoursquareVenue> trendingVenuesAroundUser = venueSearch.getTrendingVenuesAroundUser();
		
		for(FoursquareVenue aVenue : trendingVenuesAroundUser){			//all trending places
			aVenue.setType(Bubble.FS_TRENDING_VENUE);
			aVenue.setTrending(true);
			aVenue.setImageUrl(aVenue.getMainCategory().icon);
			aVenue.setVenueImageUrl(aVenue.getMainCategory().icon);
			aVenue.setDescription(aVenue.getName() + " w/ " + aVenue.hereNow.count + " here now.");
			aVenue.setCoordinates(aVenue.getLatitude(), aVenue.getLongitude());
			aVenue.setLastActivity(new Date(new Date().getTime()-1000*60*30 / (aVenue.getHereNow()+1)));
			aVenue.setColor(Bubble.BLUE);

			if(aVenue.hasSpecial())
				aVenue.setSpecialImageUrl("special_icon.png");
		}
		
		//GET for checkin: NEAREST PLACE and Favorite place
		for(FoursquareVenue aVenue : venueSearch.getVenuesForCheckin()){
			aVenue.setType(Bubble.FS_NEARBY_VENUE);
			aVenue.setImageUrl(aVenue.getMainCategory().icon);
			aVenue.setVenueImageUrl(aVenue.getMainCategory().icon);
			aVenue.setDescription(aVenue.getName() +" is closest to you.");
			aVenue.setCoordinates(aVenue.getLatitude(), aVenue.getLongitude());
			aVenue.setLastActivity(new Date(new Date().getTime()-1000*60*20 / (aVenue.getHereNow()+1)));
			aVenue.setColor(Bubble.RED);
			
			trendingVenuesAroundUser.add(aVenue);
		}
		
		return trendingVenuesAroundUser;
	}
	
	public LinkedList<FoursquareSpecial> getFoursquareSpecials(){
		specialsSearch = new FoursquareSpecialsSearch(context, MyLocation.getLat(), MyLocation.getLon());

		LinkedList<FoursquareSpecial> specials = specialsSearch.getSpecials();
		for(FoursquareSpecial aSpecial : specials){			//all specials
			aSpecial.setType(Bubble.FS_SPECIAL);
			aSpecial.setSpecialImageUrl(aSpecial.getVenue().getMainCategory().icon);
			aSpecial.setDescription("@" + aSpecial.getVenue().getName() + ": " + aSpecial.getMessage().substring(0, Math.min(45, aSpecial.getMessage().length())) );
			aSpecial.setCoordinates(aSpecial.getVenue().getLatitude(), aSpecial.getVenue().getLongitude());
			aSpecial.setColor(Bubble.ORANGE);
			Log.d("FoursquareQuery : SpecialsSearch - Adding Special at: ", aSpecial.getVenue().getName());

			//if(aSpecial.getSpecialType().equals("mayor") && I am not mayor)
		}
		return (specials);
	}
	
	public LinkedList<FoursquareUser> getFoursquareTodos(){
		friendSearch = new FoursquareFriendSearch(context);
		LinkedList<FoursquareUser> todosOfFriends = friendSearch.getTodosOfFriends();
		Log.d("FoursquareQuery : TodoSearch - Friend Search Complete: ", "");

		return (todosOfFriends);
	}
	
	private LinkedList<FoursquareUser> resizePictures(Collection<FoursquareUser> list){
		for(FoursquareObject a : list){	//adjust all picture sizes
			if(PICTURE_SIZE.equalsIgnoreCase("normal")){
			}
			else if(PICTURE_SIZE.equalsIgnoreCase("large")){
				a.setImageUrl(a.getImageUrl().replace("_thumbs", ""));
			}
			else{
			}
			Log.d("FoursquareQuery : Object Type and picture URL ", a.getType() + " " + a.getImageUrl());
		}
		return new LinkedList<FoursquareUser>(list);
	}
	
	public boolean isSessionValid(){
		return fs.isSessionValid();
	}
	
//	public LinkedList<FoursquareObject> getFoursquareNearbyData(){
//	venueSearch = new FoursquareVenueSearch(context, MyLocation.getLat(),MyLocation.getLon());
//
//	LinkedList<FoursquareUser> usersAroundUser = venueSearch.getUsersAroundUser();
//	LinkedList<FoursquareVenue> trendingVenuesAroundUser = venueSearch.getTrendingVenuesAroundUser();
//
//	LinkedList<FoursquareObject> fsObjects = new LinkedList<FoursquareObject>();
//
//	
//	for(FoursquareUser aUser : usersAroundUser){			//all nearbyFsUsers & set time to last checkin
//		aUser.setType(Bubble.FS_NEARBY_CHECKIN);
//		aUser.setImageUrl(aUser.getPhoto());
//		aUser.setDescription(aUser.getFullName() + " @ " + aUser.getCurrentCheckin().getName());
//		aUser.setCoordinates(aUser.getCurrentCheckin().getLatitude(), aUser.getCurrentCheckin().getLongitude());
//		
//		fsObjects.add(aUser);
//	}
//	fsObjects=resizePictures(fsObjects);
//	for(FoursquareVenue aVenue : trendingVenuesAroundUser){			//all trending places
//		aVenue.setType(Bubble.FS_TRENDING_VENUE);
//		aVenue.setImageUrl(aVenue.getMainCategory().icon);
//		aVenue.setDescription(aVenue.getName() + " w/ " + aVenue.hereNow.count + " here now.");
//		aVenue.setCoordinates(aVenue.getLatitude(), aVenue.getLongitude());
//
//		fsObjects.add(aVenue);
//	}
//	
//	return (fsObjects);
//}
}
