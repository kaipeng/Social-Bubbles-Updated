package com.social.bubbles;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import com.social.bubbles.R;
import com.social.bubbles.socialbubbles3.MySystem.CreateBubbleHandler;
import com.social.bubbles.tools.CircleCrop;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

public class BubbleLauncher {
    
    //the simplest in-memory cache implementation. This should be replaced with something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
    private static HashMap<String, Bitmap> cache=new HashMap<String, Bitmap>();
    
    private static File cacheDir;
    private static Context context;
    
    public static int NEW_BUBBLE = 0;
    public static int UPDATE_BUBBLE = 1;


    static PhotoLoaderThread photoLoaderThread;
    
    static BubbleLauncher mBubbleLauncher;
    
    static CreateBubbleHandler createBubbleHandler;
    
    public BubbleLauncher(Context context){
    	this.context = context;
    	this.mBubbleLauncher = this;
        //Make the background thread low priority. This way it will not affect the UI performance
        photoLoaderThread = new PhotoLoaderThread();
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
        
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"SocialBubbles/Pictures");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    public static Bitmap GetDefaultImage(String type){
    	Bitmap bitmap;
    	//change these
    	if(type.equalsIgnoreCase(Bubble.FB_FRIENDS_EVENT)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_RECENT_CHECKIN)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_NEARBY_CHECKIN)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_RECENT_CHECKIN)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_NEARBY_CHECKIN)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_TRENDING_PLACE)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_TRENDING_VENUE)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_SPECIAL)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.special_icon);
    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_TODO)){
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sub_todo);
    	}
    	else{
    		bitmap =  BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
    	}
    	return bitmap;
    }
    
    public static Bitmap getImage(String url, String type){
    	if(cache.containsKey(url))
    			return cache.get(url);
		return cache.get(GetDefaultImage(type));
    }

    
    final int stub_id=R.drawable.stub;
    public static void RefreshBubbles()
    {
		Log.d("Bubble Launcher: Called RefreshBubbles", "");

        //start thread if it's not started yet
        if(photoLoaderThread.getState()==Thread.State.NEW){
			Log.d("Bubble Launcher: Starting PhotoLoader Thread", "");
        	photoLoaderThread.start();
        }
    }
    
	class PhotoLoaderThread extends Thread {
	     public void run() {
				Log.d("Bubble Launcher: Started PhotoLoader Thread", "");
	    	 while(true){
	    		//Clear all bubbles or something to make room
		        while(!BubbleManager.displayQueue.isEmpty()){
					Log.d("Bubble Launcher: Downloading from Display Queue", "");

		        	if(BubbleManager.displayQueue.isEmpty())
		        		break;
		        	String nextId = BubbleManager.displayQueue.remove(0);
		        	Bubble nextBubble = BubbleManager.cache.get(nextId);
		        	
		        	//First, retrieve Bubble Image, edit, and everything.
		        	Bitmap bubbleBit = retrieveBubble(nextBubble);

                	if(createBubbleHandler!=null){
                		if(!BubbleManager.isOnScreen(nextBubble.getId()))
                			launchNewBubble(nextBubble, bubbleBit);
                		else
                			updateBubble(nextBubble, bubbleBit);
                	}
		        }
		        while(!BubbleManager.everythingElse.isEmpty()){
					Log.d("Bubble Launcher: Downloading from Everything Else queue", "");

		        	if(BubbleManager.everythingElse.isEmpty())
		        		break;
		        	String nextId = BubbleManager.everythingElse.remove(0);
		        	Bubble nextBubble = BubbleManager.cache.get(nextId);
		        	
		        	Bitmap bubbleBit = retrieveBubble(nextBubble);
		        }
	        }
	     }

	}
	
	private Bitmap retrieveBubble(Bubble nextBubble){
    	Bitmap venueBitmap = null;
    	Bitmap specialBitmap = null;
    	Bitmap todoBitmap = null;
    	Bitmap mainBitmap = fetchImageFromUrl(nextBubble.getImageUrl(), nextBubble.getType());
    	Bitmap messageBanner;
    	String message;
    	
    	if(nextBubble.getTodoImageUrl()!=null 
    			|| nextBubble.getType().equals(Bubble.FS_TODO))
    		todoBitmap = fetchImageFromUrl(nextBubble.getTodoImageUrl(), Bubble.FS_TODO);
    	
    	if(nextBubble.getVenueImageUrl()!=null 
    			|| nextBubble.getType().equals(Bubble.FS_TRENDING_VENUE)
    			|| nextBubble.getType().equals(Bubble.FS_SPECIAL)){
    		venueBitmap = fetchImageFromUrl(nextBubble.getVenueImageUrl(), Bubble.FS_TRENDING_VENUE);
    	}

    	if(nextBubble.getSpecialImageUrl()!=null 
    			|| nextBubble.getType().equals(Bubble.FS_SPECIAL))
    		specialBitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.special_icon);

    	Bitmap bubbleBit = editBubbleImage(nextBubble, mainBitmap, todoBitmap, venueBitmap, specialBitmap);
    	cache.put(nextBubble.getId(), bubbleBit);
    	
    	return bubbleBit;
	}
	
	private Bitmap fetchImageFromUrl(String url, String type){
    	Bitmap bitmap;
		if(url == null || url.equals("n/a")){
    		bitmap = GetDefaultImage(type);
            Log.d("Bubble Launcher: ", "Null URL - Default image used ");

    	}
    	else if(cache.containsKey(url)){
    		bitmap = (cache.get(url));
            Log.d("Bubble Launcher: ", "Picture taken from cache ");

    	}
    	else{
    		bitmap=getBitmap(url);
            if(bitmap!=null){
            	cache.put(url, bitmap);
                Log.d("Bubble Launcher: ", "Picture downloaded and cached ");

            } else{
        		bitmap = GetDefaultImage(url);
                Log.d("Bubble Launcher: ", "Picture download failed ");
            }
    		//queuePhoto(nextBubble.getImageUrl());
    	}
		return bitmap;
	}
	
	private Bitmap editBubbleImage(Bubble bubble, Bitmap mainBitmap, Bitmap todoBitmap, Bitmap venueBitmap, Bitmap specialBitmap){
	    CircleCrop cc = new CircleCrop(context.getResources());

		//bitmap=cc.overlayBubble(bitmap);
	    int width = Math.min(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels);

		if(bubble.getType().equals(Bubble.FS_TRENDING_VENUE) || bubble.getType().equals(Bubble.FS_NEARBY_VENUE)){
			Bitmap f = Bitmap.createBitmap((int)(mainBitmap.getWidth()*1.5), (int)(mainBitmap.getHeight()*1.5), Bitmap.Config.ARGB_8888);
	    	Canvas c = new Canvas(f);
	    	c.drawBitmap(mainBitmap, mainBitmap.getWidth()/3, mainBitmap.getHeight()/3, new Paint());	    	
			mainBitmap = f;
		}
	    
	    mainBitmap = cc.processBubble(mainBitmap, bubble.getColor(), (int)(width*Settings.Bubble.BUBBLE_DIMENSION));
	    
		if(bubble.getType().contains("fb"))
			mainBitmap=cc.overlayFB(mainBitmap);
		else
			mainBitmap=cc.overlayFS(mainBitmap);
		
		if(todoBitmap !=null){
			Bitmap mask = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.todo_overlay);
			mainBitmap=cc.overlay(mainBitmap, todoBitmap, mask, 1);
		}

		if(venueBitmap !=null){
			Bitmap mask = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.checkin_overlay);

			mainBitmap=cc.overlay(mainBitmap, venueBitmap, mask, 2);
		}
		
		if(specialBitmap !=null){
			mainBitmap=cc.overlay(mainBitmap, specialBitmap, null, 3);
		}

		return mainBitmap;
	}
	
	private void updateBubble(Bubble bubble, Bitmap bitmap) {
		// TODO Auto-generated method stub
		//IMPLEMENT
        Log.d("Bubble Launcher: ", "Launch bubble called ");

        Message updateMessage = Message.obtain();
        updateMessage.what = UPDATE_BUBBLE;
        updateMessage.obj = bubble.getId();	//change eventually
        updateMessage.setTarget(createBubbleHandler);
        updateMessage.sendToTarget();

      	Log.d("SENT NEW BUBBLE MESSAGE TO UPDATE WALLPAPER : ", bubble.getDescription());

	}
	private void launchNewBubble(Bubble bubble, Bitmap bitmap){
		//IMPLEMENT
        Log.d("Bubble Launcher: ", "Launch bubble called ");

        Message updateMessage = Message.obtain();
        updateMessage.what = NEW_BUBBLE;
        updateMessage.obj = bubble.getId();	//change eventually
        updateMessage.setTarget(createBubbleHandler);
        updateMessage.sendToTarget();

      	Log.d("SENT NEW BUBBLE MESSAGE TO UPDATE WALLPAPER : ", bubble.getDescription());
	}
	
	public static void setCreateBubbleHandler(CreateBubbleHandler cbh){
		createBubbleHandler = cbh;
	}
    
    private Bitmap getBitmap(String url) 
    {
        Log.d("Bubble Launcher: ", "Getting picture from: " + url);
    	
    	//I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        File f=new File(cacheDir, filename);
        
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        
        //from web
        try {
            Bitmap bitmap=null;
            BufferedInputStream is=new BufferedInputStream(new URL(url).openConnection().getInputStream());
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            bitmap = decodeFile(f);
            return bitmap;
        } catch (FileNotFoundException ex){
           ex.printStackTrace();
           Log.d("Image Loader: ", "FAILED getting: " + url);
           return null;
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=100;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
    
    public void clearCache() {
        //clear memory cache
        cache.clear();
        
        //clear SD cache
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }
}