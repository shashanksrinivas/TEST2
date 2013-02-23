package com.CPS.test2;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DrawView extends View {
	static ColorBall[] colorballs = new ColorBall[10]; // array that holds the
														// balls
	Graph theGraph = new Graph();// initialize the graph
	static int balID = 0; // variable to know what ball is being dragged
	static int fromBalID = 0;
	static int toBalID = 0;
	static int fromBalIDSingleTap = 0;
	static int toBalIDSingleTap = 0;
	private int DoubleTapOccurredState = 0;
	private int orGoToState = 0;
	private int width;
	private int height;
	Bitmap andBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.and_ball);
	Bitmap orBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.or_ball);
	Bitmap nextBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.ball_next);
	Bitmap eventuallyBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.ball_future);
	// private boolean secondDoubleTapOccurred = false;
	Paint paint = new Paint();
	GestureDetector gestureDetector;
	SimpleOnGestureListener listener = new SimpleOnGestureListener();
	Path mPath = new Path();
	String string = "";

	private QactionObserver mQactionObserver;

	public DrawView(Context context, AttributeSet attributeset) {
		super(context, attributeset);
		setFocusable(true); // necessary for getting the touch events
		gestureDetector = new GestureDetector(new LearnGestureListener());

		Point point = new Point();
		point.x = 0;
		point.y = 20;
		theGraph.addVertex(0); // 0 (start for dfs)
		for (int i = 0; i < 10; i++) {
			point.x = point.x + 50;

			colorballs[i] = new ColorBall(context, R.drawable.bol_groen,
					R.drawable.bol_rood, point);
			theGraph.addVertex(i + 1);
			theGraph.toggleEdge(0, i + 1);

		}
		paint.setDither(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setAntiAlias(true);

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
		paint.setStrokeWidth(3);
		canvas.drawLine(0, 0, 0, 800, paint);

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (colorballs[i].isLineTo(j + 1) && MainActivity.waypoint[i]) {// bitmap
																				// for
																				// OR
					paint.setColor(Color.CYAN);
					canvas.drawLine(colorballs[i].getX() + 25,
							colorballs[i].getY() + 25,
							colorballs[j].getX() + 25,
							colorballs[j].getY() + 25, paint);
					canvas.drawBitmap(orBitmap,
							(colorballs[i].getX() + colorballs[j].getX()) / 2,
							(colorballs[i].getY() + colorballs[j].getY()) / 2,
							paint);
					// bitmap for AND
				} else if (!colorballs[i].isLineTo(j + 1)
						&& MainActivity.waypoint[i] && MainActivity.waypoint[j]
						&& isRoot(i + 1) && isRoot(j + 1)) {
					paint.setColor(Color.MAGENTA);
					canvas.drawLine(colorballs[i].getX() + 25,
							colorballs[i].getY() + 25,
							colorballs[j].getX() + 25,
							colorballs[j].getY() + 25, paint);
					canvas.drawBitmap(andBitmap,
							(colorballs[i].getX() + colorballs[j].getX()) / 2,
							(colorballs[i].getY() + colorballs[j].getY()) / 2,
							paint);
				}
			}
		}

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (colorballs[i].isArrowTo(j + 1) && MainActivity.waypoint[i]) {
					paint.setColor(Color.BLUE);
					if (colorballs[i].isORMode()) {
						paint.setColor(Color.GRAY);
						paint.setPathEffect(new DashPathEffect(new float[] {
								10, 20 }, 0));// TODO:allocate elsewhere later
					} else {
						paint.setColor(Color.GRAY);
						paint.setPathEffect(null);
					}
					canvas.drawLine(colorballs[i].getX() + 25,
							colorballs[i].getY() + 25,
							colorballs[j].getX() + 25,
							colorballs[j].getY() + 25, paint);
					paint.setPathEffect(null);
					if (colorballs[j].isEventually()) {
						canvas.drawBitmap(
								eventuallyBitmap,
								(colorballs[i].getX() + colorballs[j].getX()) / 2,
								(colorballs[i].getY() + colorballs[j].getY()) / 2,
								paint);
					} else {
						canvas.drawBitmap(
								nextBitmap,
								(colorballs[i].getX() + colorballs[j].getX()) / 2,
								(colorballs[i].getY() + colorballs[j].getY()) / 2,
								paint);
					}

					float deltaX = colorballs[j].getX() - colorballs[i].getX();
					float deltaY = colorballs[j].getY() - colorballs[i].getY();
					float frac = (float) 0.05;
					float point_x_1 = colorballs[i].getX()
							+ (float) ((1 - frac) * deltaX * 0.8 + frac * 0.5
									* deltaY);
					float point_y_1 = colorballs[i].getY()
							+ (float) ((1 - frac) * deltaY * 0.8 - frac * 0.5
									* deltaX);
					float point_x_2 = colorballs[j].getX();
					float point_y_2 = colorballs[j].getY();
					float point_x_3 = colorballs[i].getX()
							+ (float) ((1 - frac) * deltaX * 0.8 - frac * 0.5
									* deltaY);
					float point_y_3 = colorballs[i].getY()
							+ (float) ((1 - frac) * deltaY * 0.8 + frac * 0.5
									* deltaX);

					mPath.moveTo((point_x_1) + 25, (point_y_1) + 25);
					mPath.lineTo((point_x_2) + 25, (point_y_2) + 25);
					mPath.lineTo((point_x_3) + 25, (point_y_3) + 25);
					mPath.lineTo((point_x_1) + 25, (point_y_1) + 25);

					paint.setStyle(Paint.Style.FILL);
					canvas.drawPath(mPath, paint);
					mPath.reset();

					/*
					 * if(colorballs[j].isValid()) paint.setColor(Color.GREEN);
					 * else paint.setColor(Color.RED);
					 * canvas.drawCircle(colorballs[j].getX()+25,
					 * colorballs[j].getY()+25, 35, paint);
					 * paint.setColor(Color.YELLOW);
					 * canvas.drawCircle(colorballs[i].getX()+25,
					 * colorballs[i].getY()+25, 30, paint)
					 */;
				}
			}
		}
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(0);
		paint.setTextScaleX(2);
		paint.setTextSize(20);
		for (int i = 0; i < 10; i++) {
			if (MainActivity.waypoint[i]) {
				canvas.drawBitmap(colorballs[i].getBitmap(),
						colorballs[i].getX(), colorballs[i].getY(), null);
				if (colorballs[i].getID() == 10) {
					paint.setTextScaleX((float) 0.9);
				}
				canvas.drawText(String.valueOf(colorballs[i].getID()),
						colorballs[i].getX() + 15, colorballs[i].getY() + 30,
						paint);
			}
		}

		// canvas.drawText("from:" + fromBalID + ",to:" + toBalID, 500, 500,
		// paint);

		// canvas.drawLine(colorballs[0].getX()+25, colorballs[0].getY()+25,
		// colorballs[1].getX()+25, colorballs[1].getY()+25, paint);
		invalidate();
		MainActivity.finalLtlString = computeLtl();
		MainActivity.mCurrentLtlOutput.setTextScaleX(2);
		MainActivity.mCurrentLtlOutput.setText("LTL: "
				+ MainActivity.finalLtlString);

	}

	// events when touching the screen
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);

	}

	class LearnGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent event) {

			int X = (int) event.getX();
			int Y = (int) event.getY();

			balID = 0;
			for (int i = 0; i < 10; i++) {
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
				if (radCircle < 60) {
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
			int X = (int) e.getX();
			int Y = (int) e.getY();

			balID = 0;
			for (int i = 0; i < 10; i++) {
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
					// showContextMenu();
					callQactionShow();
					MainActivity.finalLtlString = computeLtl();
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

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			int X = (int) e2.getX();
			int Y = (int) e2.getY();
			// move the balls the same as the finger
			if (balID > 0) {
				colorballs[balID - 1].setX(X + (int) distanceX - 25);
				colorballs[balID - 1].setY(Y + (int) distanceY - 25);
			}
			invalidate();
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			if (balID > 0) {
				Toast.makeText(
						getContext(),
						" \t#DETAILS#\n VISIT NODE:"
								+ DrawView.colorballs[balID - 1].isValid()
								+ "\n ALWAYS: "
								+ DrawView.colorballs[balID - 1].isAlways()
								+ "\nFUTURE: "
								+ DrawView.colorballs[balID - 1].isEventually()
								+ "\nOR MODE: "
								+ DrawView.colorballs[balID - 1].isORMode()
								+ "\nPICKUP OBJECT: "
								+ DrawView.colorballs[balID - 1].isPickObject()
								+ "\nDROP OBJECT: "
								+ DrawView.colorballs[balID - 1].isDropObject()
								+ "\nACTIVATE SENSOR: "
								+ DrawView.colorballs[balID - 1]
										.isActivateSensor()
								+ "\nDEACTIVATE SENSOR: "
								+ DrawView.colorballs[balID - 1]
										.isDeactivateSensor(),
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (balID > 0 && isRoot(colorballs[balID - 1].getID())) {
				/*
				 * DrawView.fromBalIDSingleTap = balID; if (colorballs[balID -
				 * 1].isValid()) { colorballs[balID - 1].setValid(false); } else
				 * { colorballs[balID - 1].setValid(true); }
				 */
				if (orGoToState == 0) {
					fromBalIDSingleTap = balID;
					orGoToState = 1;
				} else if (orGoToState == 1) {
					toBalIDSingleTap = balID;
					orGoToState = 0;
					colorballs[fromBalIDSingleTap - 1]
							.toggleLineTo(toBalIDSingleTap);
					colorballs[toBalIDSingleTap - 1]
							.toggleLineTo(fromBalIDSingleTap);
					// theGraph.toggleEdge(fromBalID, toBalID);

				}

			}
			invalidate();
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {

			if (balID > 0) {
				if (DoubleTapOccurredState == 0) {
					fromBalID = balID;
					DoubleTapOccurredState = 1;
				} else if (DoubleTapOccurredState == 1) {
					toBalID = balID;
					DoubleTapOccurredState = 0;
					colorballs[fromBalID - 1].toggleArrowTo(toBalID);

					theGraph.toggleEdge(fromBalID, toBalID);

				}

			} else {
				DoubleTapOccurredState = 0;
			}

			return true;
		}

	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		// TODO Auto-generated method stub

		super.onCreateContextMenu(menu);
		// theGraph.dfs();
		MainActivity.finalLtlString = computeLtl();
		menu.setHeaderTitle("LTL: " + computeLtl());
		menu.add("Toggle Location");
		menu.add("Toggle Always");
		menu.add("Toggle Eventually");
		menu.add("Activate Sensor");
		menu.add("Deactivate Sensor");
		menu.add("Pick up Object");
		menu.add("Drop Object");
	}

	public String computeLtl() {
		// LinkedList<String> orStrings = new LinkedList<String>();
		// LinkedList<String> andStrings = new LinkedList<String>();
		boolean visited[] = new boolean[10];// keeps track of visted nodes
		string = "";
		for (int i = 0; i < 10; i++) {
			String tmpString = "";
			if (MainActivity.waypoint[i] && isRoot(colorballs[i].getID())
					&& !visited[i]) {
				tmpString = theGraph.dfs(colorballs[i].getID());
				/*
				 * if (string == "") { string = string +
				 * theGraph.dfs(colorballs[i].getID()); } else {
				 */
				// boolean or = false;
				for (int j = 0; j < 10; j++) {
					if (colorballs[i].isLineTo(j + 1) && !visited[j]) {
						// or = true;
						visited[i] = true;
						visited[j] = true;
						if (tmpString.equals("")) {
							tmpString = theGraph.dfs(colorballs[j].getID());
						} else {
							tmpString = tmpString + " || "
									+ theGraph.dfs(colorballs[j].getID());
						}
					}
				}
				if (string == "") {
					string = "(" + tmpString + ")";
				} else {
					string = string + " && (" + tmpString + ")";
				}

				/*
				 * if (or) { orStrings.add(theGraph.dfs(colorballs[i].getID()));
				 * } else { andStrings.add(theGraph.dfs(colorballs[i].getID()));
				 * }
				 */
			}
		}
		/*
		 * for (String s : andStrings) { if (string == "") { string = s; } else
		 * { if (!s.isEmpty()) { string = string + " && " + s; } }
		 * 
		 * }
		 */
		/*
		 * if (!string.isEmpty()) string = "(" + string + ")"; String stringtemp
		 * = string; string = ""; for (String s : orStrings) { if (string == "")
		 * { string = s; } else { if (!s.isEmpty()) { string = string + " || " +
		 * s; } }
		 * 
		 * } if (!string.isEmpty()) string = "(" + string + ")"; if
		 * (!stringtemp.isEmpty() && !string.isEmpty()) { string = stringtemp +
		 * " && " + string; }else if(string.isEmpty()){ string = stringtemp; }
		 */

		return string;
	}

	public boolean isRoot(int index) {
		for (int i = 0; i < 10; i++) {
			if (colorballs[i].isArrowTo(index))
				return false;

		}
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public int getheight() {
		return height;
	}

	// this is to set the observer
	public void setObserver(QactionObserver observer) {
		mQactionObserver = observer;
	}

	// here be the magic
	private void callQactionShow() {
		if (mQactionObserver != null) {
			mQactionObserver.callback();
		}
	}
}
