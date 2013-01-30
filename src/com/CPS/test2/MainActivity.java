package com.CPS.test2;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Spinner;

public class MainActivity extends Activity implements OnCheckedChangeListener{
	
	ImageView choosenImageView;
	  Button choosePicture;
	  Button savePicture;

	  Bitmap bmp;
	  static Bitmap scaledBitmap;
	  static Bitmap alteredBitmap;
	  Canvas canvas;
	  Paint paint;
	  static Matrix matrix;
	  static boolean mapSelected = false;
	public Spinner spinnerWaypoint;
	public static int numWaypoints=10;
	
	CheckBox wayPoint1;
	CheckBox wayPoint2;
	CheckBox wayPoint3;
	CheckBox wayPoint4;
	CheckBox wayPoint5;
	CheckBox wayPoint6;
	CheckBox wayPoint7;
	CheckBox wayPoint8;
	CheckBox wayPoint9;
	CheckBox wayPoint10;
	
	static boolean waypoint[] = new boolean[10];//keeps the status of waypoints (enabled/disabled)
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        wayPoint1 = (CheckBox) findViewById(R.id.wayPoint1);
        wayPoint2 = (CheckBox) findViewById(R.id.wayPoint2);
        wayPoint3 = (CheckBox) findViewById(R.id.wayPoint3);
        wayPoint4 = (CheckBox) findViewById(R.id.wayPoint4);
        wayPoint5 = (CheckBox) findViewById(R.id.wayPoint5);
        wayPoint6 = (CheckBox) findViewById(R.id.wayPoint6);
        wayPoint7 = (CheckBox) findViewById(R.id.wayPoint7);
        wayPoint8 = (CheckBox) findViewById(R.id.wayPoint8);
        wayPoint9 = (CheckBox) findViewById(R.id.wayPoint9);
        wayPoint10 = (CheckBox) findViewById(R.id.wayPoint10);
        wayPoint1.setOnCheckedChangeListener(this);
        wayPoint2.setOnCheckedChangeListener(this);
        wayPoint3.setOnCheckedChangeListener(this);
        wayPoint4.setOnCheckedChangeListener(this);
        wayPoint5.setOnCheckedChangeListener(this);
        wayPoint6.setOnCheckedChangeListener(this);
        wayPoint7.setOnCheckedChangeListener(this);
        wayPoint8.setOnCheckedChangeListener(this);
        wayPoint9.setOnCheckedChangeListener(this);
        wayPoint10.setOnCheckedChangeListener(this);
        spinnerWaypoint = (Spinner) findViewById(R.id.spinnerWaypoint);
        spinnerWaypoint.setOnItemSelectedListener(new OnItemSelectedListener() {
        	
 			@Override
 			public void onItemSelected(AdapterView<?> arg0, View arg1,
 					int arg2, long arg3) {
 				numWaypoints = Integer.valueOf(arg0.getItemAtPosition(arg2).toString());
 				
 			}

 			@Override
 			public void onNothingSelected(AdapterView<?> arg0) {
 				numWaypoints = 0;
 				
 			}
 		});
		DrawView dv = (DrawView) findViewById(R.id.draw_view);
		registerForContextMenu(dv);
		
        Button buttonLoadImage = (Button) findViewById(R.id.selectMap);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent choosePictureIntent = new Intent(
				          Intent.ACTION_PICK,
				          android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				      startActivityForResult(choosePictureIntent, 0);
				
			}
        });
       
    }
    @Override
	public boolean onContextItemSelected(MenuItem item) {
    	 if(item.getTitle()=="Pick up Object"){
    		 
    		 DrawView.colorballs[DrawView.balID - 1].togglePickObject();
    		 }
    	 else if(item.getTitle()=="Drop Object"){
	    	 DrawView.colorballs[DrawView.balID - 1].toggleDropObject();
	     }  
	     else if(item.getTitle()=="Activate Sensor"){
	    	 DrawView.colorballs[DrawView.balID - 1].toggleActivateSensor();
	     }  
	     else if(item.getTitle()=="Deactivate Sensor"){
	    	 DrawView.colorballs[DrawView.balID - 1].toggleDeactivateSensor();
	     } 
	     else if(item.getTitle()=="Go to Location"){
	    	 DrawView.colorballs[DrawView.balID - 1].setClickState(1);
	    	 DrawView.fromBalID = DrawView.balID;
	     }
    	 
	     else {return false;}  
	 return true;  
		//return super.onContextItemSelected(item);
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode,
    	      Intent intent) {
    	    super.onActivityResult(requestCode, resultCode, intent);

    	    if (resultCode == RESULT_OK) {
    	      Uri imageFileUri = intent.getData();
    	      try {
    	        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
    	        bmpFactoryOptions.inJustDecodeBounds = true;
    	        bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(
    	                imageFileUri), null, bmpFactoryOptions);

    	        bmpFactoryOptions.inJustDecodeBounds = false;
    	        bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(
    	                imageFileUri), null, bmpFactoryOptions);
    	        scaledBitmap = Bitmap.createScaledBitmap(bmp, 800, 600, true);
    	        alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp
    	            .getHeight(), bmp.getConfig());
    	        canvas = new Canvas(alteredBitmap);
    	        paint = new Paint();
    	        paint.setColor(Color.GREEN);
    	        paint.setStrokeWidth(5);
    	        matrix = new Matrix();
    	        canvas.drawBitmap(bmp, matrix, paint);
    	        mapSelected = true;
    	        //choosenImageView.setImageBitmap(alteredBitmap);
    	        //choosenImageView.setOnTouchListener(this);
    	      } catch (Exception e) {
    	        Log.v("ERROR", e.toString());
    	      }
    	    }
    	  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch(buttonView.getId()){
		case R.id.wayPoint1:
			waypoint[0] = isChecked;
			break;
		case R.id.wayPoint2:
			waypoint[1] = isChecked;
			break;
		case R.id.wayPoint3:
			waypoint[2] = isChecked;
			break;
		case R.id.wayPoint4:
			waypoint[3] = isChecked;
			break;
		case R.id.wayPoint5:
			waypoint[4] = isChecked;
			break;
		case R.id.wayPoint6:
			waypoint[5] = isChecked;
			break;
		case R.id.wayPoint7:
			waypoint[6] = isChecked;
			break;
		case R.id.wayPoint8:
			waypoint[7] = isChecked;
			break;
		case R.id.wayPoint9:
			waypoint[8] = isChecked;
			break;
		case R.id.wayPoint10:
			waypoint[9] = isChecked;
			break;
		}
		
	}

	
    
}
