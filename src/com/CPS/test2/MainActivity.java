package com.CPS.test2;

import java.io.IOException;
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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

public class MainActivity extends Activity implements OnCheckedChangeListener {

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
	public static int numWaypoints = 10;
	static String finalLtlString;
	String hyperFinalString;

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

	// dropbox related allocations
	private DbxAccountManager mDbxAcctMgr;
	private static final String appKey = "jy1f7p40xoajjh3";
	private static final String appSecret = "xsiqq2z1wjsjd9l";
	private static final int REQUEST_LINK_TO_DBX = 0;

	private TextView mTestOutput;
	private Button mLinkButton;
	private Button mUploadStringButton;
	private Button mDelMissionButton;
	private Button mAddMissionButton;

	static LinkedList<String> stringList = new LinkedList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
		// spinnerWaypoint = (Spinner) findViewById(R.id.spinnerWaypoint);
		// spinnerWaypoint.setOnItemSelectedListener(new
		// OnItemSelectedListener() {

		/*
		 * @Override public void onItemSelected(AdapterView<?> arg0, View arg1,
		 * int arg2, long arg3) { numWaypoints =
		 * Integer.valueOf(arg0.getItemAtPosition(arg2).toString());
		 * 
		 * }
		 * 
		 * @Override public void onNothingSelected(AdapterView<?> arg0) {
		 * numWaypoints = 0;
		 * 
		 * } });
		 */
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

		mTestOutput = (TextView) findViewById(R.id.ltlTextView);
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
					stringList.removeLast();
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

			}
		});

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Pick up Object") {

			DrawView.colorballs[DrawView.balID - 1].togglePickObject();
		} else if (item.getTitle() == "Drop Object") {
			DrawView.colorballs[DrawView.balID - 1].toggleDropObject();
		} else if (item.getTitle() == "Activate Sensor") {
			DrawView.colorballs[DrawView.balID - 1].toggleActivateSensor();
		} else if (item.getTitle() == "Deactivate Sensor") {
			DrawView.colorballs[DrawView.balID - 1].toggleDeactivateSensor();
		} else if (item.getTitle() == "Toggle Location") {
			//DrawView.colorballs[DrawView.balID - 1].setClickState(1);
			//DrawView.fromBalID = DrawView.balID;
			if (DrawView.colorballs[DrawView.balID - 1].isValid()) {
				DrawView.colorballs[DrawView.balID - 1].setValid(false);
			} else {
				DrawView.colorballs[DrawView.balID - 1].setValid(true);
			}
			
		}else if(item.getTitle() == "Toggle Always"){
			DrawView.colorballs[DrawView.balID - 1].toggleAlways();
		}

		else {
			return false;
		}
		return true;
		// return super.onContextItemSelected(item);
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
				scaledBitmap = Bitmap.createScaledBitmap(bmp, 800, 600, true);
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

	@Override
	protected void onResume() {
		super.onResume();
		if (mDbxAcctMgr.hasLinkedAccount()) {
			showLinkedView();
			doDropboxTest();
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
			mTestOutput.setText(hyperFinalString);
			String length = "" + hyperFinalString.length();
			final String TEST_DATA = length + "\n" + hyperFinalString;

			final String TEST_FILE_NAME = "ltl_string.txt";
			DbxPath testPath = new DbxPath(DbxPath.ROOT, TEST_FILE_NAME);

			// Create DbxFileSystem for synchronized file access.
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr
					.getLinkedAccount());

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

}
