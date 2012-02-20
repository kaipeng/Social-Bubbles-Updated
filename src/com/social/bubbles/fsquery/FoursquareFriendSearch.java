package com.social.bubbles.fsquery;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

/**
import android.content.Context;
import android.util.Log;
**/

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.social.bubbles.Bubble;
import com.social.bubbles.fsquery.FoursquareFriendResponseClass.FoursquareFriendResponse;
import com.social.bubbles.fsquery.FoursquareTodoResponseClass.FoursquareTodoResponse;

public class FoursquareFriendSearch extends FoursquareSearch {
	private FoursquareFriendResponse response;
	
	
	public FoursquareFriendSearch(Context context){
		super(context);
		String query = "users/self/friends";
		
		query = this.queryFoursquare(query);
		
		Log.d("FoursquareQuery : FoursquareFriendsSearch Result ", query);
		if(query==null){
			query = this.queryFoursquare(query);

		}
		FoursquareFriendResponseClass a = new Gson().fromJson(query,FoursquareFriendResponseClass.class);
		if(a!= null)
			response = a.response;
	}
	
	
	public LinkedList<FoursquareUser> getFriends(){
		if(response==null)
			return new LinkedList<FoursquareUser>();
		if(response.friends == null)
			return new LinkedList<FoursquareUser>();

		return new LinkedList<FoursquareUser>(Arrays.asList(response.friends.items));
	}
	
	
	public LinkedList<FoursquareUser> getTodosOfFriends(){
		LinkedList<FoursquareUser> friends = getFriends();
		LinkedList<FoursquareUser> friendsWithTodos = new LinkedList<FoursquareUser>();

		Log.d("FoursquareFriendsSearch :  Parsing through list of friends: ", ""+friends.size());

		for (FoursquareUser aFriend : friends){
			String query = "users/" + aFriend.getId() + "/todos";
			Bundle params = new Bundle();
			params.putString("sort", "recent");
			params.putString("limit", "2");
			query = this.queryFoursquare(query, params);
			
			FoursquareTodoResponseClass ftr = new Gson().fromJson(query,FoursquareTodoResponseClass.class);
			if(ftr==null){
				return friendsWithTodos;
			}
			FoursquareTodoResponse response = ftr.response;
			Log.d("FoursquareQuery : FoursquareTodoSearch Result ", query);
			
			//Get Friend (wholistic) and replace to get Contact Info, etc
			String query2 = "users/"+aFriend.getId();
			try {
				query2 = FoursquareSearch.fs.request(query2);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FoursquareUser response2 = null;
			if (query2!=""){
				try{
				response2 = new Gson().fromJson(query2, FoursquareUserResponseClass.class).response.user;
				}catch(Exception e){e.printStackTrace();}
			}
			Log.d("FoursquareQuery : FoursquareUser Contact Search Result ", query2);
			aFriend=response2;
			if(aFriend==null)
				continue;
			for(FoursquareTodo todo: response.getTodos()){
				if(!todo.isDone()){
					todo.setLastActivity(new Date(todo.getCreatedAt()));
	//				Log.d("FoursquareTodoSearch : Todo Created at: ", ""+todo.getCreatedAt());
	//				Log.d("FoursquareTodoSearch : Todo Last Activity: ", ""+todo.getLastActivity().toString());
					if(aFriend.getLastActivity()!=null && todo.getLastActivity().after(aFriend.getLastActivity())){
							aFriend.setLastActivity(todo.getLastActivity());
					}
					todo.setUser(aFriend);
					
					todo.setType(Bubble.FS_TODO);
					todo.setImageUrl(todo.getVenue().getMainCategory().icon);

					todo.setDescription(todo.getUser().getFullName() + ": Todo @ " + todo.getVenue().getName());
					todo.setCoordinates(todo.getVenue().getLatitude(), todo.getVenue().getLongitude());
		
					Log.d("FoursquareQuery : Todo Last Activity: ", ""+todo.getLastActivity().toString());

					aFriend.addTodo(todo);
				}
			}
			if(!aFriend.getTodos().isEmpty()){
				Log.d("FoursquareQuery : TodoSearch: ", "Friend w/todo Added");
				friendsWithTodos.add(aFriend);
			}
		}
		return friendsWithTodos;
	}
}
