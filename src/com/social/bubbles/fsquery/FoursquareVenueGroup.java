package com.social.bubbles.fsquery;

import java.util.Arrays;
import java.util.LinkedList;

	public  class FoursquareVenueGroup extends FoursquareObject{
		private String type;
		String name;
		private FoursquareVenue[] items;
		
		public String getType(){
			return type;
		}
		public String getName(){
			return name;
		}
		public LinkedList<FoursquareVenue> getVenues(){
			return new LinkedList<FoursquareVenue>(Arrays.asList(items));
		}
	}