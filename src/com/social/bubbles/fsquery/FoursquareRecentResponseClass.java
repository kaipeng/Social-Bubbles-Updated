package com.social.bubbles.fsquery;

import com.social.bubbles.fsquery.FoursquareVenue.FoursquareHereNowResponseClass.FoursquareHereNowResponse.FoursquareHereNow.FoursquareCheckin;

	public class FoursquareRecentResponseClass extends FoursquareObject{
		FoursquareRecentResponse response;
		public static class FoursquareRecentResponse extends FoursquareObject{
			FoursquareCheckin[] recent;
		}
		
	}
	
