package com.social.bubbles.fsquery;

import java.util.Date;
import java.util.LinkedList;

	public  class FoursquareVenueResponse extends FoursquareObject{
		private FoursquareVenueGroup[] groups;
		public LinkedList<FoursquareVenue> getAllVenues(){
			if(groups == null)
				return new LinkedList<FoursquareVenue>();
			
			LinkedList<FoursquareVenue> venues = new LinkedList<FoursquareVenue>();
			for (FoursquareVenueGroup g : groups){
				venues.addAll(g.getVenues());
			}
			return venues;
		}
		public LinkedList<FoursquareVenue> getCheckinVenues(){
			if(groups == null)
				return new LinkedList<FoursquareVenue>();
			
			LinkedList<FoursquareVenue> venues = new LinkedList<FoursquareVenue>();
			for (FoursquareVenueGroup g : groups){
				if (g.name.equals("My Favorites") || g.name.equals("Nearby")){
					for(FoursquareVenue v : g.getVenues()){
						v.setLastActivity(new Date());
						venues.add(v);
						break;
					}
				}
			}
			return venues;
		}
		
		public LinkedList<FoursquareVenue> getTrendingVenues(){
			if(groups == null)
				return new LinkedList<FoursquareVenue>();
			
			LinkedList<FoursquareVenue> venues = new LinkedList<FoursquareVenue>();
			for (FoursquareVenueGroup g : groups){
				if (g.name.equals("Trending Now")){
					for(FoursquareVenue v : g.getVenues()){
						v.setLastActivity(new Date());
						venues.add(v);
					}
				}
			}
			return venues;
		}
		
	}