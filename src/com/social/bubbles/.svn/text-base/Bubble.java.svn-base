package com.social.bubbles;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.bubbles.location.MyLocation;
import com.social.bubbles.fsquery.FoursquareObject;

public class Bubble implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1671660991705117668L;
	
	protected String id;
	private String imageUrl;
	private String todoImageUrl;
	private String venueImageUrl;
	private String specialImageUrl;
	private String timeAgo;
	private int color;

	private Date lastDownloaded = new Date();
	private Date lastActivity = new Date();

	protected boolean isMine;
	private String type;
	public String description;
	
	public LinkedList<SubBubble> subBubbles = new LinkedList<SubBubble>();
	
	protected double lat, lon;
	
	public void setCoordinates(double mlat, double mlon){
		lat = mlat;
		lon = mlon;
	}

	public double getLat(){
		return lat;
	}
	public double getLon(){
		return lon;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public final static String FB_MY_EVENT			= "fbmyevent";
	public final static String FB_FRIENDS_EVENT		= "fbfriendsevent";
	public final static String FB_NEARBY_CHECKIN	= "fbnearbycheckin";
	public final static String FB_RECENT_CHECKIN	= "fbrecentcheckin";
	public final static String FB_TRENDING_PLACE	= "fbtrendingplace";
	
	public final static String FS_NEARBY_CHECKIN	= "fsnearbycheckin";
	public final static String FS_RECENT_CHECKIN	= "fsrecentcheckin";
	public final static String FS_SPECIAL			= "fsspecial";
	public final static String FS_TODO				= "fstodo";
	public final static String FS_TRENDING_VENUE	= "fstrendingvenue";
	
	public final static int BLUE				= 1;
	public final static int RED					= 2;
	public final static int YELLOW				= 3;
	public final static int GREEN				= 4;
	public final static int PURPLE				= 5;
	public final static int ORANGE				= 6;
	public final static int GREYED				= 7;

	public static final String FS_NEARBY_VENUE = "fsnearbyvenue";





	public long rank; //smaller the better
	
	//setters
	public void updateRank(){
		double offset;
		if(type.equalsIgnoreCase(Bubble.FB_MY_EVENT)){
			offset = -2;
    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_RECENT_CHECKIN)){
			offset = 1;
    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_NEARBY_CHECKIN)){
			offset = 1;
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_RECENT_CHECKIN)){
			offset = 0;
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_NEARBY_CHECKIN)){
			offset = 1;
    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_TRENDING_PLACE)){
			offset = 3;
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_TRENDING_VENUE)){
			offset = 3;
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_SPECIAL)){
			offset = 4;
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_TODO)){
			offset = -1;
    	}
    	else{
			offset = 1;
    	}
		if(this.getTodoImageUrl()!=null)
			offset-=5;
		rank = (long) (Math.pow(2, offset)*Math.abs(lastActivity.getTime()-(new Date()).getTime()));
	}
	
	public void setImageUrl(String imageUrl){
		this.imageUrl = imageUrl;
	}
	
	public void setLastDownloaded() {
		this.lastDownloaded = new Date();
	}
	public Date getLastActivity() {
		return lastActivity;
	}

	public void setLastActivity(Date lastActivity) {
		this.lastActivity = lastActivity;
	}
	public void setType(String type) {
		this.type = type;
	}


	//getters
	public long getRank(){
		return rank;
	}
	public String getId(){
		return id;
	}
	public String getImageUrl(){
		return imageUrl;
	}
	public LinkedList<SubBubble> getSubBubbles(Context context){
		return subBubbles;
	}
	public Date getLastDownloaded() {
		return lastDownloaded;
	}

	public String getType() {
		return type;
	}
	
	
	public boolean equals(Bubble object){
		return this.id.equalsIgnoreCase(object.id);
	}

	public void setVenueImageUrl(String venueImageUrl) {
		this.venueImageUrl = venueImageUrl;
	}

	public String getVenueImageUrl() {
		return venueImageUrl;
	}
	public void setTodoImageUrl(String todoImageUrl) {
		this.todoImageUrl = todoImageUrl;
	}

	public String getTodoImageUrl() {
		return todoImageUrl;
	}
	public void setSpecialImageUrl(String specialImageUrl) {
		this.specialImageUrl = specialImageUrl;
	}

	public String getSpecialImageUrl() {
		return specialImageUrl;
	}
	
	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}
	public String getTimeAgo() {
		if(this.getType().equals(FS_SPECIAL)){
			return("Available now.");
		}
		if(this.getType().equals(FS_TRENDING_VENUE)){
			return("Trending now.");
		}
		if(this.getLastActivity().after(new Date())){
			return(getLastActivity().toString());
		}
		if(this.getLastActivity().after(new Date(new Date().getTime()-1000*60))){
			return(-((this.getLastActivity().getTime()-(new Date().getTime()))/1000) + " seconds ago.");
		}
		if(this.getLastActivity().after(new Date(new Date().getTime()-1000*60*60))){
			return(-((this.getLastActivity().getTime()-(new Date().getTime()))/(1000*60)) + " minutes ago.");
		}
		if(this.getLastActivity().after(new Date(new Date().getTime()-1000*60*60*24))){
			return(-((this.getLastActivity().getTime()-(new Date().getTime()))/(1000*60*60)) + " hours ago.");
		}
		if(this.getLastActivity().after(new Date(new Date().getTime()-1000*60*60*24*7))){
			return(-((this.getLastActivity().getTime()-(new Date().getTime()))/(1000*60*60*24)) + " days ago.");
		}
			return("On " + this.getLastActivity().toString().substring(4, 7) + " " +  this.getLastActivity().getDate()+".");

	}
	
	public int getDistance(){
		if(this.getLat()!=0){
			try{
				double lat = this.getLat();
				double lon = this.getLon();
				
				float dist[] = {0};
				Location.distanceBetween(lat, lon, MyLocation.getLat(), MyLocation.getLon(), dist);
				
				return (int)dist[0];

			} catch(Exception e){}
		}
		return -1;
	}
	
	public interface SubBubble {
		public Bitmap getImage();
		public void onClick();
		public String getActionDescription();
	}
}
