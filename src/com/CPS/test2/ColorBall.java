package com.CPS.test2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class ColorBall {
	private Bitmap imgValid; // the image of the ball
	private Bitmap imgInvalid;
	private int coordX = 0; // the x coordinate at the canvas
	private int coordY = 0; // the y coordinate at the canvas
	private int id; // gives every ball his own id, for now not necessary
	private int label;//label of each node
	private boolean enabled = false;
	private static int count = 1;
	private boolean goRight = true;
	private boolean goDown = true;
	private boolean arrowTo[] = new boolean[10];
	private boolean lineTo[] = new boolean[10];
	private boolean valid = true;//whether you can or can't visit a location
	private boolean always = false;//for visiting infinitely often
	private boolean implies = false;
	private boolean and = true;
	private boolean or = false;
	private boolean orMode = false;
	private boolean eventually = true;
	private boolean next = false;
	private boolean until = false;
	private boolean alwaysEventually = true;
	private boolean pickObject = false;
	private boolean dropObject = false;
	private boolean activateSensor = false;
	private boolean deactivateSensor = false;
	private String ltlString = "";
	
	private int clickState = 0;

	public ColorBall(Context context, int drawable1, int drawable2) {

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		imgValid = BitmapFactory.decodeResource(context.getResources(),
				drawable1);
		imgInvalid = BitmapFactory.decodeResource(context.getResources(),
				drawable2);
		id = label = count;
		ltlString.concat(String.valueOf(id));
		ltlString = "F(" + ltlString + ")";
		count++;

	}
	
	public ColorBall(ColorBall another){
		this.activateSensor = another.activateSensor;
		this.always = another.always;
		this.clickState = another.clickState;
		this.coordX = another.coordX;
		this.coordY = another.coordY;
		this.deactivateSensor = another.deactivateSensor;
		this.dropObject = another.dropObject;
		this.enabled = another.enabled;
		this.eventually = another.eventually;
		this.goDown = another.goDown;
		this.goRight = another.goRight;
		this.id = another.id;
		this.imgInvalid = another.imgInvalid;
		this.implies = another.implies;
		this.imgValid = another.imgValid;
		this.orMode = another.orMode;
		this.pickObject = another.pickObject;
		this.valid = another.valid;
		this.ltlString = another.ltlString;
		for(int i=0;i<10;i++){
			this.arrowTo[i] = another.arrowTo[i];
			this.lineTo[i] = another.lineTo[i];
			
		}
	}
	public void copy(ColorBall another){
		this.activateSensor = another.activateSensor;
		this.always = another.always;
		this.clickState = another.clickState;
		this.coordX = another.coordX;
		this.coordY = another.coordY;
		this.deactivateSensor = another.deactivateSensor;
		this.dropObject = another.dropObject;
		this.enabled = another.enabled;
		this.eventually = another.eventually;
		this.goDown = another.goDown;
		this.goRight = another.goRight;
		this.id = another.id;
		this.imgInvalid = another.imgInvalid;
		this.implies = another.implies;
		this.imgValid = another.imgValid;
		this.orMode = another.orMode;
		this.pickObject = another.pickObject;
		this.valid = another.valid;
		this.ltlString = another.ltlString;
		for(int i=0;i<10;i++){
			this.arrowTo[i] = another.arrowTo[i];
			this.lineTo[i] = another.lineTo[i];
			
		}
	}

	public ColorBall(Context context, int drawable1, int drawable2, Point point) {

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		imgValid = BitmapFactory.decodeResource(context.getResources(),
				drawable1);
		imgInvalid = BitmapFactory.decodeResource(context.getResources(),
				drawable2);
		id = label = count;
		// ltlString.concat(String.valueOf(id));
		ltlString = String.valueOf(id);
		setValid(isValid());
		count++;
		coordX = point.x;
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
	public int getLabel() {
		return label;
	}
	
	public void setLabel(int value){
		label = value;
	}

	public Bitmap getBitmap() {
		if (valid) {
			return imgValid;
		} else {
			return imgInvalid;
		}
	}

	public int getWidth() {
		return imgValid.getWidth();
	}

	public int getHeight() {
		return imgValid.getHeight();
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isImplies(){
		return implies;
	}
	
	public void toggleImplies(){
		implies = !implies;
		
	}

	public void setImplies(boolean state){
		implies = state;
	}
	
	public void setEnabled(boolean value) {
		enabled = value;
	}

	public void disable() {
		enabled = false;
	}

	public void toggleArrowTo(int id) {
		arrowTo[id - 1] = !arrowTo[id - 1];
	}

	public boolean isArrowTo(int id) {
		return arrowTo[id - 1];
	}

	public void unsetArrowTo(int i) {
		arrowTo[i - 1] = false;
	}
	
	public void toggleLineTo(int i) {
		lineTo[i - 1] = !lineTo[i - 1];
	}
	
	public boolean isLineTo(int i) {
		return lineTo[i - 1];
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean value) {
		valid = value;

	}
	
	public boolean isAlways() {
		return always;
	}
	
	public void toggleAlways(){
		always = !always;
	}
	
	public boolean isEventually() {
		return eventually;
	}
	
	public boolean isNext() {
		return next;
	}
	
	public void setNext(boolean value){
		next=value;
	}
	
	public void toggleNext(){
		next = !next;
	}
	
	public boolean isUntil() {
		return until;
	}
	
	public void setUntil(boolean value){
		until = value;
	}
	public void toggleUntil(){
		until = !until;
	}
	
	public boolean isAnd() {
		return and;
	}
	
	public void setAnd(boolean value){
		and = value;
	}
	public void toggleAnd(){
		and = !and;
	}
	
	public boolean isOr() {
		return or;
	}
	
	public void setOr(boolean value){
		or = value;
	}
	public void toggleOr(){
		or = !or;
	}
	
	public void toggleEventually(){
		eventually = !eventually;
	}
	
	public void toggleORMode(){
		orMode = !orMode;
	}
	
	public boolean isORMode(){
		return orMode;
	}
	
	public boolean isAlwaysEventually(){
		return alwaysEventually;
	}
	
	public void setAlwaysEventually(boolean value){
		alwaysEventually = value;
		
	}
	
	public void toggleAlwaysEventually(){
		alwaysEventually = !alwaysEventually;
	}
	
	public void setClickState(int value){
		clickState = value;
	}
	
	public int getClickState(){
		return clickState;
	}

	public String getLtlString() {
		/*
		 * if(pickObject && activateSensor){ ltlString = id +
		 * " U (PickObj && ActSen) "; }else if(pickObject){ ltlString = id +
		 * " U PickObj "; }else if(activateSensor){ ltlString = id +
		 * " U ActSen "; }else{ ltlString = id + ""; }
		 */
		ltlString = "";
			if (pickObject) {
			ltlString = !ltlString.isEmpty()?ltlString + " && F( PickObj )":"F( PickObj )";
			}
			if (activateSensor) {
				ltlString = !ltlString.isEmpty()?ltlString + " && F( ActSen )":"F( ActSen )";
			}
			if (dropObject) {
				ltlString = !ltlString.isEmpty()?ltlString + " && F( DropObj )":"F( DropObj )";
			}
			if (deactivateSensor) {
				ltlString = !ltlString.isEmpty()?ltlString + " && F( DeactSen )":"F( DeactSen )";
			}
			
		
			
		
		if(pickObject || dropObject || activateSensor || deactivateSensor){
			ltlString = " U( " + ltlString + ")";
		}
		
		ltlString = id + ltlString;
		
		if (!valid) {
			ltlString = "G NOT(" + ltlString + ")";
		} else {
			ltlString = "F(" + ltlString + ")";
		}
		return ltlString;
	}

	public void togglePickObject() {
		pickObject = !pickObject;

		// setValid(isValid());
	}
	public boolean isPickObject(){
		return pickObject;
	}

	public void toggleDropObject() {
		dropObject = !dropObject;
	}
	public boolean isDropObject(){
		return dropObject;
	}

	public void toggleActivateSensor() {
		activateSensor = !activateSensor;
	}
	public boolean isActivateSensor(){
		return activateSensor;
	}

	public void toggleDeactivateSensor() {
		deactivateSensor = !deactivateSensor;
	}
	public boolean isDeactivateSensor(){
		return deactivateSensor;
	}
	
	public boolean isLeaf(){
		for(int i=0;i<10;i++){
			if(isArrowTo(i))
				return false;
			
		}
		return true;
	}

	

	public void moveBall(int goX, int goY) {
		// check the borders, and set the direction if a border has reached
		if (coordX > 270) {
			goRight = false;
		}
		if (coordX < 0) {
			goRight = true;
		}
		if (coordY > 400) {
			goDown = false;
		}
		if (coordY < 0) {
			goDown = true;
		}
		// move the x and y
		if (goRight) {
			coordX += goX;
		} else {
			coordX -= goX;
		}
		if (goDown) {
			coordY += goY;
		} else {
			coordY -= goY;
		}

	}

}
