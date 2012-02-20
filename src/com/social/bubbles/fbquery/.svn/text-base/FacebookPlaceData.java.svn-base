package com.social.bubbles.fbquery;

import java.util.LinkedList;


public class FacebookPlaceData extends FacebookData {
	private FacebookPlace[] data;
	
	public FacebookPlace[] getData(){
		return data;
	}
	public void setData(FacebookPlace[] data){
		this.data = data;
	}
	public void setData(LinkedList<FacebookPlace> data){
		FacebookPlace[] temp = new FacebookPlace[data.size()];
		for(int i = 0; i<data.size(); i++)
			temp[i]=data.get(i);
		this.data = temp;
	}
}