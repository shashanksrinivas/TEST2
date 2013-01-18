package com.CPS.test2;



import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class MainActivity extends Activity {

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
		
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	
    
}
