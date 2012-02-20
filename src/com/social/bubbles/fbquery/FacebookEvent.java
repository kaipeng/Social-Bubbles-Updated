package com.social.bubbles.fbquery;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.social.bubbles.BubbleManager;
import com.social.bubbles.Main;
import com.social.bubbles.R;
import com.social.bubbles.Utils;
import com.social.bubbles.Bubble.SubBubble;
import com.social.bubbles.fbquery.FacebookUser.UserOpenFacebookProfile;
import com.social.bubbles.fsquery.FoursquareVenue.MapObject;


public class FacebookEvent extends FacebookObject {
	private String name;
	private String start_time;
	private String end_time;
	private String location;
	private String rsvp_status;
		
	//setters
	public void setName(String name){
		this.name = name;
	}
	public void setStart_time(String start_time){
		this.start_time = start_time;
	}
	public void setEnd_time(String end_time){
		this.end_time = end_time;
	}
	public void setLocation(String location){
		this.location = location;
	}
	public void setRsvp_status(String rsvp_status){
		this.rsvp_status = rsvp_status;
	}
	//getters
	public String getName(){
		return name;
	}
	public String getStart_time(){
		return start_time;
	}
	public Date getEndTime(){
		return Utils.timeToDate(end_time);
	}
	public String getLocation(){
		return location;
	}
	public String getRsvp_status(){
		return rsvp_status;
	}
	public void setMine(boolean isMine) {
		super.isMine = isMine;
	}
	public boolean isMine() {
		return isMine;
	}
	
	@Override
	public LinkedList<SubBubble> getSubBubbles(Context context){
		LinkedList<SubBubble> sb = new LinkedList<SubBubble>();
		sb.add(new EventOpenFacebookProfile(context,id));
		sb.add(new EventAddToCalendar(context,name,Utils.timeToDate(start_time).getTime(),Utils.timeToDate(end_time).getTime(),location));
		try{
			if(this.getLat()!=0){
				sb.add(new MapObject(context,this.name,this.getLat(), this.getLon() ));
			}	
		}catch(NullPointerException e){e.printStackTrace();}
		try{
			if(this.getLocation()!=null){
				sb.add(new MapObject(context,this.name,this.getLocation()));
			}	
		}catch(NullPointerException e){e.printStackTrace();}
		return sb;
	}
	public class EventOpenFacebookProfile implements SubBubble{
		Context context;
		 ;
		String name;
		String id;
		public EventOpenFacebookProfile(Context context, String id ){
			this.context = context;
			this.id = id;
			
		}
		@Override
		public Bitmap getImage() {
			Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.sub_facebook);
			return image;
		}

		@Override
		public void onClick() {
			// TODO test this
			boolean fbInstalled = false;
			PackageManager packageManager = context.getPackageManager();
			List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(0);
			for (ApplicationInfo ai : installedApps){
				if (ai.packageName=="com.facebook.katata"){
					fbInstalled = true;
				}
			}
			Main.showToast("Opening " + this.name + "'s Facebook profile.", false);
			if (fbInstalled){
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setClassName("com.facebook.katana", "com.facebook.katana.ProfileTabHostActivity");
				intent.putExtra("extra_event_id", id);
				context.startActivity(intent);
			}else {
				String url = "http://www.facebook.com/event.php?eid="+id;
	    	    Intent i = new Intent(Intent.ACTION_VIEW);
	    	    i.setData(Uri.parse(url));
	    	    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	    context.startActivity(i);
			}

		}
		@Override
		public String getActionDescription() {
			return "Open " + this.name + "'s Profile";
		}
	}
	public class EventAddToCalendar  implements SubBubble {
		Context context;
		 ;
		long beginTime;
		long endTime;
		String name;
		String location;
		
		public EventAddToCalendar(Context context , String name, long l, long m, String location){
			this.context = context;
			
			this.name = name;
			this.beginTime = l;
			this.endTime = m;
			this.location = location;
		}

		@Override
		public Bitmap getImage() {
			Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.sub_calendar);
			return image;
		}

		@Override
		public void onClick() {
			// TODO Auto-generated method stub
			Main.showToast("Adding " + this.name + " to your calendar.", false);

			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
			intent.putExtra("beginTime", beginTime);
			//intent.putExtra("allDay", true);
			//intent.putExtra("rrule", "FREQ=YEARLY");
			intent.putExtra("endTime", endTime);
			intent.putExtra("title", name);
		}
		@Override
		public String getActionDescription() {
			// TODO Auto-generated method stub
			return "Add " + this.name + " to your calendar.";
		}
	}
}
