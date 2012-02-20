package com.social.bubbles.fsquery;

import java.util.Arrays;
import java.util.LinkedList;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;


import com.social.bubbles.Bubble;
import com.social.bubbles.fsquery.FoursquareSpecialsResponseClass.FoursquareSpecialsResponse;

import com.google.gson.Gson;

public class FoursquareSpecialsSearch extends FoursquareSearch{
	private FoursquareSpecialsResponse response;
	
	
	public FoursquareSpecialsSearch(Context context, double latitude, double longitude){
		super(context);
		
		String query = "specials/search";
		Bundle params = new Bundle();
		params.putString("ll", latitude+","+longitude);
		params.putString("limit", "6");

		query = this.queryFoursquare(query, params);
		if (query!=""){
			try{
			response = new Gson().fromJson(query, FoursquareSpecialsResponseClass.class).response;
			}catch(Exception e){e.printStackTrace();}
		}else {
			response = null;
		}
		Log.d("FoursquareQuery : FoursquareSpecialsSearch Result ", query);
	}
	
	
	public LinkedList<FoursquareSpecial> getSpecials(){
		if (response==null){
			return new LinkedList<FoursquareSpecial>();
		}
		if (response.specials==null){
			return new LinkedList<FoursquareSpecial>();
		}
		return new LinkedList<FoursquareSpecial>(Arrays.asList(response.specials.items));
	}
}
