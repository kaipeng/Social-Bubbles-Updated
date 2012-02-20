package com.social.bubbles.fbquery;

import java.util.Date;

import com.social.bubbles.Bubble;

import android.graphics.Bitmap;


public class FacebookObject extends Bubble{
	private String picture;
	
	
	
	public String toString(){
		return id;
	}



	public void setPicture(String picture) {
		this.picture = picture;
	}



	public String getPicture() {
		return picture;
	}

}
