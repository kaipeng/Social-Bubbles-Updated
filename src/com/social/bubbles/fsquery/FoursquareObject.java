package com.social.bubbles.fsquery;

import com.social.bubbles.Bubble;


public class FoursquareObject extends Bubble{
	private long createdAt;

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public long getCreatedAt() {
		return createdAt*1000;
	}
}
