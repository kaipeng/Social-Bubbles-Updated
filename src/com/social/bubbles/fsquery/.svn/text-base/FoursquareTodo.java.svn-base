package com.social.bubbles.fsquery;

public class FoursquareTodo extends FoursquareObject {
	private FoursquareTip tip;

	public FoursquareVenue getVenue(){
		return tip.venue;
	}
	
	public String getText(){
		return tip.text;
	}
	
	public String getStatus(){
		return tip.status;
	}
	
	public void setUser(FoursquareUser user) {
		tip.user = user;
	}
	public FoursquareUser getUser() {
		return tip.user;
	}
	public boolean isDone(){
		if(tip.status != null)
			return (tip.status).equalsIgnoreCase("done");
		return false;
	}

}
