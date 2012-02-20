package com.social.bubbles.fbquery;

import java.util.LinkedList;


public class FacebookCheckinData extends FacebookData {
	private FacebookCheckin[] data;
	
	public FacebookCheckin[] getData(){
		return data;
	}
	public void setData(FacebookCheckin[] data){
		this.data = data;
	}
	public void setData(LinkedList<FacebookCheckin> data){
		FacebookCheckin[] temp = new FacebookCheckin[data.size()];
		for(int i = 0; i<data.size(); i++)
			temp[i]=data.get(i);
		this.data = temp;
	}
	
}
