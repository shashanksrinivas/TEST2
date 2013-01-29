package com.CPS.test2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class ColorBall  {
 private Bitmap imgValid; // the image of the ball
 private Bitmap imgInvalid;
 private int coordX = 0; // the x coordinate at the canvas
 private int coordY = 0; // the y coordinate at the canvas
 private int id; // gives every ball his own id, for now not necessary
 private boolean enabled = false;
 private static int count = 1;
 private boolean goRight = true;
 private boolean goDown = true;
 private boolean lineTo[] = new boolean[10];
 private boolean valid = true;
 private String ltlString = "";
 
 
	public ColorBall(Context context, int drawable1, int drawable2) {

		BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        imgValid = BitmapFactory.decodeResource(context.getResources(), drawable1); 
        imgInvalid = BitmapFactory.decodeResource(context.getResources(), drawable2); 
        id=count;
        ltlString.concat(String.valueOf(id));
        ltlString = "F(" + ltlString + ")";
		count++;

	}
	
	public ColorBall(Context context, int drawable1, int drawable2, Point point) {

		BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        imgValid = BitmapFactory.decodeResource(context.getResources(), drawable1);
        imgInvalid = BitmapFactory.decodeResource(context.getResources(), drawable2);
        id=count;
        //ltlString.concat(String.valueOf(id));
        ltlString = "F(" + String.valueOf(id) + ")";
		count++;
		coordX= point.x;
		coordY = point.y;

	}
	
	public static int getCount() {
		return count;
	}
	
	void setX(int newValue) {
        coordX = newValue;
    }
	
	public int getX() {
		return coordX;
	}

	void setY(int newValue) {
        coordY = newValue;
   }
	
	public int getY() {
		return coordY;
	}
	
	public int getID() {
		return id;
	}
	
	public Bitmap getBitmap() {
		if(valid){
		return imgValid;
		}else{
			return imgInvalid;
		}
	}
	
	public int getWidth(){
		return imgValid.getWidth();
	}
	
	public int getHeight(){
		return imgValid.getHeight();
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void enable(){
		enabled=true;
	}
	
	public void disable(){
		enabled=false;
	}
	
	public void setLineTo(int i){
		lineTo[i-1]=true;
	}
	
	public boolean isLineTo(int i){
		return lineTo[i-1];
	}
	
	public void unsetLineTo(int i){
		lineTo[i-1] = false;
	}
	
	public boolean isValid(){
		return valid;
	}
	
	public void setValid(boolean value){
		valid=value;
		if(!valid){
			ltlString = "G not(" + id + ")";
		}else{
			ltlString = "F(" + id + ")";
			
		}
	}
	
	public String getLtlString(){
		return ltlString;
	}
	
	public void moveBall(int goX, int goY) {
		// check the borders, and set the direction if a border has reached
		if (coordX > 270){
			goRight = false;
		}
		if (coordX < 0){
			goRight = true;
		}
		if (coordY > 400){
			goDown = false;
		}
		if (coordY < 0){
			goDown = true;
		}
		// move the x and y 
		if (goRight){
			coordX += goX;
		}else
		{
			coordX -= goX;
		}
		if (goDown){
			coordY += goY;
		}else
		{
			coordY -= goY;
		}
		
	}
	
}
