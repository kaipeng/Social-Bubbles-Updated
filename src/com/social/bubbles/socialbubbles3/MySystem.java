package com.social.bubbles.socialbubbles3;
 
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;


import com.badlogic.gdx.math.Vector2;
import com.social.bubbles.Bubble;
import com.social.bubbles.Bubble.SubBubble;
import com.social.bubbles.BubbleLauncher;
import com.social.bubbles.BubbleManager;
import com.social.bubbles.Main;
import com.social.bubbles.R;
import com.social.bubbles.Settings;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


/**
 * @author Mike NTG
 *Adapted from:
 */
 
/**
 * Android Live Wallpaper painting thread Archetype
 * @author antoine vianey
 * GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 */
public class MySystem extends Thread {
 
    /** Reference to the View and the context */
    private SurfaceHolder surfaceHolder;
    private Context context;
 
    /** State */
    private boolean wait;
    private boolean run;
 
    /** Dimensions */
    private int width;
    private int height;
 
    /** Time tracking */
    private long previousTime;
    private long currentTime;
	
	/** Options (subbubble) display **/
	private boolean option;
	
	/** Particle **/
	private  List<MyParticle> mParticleSystem = Collections.synchronizedList(new LinkedList<MyParticle>());
	private  List<MyParticle> mSubParticleSystem = Collections.synchronizedList(new LinkedList<MyParticle>());
	private  List<MyParticle> mParticleSystemQueue = Collections.synchronizedList(new LinkedList<MyParticle>());

	private Random ran;
	
	private int touchedParticleIndex;
		
	Bitmap img;
	Resources res;
	SocialBubblesService socialEngine;
    CreateBubbleHandler createBubbleHandler;
	
    public MySystem(SurfaceHolder surfaceHolder, 
            Context context, Resources res, SocialBubblesService se) {
    	
    	//myPhysics = new MyPhysics(context, mParticleSystem);

    	createBubbleHandler = new CreateBubbleHandler();
    	BubbleLauncher.setCreateBubbleHandler(createBubbleHandler);
    	
    	// keep a reference of the context and the surface
        // the context is needed if you want to inflate
        // some resources from your livewallpaper .apk
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        // don't animate until surface is created and displayed
        this.wait = true;
        
        //gets the resource reference
        this.res= res;
        ran= new Random();
        
        //it will help to launch secondary apps
        socialEngine= se;
        
        //getting the dimensions
        this.width= context.getResources().getDisplayMetrics().widthPixels;
        this.height= context.getResources().getDisplayMetrics().heightPixels;
        

		//Initializes Sub BUBBLES
		//CREATES THE INITIAL BUBBLES
		createInitialBubbles();
		
		option= false;
		
		touchedParticleIndex = -1;
				
//		if(mParticleSystem.size()>= 2){
//			p= new MyParticle(150,0,0xFFFFFFFF,width,width, "bubble");
//			mParticleSystem.add(p);
//		}
    	backgroundBitmap = BitmapFactory.decodeResource(res, backgroundBitmaps[ran.nextInt(3)]);
    	highlightImg= BitmapFactory.decodeResource(res, R.drawable.highlight_img);
	    }
    
	

    /**
     * Pauses the live wallpaper animation
     */
    public void pausePainting() {
        this.wait = true;
        synchronized(this) {
            this.notify();
        }
    }
    
    Bitmap highlightImg;
     Bitmap backgroundBitmap;
	int []backgroundBitmaps= new int[]{
	R.drawable.cloud_gate_day,
	R.drawable.cloud_gate_afternoon,
	R.drawable.cloud_gate_evening,
	R.drawable.cloud_gate_night,
	R.drawable.cloud_gate_day1,
	};
	
    /**
     * Resume the live wallpaper animation
     */
    public void resumePainting() {
    	int i = ran.nextInt(backgroundBitmaps.length);
    	backgroundBitmap = BitmapFactory.decodeResource(res, backgroundBitmaps[i]);

        this.wait = false;
        synchronized(this) {
            this.notify();
        }
    }
 
    /**
     * Stop the live wallpaper animation
     */
    public void stopPainting() {
        this.run = false;
        synchronized(this) {
            this.notify();
        }
    }
 
    long lastTouched;
    @Override
    public void run() {
        this.run = true;
        Canvas c = null;
        while (run) {
            try {
                c = this.surfaceHolder.lockCanvas(null);
                synchronized (this.surfaceHolder) {
            		if(System.currentTimeMillis()-lastTouched> 250){ // not sure if the  time is correct------------------------
            	    	option = false;
            		}
                    //if (!option){
                    //}
                    if (!option){
                    	executeAnyActivatedSubBubble();
                    	updatePhysics(mParticleSystem);
                    	doDraw(c, mParticleSystem);
                    	clearQueue();
                    }
                    //Draw only if a par00ticle was touched
                    if (option){
                    	MyParticle touchParticle = mParticleSystem.get(touchedParticleIndex); //---------------------------------------------------
                    	doDrawTouchPoint(c,touchParticle);
						doDrawOptions(c,touchParticle, mSubParticleSystem);
						doCheckIfOptions(c,touchParticle,touchParticle.name, mSubParticleSystem);
						previousTime = currentTime;
					}
                    currentTime = System.currentTimeMillis();
					if(currentTime-previousTime>250){ // not sure if the  time is correct------------------------
						option= false;
					}
                }
            } finally {
                if (c != null) {
                    this.surfaceHolder.unlockCanvasAndPost(c);
                }
            }
            // pause if no need to animate
            synchronized (this) {
                if (wait) {
                    try {
                        wait();
                    } catch (Exception e) {}
                }
            }
			
			// check if schedule next drawing here-.--------------------
        }
    }
 
   

	

	/**
     * Invoke when the surface dimension change
     */
    public void setSurfaceSize(int width, int height) {
        this.width = width;
        this.height = height;
        synchronized(this) {
            this.notify();
        }
    }
 
    // ===========================================================
	// Do Methods
	// ===========================================================
    
    boolean isTouching = false;
    /**
     * Invoke while the screen is touched
     */
    public void doTouchEvent(MotionEvent event) {
        // handle the event here
        // if there is something to animate
        // then wake up
        this.wait = false;
        int index= -1;
		isTouching = true;

        //THIS IS TO CHANGE TOUCHED BUBBLE INDEX ONLY IF IT HAS BEEN 400MS.
        //THIS IS TO AVOID THE PROBLEM OF SELECTING OTHER BUBBLES IF WE ACCIDENTALLY MOVE OUR FINGER FAST ENOUGH SO THAT
        //IT LEAVES THE CURRENT SELECTED BUBBLE
        if(System.currentTimeMillis()-lastTouched> 400){
        //gets the index of the particle touched on screen, returns -1 otherwise
        index= isInParticleSystem(event.getX(),event.getY(), mParticleSystem);
        touchedParticleIndex= index;
        }
        else{
        	index = touchedParticleIndex;
        }
        //if touched a particle then index is >= 0
        option= index >= 0;


        //move the selected particle around
        if (option){
	    	synchronized(this.mParticleSystem){
	    		MyParticle touchParticle = mParticleSystem.get(index);
//				p.setX(event.getX());   // it does not center the picture.
//				p.setY(event.getY());
	    		
	    		//it centers the picture in this point
	    		touchParticle.setX(event.getX()-(touchParticle.getWidth())/2);   
	    		touchParticle.setY(event.getY()-(touchParticle.getHeight()/2));
	    		
				if(System.currentTimeMillis()-lastTouched> 200){ // not sure if the  time is correct------------------------
			    	updateSubBubbles(touchParticle.bubble);
					configureSubParticles(event.getX(), event.getY(), this.mSubParticleSystem);
		    		touchParticle.lastx=touchParticle.x;
		    		touchParticle.lasty=touchParticle.y;
				}
	    		touchParticle.vy=(float) (Settings.Bubble.BUBBLE_SPEED*.5*((touchParticle.y-touchParticle.lasty)/height)+1);					
	    		touchParticle.vx=(float) (Settings.Bubble.BUBBLE_SPEED*1.5*(touchParticle.x-touchParticle.lastx)/width);				
			}
        }
        
        
        lastTouched = System.currentTimeMillis();

        //this.myPhysics.logPositions();
		
//		//updating the base position  //only when animating the particle to restore position------------------
//		synchronized(this){
//			pBaseX= event.getX();
//		}
//		
//		synchronized(this){
//			pBaseY= event.getY();
//		}
//		
//        synchronized(this) {
//            notify();
//        }
    }
 
	private void updateSubBubbles(Bubble selectedBubble){
		//TODO implement this
		mSubParticleSystem.clear();
		//test code
		//img= BitmapFactory.decodeResource(res, R.drawable.sub_todo);		// res.getDrawable(R.drawable.bubble);
		//mSubParticleSystem.add( new MyParticle(0,0,0xFFFFFFFF,img.getWidth(),img.getHeight(), "todo",img));
		
		if (selectedBubble==null){
			return;
		}
		//test
		LinkedList<SubBubble> subBubbles = selectedBubble.getSubBubbles(context);
		for (SubBubble sb : subBubbles){
			Bitmap image = sb.getImage();
			mSubParticleSystem.add(new MyParticle(0,0,0xFFFFFFFF,image.getWidth(),image.getHeight(),sb.getActionDescription(),image,sb));
		}
	}

	//Arrange SubBubbles on a parabola with focus at X position of original touch
	private void configureSubParticles(float x, float y, List<MyParticle> mSubParticleSystem2) {
		double baseY;
		double orientation;
		if(y > height/2.5){	//bottom half of screen
			orientation = -1;
		}else{				//top half of screen
			orientation = 1;
		}
		Log.d("Configure SubParticles: ", " Orientation = " + orientation);
		if(mSubParticleSystem2.size()<=5){
			for(int i=0; i< mSubParticleSystem2.size(); i++){
				//Spread Bubbles over width of screen
				int newX = (int) (width/mSubParticleSystem2.size() * (i+.5) - mSubParticleSystem2.get(i).getWidth()/2);
				//Set Y according to parabola with focus at original x
				mSubParticleSystem2.get(i).setX(newX);

				newX+= mSubParticleSystem2.get(i).getWidth();
				int newY = (int) (y - 40+ Math.pow((mSubParticleSystem2.size()), .2)*(orientation*(230-mSubParticleSystem2.size()*10 -(newX-x)*(newX-x)/(1.4*width))));
				mSubParticleSystem2.get(i).setY(newY);
				Log.d("Configure SubParticles", "New Y = " + newY + " New X = " + newX);
			}
		}
		else{
			int half = mSubParticleSystem2.size()-mSubParticleSystem2.size()/2;
			double topOrientation;
			double bottomOrientation;
			int bottomOffset=0;

			if(y >= height*2/3){	//bottom third of screen
				topOrientation = -1;
				bottomOrientation = -1;
				bottomOffset = 120;
			}
			else if(y > height*1/3 && y < height*2/3){	//middle third of screen
				topOrientation = -1;
				bottomOrientation = 1;
			}
			else{	//top third of screen
				topOrientation = 1;
				bottomOrientation = 1;
				bottomOffset = 120;
			}
			
			for(int i=0; i< half; i++){
				//Spread Bubbles over width of screen
				int newX = (int) (width/half * (i+.5) - mSubParticleSystem2.get(i).getWidth()/2);
				//Set Y according to parabola with focus at original x
				mSubParticleSystem2.get(i).setX(newX);

				int newY = (int) (y - 40+ Math.pow((half), .2)*(topOrientation*(230-(half)*10 -(newX-x)*(newX-x)/(1.8*width))));
				newX+= mSubParticleSystem2.get(i).getWidth();
				mSubParticleSystem2.get(i).setY(newY);
				Log.d("Configure SubParticles", "New Y = " + newY + " New X = " + newX);
			}
			for(int i=0; i< mSubParticleSystem2.size()-half; i++){
				//Spread Bubbles over width of screen
				int newX = (int) (width/(mSubParticleSystem2.size()-half) * (i+.5) - mSubParticleSystem2.get(i+half).getWidth()/2);
				//Set Y according to parabola with focus at original x
				mSubParticleSystem2.get(i+half).setX(newX);

				int newY = (int) (y - 40+ Math.pow((mSubParticleSystem2.size()-half), .2)*(bottomOrientation*(bottomOffset+230-(mSubParticleSystem2.size()-half)*9 -(newX-x)*(newX-x)/(1.8*width))));
				newX+= mSubParticleSystem2.get(i+half).getWidth();
				mSubParticleSystem2.get(i+half).setY(newY);
				Log.d("Configure SubParticles", "New Y = " + newY + " New X = " + newX);
			}
		}
	}

	/**
     * Do the actual drawing stuff : DRAWS ALL THE PARTICLES
     */
    private void doDraw(Canvas canvas, List<MyParticle> mParticleSystem2) {
    	if(canvas==null)
    		return;
		 Paint paint = new Paint();
		 canvas.drawBitmap(backgroundBitmap, new Rect(0,0, Math.min(backgroundBitmap.getWidth(), width), Math.min(backgroundBitmap.getHeight(), height)), new Rect(0,0, width, height), paint);
		 
		 
		 //CALL AND DISPLAY NEXT BUBBLE HERE
	     synchronized(mParticleSystem2){
			 for (MyParticle p: mParticleSystem2){
				 if(p.y<=height){
					 paint.setAlpha(180);
					 canvas.drawBitmap(p.getImg(), p.getX(), p.getY(), paint);
				 }
			 }
	     }
	}
    
    //DRAWS THE SUB-BUBBLES
    private void doDrawOptions(Canvas c, MyParticle bubble, List<MyParticle> mSubParticleSystem2) {
    	Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(20);

		//c.drawColor(0xff000000);

		synchronized(mSubParticleSystem2){
		//DRAW THE SUB BUBBLES HERE
		for(MyParticle subBubble: mSubParticleSystem2){
			c.drawBitmap(subBubble.getImg(), subBubble.getX(), subBubble.getY(), new Paint());
			if(subBubble.isHighlighted){
				c.drawBitmap(highlightImg, subBubble.getX(), subBubble.getY(), new Paint());
				//DRAW SELECTED SUBBUBBLE DESCRIPTION
				paint.setColor(Color.GRAY);
				c.drawText(subBubble.getName(), width/2+1, subBubble.getY()+subBubble.getHeight()/2 + (bubble.y>height/2?-2:-1)*100+1, paint);
				paint.setColor(Color.WHITE);
				c.drawText(subBubble.getName(), width/2, subBubble.getY()+subBubble.getHeight()/2 + (bubble.y>height/2?-2:-1)*100, paint);
				}
			}
		}
	}
    
    private void doDrawTouchPoint(Canvas c, MyParticle tp){
		Paint paint = new Paint();
		paint.setColor(tp.getColor());
		c.drawBitmap(backgroundBitmap, new Rect(0,0, Math.min(backgroundBitmap.getWidth(), width), Math.min(backgroundBitmap.getHeight(), height)), new Rect(0,0, width, height), paint);
		
		//Draw Current Bubble BIG!
		if(tp.bubble!=null){
			Bitmap bubbleBitmap = BubbleLauncher.getImage(tp.bubble.getImageUrl(), tp.bubble.getType());
			if(bubbleBitmap!=null){
				c.drawBitmap(bubbleBitmap,  new Rect(0,0, Math.min(bubbleBitmap.getWidth(), width), Math.min(bubbleBitmap.getHeight(), height)), new Rect((width-2*bubbleBitmap.getWidth())/2, 50, width-(width-2*bubbleBitmap.getWidth())/2, 2*(bubbleBitmap.getHeight())+50), paint);
		
			}
		}
		
		//Draw actual Bubble
		c.drawBitmap(tp.getImg(), tp.getX(), tp.getY(), new Paint());
		
		int orientation;
		if(tp.y > height/2){	//bottom half of screen
			orientation = -1;
		}else{				//top half of screen
			orientation = 1;
		}
		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);

		//DRAW DESCRIPTIVE TEXT
		if(tp.bubble!=null){
			int offset=0;
			paint.setColor(Color.GRAY);
			c.drawText(tp.bubble.getDescription().substring(0, Math.min(tp.bubble.getDescription().length(), 29)), width/2+1, tp.y+50+250*orientation+1, paint);
			paint.setColor(Color.WHITE);
			c.drawText(tp.bubble.getDescription().substring(0, Math.min(tp.bubble.getDescription().length(), 29)), width/2, tp.y+50+250*orientation, paint);
			if(tp.bubble.getDescription().length()>29){
				offset+=34;
				paint.setColor(Color.GRAY);
				c.drawText(tp.bubble.getDescription().substring(29, Math.min(tp.bubble.getDescription().length(), 59)), width/2+1, tp.y+50+250*orientation+offset+1, paint);
				paint.setColor(Color.WHITE);
				c.drawText(tp.bubble.getDescription().substring(29, Math.min(tp.bubble.getDescription().length(), 59)), width/2, tp.y+50+250*orientation+offset, paint);
			}
			paint.setTextSize(24);
			paint.setColor(Color.GRAY);
			c.drawText(tp.bubble.getTimeAgo(), width/2+1, tp.y+50+250*orientation + offset+34+1, paint);				
			paint.setColor(Color.WHITE);
			c.drawText(tp.bubble.getTimeAgo(), width/2, tp.y+50+250*orientation + offset+34, paint);
			offset+=34;
			if(tp.bubble.getDistance()>=0){
				int dist = tp.bubble.getDistance();
				String unit = "m";
				if(dist>2000){
					dist = dist/1000;
					unit = "km";
				}
				paint.setColor(Color.GRAY);
				c.drawText(dist + unit+" away", width/2+1, tp.y+50+250*orientation + offset+28+1, paint);
				paint.setColor(Color.WHITE);
				c.drawText(dist + unit+" away", width/2, tp.y+50+250*orientation + offset+28, paint);
			}
		}
	}
    
    private void executeAnyActivatedSubBubble(){
    	
		for (MyParticle subParticle : mSubParticleSystem){
			if (subParticle!=null && subParticle.isHighlighted && System.currentTimeMillis()-lastDropped > 1000){
				subParticle.subbubble.onClick();
				lastDropped = System.currentTimeMillis();
				subParticle.isHighlighted=false;
				break;
			}
		}
    }
    
    //EXECUTES THE OPTION ACTION: ON DROP
    private long lastDropped=0;
    private void doCheckIfOptions(Canvas c, MyParticle p, String bubbleId, List<MyParticle> mSubParticleSystem2) {
    	//boolean result= false;
		//result= isInSystem(p.getX(),p.getY(),op1);
		
		//CHECKS WHICH OPTION TO EXECUTE AND LAUNCHS IT
		
//		if(isInSystem(p.getX(),p.getY(),op1)){  //does not apply to pictures
		for (MyParticle subParticle : mSubParticleSystem2){
			if (isInSystem(p.getX()+(p.getWidth()/2),p.getY()+(p.getHeight()/2),subParticle)){
				//TODO implement this
				subParticle.isHighlighted=true;
			}else
				subParticle.isHighlighted=false;
		}
	}
    
    public void displayText(String text){
    	Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(20);
		Canvas c = surfaceHolder.lockCanvas(null);
		c.drawText(text, 150, 400, paint);
    }
    
    // ===========================================================
	// Physics
	// ===========================================================
 
    /**
     * Update the animation, sprites or whatever.
     * If there is nothing to animate set the wait
     * attribute of the thread to true
     */
    private void updatePhysics(List<MyParticle> mParticleSystem) {
        // if nothing was updated :
        // this.wait = true;
    	
    	synchronized(mParticleSystem){
    	for (MyParticle p : mParticleSystem){
    		//setBounceAcceleration(p);
    		move(p);
    	}
    	}
    	
    }
    //sets the position for p
    private void move(MyParticle particle){
    	particle.x+=particle.vx;
    	particle.y+=particle.vy;
    	
    	if(particle.y>height*1.3){
    		particle.y=0;
    	}
    	else if(particle.y<0){
    		particle.y=(float) (height*1.3);
    	}
    	if(particle.x>width-particle.width){
    		particle.vx*=-1;
    		particle.x=width-particle.width;
    	}
    	else if(particle.x<0){
    		particle.vx*=-1;
    		particle.x=0;
    	}
    	
//		float speed = particle.vx*particle.vx + particle.vy*particle.vy;
//		if(speed > MAX_SPEED){
//			particle.vx*=MAX_SPEED/speed;
//			particle.vy*=MAX_SPEED/speed;
//		}
		if(Math.abs(particle.vx) > Settings.Bubble.BUBBLE_SPEED/2){
			particle.vx*=Math.sqrt(Settings.Bubble.BUBBLE_SPEED/(2*Math.abs(particle.vx)));
		}
    }
    
    //sets the acceleration from collision
    private void setBounceAcceleration(MyParticle particle){
    	float x=particle.x;
    	float y=particle.y;
	   	for(MyParticle p : mParticleSystem){
	   		float dx = (x-p.x);
	   		float dy = (y-p.y);
	   		//if contact
	   		if((Math.sqrt(dx*dx+dy*dy) < p.getWidth())){
	   			//dx = -(p.getWidth()-(x-p.x))/p.getWidth();
	   			//dy = -(p.getHeight()-(y-p.y))/p.getHeight();
//	   			particle.vx+=dx;
//	   			p.vx-=dx;
//	   			float z = (float) ((1-Math.cos(Math.atan(dy/dx)))*p.getWidth());
//	   			
//	   			particle.x+=p.getWidth()-dx;
//	   			p.x-=p.getWidth()-dx;
	   			particle.vx*=-1;
	   			p.vx*=-1;
	   			//particle.vy+=dy;
	   			
	   			
//	   			p.vx-=dx;
//	   			p.vy-=dy;
//	   			
//	   			speed = p.vx*p.vx + p.vy*p.vy;
//	   			if(speed > MAX_SPEED){
//	   				p.vx*=MAX_SPEED/speed;
//	   				p.vy*=MAX_SPEED/speed;
//	   				}
	   		}
	   	}
    }
	
	
    // ===========================================================
	// additional Methods
	// ===========================================================

	// changes the particle width and height
	public void setWidth(int w) {
		for (MyParticle p : this.mParticleSystem){
			p.setHeight(w);
			p.setWidth(w);
		}
	}
	
	//checks if the touch position is within a particle
    private boolean isInSystem(float x, float y, MyParticle p) {
		boolean result= false;
		
		result = p.isInParticle(x, y);
		
		return result;
	}
    
    //checks if the touch position is within a particle
    private int isInParticleSystem(float x, float y, List<MyParticle> mParticleSystem2) {
		int index= -1;
	    ListIterator<MyParticle> itr = mParticleSystem2.listIterator();
	    while(itr.hasNext()){
	    	itr.next();
	    };
	    while(itr.hasPrevious()){
	    	int in = itr.previousIndex();
	    	MyParticle p = itr.previous();
			if (p.isInParticle(x, y)){
				return in;
			}
	    }
				
		for (MyParticle p: mParticleSystem2){
			if (p.isInParticle(x, y)){
				return mParticleSystem2.indexOf(p);
			}
		}		
		return index;
	}
    
    final float MAX_SPEED=1;
    final float MIN_SPEED=(float).5;


    private boolean areTouching(float x, float y){
	   	for(MyParticle p : mParticleSystem){
	   		float dx = (x-p.x);
	   		float dy = (y-p.y);
	   		//if contact
	   		if((Math.sqrt(dx*dx+dy*dy) < p.getWidth())){
	   			return true;
	   		}
	   	}
	   	return false;
    }
    
   private void findSpace(MyParticle newbie){
	   float x = newbie.x;
	   float y = newbie.y;
	   int xinc = 20;
	   int yinc = (int) (80*(ran.nextInt(1)-.5));
	   
	   for(int i = 0; i < width/xinc * Math.abs(1.3*height/yinc); i++){		   
		   
		   if(!areTouching(x, y)){
	   			newbie.x=x;
	   			newbie.y=y;
	 		   Log.d("FINDING SPACE for ", "Success on TRY "+i);
	   			return;
		   	}
	   		x+=xinc;
	   		if(x>width-newbie.getWidth()){
	   			x=0; y+=yinc;
	   		}
	   		if(y>height*1.3){
	   			y=0; x=0;
	   		}
	   		if(y<0){
	   			y=(float) (height*1.3); x=width;
	   		}

	   }
			newbie.x=ran.nextInt(width-newbie.width);
			newbie.y=ran.nextInt((int) (1.3*height));
    }
    
 
    //creates initial set of particles
    public void createInitialBubbles (){
    	MyParticle p;
	    //loading images to the array
		int []initialBubbleImages= new int[]{
		R.drawable.sub_facebook,
		R.drawable.sub_foursquare,
		R.drawable.sub_todo,
		R.drawable.sub_checkin,
		R.drawable.sub_search,
		R.drawable.sub_call,
		R.drawable.sub_sms,
		R.drawable.sub_googletalk,
		R.drawable.sub_facebook,
		R.drawable.sub_foursquare,
		R.drawable.sub_todo,
		R.drawable.sub_checkin,
		R.drawable.sub_search,
		R.drawable.sub_call,
		R.drawable.sub_sms,
		R.drawable.sub_googletalk
		};
		
		for (int i= 0; i<initialBubbleImages.length; i++){
			img= BitmapFactory.decodeResource(res, initialBubbleImages[i]);

    		int w= ran.nextInt(Math.max(1, width));
			int h= ran.nextInt(Math.max(1, (int)1.3*height));
			p= new MyParticle(w,h,0xFFFFFFFF,img.getWidth(),img.getHeight(), "initial"+i,img, (float)(Settings.Bubble.BUBBLE_SPEED*(.25+ran.nextFloat()*.7)), (float)(Settings.Bubble.BUBBLE_SPEED*(.5-ran.nextFloat())));
			Log.d("New Particle Properties: ", "X="+p.getX()+ " Y="+p.getY() + " Width= "+p.getWidth() + " Height="+p.getHeight() + " Name=" + p.getName());

			addBubble(p);
		}
    	
//    	//createInitBubbles(this.mParticleSystem,n);
//    	//createInitBubbles(n);
//    	MyParticle p;
//    	mParticleSystem.clear();
//    	for (int i= 0; i<n; i++){
//    		int w= 100;//ran.nextInt(width);
//			int h= 100;//ran.nextInt(height);
//			img= BitmapFactory.decodeResource(res, images[i]);
//			p= new MyParticle(w,h,0xFFFFFFFF,width,width, "bubble",img);
//    		this.mParticleSystem.add(p);   		
//    	}
    }
    
    //by kai
    //Create a bubble based on a particular ID with an random initial position
    public void createBubble(String id){
    	MyParticle p;
		Bitmap pic = BubbleLauncher.getImage(id, BubbleManager.cache.get(id).getType());
		Bubble bubble = BubbleManager.getBubble(id);

    		int w= ran.nextInt(Math.max(1, width-pic.getWidth()));
			int h= ran.nextInt(Math.max(1, (int)1.3*height));
			p= new MyParticle(w,h,0xFFFFFFFF,pic.getWidth(),pic.getHeight(), id, pic, bubble, (float)(Settings.Bubble.BUBBLE_SPEED*(.25+ran.nextFloat()*.7)), (float)(Settings.Bubble.BUBBLE_SPEED*(.5-ran.nextFloat())));
			Log.d("New Particle Properties: ", "X="+p.getX()+ " Y="+p.getY() + " Width= "+p.getWidth() + " Height="+p.getHeight() + " Name=" + p.getName());

		addBubble(p);
    }
    
    private void addBubble(MyParticle p){
    	if(option){
    		this.mParticleSystemQueue.add(p);
    	}else{
    		this.findSpace(p);
    		this.mParticleSystem.add(p);
    		
        	if(this.mParticleSystem.size() > Settings.Bubble.DISPLAY_QUEUE_SIZE){
        		this.mParticleSystem.remove(0);

        	}
    	}
    }
    private void clearQueue(){
    	if(!option){
    		for(MyParticle p : mParticleSystemQueue){
        		this.mParticleSystem.add(p);
        		
            	if(this.mParticleSystem.size() > Settings.Bubble.DISPLAY_QUEUE_SIZE){
            		this.mParticleSystem.remove(0);

            	}    		}
    		mParticleSystemQueue.clear();
    	}
    }
    
    public class CreateBubbleHandler extends Handler {
        private static final int NEW_BUBBLE = 0;

		public void handleMessage(Message msg) {
                switch (msg.what) {
                case NEW_BUBBLE:
	                   createBubble((String) msg.obj);      
                }  
		}
    } 
}