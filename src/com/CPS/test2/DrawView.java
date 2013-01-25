package com.CPS.test2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View implements OnGestureListener{
	private ColorBall[] colorballs = new ColorBall[10]; // array that holds the
														// balls
	private int balID = 0; // variable to know what ball is being dragged
	Paint paint = new Paint();
	GestureDetector gestureDetector;
	
	

	public DrawView(Context context, AttributeSet attributeset) {
		super(context, attributeset);
		setFocusable(true); // necessary for getting the touch events
		gestureDetector = new GestureDetector(this);
		Point point = new Point();
		point.x = 0;
		point.y = 20;
		for (int i = 0; i < 10; i++) {
			point.x = point.x + 50;
			colorballs[i] = new ColorBall(context, R.drawable.bol_rood, point);
		}

	}

	

	// the method that draws the balls
	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.drawColor(0xFFCCCCCC); //if you want another background color
		// anvas.drawBitmap(MainActivity.bmp,0,0,null);
		// draw the balls on the canvas
		// for (ColorBall ball : colorballs) {
		// canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(), null);
		// }
		if (MainActivity.mapSelected) {
			canvas.drawBitmap(MainActivity.scaledBitmap, MainActivity.matrix,
					null);
		}
		for (int i = 0; i < MainActivity.numWaypoints; i++) {

			canvas.drawBitmap(colorballs[i].getBitmap(), colorballs[i].getX(),
					colorballs[i].getY(), null);

			canvas.drawText(String.valueOf(colorballs[i].getID()),
					colorballs[i].getX() + colorballs[i].getWidth() / 2,
					colorballs[i].getY() + colorballs[i].getHeight() / 2, paint);
		}

	}

	// events when touching the screen
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);

	}



	@Override
	public boolean onDown(MotionEvent event) {
		

		int X = (int) event.getX();
		int Y = (int) event.getY();

		
			balID = 0;
			for (int i = 0; i < MainActivity.numWaypoints; i++) {
				// check if inside the bounds of the ball (circle)
				// get the center for the ball
				int centerX = colorballs[i].getX() + 25;
				int centerY = colorballs[i].getY() + 25;

				// calculate the radius from the touch to the center of the ball
				double radCircle = Math
						.sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
								* (centerY - Y)));

				// if the radius is smaller then 23 (radius of a ball is 22),
				// then it must be on the ball
				if (radCircle < 23) {
					balID = colorballs[i].getID();
					break;
				}

				// check all the bounds of the ball (square)
				// if (X > ball.getX() && X < ball.getX()+50 && Y > ball.getY()
				// && Y < ball.getY()+50){
				// balID = ball.getID();
				// break;
				// }
			}
			invalidate();
			return true;

		
	}



	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		int X = (int) e2.getX();
		int Y = (int) e2.getY();
		// move the balls the same as the finger
					if (balID > 0) {
						colorballs[balID - 1].setX(X+(int)distanceX - 25);
						colorballs[balID - 1].setY(Y+(int)distanceY - 25);
					}
					invalidate();
					return true;
	}



	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
