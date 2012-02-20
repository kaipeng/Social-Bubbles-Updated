package com.social.bubbles.fbquery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.bubbles.location.MyLocation;
import com.social.bubbles.Main;
import com.social.bubbles.R;
import com.social.bubbles.Bubble.SubBubble;
import com.social.bubbles.fsquery.FoursquareUser;
import com.social.bubbles.fsquery.FoursquareUser.UserLookup;
import com.social.bubbles.fsquery.FoursquareVenue.MapObject;
import com.social.bubbles.fsquery.FoursquareVenue.PostTodoSubbubble;
import com.social.bubbles.fsquery.FoursquareVenue.VenueOpenFoursquareProfile;

import android.app.SearchManager;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.widget.Toast;


public class FacebookUser extends FacebookObject {
	private String name;
	private String gender;
	private FacebookLocation location;

	
	//supplementary
	private FacebookEventData events;
	//private FacebookCheckinData facebookCheckinData;
	
	private FacebookCheckin currentCheckin = null;
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setGender(String gender){
		this.gender = gender;
	}
	
	public String getName(){
		return name;
	}
	
	public String getGender(){
		return gender;
	}

//	public void setFacebookEventData(FacebookEventData facebookEventData) {
//		this.facebookEventData = facebookEventData;
//	}
//
	public FacebookEventData getFacebookEventData() {
		return events;
	}
//
//	public void setFacebookCheckinData(FacebookCheckinData facebookCheckinData) {
//		this.facebookCheckinData = facebookCheckinData;
//	}
//	public FacebookCheckinData getFacebookCheckinData() {
//		return facebookCheckinData;
//	}
	public void setCurrentCheckin(FacebookCheckin currentCheckin){
		this.currentCheckin = currentCheckin;
	}
	public FacebookCheckin getCurrentCheckin() {
		return currentCheckin;
	}

	public void setLocation(FacebookLocation location) {
		this.location = location;
	}

	public FacebookLocation getLocation() {
		return location;
	}

	//subbubbles
	@Override
	public LinkedList<SubBubble> getSubBubbles(Context context){
		LinkedList<SubBubble> sb = new LinkedList<SubBubble>();
		sb.add(new UserOpenFacebookProfile(context,id,this.getName()));
		sb.add(new UserLookup(context, this.getName()));
		try{
			if(this.getLat()!=0){
				sb.add(new MapObject(context,this.name,this.getLat(), this.getLon() ));
			}	
		}catch(NullPointerException e){e.printStackTrace();}
		//sb.add(new AddFacebookUserToContactsSubBubbles(context,id,name,service));
		return sb;
	}
	public class UserOpenFacebookProfile implements SubBubble{
		Context context;
		 ;
		String name;
		String id;
		public UserOpenFacebookProfile(Context context, String id, String name ){
			this.context = context;
			this.id = id;
			
			this.name = name;
		}
		@Override
		public Bitmap getImage() {
			// TODO Auto-generated method stub
			Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.sub_facebook);
			return image;
		}

		@Override
		public void onClick() {
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
				intent.putExtra("extra_user_id", id);
				context.startActivity(intent);
			}else {
				String url = "http://m.facebook.com/profile.php?id="+id;
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		}
		@Override
		public String getActionDescription() {
			// TODO Auto-generated method stub
			return "Open " + this.name + "'s Profile";
		}
	}
	public class AddFacebookUserToContactsSubBubbles implements SubBubble {
		Context context;
		 ;
		String name;
		String id;
				String phoneNumber;

		public AddFacebookUserToContactsSubBubbles(Context context, String id, String name ){

			this.context = context;
			this.id = id;
			
			this. name = name;
		}
		@Override
		public Bitmap getImage() {
			Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.sub_contact);
			return image;
		}

		@Override
		public void onClick() {
			// TODO Test this shit
			// create a new name
			 String DisplayName = this.name;


            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            //------------------------------------------------------ Names
            if(DisplayName != null)
            {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, DisplayName).build());
            }                    

            // Asking the Contact provider to create a new contact                  
            try 
            {
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                Main.showToast("Added " + this.name + " to you contacts list.", false);
            } 
            catch (Exception e) 
            {               
                e.printStackTrace();
                
            }
		}

		@Override
		public String getActionDescription() {
			// TODO Auto-generated method stub
			return "Add " + name + " to your contacts list.";
		}
		
	}

	public class UserLookup implements SubBubble{
		Context context;
		 ;
		String name;
		public UserLookup(Context context, String name ){
			this.context = context;
			
			this.name = name;
		}
		@Override
		public Bitmap getImage() {
			// TODO Auto-generated method stub
			Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.sub_search);
			return image;
		}

		@Override
		public void onClick() {
			// TODO Auto-generated method stub
			if(this.name==null)
				this.name = "";
        	Main.showToast("Looking up " + this.name, false);

    	    Intent i = new Intent(Intent.ACTION_SEARCH);
    	    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	    i.putExtra(SearchManager.QUERY, this.name);

    	    context.startActivity(i);
		}
		@Override
		public String getActionDescription() {
			// TODO Auto-generated method stub
			return "Searching for " + name;
		}	
	}

	
	
	public void setGeo(){
			try{
				lat = this.getCurrentCheckin().getLocation().getLatitude();
				lon = this.getCurrentCheckin().getLocation().getLongitude();
			} catch(Exception e){}
		}
}
