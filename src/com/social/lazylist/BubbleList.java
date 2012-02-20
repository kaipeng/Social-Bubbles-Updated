package com.social.lazylist;

import java.util.ArrayList;
import java.util.LinkedList;


import com.bubbles.location.MyLocation;
import com.social.bubbles.Bubble;
import com.social.bubbles.BubbleManager;
import com.social.bubbles.DownloadManager;
import com.social.bubbles.R;
import com.social.bubbles.Bubble.SubBubble;
import com.social.bubbles.socialbubbles3.SocialBubblesSetting;
import com.social.bubbles.tools.CircleCrop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;


public class BubbleList extends Activity implements OnItemClickListener {
    
    ListView list;
    LazyAdapter adapter;
    Activity mActivity;
	
    Bundle a;
    
    //For Progress Bar
    private ProgressBar mProgress;
	//For Progress Bar
    private EditText filterText = null;

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            adapter.getFilter().filter(s);
        }

    };

	// handler for the background updating
	Handler progressBarHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	mProgress.setVisibility(ProgressBar.VISIBLE);
	    	String a = (msg.obj).toString();
	    	int progress = Integer.decode(a);
	    	mProgress.setProgress(progress);
	    	if(progress>=999)
	    		mProgress.setVisibility(ProgressBar.GONE);
	    }
	};
	
    /** For the popup menu. */
	private IconContextMenu iconContextMenu = null;
	
    protected LinkedList<Bubble> contextMenuList = new LinkedList<Bubble>();
    /** For the popup menu. */	
	 Resources res;
	 CircleCrop cc;
	
	 //ItemClickHandler listHandle;
	 
	 
	View contentView;
	Long lastDropped = System.currentTimeMillis();
	OnKeyListener keyListener = new OnKeyListener(){
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) { 
            //do what you want
        	if(System.currentTimeMillis()-lastDropped > 500){

        	if(b.getVisibility()==View.VISIBLE){
        		b.setVisibility(View.GONE);
        		b2.setVisibility(View.GONE);

        	}else{
        		b.setVisibility(View.VISIBLE);
        		b2.setVisibility(View.VISIBLE);

        	}
        	lastDropped = System.currentTimeMillis();
        	return true; 
        	}
        	return false;
        } 
        return false;
	}
	};
	Button b, b2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);


        //listHandle= new ItemClickHandler();
        
        contentView = findViewById(R.layout.bubblelist);
        setContentView(R.layout.bubblelist);
        
        filterText = (EditText) findViewById(R.id.search_box);
        filterText.addTextChangedListener(filterTextWatcher);
        filterText.setOnKeyListener(keyListener);

        
        mActivity = this;
        list=(ListView)findViewById(R.id.list);
        adapter=new LazyAdapter(mActivity);
        list.setAdapter(adapter);
        list.setOnKeyListener(keyListener);
        list.setOnItemClickListener(ItemClickHandler);
        list.setOnItemLongClickListener(new ItemLongClickHandler());

        list.setTextFilterEnabled(true);
       
        
        // Inform the list we provide context menus for items
        //list.setOnCreateContextMenuListener(this);
        
        b=(Button)findViewById(R.id.button1);
        b.setOnClickListener(listener);
        b.setVisibility(View.GONE);
        b2=(Button)findViewById(R.id.button2);
        b2.setOnClickListener(listener2);
        b2.setVisibility(View.GONE);

        
		//updateHandler = new UpdateHandler();
		DownloadManager.refreshBubbleHandler=(refreshBubbleHandler);
		
		DownloadManager.progressBarHandler=progressBarHandler;
        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
		mProgress.setVisibility(ProgressBar.GONE);
		
	    /** For the popup menu. */
        res = getResources();
        
        //init sample list for test
        //initSampleList();
        
        cc = new CircleCrop(res);

    }
  
 
    
    int menuCount=0;
    private void setupContextMenuIcons(Bubble bubble){
    	final LinkedList<SubBubble> subBubbles = bubble.getSubBubbles(this);
    	//init the menu
    	contextBubble=bubble;
    	menuCount++;
        iconContextMenu = new IconContextMenu(this, menuCount);
        
        int i =0;
        for(SubBubble sb : subBubbles){
            iconContextMenu.addItem(res, sb.getActionDescription(), cc.resizeBitmap(sb.getImage(),55,55), i++);
        }
             
        //set onclick listener for context menu
        iconContextMenu.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
			@Override
			public void onClick(int menuId) {
				subBubbles.get(menuId).onClick();
			}
		});
	    /** For the popup menu. */
    }
    
    /**
     * list item long click handler
     * used to show the context menu
     */
    private class ItemLongClickHandler implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			setupContextMenuIcons(adapter.getItem(position));
			showDialog(menuCount);
			//onResume();
			return true;
		}
	};
	
    private OnItemClickListener ItemClickHandler = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			setupContextMenuIcons(adapter.getItem(position));
			showDialog(menuCount);
			//onResume();
		}
	};
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		setupContextMenuIcons(adapter.getItem(position));
		showDialog(menuCount);
	}
	
	int bubbleChosen;
	Bubble contextBubble;

	/**
	 * create context menu
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
		if (id == (menuCount)) {
			return iconContextMenu.createMenu(contextBubble.getDescription());
		}
		return super.onCreateDialog(id);
	}
	
    
	   @Override
	    public void onResume() {
		   		onCreate(null);

	            super.onResume();
            	BubbleManager.queueQualifyingBubbles();
            	adapter=new LazyAdapter(mActivity);
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                //list.setOnItemLongClickListener(new ItemLongClickHandler());
	    }
    
	   
    
    protected Handler refreshBubbleHandler = new Handler() {
		private static final int BUBBLE_UPDATE = 1;
		public void handleMessage(Message msg) {
                	Log.d("           RECEIVED MESSAGE TO UPDATE UI :","");
                	BubbleManager.queueQualifyingBubbles();
                	adapter=new LazyAdapter(mActivity);
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    //list.setOnItemLongClickListener(new ItemLongClickHandler());
                    //to be determined
        }
    };
			    
    
    @Override
    public void onDestroy()
    {
        list.setAdapter(null);
        
        super.onDestroy();
        filterText.removeTextChangedListener(filterTextWatcher);

    }
    
    public OnClickListener listener=new OnClickListener(){
        @Override
        public void onClick(View arg0) {
        	BubbleManager.queueQualifyingBubbles();
        	adapter=new LazyAdapter(mActivity);
            list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            
            Message updateMessage = Message.obtain();

    		if(DownloadManager.downloadSignalHandler!=null){
    	 	   Log.d("Force Start Downloading: BubbleList Refresh Button ", "");
    	 	    updateMessage.what=1;
    	        updateMessage.setTarget(DownloadManager.downloadSignalHandler);
    	        updateMessage.sendToTarget();
    		}
        }
    };
    
    public OnClickListener listener2=new OnClickListener(){
        @Override
        public void onClick(View arg0) {
        	Toast toast = Toast.makeText(mActivity, "Choose '<Social Bubbles Wallpaper>' from the list to start the Live Wallpaper.",Toast.LENGTH_LONG);
        	toast.show();
        	
        	Intent intent = new Intent();
        	intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
        	startActivity(intent);
        }
    };
    

}