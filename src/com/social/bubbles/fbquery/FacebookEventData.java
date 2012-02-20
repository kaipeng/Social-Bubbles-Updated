package com.social.bubbles.fbquery;

import java.util.LinkedList;

public class FacebookEventData extends FacebookData {
	private FacebookEvent[] data;
	
	public void setData(FacebookEvent[] data){
		this.data = data;
	}
	
	public FacebookEvent[] getData(){
		if(data==null)
			return new FacebookEvent[]{};
		return data;
	}
	
	public void setData(LinkedList<FacebookEvent> data){
		FacebookEvent[] temp = new FacebookEvent[data.size()];
		for(int i = 0; i<data.size(); i++)
			temp[i]=data.get(i);
		this.data = temp;
	}
}
