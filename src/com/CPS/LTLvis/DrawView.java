package com.CPS.LTLvis;

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
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class DrawView extends View {
	static ColorBall[] colorballs = new ColorBall[11]; // array that holds the
														// balls
	static ColorBall startColorball;// the start node in the graph
	private boolean templateMode = false;// template mode does not use the
											// recursive algorithm
	Graph theGraph = new Graph();// initialize the graph
	static int balID = 0; // variable to know what ball is being dragged
	static int fromBalID = 0;
	static int toBalID = 0;
	static int fromBalIDSingleTap = 0;
	static int toBalIDSingleTap = 0;
	private int DoubleTapOccurredState = 0;
	private int orGoToState = 0;
	static boolean visited[] = new boolean[11];// keeps track of visted nodes
	private int width;
	private int height;
	private int mWindowHeight = this.getHeight();
	private int mWindowWidth = this.getWidth();
	Bitmap andBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.and_ball);
	Bitmap orBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.or_ball);
	Bitmap nextBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.ball_next);
	Bitmap eventuallyBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.ball_future);
	Bitmap impliesBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.ball_implies);
	Bitmap alwaysBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.ball_always);
	Bitmap untilBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.ball_until);
	Bitmap templateBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.ball_template);
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
		mWindowHeight = this.getHeight();
		mWindowWidth = this.getWidth();
		Point point = new Point();
		point.x = 0;
		point.y = 20;

		/*
		 * Point startBallPoint = new Point(); startBallPoint.x = 0;
		 * startBallPoint.y = 0;
		 */
		theGraph.addVertex(0); // 0 (start for dfs)
		for (int i = 0; i < 10; i++) {
			point.x = point.x + 50;

			colorballs[i] = new ColorBall(context, R.drawable.bol_groen,
					R.drawable.bol_rood, point);
			theGraph.addVertex(i + 1);
			// theGraph.toggleEdge(0, i + 1);

		}
		/*
		 * startColorball = new ColorBall(context, R.drawable.bol_groen,
		 * R.drawable.bol_rood, startBallPoint);
		 */

		// setup start node with default arrows pointing to all other nodes
		/*
		 * for (int i = 1; i < 10; i++) { colorballs[0].setArrowTo(i + 1);//
		 * this is mandatory for source node theGraph.setEdge(1, i + 1);
		 * theGraph.unsetEdge(i + 1, 1); }
		 */
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

		// Draw the Map first
		if (MainActivity.mapSelected) {
			canvas.drawBitmap(MainActivity.scaledBitmap, MainActivity.matrix,
					null);
		}
		paint.setStrokeWidth(3);
		paint.setPathEffect(null);
		paint.setColor(Color.GREEN);
		if(!MainActivity.templateMode)
		canvas.drawCircle(colorballs[0].getX() + 25, colorballs[0].getY() + 25,
				30, paint);
		// set paint properties
		paint.setStrokeWidth(3);
		paint.setColor(Color.GRAY);
		canvas.drawLine(0, 0, 0, 800, paint);

		// set edges from source to all other nodes
		// for (int i = 1; i < 10; i++) {
		// if (isLeastInCompletelyConnected(i)){
		// colorballs[0].setArrowTo(i + 1);// this is mandatory for source
		// // node
		// theGraph.setEdge(1, i + 1);
		// theGraph.unsetEdge(i + 1, 1);
		// }else{
		// colorballs[0].unsetArrowTo(i + 1);
		// theGraph.unsetEdge(1, i + 1);
		// }
		// }

		// for b1 boolean OR
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (colorballs[i].isLineTo(j + 1) && MainActivity.waypoint[i]
						&& MainActivity.waypoint[j]) {// bitmap
					// for
					// OR
					paint.setColor(Color.CYAN);
					if (colorballs[i].getB1State() == b1.OR
							|| colorballs[j].getB1State() == b1.OR)
						paint.setPathEffect(new DashPathEffect(new float[] {
								10, 20 }, 0));
					else
						paint.setPathEffect(null);
					canvas.drawLine(colorballs[i].getX() + 25,
							colorballs[i].getY() + 25,
							colorballs[j].getX() + 25,
							colorballs[j].getY() + 25, paint);
					paint.setPathEffect(null);
					/*
					 * canvas.drawBitmap(orBitmap, (colorballs[i].getX() +
					 * colorballs[j].getX()) / 2, (colorballs[i].getY() +
					 * colorballs[j].getY()) / 2, paint);
					 */
					// bitmap for AND
				}
				/*
				 * else if (!colorballs[i].isLineTo(j + 1) &&
				 * MainActivity.waypoint[i] && MainActivity.waypoint[j] &&
				 * isRoot(i + 1) && isRoot(j + 1) && !isLineToNonRootNode(j + 1)
				 * && !isLineToNonRootNode(i + 1)) {
				 * paint.setColor(Color.MAGENTA); paint.setPathEffect(null);
				 * canvas.drawLine(colorballs[i].getX() + 25,
				 * colorballs[i].getY() + 25, colorballs[j].getX() + 25,
				 * colorballs[j].getY() + 25, paint);
				 * 
				 * canvas.drawBitmap(andBitmap, (colorballs[i].getX() +
				 * colorballs[j].getX()) / 2, (colorballs[i].getY() +
				 * colorballs[j].getY()) / 2, paint);
				 * 
				 * }
				 */
			}
		}

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (colorballs[i].isArrowTo(j + 1) && MainActivity.waypoint[i]
						&& MainActivity.waypoint[j]) {

					if (colorballs[i].getB1State() == b1.OR) {
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
					// bitmap for b2_state
					// place bitmap at 1/4th the distance on the line connecting
					// i and j
					b2 temp = colorballs[j].getB2State();
					if (!colorballs[i].getLabel().isEmpty()) {
						switch (colorballs[j].getB2State()) {
						case AND:
							canvas.drawBitmap(andBitmap,
									((colorballs[i].getX() + colorballs[j]
											.getX()) / 2 + (colorballs[i]
											.getX())) / 2,
									((colorballs[i].getY() + colorballs[j]
											.getY()) / 2 + (colorballs[i]
											.getY())) / 2, paint);
							break;
						case OR:
							canvas.drawBitmap(orBitmap,
									((colorballs[i].getX() + colorballs[j]
											.getX()) / 2 + (colorballs[i]
											.getX())) / 2,
									((colorballs[i].getY() + colorballs[j]
											.getY()) / 2 + (colorballs[i]
											.getY())) / 2, paint);
							break;
						case IMPLIES:
							canvas.drawBitmap(impliesBitmap,
									((colorballs[i].getX() + colorballs[j]
											.getX()) / 2 + (colorballs[i]
											.getX())) / 2,
									((colorballs[i].getY() + colorballs[j]
											.getY()) / 2 + (colorballs[i]
											.getY())) / 2, paint);
							break;
						case NONE:
							break;

						}
					}

					// bitmap for t1_state
					// place bitmap on the middle of the line connecting i and j

					switch (colorballs[j].getT1State()) {
					case EVENTUALLY:
						canvas.drawBitmap(
								eventuallyBitmap,
								(colorballs[i].getX() + colorballs[j].getX()) / 2,
								(colorballs[i].getY() + colorballs[j].getY()) / 2,
								paint);
						break;
					case ALWAYS:
						canvas.drawBitmap(
								alwaysBitmap,
								(colorballs[i].getX() + colorballs[j].getX()) / 2,
								(colorballs[i].getY() + colorballs[j].getY()) / 2,
								paint);
						break;
					case NEXT:
						canvas.drawBitmap(
								nextBitmap,
								(colorballs[i].getX() + colorballs[j].getX()) / 2,
								(colorballs[i].getY() + colorballs[j].getY()) / 2,
								paint);
						break;
					case UNTIL:
						canvas.drawBitmap(
								untilBitmap,
								(colorballs[i].getX() + colorballs[j].getX()) / 2,
								(colorballs[i].getY() + colorballs[j].getY()) / 2,
								paint);
						break;
					case NONE:
						break;
					}

					// bitmap for t2_state
					// place bitmap at 3/4t the distance on the line connecting
					// i and j
					switch (colorballs[j].getT2State()) {
					case EVENTUALLY:
						canvas.drawBitmap(
								eventuallyBitmap,
								((colorballs[i].getX() + colorballs[j].getX()) / 2 + (colorballs[j]
										.getX())) / 2,
								((colorballs[i].getY() + colorballs[j].getY()) / 2 + (colorballs[j]
										.getY())) / 2, paint);
						break;
					case ALWAYS:
						canvas.drawBitmap(
								alwaysBitmap,
								((colorballs[i].getX() + colorballs[j].getX()) / 2 + (colorballs[j]
										.getX())) / 2,
								((colorballs[i].getY() + colorballs[j].getY()) / 2 + (colorballs[j]
										.getY())) / 2, paint);
						break;
					case NONE:
						break;
					}
					// /Template section
					// draw the template bitmap if b2 is implies and t1 is next
					// and you are in template mode
					if (MainActivity.templateMode) {
						if ((colorballs[j].getB2State() == b2.IMPLIES || colorballs[j].getB2State()==b2.AND)
								&& colorballs[j].getT1State() == t1.NEXT) {
							canvas.drawBitmap(templateBitmap, (colorballs[i]
									.getX() + colorballs[j].getX()) / 2,
									(colorballs[i].getY() + colorballs[j]
											.getY()) / 2, paint);

						}
						
						//place a dummy start node and draw bitmaps
						paint.setColor(Color.GRAY);
						canvas.drawText("S", 25, 25, paint);
						paint.setStyle(Paint.Style.FILL);
						canvas.drawCircle(0, 0, 25, paint);

						for (int k = 0; k < 10; k++) {
							if (MainActivity.waypoint[k] && isRoot(k + 1) && isLeastInCompletelyConnected(k)) {
								canvas.drawLine(0, 0, colorballs[k].getX() + 25,
										colorballs[k].getY() + 25, paint);
								if (colorballs[k].getT1State()!=t1.NONE) {
									float deltaX2 = colorballs[k].getX() - 0;
									float deltaY2 = colorballs[k].getY() - 0;
									float frac2 = (float) 0.05;
									float point_x_12 = 0 + (float) ((1 - frac2) * deltaX2 * 0.8 + frac
											* 0.5 * deltaY2);
									float point_y_12 = 0 + (float) ((1 - frac2) * deltaY2 * 0.8 - frac
											* 0.5 * deltaX2);
									float point_x_22 = colorballs[k].getX();
									float point_y_22 = colorballs[k].getY();
									float point_x_32 = 0 + (float) ((1 - frac2) * deltaX2 * 0.8 - frac
											* 0.5 * deltaY2);
									float point_y_32 = 0 + (float) ((1 - frac2) * deltaY2 * 0.8 + frac
											* 0.5 * deltaX2);

									mPath.moveTo((point_x_12) + 25, (point_y_12) + 25);
									mPath.lineTo((point_x_22) + 25, (point_y_22) + 25);
									mPath.lineTo((point_x_32) + 25, (point_y_32) + 25);
									mPath.lineTo((point_x_12) + 25, (point_y_12) + 25);

									paint.setStyle(Paint.Style.FILL);
									canvas.drawPath(mPath, paint);
									mPath.reset();
								}

								switch (colorballs[k].getT1State()) {
								case EVENTUALLY:
									canvas.drawBitmap(
											eventuallyBitmap,
											(colorballs[k].getX()) / 2,
											(colorballs[k].getY()) / 2,
											paint);
									break;
								case ALWAYS:
									canvas.drawBitmap(
											alwaysBitmap,
											(colorballs[k].getX()) / 2,
											(colorballs[k].getY()) / 2,
											paint);
									break;
								case NEXT:
									canvas.drawBitmap(
											nextBitmap,
											(colorballs[k].getX()) / 2,
											(colorballs[k].getY()) / 2,
											paint);
									break;
								case UNTIL:
									canvas.drawBitmap(
											untilBitmap,
											(colorballs[k].getX()) / 2,
											(colorballs[k].getY()) / 2,
											paint);
									break;
								case NONE:
									break;
								}

								// bitmap for t2_state
								// place bitmap at 3/4t the distance on the line connecting
								// i and j
								switch (colorballs[j].getT2State()) {
								case EVENTUALLY:
									canvas.drawBitmap(
											eventuallyBitmap,
											((colorballs[k].getX()) / 2 + (colorballs[k]
													.getX())) / 2,
											(( colorballs[k].getY()) / 2 + (colorballs[k]
													.getY())) / 2, paint);
									break;
								case ALWAYS:
									canvas.drawBitmap(
											alwaysBitmap,
											((colorballs[k].getX()) / 2 + (colorballs[k]
													.getX())) / 2,
											(( colorballs[k].getY()) / 2 + (colorballs[k]
													.getY())) / 2, paint);
									break;
								case NONE:
									break;
								}

							}
						}
						
					}
					///end template mode drawing
				}
			}
		}
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(0);
		paint.setTextScaleX(2);
		paint.setTextSize(20);
		// draw the waypoints themselves
		for (int i = 0; i < 10; i++) {
			if (MainActivity.waypoint[i]) {
				canvas.drawBitmap(colorballs[i].getBitmap(),
						colorballs[i].getX(), colorballs[i].getY(), null);
				if (colorballs[i].getID() == 10) {
					paint.setTextScaleX((float) 0.9);
				}
				canvas.drawText(String.valueOf(colorballs[i].getLabel()),
						colorballs[i].getX() + 15, colorballs[i].getY() + 30,
						paint);
			}
		}

		invalidate();
		if (!MainActivity.templateMode) {
			MainActivity.finalLtlString = computeLTLrec(1,colorballs[0].getB1State());
		} else {
			MainActivity.finalLtlString = computeLtl();
		}
		MainActivity.mCurrentLtlOutput.setTextScaleX(2);
		MainActivity.mCurrentLtlOutput.setText("LTL: "
				+ MainActivity.finalLtlString);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWindowHeight = heightMeasureSpec;
		mWindowWidth = widthMeasureSpec;
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
				if (radCircle < 30) {
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
				if (radCircle < 23 && MainActivity.waypoint[i]) {
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
				int h = mWindowHeight;
				if (X > 20 && X < mWindowWidth && Y > 20 && Y < 600) {
					colorballs[balID - 1].setX(X + (int) distanceX - 25);
					colorballs[balID - 1].setY(Y + (int) distanceY - 25);
				}
			}
			invalidate();
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			// if (balID > 0) {
			// Toast.makeText(
			// getContext(),
			// " \t#DETAILS#\n VISIT NODE:"
			// + DrawView.colorballs[balID - 1].isValid()
			// + "\n ALWAYS: "
			// + DrawView.colorballs[balID - 1].isAlways()
			// + "\nFUTURE: "
			// + DrawView.colorballs[balID - 1].isEventually()
			// + "\nOR MODE: "
			// + DrawView.colorballs[balID - 1].isORMode()
			// + "\nPICKUP OBJECT: "
			// + DrawView.colorballs[balID - 1].isPickObject()
			// + "\nDROP OBJECT: "
			// + DrawView.colorballs[balID - 1].isDropObject()
			// + "\nACTIVATE SENSOR: "
			// + DrawView.colorballs[balID - 1]
			// .isActivateSensor()
			// + "\nDEACTIVATE SENSOR: "
			// + DrawView.colorballs[balID - 1]
			// .isDeactivateSensor(),
			// Toast.LENGTH_LONG).show();
			// }
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (balID > 0 /* && isRoot(colorballs[balID - 1].getID()) */) {
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
					if (fromBalIDSingleTap != toBalIDSingleTap
							&& MainActivity.waypoint[fromBalIDSingleTap - 1]
							&& MainActivity.waypoint[toBalIDSingleTap - 1]
					/* && fromBalIDSingleTap != 1/* not on source node */
					/* && toBalIDSingleTap != 1 */) {
						if (colorballs[fromBalIDSingleTap - 1]
								.isArrowTo(toBalIDSingleTap)) {// check for
																// arrows first
																// and override
																// them
							colorballs[fromBalIDSingleTap - 1]
									.toggleArrowTo(toBalIDSingleTap);
							theGraph.toggleEdge(fromBalIDSingleTap,
									toBalIDSingleTap);
						}
						if (colorballs[toBalIDSingleTap - 1]
								.isArrowTo(fromBalIDSingleTap)) {
							colorballs[toBalIDSingleTap - 1]
									.toggleArrowTo(fromBalIDSingleTap);
							theGraph.toggleEdge(toBalIDSingleTap,
									fromBalIDSingleTap);
						}
						colorballs[fromBalIDSingleTap - 1]
								.toggleLineTo(toBalIDSingleTap);
						colorballs[toBalIDSingleTap - 1]
								.toggleLineTo(fromBalIDSingleTap);
						// theGraph.toggleEdge(fromBalID, toBalID);
					}

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

					if (fromBalID != toBalID
							&& MainActivity.waypoint[fromBalID - 1]
							&& MainActivity.waypoint[toBalID - 1]
					/* && fromBalID != 1 && toBalID != 1 */) {
						if (colorballs[fromBalID - 1].isLineTo(toBalID)) {
							colorballs[fromBalID - 1].toggleLineTo(toBalID);
							colorballs[toBalID - 1].toggleLineTo(fromBalID);

						}
						if (colorballs[toBalID - 1].isArrowTo(fromBalID)) {
							colorballs[toBalID - 1].toggleArrowTo(fromBalID);
							theGraph.toggleEdge(toBalID, fromBalID);
						}

						colorballs[fromBalID - 1].toggleArrowTo(toBalID);
						theGraph.toggleEdge(fromBalID, toBalID);
					}

				}

			} else {
				DoubleTapOccurredState = 0;
			}

			return true;
		}

	}

	public String computeLtl() {
		// LinkedList<String> orStrings = new LinkedList<String>();
		// LinkedList<String> andStrings = new LinkedList<String>();
		// boolean visited[] = new boolean[10];// keeps track of visted nodes
		for (int i = 0; i < 10; i++) {
			visited[i] = false;
		}
		string = "";
		for (int i = 0; i < 10; i++) {
			String tmpString = "";
			if (MainActivity.waypoint[i] && isRoot(colorballs[i].getID())
					&& !visited[i]
					&& !isLineToNonRootNode(colorballs[i].getID())) {
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

	public String getText(t1 t1_state) {
		String retString = "";
		switch (t1_state) {
		case ALWAYS:
			retString = "G";
			break;
		case EVENTUALLY:
			retString = "F";
			break;
		case NEXT:
			retString = "X";
			break;
		case UNTIL:
			retString = "U";
			break;
		case NONE:
			retString = "";
			break;

		}
		return retString;
	}

	public String getText(t2 t2_state) {
		String retString = "";
		switch (t2_state) {
		case ALWAYS:
			retString = "G";
			break;
		case EVENTUALLY:
			retString = "F";
			break;

		case NONE:
			retString = "";
			break;

		}
		return retString;
	}

	public String getText(b2 b2_state) {
		String retString = "";
		switch (b2_state) {
		case AND:
			retString = "&&";
			break;
		case IMPLIES:
			retString = "=>";
			break;
		case OR:
			retString = "||";
			break;

		case NONE:
			retString = "";
			break;

		}
		return retString;
	}

	public String getText(b1 b1_state) {
		String retString = "";
		switch (b1_state) {
		case AND:
			retString = "&&";
			break;

		case OR:
			retString = "||";
			break;

		}
		return retString;
	}

	public String computeLTLrec(int balID, b1 b_1_prev) {
		String ltl = "";
		String neg = "";
		String ltlBuf = "";
		// if (!colorballs[balID - 1].isValid())// if ball is red ie do not
		// visit
		// neg = "NOT";

		if (colorballs[balID - 1].isLeaf()) {
			return neg + "(" + getCompleteLabel(balID - 1) + ")";
		} else {
			ltl = "";
			for (int i = 1; i < 10; i++) {
				if (theGraph.hasEdge(balID, i + 1) && MainActivity.waypoint[i]) {
					
					b1 b_1 = colorballs[i].getB1State();
					b2 b_2 = colorballs[i].getB2State();
					t1 t_1 = colorballs[i].getT1State();
					t2 t_2 = colorballs[i].getT2State();
					ltlBuf = computeLTLrec(i + 1, b_1);
					// colorballs[balID-1].getLabel()
					if (t_1 == t1.UNTIL) {
						if (ltl == "") {
							if (getCompleteLabel(balID - 1) != "") {
								ltl = "((" + neg + "("
										+ getCompleteLabel(balID - 1) + "))"
										+ getText(t_1) + getText(t_2) + "("
										+ ltlBuf + "))";
							} else {
								ltl = "(" + getText(t_1) + getText(t_2) + "("
										+ ltlBuf + "))";
							}
						} else {
							if (getCompleteLabel(balID - 1) != "") {
								ltl = ltl + getText(b_1_prev) + "((" + neg + "("
										+ getCompleteLabel(balID - 1) + "))"
										+ getText(t_1) + getText(t_2) + "("
										+ ltlBuf + "))";
							} else {
								ltl = ltl + getText(b_1_prev) + "(" + getText(t_1)
										+ getText(t_2) + "(" + ltlBuf + "))";
							}
						}

					} else {
						if (ltl == "") {
							if (!getCompleteLabel(balID - 1).isEmpty()) {
								ltl = "((" + neg + "("
										+ getCompleteLabel(balID - 1) + "))"
										+ getText(b_2) + getText(t_1)
										+ getText(t_2) + "(" + ltlBuf + "))";
							} else {
								ltl = "(" + getText(t_1) + getText(t_2) + "("
										+ ltlBuf + "))";
							}
						} else {
							if (!getCompleteLabel(balID - 1).isEmpty()) {
								ltl = ltl + getText(b_1_prev) + "((" + neg + "("
										+ getCompleteLabel(balID - 1) + "))"
										+ getText(b_2) + getText(t_1)
										+ getText(t_2) + "(" + ltlBuf + "))";
							} else {

								ltl = ltl + getText(b_1_prev) + "(" + getText(t_1)
										+ getText(t_2) + "(" + ltlBuf + "))";
							}
						}
					}
				}
			}
		}

		return ltl;
	}

	public boolean isCompletelyConnected(int index) {
		boolean flags[] = new boolean[10];
		int edgeCount = 0;
		int nodeCount = 1;
		flags[index] = true;
		/*
		 * for (int i = 0; i < 10; i++) { if
		 * (DrawView.colorballs[index].isLineTo(i + 1)) { flags[i] = true;
		 * nodeCount++; } }
		 */
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (DrawView.colorballs[i].isLineTo(j + 1)
						&& ((flags[i] && !flags[j]) || (flags[j] && !flags[i]))) {
					flags[i] = flags[j] = true;
					nodeCount++;
				}
			}
		}

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (flags[i] && flags[j]) {
					if (DrawView.colorballs[i].isLineTo(j + 1)) {
						edgeCount++;
					}
				}
			}
		}
		if ((nodeCount * (nodeCount - 1)) / 2 == edgeCount / 2) {
			return true;
		}
		return false;

	}

	public boolean isLeastInCompletelyConnected(int index) {

		for (int i = 0; i < 10; i++) {
			if (isCompletelyConnected(i) && colorballs[i].isLineTo(index + 1)) {
				if (i < index)
					return false;
			}
		}
		return true;
	}

	public boolean isRoot(int balID) {
		for (int i = 0; i < 10; i++) {
			if (colorballs[i].isArrowTo(balID))
				return false;

		}
		return true;
	}

	public boolean isLineToNonRootNode(int balID) {
		for (int i = 0; i < 10; i++) {
			if (DrawView.colorballs[balID - 1].isLineTo(i + 1)
					&& !isRoot(i + 1)) {
				return true;
			}
		}
		return false;
	}

	public boolean isLineToRootNodeWithoutArrows(int balID) {
		for (int i = 0; i < 10; i++) {
			if (DrawView.colorballs[balID - 1].isLineTo(i + 1) && isRoot(i + 1)) {
				for (int j = 0; j < 10; j++) {
					if (colorballs[i].isArrowTo(j + 1))
						return false;
				}

			}
		}
		return true;

	}

	public boolean isLineToRootNodeWithArrows(int balID) {
		for (int i = 0; i < 10; i++) {
			if (DrawView.colorballs[balID - 1].isLineTo(i + 1) && isRoot(i + 1)) {
				for (int j = 0; j < 10; j++) {
					if (DrawView.colorballs[i].isArrowTo(j + 1))
						return true;
				}

			}
		}
		return false;
	}

	public boolean hasNoLineToRootNodeWithArrows(int balID) {
		for (int i = 0; i < 10; i++) {
			if (DrawView.colorballs[balID - 1].isLineTo(i + 1) && isRoot(i + 1)) {
				for (int j = 0; j < 10; j++) {
					if (DrawView.colorballs[i].isArrowTo(j + 1))
						return false;
				}

			}
		}
		return true;
	}

	public String getCompleteLabel(int index) {
		String neg = "";
		if (!colorballs[index].isValid()
				&& !colorballs[index].getLabel().isEmpty())// if ball is red ie
															// do not visit
			neg = "!";
		String label = neg + colorballs[index].getLabel();
		if (isCompletelyConnected(index)) {
			for (int i = 0; i < 10; i++) {
				if (!colorballs[i].isValid())// if ball is red ie do not visit
					neg = "!";
				else
					neg = "";

				if (colorballs[index].isLineTo(i + 1) && i != index
						&& MainActivity.waypoint[i]) {
					if (colorballs[index].getB1State() == b1.OR) {
						if (!label.isEmpty()) {
							label = label + "||" + neg
									+ colorballs[i].getLabel();
						} else {
							label = neg + colorballs[i].getLabel();
						}
					} else {
						if (!label.isEmpty()) {
							label = label + "&&" + neg
									+ colorballs[i].getLabel();
						} else {
							label = neg + colorballs[i].getLabel();
						}
					}
				}
			}
		}
		return label;
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
