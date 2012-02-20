package com.social.bubbles.fbquery;

import com.bubbles.location.MyLocation;

import android.graphics.Bitmap;
import android.location.Location;


public class FacebookPlace extends FacebookObject {
	private String name;
	private String category;
	private int checkins;
	private FacebookLocation location;
	
	private int currentCheckins;
	

	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

	public void setLocation(FacebookLocation location) {
		this.location = location;
	}

	public FacebookLocation getLocation() {
		return location;
	}

	public void setCurrentCheckins(int currentCheckins) {
		this.currentCheckins = currentCheckins;
	}

	public int getCurrentCheckins() {
		return currentCheckins;
	}

	public void setCheckins(int checkins) {
		this.checkins = checkins;
	}

	public int getCheckins() {
		return checkins;
	}
	

	public void setGeo(){
		if(this.location!=null){
			try{
				this.lat = this.location.getLatitude();
				this.lon = this.location.getLongitude();
			} catch(Exception e){}
		}
	}

}
