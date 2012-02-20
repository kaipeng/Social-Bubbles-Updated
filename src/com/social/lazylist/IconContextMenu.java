/*
 * Copyright (C) 2010 Tani Group 
 * http://android-demo.blogspot.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.social.lazylist;

import java.util.ArrayList;
import android.graphics.drawable.BitmapDrawable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author nguyendt
 * 
 */
public class IconContextMenu implements DialogInterface.OnCancelListener, 
										DialogInterface.OnDismissListener{

	private static final int LIST_PREFERED_HEIGHT = 65;
	
	private IconMenuAdapter menuAdapter = null;
	private Activity parentActivity = null;
	private int dialogId = 0;
	
	private IconContextMenuOnClickListener clickHandler = null;
	
	/**
	 * constructor
	 * @param parent
	 * @param id
	 */
	public IconContextMenu(Activity parent, int id) {
		this.parentActivity = parent;
		this.dialogId = id;
		
		menuAdapter = new IconMenuAdapter(parentActivity);
	}
	
	/**
	 * Add menu item
	 * @param menuItem
	 */
	public void addItem(Resources res, CharSequence title,
			Bitmap bitmap, int actionTag) {
		menuAdapter.addItem(new IconContextMenuItem(res, title, bitmap, actionTag));
	}
	
	/**
	 * Set menu onclick listener
	 * @param listener
	 */
	public void setOnClickListener(IconContextMenuOnClickListener listener) {
		clickHandler = listener;
	}
	
	/**
	 * Create menu
	 * @return
	 */
	public Dialog createMenu(String menuItitle) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        builder.setTitle(menuItitle);
        builder.setAdapter(menuAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialoginterface, int i) {
				IconContextMenuItem item = (IconContextMenuItem) menuAdapter.getItem(i);
				
				if (clickHandler != null) {
					clickHandler.onClick(item.actionTag);
				}
			}
		});

        builder.setInverseBackgroundForced(true);

        AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(this);
        dialog.setOnDismissListener(this);
        return dialog;
	}
	
	public void onCancel(DialogInterface dialog) {
        cleanup();
    }

    public void onDismiss(DialogInterface dialog) {
        //cleanup();
    }
   
    private void cleanup() {
    	parentActivity.dismissDialog(dialogId);
    }
	
    /**
     * IconContextMenu On Click Listener interface
     */
    public interface IconContextMenuOnClickListener {
		public abstract void onClick(int menuId);
    }
    
	/**
	 * Menu-like list adapter with icon
	 */
    protected class IconMenuAdapter extends BaseAdapter {
		private Context context = null;
		
	    private ArrayList<IconContextMenuItem> mItems = new ArrayList<IconContextMenuItem>();
		
	    public IconMenuAdapter(Context context) {
	    	this.context = context;
	    }
	    
	    /**
	     * add item to adapter
	     * @param menuItem
	     */
	    public void addItem(IconContextMenuItem menuItem) {
	    	mItems.add(menuItem);
	    }
	    
		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			IconContextMenuItem item = (IconContextMenuItem) getItem(position);
			return item.actionTag;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			IconContextMenuItem item = (IconContextMenuItem) getItem(position);
			
			Resources res = parentActivity.getResources();
			
			if (convertView == null) {
	        	TextView temp = new TextView(context);
	        	AbsListView.LayoutParams param = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, 
	        																  AbsListView.LayoutParams.WRAP_CONTENT);
	        	temp.setLayoutParams(param);
	        	temp.setPadding((int)toPixel(res, 15), 0, (int)toPixel(res, 15), 0);
	        	temp.setGravity(android.view.Gravity.CENTER_VERTICAL);
	        	
	        	Theme th = context.getTheme();
				TypedValue tv = new TypedValue();
				
				if (th.resolveAttribute(android.R.attr.textAppearanceLargeInverse, tv, true)) {
					temp.setTextAppearance(context, tv.resourceId);
				}
	        	
	        	temp.setMinHeight(LIST_PREFERED_HEIGHT);
	        	temp.setCompoundDrawablePadding((int)toPixel(res, 14));
	        	convertView = temp;
			}
			
			TextView textView = (TextView) convertView;
			textView.setTag(item);
			textView.setTextSize(17);
			textView.setText(item.text);
			textView.setCompoundDrawablesWithIntrinsicBounds(item.image, null, null, null);
        	
	        return textView;
		}
		
		private float toPixel(Resources res, int dip) {
			float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, res.getDisplayMetrics());
			return px;
		}
	}

	/**
	 * menu-like list item with icon
	 */
    protected class IconContextMenuItem {
		public final CharSequence text;
		public final Drawable image;
		public final int actionTag;


		/**
		 * public constructor
		 * @param res
		 * 			resource handler
		 * @param title
		 * 			menu item title
		 * @param imageResourceId
		 * 			id of icon in resource
		 * @param actionTag
		 * 			indicate action of menu item
		 */
		public IconContextMenuItem(Resources res, CharSequence title,
				Bitmap bitmap, int actionTag) {
			text = title;
			if (bitmap != null) {
				image = new BitmapDrawable(res, bitmap);
			} else {
				image = null;
			}
			this.actionTag = actionTag;
		}
	}
}
