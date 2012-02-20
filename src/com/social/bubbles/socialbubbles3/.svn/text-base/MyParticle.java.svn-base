package com.social.bubbles.socialbubbles3;

import org.anddev.andengine.opengl.texture.source.ITextureSource;

import com.social.bubbles.Bubble;
import com.social.bubbles.Bubble.SubBubble;

import android.graphics.Bitmap;

/**
 * @author Mike NTG
 *
 */
 
public class MyParticle implements ITextureSource{

	protected float x;
	protected float y;
	
	protected float lastx;
	protected float lasty;
	
	protected float vx=1;
	protected float vy;
	
	protected int color;
	protected int width;
	protected int height;
	protected String name;
	protected String url;
	protected Bitmap img;
	protected Bubble bubble;
	protected SubBubble subbubble;
	protected boolean isHighlighted;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public MyParticle(float x, float y, int color, int width, int height, String name,Bitmap b, Bubble bubble, float vy, float vx) {
//		this.x = x-(width/2);
//		this.y = y-(height-2);
		this.x = x;
		this.y = y;
		this.color = color;
		this.height = height;
		this.width = width;
		this.setName(name);
		this.img= b;
		this.bubble = bubble;
		this.subbubble = null;
		this.isHighlighted = false;
		
		this.vy=vy;
		this.vx=vx;

	}
	public MyParticle(float x, float y, int color, int width, int height, String name,Bitmap b, SubBubble subbubble) {
//		this.x = x-(width/2);
//		this.y = y-(height-2);
		this.x = x;
		this.y = y;
		this.color = color;
		this.height = height;
		this.width = width;
		this.setName(name);
		this.img= b;
		this.bubble = null;
		this.subbubble = subbubble;
		this.isHighlighted = false;

		
		this.vy=0;
		this.vx=0;

	}
	public MyParticle(float x, float y, int color, int width, int height, String name,Bitmap b, float vy, float vx) {
		
		this.x = x;
		this.y = y;
		this.color = color;
		this.width = width;
		this.height = height;
		this.name = name;
		this.img = b;
		this.bubble = null;
		this.subbubble = null;
		this.isHighlighted = false;

		
		this.vy=vy;
		this.vx=vx;
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public int getColor() {
		return this.color;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public String getName() {
		return name;
	}
	
	public Bitmap getImg() {
		return img;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public void setColor(int c) {
		this.color = c;
	}
	
	public void setWidth(int w) {
		this.width = w;
	}
	
	public void setHeight(int h) {
		this.height = h;
	}
	
	public void setImg (Bitmap b){
		img= b;
	}
	
	public boolean isInParticle(float x, float y){
		boolean result= false;
		
		//Check the bounding box. check if it is circle or square, bounding box me be different
		
//		if ((x>= this.x-width)&& (x<= this.x+width)){
//			if ((y>= this.y-height)&& (y<= this.y+height)){
//				result= true;
//			}
//			
//		}
		
		
		//THIS ONE ONLY VALID FOR BUBBLES
		//result= ((x>= this.x-width)&& (x<= this.x+width)) && ((y>= this.y-height)&& (y<= this.y+height)); // this if (x,Y)= center and width is radius
		
		result= ((x>= this.x)&& (x<= this.x+width)) && ((y>= this.y)&& (y<= this.y+height)); // this if (x,y)= top/left width ad height
		
		
		
		
		return result;
	}
	
	public Bitmap onLoadBitmap(){
		return this.getImg();
	}
	

    public ITextureSource clone(){
    	ITextureSource a = new MyParticle(x, y, color, width, height, name, img, vy, vx);
    	return a;
    }

}
