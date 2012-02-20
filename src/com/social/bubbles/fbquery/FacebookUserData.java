package com.social.bubbles.fbquery;

import java.util.LinkedList;


public class FacebookUserData extends FacebookData {
	private FacebookUser[] data;
	
	public FacebookUser[] getData(){
		return data;
	}
	public void setData(FacebookUser[] data){
		this.data = data;
	}
	public void setData(LinkedList<FacebookUser> data){
		FacebookUser[] temp = new FacebookUser[data.size()];
		for(int i = 0; i<data.size(); i++)
			temp[i]=data.get(i);
		this.data = temp;
	}
}