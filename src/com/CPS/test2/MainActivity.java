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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

public class MainActivity extends Activity {
	
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
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        
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

	
    
}
