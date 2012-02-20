package com.social.bubbles.tools;

import com.social.bubbles.Bubble;
import com.social.bubbles.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Region;

public class CircleCrop {
	private Resources res;
	
    public CircleCrop(Resources res){
    	
    	this.res = res;
    }
    public Bitmap processBubble(Bitmap b, int color, int dimension){
    	Bitmap r = squareCropBitmap(b, dimension);
    	r = circleCropBitmap(r);
    	r = overlayBubble(r, color);
    	return r;
    }
    public Bitmap circleCropResource(int id){
    	Bitmap b = BitmapFactory.decodeResource(res,id);
    	b = circleCropBitmap(b);
    	return b;
    }
    public Bitmap circleCropBitmap(Bitmap b){
    	Bitmap bg = Bitmap.createBitmap(b.getWidth(),b.getHeight(),Bitmap.Config.ARGB_8888);
    	Canvas c = new Canvas(bg);
    	c.drawBitmap(b, 0, 0, new Paint());
    	
    	Path p = new Path();
    	p.addCircle(b.getWidth()/2F,b.getHeight()/2F,b.getWidth()/2F,Path.Direction.CW);
    	c.clipPath(p,Region.Op.DIFFERENCE);
    	c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
    	return bg;
    }
    public Bitmap squareCropBitmap(Bitmap b, int dimension){
    	Bitmap bg;
    	if(b.getWidth()>b.getHeight()){
    		bg = Bitmap.createBitmap(b, ((b.getWidth()-b.getHeight())/2), 0, b.getHeight(), b.getHeight());
        	
    	}else{
        	bg = Bitmap.createBitmap(b, 0, ((b.getHeight()-b.getWidth())/8), b.getWidth(), b.getWidth());

    	}
    	if(bg==null)
    		return bg;
    	bg = resizeBitmap(bg, dimension, dimension);
    	
    	return bg;
    }
    public Bitmap resizeBitmap(Bitmap b, int newWidth, int newHeight){
    	 int width = b.getWidth();
         int height = b.getHeight();
        
         // calculate the scale - in this case = 0.4f
         float scaleWidth = ((float) newWidth) / width;
         float scaleHeight = ((float) newHeight) / height;
        
         // createa matrix for the manipulation
         Matrix matrix = new Matrix();
         // resize the bit map
         matrix.postScale(scaleWidth, scaleHeight);
         // rotate the Bitmap
         //matrix.postRotate(45);
  
         // recreate the new Bitmap
         Bitmap resizedBitmap = Bitmap.createBitmap(b, 0, 0,
                           width, height, matrix, true);
         b=null;
         
         return resizedBitmap;
    }
    public Bitmap overlayBubble(Bitmap b, int color){
    	Bitmap bubble;
    	switch (color) {
	        case Bubble.BLUE:	bubble=BitmapFactory.decodeResource(res, R.drawable.blue_bubble);
	        	break;
	        case Bubble.ORANGE: bubble=BitmapFactory.decodeResource(res, R.drawable.orange_bubble);
        		break;
	        case Bubble.YELLOW: bubble=BitmapFactory.decodeResource(res, R.drawable.yellow_bubble);
    			break;
	        case Bubble.GREEN: bubble=BitmapFactory.decodeResource(res, R.drawable.green_bubble);
    			break;
	        case Bubble.GREYED: bubble=BitmapFactory.decodeResource(res, R.drawable.greyed_bubble);
				break;
	        case Bubble.RED: bubble=BitmapFactory.decodeResource(res, R.drawable.red_bubble);
				break;
    		default: bubble=BitmapFactory.decodeResource(res, R.drawable.bubble);
    			break;
	    }
    	BitmapFactory.decodeResource(res, R.drawable.bubble);
    	bubble = resizeBitmap(bubble,b.getWidth()+14,b.getHeight()+14);
    	
    	Bitmap f = Bitmap.createBitmap(b.getWidth()+14, b.getHeight()+14, Bitmap.Config.ARGB_8888);
    	Canvas c = new Canvas(f);
    	Paint paint= new Paint();
    	paint.setAlpha(200);
    	c.drawBitmap(b, 6, 6, new Paint());
    	c.drawBitmap(bubble, 0, 0,paint);
    	bubble = null;
    	return f;
    }
    
    public Bitmap overlay(Bitmap b, Bitmap icon, Bitmap mask, int corner){
    	icon = resizeBitmap(icon,b.getWidth()/3,b.getHeight()/3);

    	int x, y;
    	
    	switch(corner){
    	case 1: x = b.getWidth()-icon.getWidth(); y = 0; break;
    	case 2: x = b.getWidth()-icon.getWidth(); y = b.getHeight()-icon.getHeight(); break;
    	case 3: x = 0; y = b.getHeight()-icon.getHeight(); break;
    	default: x = b.getWidth()-icon.getWidth(); y = 0; break;
    	}
    	
    	Bitmap f = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
    	Canvas c = new Canvas(f);
    	Paint paint= new Paint();
    	paint.setAlpha(150);
    	c.drawBitmap(b, 0, 0, new Paint());
    	c.drawBitmap(icon, x, y, paint);
    	
    	if(mask != null){
        	mask = resizeBitmap(mask,b.getWidth()/3,b.getHeight()/3);
        	c.drawBitmap(mask, x, y, new Paint());
    	}
    		
    	return f;
    }
    
    public Bitmap overlayFB(Bitmap b){
    	Bitmap icon = BitmapFactory.decodeResource(res, R.drawable.facebook_icon);
    	icon = resizeBitmap(icon,b.getWidth()/4,b.getHeight()/4);
    	
    	Bitmap f = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
    	Canvas c = new Canvas(f);
    	Paint paint= new Paint();
    	paint.setAlpha(150);
    	c.drawBitmap(b, 0, 0, new Paint());
    	c.drawBitmap(icon, 0, 0, paint);
    	icon = null;
    	return f;
    }
    public Bitmap overlayFS(Bitmap b){
    	Bitmap icon = BitmapFactory.decodeResource(res, R.drawable.foursquareicon);
    	icon = resizeBitmap(icon,b.getWidth()/4,b.getHeight()/4);
    	
    	Bitmap f = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
    	Canvas c = new Canvas(f);
    	Paint paint= new Paint();
    	paint.setAlpha(150);
    	c.drawBitmap(b, 0, 0, new Paint());
    	c.drawBitmap(icon, 0, 0, paint);
    	icon = null;
    	return f;
    }
    
    public static Bitmap GetDefaultImage(String type){
    	Bitmap bitmap = null;
    	//change these
    	if(type.equalsIgnoreCase(Bubble.FB_FRIENDS_EVENT)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_RECENT_CHECKIN)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_NEARBY_CHECKIN)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_RECENT_CHECKIN)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_NEARBY_CHECKIN)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FB_TRENDING_PLACE)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_TRENDING_VENUE)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_SPECIAL)){

    	}
    	else if(type.equalsIgnoreCase(Bubble.FS_TODO)){

    	}
    	else{

    	}
    	return bitmap;
    }
}