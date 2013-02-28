package com.CPS.test2;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

public class MainActivity extends Activity implements OnCheckedChangeListener {

	ImageView choosenImageView;
	Button choosePicture;
	Button savePicture;
	DrawView dv;
	Bitmap bmp;
	static Bitmap scaledBitmap;
	static Bitmap alteredBitmap;
	RadioGroup robotGroup;
	private int mCurrentMission=0;
	
	
	
	Canvas canvas;
	Paint paint;
	static Matrix matrix;
	static boolean mapSelected = false;
	public Spinner spinnerWaypoint;
	public static int numWaypoints = 10;
	static String finalLtlString;
	String hyperFinalString;
	
	//action id
		private static final int ID_LOC = 1;
		private static final int ID_ALWAYS = 2;
		private static final int ID_EVENTUALLY = 3;
		private static final int ID_ORMODE = 4;
		private static final int ID_PICKUP = 5;
		private static final int ID_DROP = 6;	
		private static final int ID_ACTSEN = 7;
		private static final int ID_DEACTSEN = 8;

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

	static boolean waypoint[] = new boolean[10];// keeps the status of waypoints
												// (enabled/disabled)
	
	static LinkedList<ColorBall> currentColorBallList = new LinkedList<ColorBall>();
	static LinkedList<LinkedList<ColorBall>> listOfColorBallLists = new LinkedList<LinkedList<ColorBall>>();

	// dropbox related allocations
	private DbxAccountManager mDbxAcctMgr;
	private static final String appKey = "6j4m2i10o3v8o6a";
	private static final String appSecret = "n7umsheli0ppuui";
	private static final int REQUEST_LINK_TO_DBX = 0;
	private String uploadFileName ="ltl_string_r1.txt";

	private TextView mTestOutput;
	static TextView mCurrentLtlOutput;
	private Button mLinkButton;
	private Button mUploadStringButton;
	private Button mDelMissionButton;
	private Button mAddMissionButton;
	private Button mPreviewLtlButton;

	static LinkedList<String> stringList = new LinkedList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
/////////////////////////////////////////////////////////////////////////////////////////
////stuff related to quick action menu
		ActionItem toggleLocItem 	= new ActionItem(ID_LOC, "VISIT / AVOID", getResources().getDrawable(R.drawable.toggle_loc));
		ActionItem toggleAlwaysItem 	= new ActionItem(ID_ALWAYS, "ONCE / ALWAYS", getResources().getDrawable(R.drawable.toggle_always));
        ActionItem toggleEventuallyItem 	= new ActionItem(ID_EVENTUALLY, "NEXT / LATER", getResources().getDrawable(R.drawable.toggle_eventually));
        ActionItem toggleORModeItem = new ActionItem(ID_ORMODE, "OR NEXT(AND)/\nAND NEXT(LATER)", getResources().getDrawable(R.drawable.or_mode));
        ActionItem pickupItem 	= new ActionItem(ID_PICKUP, "PICKUP OBJECT (TRUE/FALSE)", getResources().getDrawable(R.drawable.pickup_obj));
        ActionItem dropItem 	= new ActionItem(ID_DROP, "DROP OBJECT (TRUE/FALSE)", getResources().getDrawable(R.drawable.drop_obj));
        ActionItem actSenItem 		= new ActionItem(ID_ACTSEN, "ACTIVATE SENSOR (TRUE/FALSE)", getResources().getDrawable(R.drawable.activate_sensor));
        ActionItem deactSenItem = new ActionItem(ID_DEACTSEN,"DEACTIVATE SENSOR (TRUE/FALSE)",getResources().getDrawable(R.drawable.deactivate_sensor));
        
        //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
        //prevItem.setSticky(true);
        //nextItem.setSticky(true);
		
		//create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout 
        //orientation
		final QuickAction quickAction = new QuickAction(this, QuickAction.VERTICAL);
		
		//add action items into QuickAction
        quickAction.addActionItem(toggleLocItem);
		quickAction.addActionItem(toggleAlwaysItem);
        quickAction.addActionItem(toggleEventuallyItem);
        quickAction.addActionItem(toggleORModeItem);
        quickAction.addActionItem(pickupItem);
        quickAction.addActionItem(dropItem);
        quickAction.addActionItem(actSenItem);
        quickAction.addActionItem(deactSenItem);
        
                
        //Set listener for action item clicked
		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {				
				ActionItem actionItem = quickAction.getActionItem(pos);
                 
				//here we can filter which action item was clicked with pos or actionId parameter
				if (actionId == ID_LOC) {
					if (DrawView.colorballs[DrawView.balID - 1].isValid()) {
						DrawView.colorballs[DrawView.balID - 1].setValid(false);
						Toast.makeText(getApplicationContext(), "AVOID SELECTED", Toast.LENGTH_SHORT).show();
					} else {
						DrawView.colorballs[DrawView.balID - 1].setValid(true);
						Toast.makeText(getApplicationContext(), "VISIT SELECTED", Toast.LENGTH_SHORT).show();
					}
					
				} else if (actionId == ID_ALWAYS) {
					DrawView.colorballs[DrawView.balID - 1].toggleAlways();
					if(DrawView.colorballs[DrawView.balID - 1].isAlways()){
						Toast.makeText(getApplicationContext(), "ALWAYS SELECTED", Toast.LENGTH_SHORT).show();
					}else{
					Toast.makeText(getApplicationContext(), "ONCE SELECTED", Toast.LENGTH_SHORT).show();
					}
				}else if (actionId == ID_EVENTUALLY) {
					DrawView.colorballs[DrawView.balID - 1].toggleEventually();
					if(DrawView.colorballs[DrawView.balID - 1].isEventually()){
					Toast.makeText(getApplicationContext(), "LATER SELECTED", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "NEXT SELECTED", Toast.LENGTH_SHORT).show();
					}
				}else if(actionId == ID_ORMODE){
					DrawView.colorballs[DrawView.balID - 1].toggleORMode();
					if(DrawView.colorballs[DrawView.balID - 1].isORMode()){
						Toast.makeText(getApplicationContext(), "IN OR MODE", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "IN AND MODE", Toast.LENGTH_SHORT).show();
					}
				}else if (actionId == ID_PICKUP) {
				
					DrawView.colorballs[DrawView.balID - 1].togglePickObject();
					if(DrawView.colorballs[DrawView.balID - 1].isPickObject()){
					Toast.makeText(getApplicationContext(), "PICK OBJECT TRUE", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "PICK OBJECT FALSE", Toast.LENGTH_SHORT).show();
					}
				}else if (actionId == ID_DROP) {
					DrawView.colorballs[DrawView.balID - 1].toggleDropObject();
					if(DrawView.colorballs[DrawView.balID - 1].isDropObject()){
					Toast.makeText(getApplicationContext(), "DROP OBJECT TRUE", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "DROP OBJECT FALSE", Toast.LENGTH_SHORT).show();
					}
				}else if (actionId == ID_ACTSEN) {
					DrawView.colorballs[DrawView.balID - 1].toggleActivateSensor();
					if(DrawView.colorballs[DrawView.balID - 1].isActivateSensor()){
					Toast.makeText(getApplicationContext(), "ACTIVATE SENSOR TRUE", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "ACTIVATE SENSOR FALSE", Toast.LENGTH_SHORT).show();
					}
				}else if (actionId == ID_DEACTSEN) {
					DrawView.colorballs[DrawView.balID - 1].toggleDeactivateSensor();
					if(DrawView.colorballs[DrawView.balID - 1].isDeactivateSensor()){
					Toast.makeText(getApplicationContext(), "DEACTIVATE SENSOR TRUE", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "DEACTIVATE SENSOR FALSE", Toast.LENGTH_SHORT).show();
					}
				}else {
					Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		//set listnener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
		//by clicking the area outside the dialog.
		quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {			
			@Override
			public void onDismiss() {
				Toast.makeText(getApplicationContext(), "NO OPTION SELECTED", Toast.LENGTH_LONG).show();
			}
		});
////end of stuff related to quick action menu
////////////////////////////////////////////////////////////////////////////////////////
		
		
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
		
		dv = (DrawView) findViewById(R.id.draw_view);
		registerForContextMenu(dv);
		dv.setObserver(new QactionObserver() {
			
			@Override
			public void callback() {
				quickAction.show((Button) findViewById(R.id.selectMap));// TODO Auto-generated method stub
				
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

		mTestOutput = (TextView) findViewById(R.id.ltlTextView);
		mCurrentLtlOutput = (TextView) findViewById(R.id.currentLtlTextView);
		mLinkButton = (Button) findViewById(R.id.transferLTL);
		mLinkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickLinkToDropbox();
			}
		});

		mUploadStringButton = (Button) findViewById(R.id.uploadLTL);
		mUploadStringButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doUpload();

			}
		});
		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
				appKey, appSecret);

		mDelMissionButton = (Button) findViewById(R.id.delMission);
		mDelMissionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!stringList.isEmpty())
				{
					stringList.removeLast();//remove string and associated colorball list
					listOfColorBallLists.remove(listOfColorBallLists.size()-1);
					
				}
				mTestOutput.setText("");
				for(String s: stringList){
					mTestOutput.append("¥ " + s + "\n");
				}

			}
		});

		mAddMissionButton = (Button) findViewById(R.id.addMission);
		mAddMissionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stringList.add(finalLtlString);
				mTestOutput.setText("");
				for(String s: stringList){
					mTestOutput.append("¥ " + s + "\n");
				}
				currentColorBallList.clear();
				for(int i=0;i<10;i++){
					if(true){//create colorball list 
						ColorBall tempBall = new ColorBall(DrawView.colorballs[i]);
					currentColorBallList.add(tempBall);
					}
				}
				listOfColorBallLists.add(currentColorBallList);//add colorball list to list of colorball lists
				mCurrentMission = listOfColorBallLists.size();
				//debug
				for(int i=0;i<mCurrentMission;i++){
				ColorBall temp = listOfColorBallLists.get(i).get(0);
				int y=0;
				}

			}
		});
		
		mPreviewLtlButton = (Button) findViewById(R.id.previewLtlButton);
		registerForContextMenu(mPreviewLtlButton);
		mPreviewLtlButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dv.showContextMenu();
			}
		});

	}
	
	
	 
	

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		int index=0;
		for(String s: stringList){
			if(item.getTitle() == s){
				mCurrentMission = index;
				
				LinkedList<ColorBall> tempList = listOfColorBallLists.get(index) ;
				//debug
				
				for(int i=0;i<10;i++){
					//if(waypoint[i])
					DrawView.colorballs[i].copy(tempList.get(i));
					int x = tempList.get(0).getX();
					int y = tempList.get(0).getY();
				}
			}
			index++;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == REQUEST_LINK_TO_DBX) {
			if (resultCode == Activity.RESULT_OK) {
				doDropboxTest();
			} else {
				mTestOutput.setText("Link to Dropbox failed or was cancelled.");
			}
		}

		if (resultCode == RESULT_OK) {
			Uri imageFileUri = intent.getData();
			try {
				BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
				bmpFactoryOptions.inJustDecodeBounds = true;
				bmp = BitmapFactory
						.decodeStream(
								getContentResolver().openInputStream(
										imageFileUri), null, bmpFactoryOptions);

				bmpFactoryOptions.inJustDecodeBounds = false;
				bmp = BitmapFactory
						.decodeStream(
								getContentResolver().openInputStream(
										imageFileUri), null, bmpFactoryOptions);
				scaledBitmap = Bitmap.createScaledBitmap(bmp, dv.getWidth(), dv.getHeight(), true);
				alteredBitmap = Bitmap.createBitmap(bmp.getWidth(),
						bmp.getHeight(), bmp.getConfig());
				canvas = new Canvas(alteredBitmap);
				paint = new Paint();
				paint.setColor(Color.GREEN);
				paint.setStrokeWidth(5);
				matrix = new Matrix();
				canvas.drawBitmap(bmp, matrix, paint);
				mapSelected = true;
				// choosenImageView.setImageBitmap(alteredBitmap);
				// choosenImageView.setOnTouchListener(this);
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
		switch (buttonView.getId()) {
		case R.id.wayPoint1:
			waypoint[0] = isChecked;
			dv.colorballs[0].setEnabled(isChecked);
			break;
		case R.id.wayPoint2:
			waypoint[1] = isChecked;
			DrawView.colorballs[1].setEnabled(isChecked);
			break;
		case R.id.wayPoint3:
			waypoint[2] = isChecked;
			DrawView.colorballs[2].setEnabled(isChecked);
			break;
		case R.id.wayPoint4:
			waypoint[3] = isChecked;
			DrawView.colorballs[3].setEnabled(isChecked);
			break;
		case R.id.wayPoint5:
			waypoint[4] = isChecked;
			DrawView.colorballs[4].setEnabled(isChecked);
			break;
		case R.id.wayPoint6:
			waypoint[5] = isChecked;
			DrawView.colorballs[5].setEnabled(isChecked);
			break;
		case R.id.wayPoint7:
			waypoint[6] = isChecked;
			DrawView.colorballs[6].setEnabled(isChecked);
			break;
		case R.id.wayPoint8:
			waypoint[7] = isChecked;
			DrawView.colorballs[7].setEnabled(isChecked);
			break;
		case R.id.wayPoint9:
			waypoint[8] = isChecked;
			DrawView.colorballs[8].setEnabled(isChecked);
			break;
		case R.id.wayPoint10:
			waypoint[9] = isChecked;
			DrawView.colorballs[9].setEnabled(isChecked);
			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mDbxAcctMgr.hasLinkedAccount()) {
			showLinkedView();
			//doDropboxTest();
		} else {
			showUnlinkedView();
		}
	}

	private void showLinkedView() {
		mLinkButton.setVisibility(View.GONE);
		mTestOutput.setVisibility(View.VISIBLE);
		mUploadStringButton.setVisibility(View.VISIBLE);
	}

	private void showUnlinkedView() {
		mLinkButton.setVisibility(View.VISIBLE);
		mTestOutput.setVisibility(View.GONE);
		mUploadStringButton.setVisibility(View.GONE);
	}

	private void onClickLinkToDropbox() {
		mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);
	}

	private void doDropboxTest() {
		try {
			final String TEST_DATA = "hello";
			final String TEST_FILE_NAME = "ltl_string.txt";
			DbxPath testPath = new DbxPath(DbxPath.ROOT, TEST_FILE_NAME);

			// Create DbxFileSystem for synchronized file access.
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr
					.getLinkedAccount());

			// Print the contents of the root folder. This will block until we
			// can
			// sync metadata the first time.
			List<DbxFileInfo> infos = dbxFs.listFolder(DbxPath.ROOT);
			mTestOutput.setText("\nContents of app folder:\n");
			for (DbxFileInfo info : infos) {
				mTestOutput.append("    " + info.path + ", "
						+ info.modifiedTime + '\n');
			}

			// Create a test file only if it doesn't already exist.

			if (!dbxFs.exists(testPath)) {
				DbxFile testFile = dbxFs.create(testPath);

				try {
					testFile.writeString(TEST_DATA);

				} finally {
					testFile.close();
				}
				mTestOutput.append("\nCreated new file '" + testPath + "'.\n");
			}
			/*
			 * else if (dbxFs.exists(testPath)) {
			 * 
			 * DbxFile testFile = dbxFs.create(testPath);
			 * 
			 * try { testFile.writeString(TEST_DATA); } finally {
			 * testFile.close(); } //mTestOutput.append("\nCreated new file '" +
			 * testPath + "'.\n"); }
			 */

			/*
			 * if (dbxFs.isFile(testPath)){ DbxFile testFile =
			 * dbxFs.open(testPath); try { testFile.writeString(TEST_DATA); }
			 * finally { testFile.close(); } }
			 */

			// Read and print the contents of test file. Since we're not making
			// any attempt to wait for the latest version, this may print an
			// older cached version. Use getSyncStatus() and/or a listener to
			// check for a new version.
			if (dbxFs.isFile(testPath)) {
				String resultData;
				DbxFile testFile = dbxFs.open(testPath);
				try {
					resultData = testFile.readString();
					// testFile.writeString(TEST_DATA);
				} finally {
					testFile.close();
				}
				mTestOutput.append("\nRead file '" + testPath
						+ "' and got data:\n    " + resultData);
			} else if (dbxFs.isFolder(testPath)) {
				mTestOutput.append("'" + testPath.toString()
						+ "' is a folder.\n");
			}
		} catch (IOException e) {
			mTestOutput.setText("Dropbox test failed: " + e);
		}
	}

	private void doUpload() {
		try {
			hyperFinalString = "";
			for (String s : stringList) {
				if (hyperFinalString.isEmpty()) {
					hyperFinalString = s;
				} else {
					hyperFinalString = hyperFinalString + "&&" + s;
				}
			}
			hyperFinalString = hyperFinalString.replace("F", "<>");
			hyperFinalString = hyperFinalString.replace("G","[]");
			hyperFinalString = hyperFinalString.replace("NOT", "!");
			mTestOutput.setText(hyperFinalString);
			String length = "" + hyperFinalString.length();
			final String TEST_DATA = length + "\n" + hyperFinalString;

			final String TEST_FILE_NAME = uploadFileName;//"ltl_string.txt";
			DbxPath testPath = new DbxPath(DbxPath.ROOT, TEST_FILE_NAME);

			// Create DbxFileSystem for synchronized file access.
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr
					.getLinkedAccount());
			
			if (!dbxFs.exists(testPath)) {
				DbxFile testFile = dbxFs.create(testPath);
				testFile.close();
			}

			if (dbxFs.exists(testPath)) {

				DbxFile testFile = dbxFs.open(testPath);

				try {
					testFile.writeString(TEST_DATA);
					// add syncing so it syncs instantaneously instead of having
					// to wait a few seconds
				} finally {
					testFile.close();
				}
				// mTestOutput.append("\nCreated new file '" + testPath +
				// "'.\n");
			}
		} catch (IOException e) {
			mTestOutput.setText("Dropbox test failed: " + e);
		}
		
		

	}
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.robot1:
	            if (checked)
	            	uploadFileName = "ltl_string_r1.txt";
	                // Pirates are the best
	            break;
	        case R.id.robot2:
	            if (checked)
	            	uploadFileName = "ltl_string_r2.txt";
	                // Ninjas rule
	            break;
	    }
	}





	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		//menu.add("hello");
		super.onCreateContextMenu(menu, v, menuInfo);
		
		for(String s: stringList){
			menu.add(s);
		}
	}
	
	

}
