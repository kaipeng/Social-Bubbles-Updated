package com.social.bubbles.fsquery;
	public  class FoursquareTip extends FoursquareObject {
		 String text;
		 String status;
		 FoursquareVenue venue;
		 FoursquareUser user;
		 
		 public void setUser(FoursquareUser user){
			 this.user = user;
		 }
	}