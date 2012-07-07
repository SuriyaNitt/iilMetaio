/**
 * ARViewActivity.java
 * Example SDK Internal
 * 
 * Created by Arsalan Malik on 03.11.2011
 * Copyright 2011 metaio GmbH. All rights reserved.

 */

package com.IilMetaio.simple;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

//import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.IilMetaio.*;
import com.metaio.tools.io.AssetsManager;
import com.metaio.unifeye.UnifeyeDebug;
import com.metaio.unifeye.UnifeyeGLSurfaceView;
import com.metaio.unifeye.UnifeyeSensorsManager;
import com.metaio.unifeye.ndk.AS_UnifeyeSDKMobile;
import com.metaio.unifeye.ndk.ERENDER_SYSTEM;
import com.metaio.unifeye.ndk.IUnifeyeMobileAndroid;
import com.metaio.unifeye.ndk.IUnifeyeMobileCallback;
import com.metaio.unifeye.ndk.IUnifeyeMobileGeometry;
import com.metaio.unifeye.ndk.LLACoordinate;
import com.metaio.unifeye.ndk.PoseVector;
import com.metaio.unifeye.ndk.Vector2di;
import com.metaio.unifeye.ndk.Vector3d;



/**
 * This is base activity to use Unifeye SDK Mobile. It creates UnifeyeGLSurface
 * view and handle all its callbacks and lifecycle.
 * 
 * 
 * @author arsalan.malik
 * 
 */
public abstract class ARViewActivity extends Activity implements
		UnifeyeGLSurfaceView.Callback, OnTouchListener, UnifeyeSensorsManager.Callback{
	
	static {
		IUnifeyeMobileAndroid.loadNativeLibs();
	}

	/**
	 * Sensor manager
	 */
	protected UnifeyeSensorsManager mSensorsManager;
	
	// My code 7/5/2012 12:10 PM
	public IUnifeyeMobileGeometry mNorth, mSouth, mEast, mWest;
	public float compass_angle = 40;

	/**
	 * Unifeye OpenGL View
	 */
	protected UnifeyeGLSurfaceView mUnifeyeSurfaceView;
	
	public boolean copter_clicked = false;

	/**
	 * The detected coordinate system id of the previous frame. This is used in onDrawFrame
	 * to check if a target has been just detected.
	 */
	protected int mDetectedCosID = -1;

	/**
	 * GUI overlay, only valid in onStart and if a resource is provided in
	 * getGUILayout.
	 */
	protected View mGUIView;

	/**
	 * UnifeyeSDK object
	 */
	protected IUnifeyeMobileAndroid mMobileSDK;

	/**
	 * flag for the renderer
	 */
	private boolean rendererInitialized = false;

	/**
	 * Wake lock to avoid screen time outs.
	 * <p>
	 * The application must request WAKE_LOCK permission.
	 */
	protected PowerManager.WakeLock mWakeLock;

	/**
	 * UnifeyeMobile callback handler
	 */
	private IUnifeyeMobileCallback mHandler;
	
	
	// My code 7/3/2012 1:13 PM
	public IUnifeyeMobileGeometry mGeometry, truckGeom;
	
	// My code 7/3/2012 8:40 PM
	public String newTracking = "TrackingData_ML3D.xml";
	public String ext_dir = Environment.getExternalStorageDirectory().toString();
	
	//My code 7/5/2012 10:51 PM
	public TextView fps_text;
	public TextView latitude_text;
	public TextView longitude_text;
	
	public LocationManager location;
	public Location loc;
	
	public TextView text1,text2;
	
	public String Lat, Long; 
	/**
	 * Provide resource for GUI overlay if required.
	 * <p>
	 * The resource is inflated into mGUIView which is added in onStart
	 * 
	 * @return Resource ID of the GUI view
	 */
	
	// My code 7/3/2012 8:17 PM
	protected int getGUILayout() {
		return R.layout.but_to_chan_track;
		//return 0;
	};

	
	// My code 7/5/2012 11:46 PM
	
	
	
	/**
	 * Provide Unifeye callback handler if desired.
	 * 
	 * @see IUnifeyeMobileCallback
	 * 
	 * @return Return unifeye callback handler
	 */
	protected IUnifeyeMobileCallback getMobileSDKCallbackHandler() {
		return mHandler;
	};

	/**
	 * Load contents to unifeye in this method, e.g. tracking data, geometries
	 * etc.
	 */
	protected abstract void loadUnifeyeContents();
	
	public PoseVector poses;

	/**
	 * Called when a geometry is touched.
	 * 
	 * @param geometry
	 *            Geometry that is touched
	 */
	protected void onGeometryTouched(IUnifeyeMobileGeometry geometry) {
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		loc = location.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		// My Code 7/5/2012 10:52 PM
		fps_text = (TextView) findViewById(R.id.fps_status);
		latitude_text = (TextView) findViewById(R.id.latitude);
		longitude_text = (TextView) findViewById(R.id.longitude);
		
		/*if(loc != null)
		{
			latitude_text.setText(String.valueOf(loc.getLatitude()));
			longitude_text.setText(String.valueOf(loc.getLongitude()));
		}*/
		
		
		
		UnifeyeDebug.log("ARViewActivity.onCreate()");
		//mMobileSDK = null;
		
		mMobileSDK = AS_UnifeyeSDKMobile.CreateUnifeyeMobileAndroid(this, Configuration.signature);
		mUnifeyeSurfaceView = null;

		mHandler = null;

		try {
			// create the sensor manager
			if (mSensorsManager == null) {
				mSensorsManager = new UnifeyeSensorsManager(getApplicationContext());
			}

			// create the MobileSDK
			Log.d("NOS", "creating UnifeyeMobile in onCreate()");
			createMobileSDK();
			
			// Inflate GUI view if provided
			mGUIView = View.inflate(this, getGUILayout(), null);
		} catch (Exception e) {

		}

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				getPackageName());

	}

	@Override
	protected void onStart() {
		super.onStart();
		
		UnifeyeDebug.log("ARViewActivity.onStart(): "
				+ Thread.currentThread().getId());

		try {
			mUnifeyeSurfaceView = null;

			// start/resume the camera
			if (mMobileSDK != null) {

				// create a empty layout, required for the camera on some devices
				setContentView(new FrameLayout(this));
							
				Vector2di cameraResolution = mMobileSDK.startCamera(
						Configuration.Camera.deviceId,
						Configuration.Camera.resolutionX,
						Configuration.Camera.resolutionY);

				// Create views (UnifeyeGL and GUI)

				// Add Unifeye GL Surface view
				mUnifeyeSurfaceView = new UnifeyeGLSurfaceView(this);
				mUnifeyeSurfaceView.registerCallback(this);
				mUnifeyeSurfaceView.setKeepScreenOn(true);
				mUnifeyeSurfaceView.setOnTouchListener(this);

				// Get layout params that stretches surface view to entire
				// screen while keeping aspect ratio.
				// Pass false, to fit entire surface view in the center of the
				// parent
				FrameLayout.LayoutParams params = mUnifeyeSurfaceView
						.getLayoutParams(cameraResolution, true);

				UnifeyeDebug.log("UnifeyeSurfaceView layout: " + params.width + ", "
						+ params.height);

				addContentView(mUnifeyeSurfaceView, params);

				mUnifeyeSurfaceView.setZOrderMediaOverlay(true);

			}

			// If GUI view is inflated, add it
			if (mGUIView != null) {
				addContentView(mGUIView, new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				mGUIView.bringToFront();
			}

		} catch (Exception e) {
			UnifeyeDebug.log(e.getMessage());
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		UnifeyeDebug.log("ARViewActivity.onPause()");

		if (mWakeLock != null)
			mWakeLock.release();

		// pause the Unifeye surface
		if (mUnifeyeSurfaceView != null)
			mUnifeyeSurfaceView.onPause();

		if (mSensorsManager != null) {
			mSensorsManager.registerCallback(null);
			mSensorsManager = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		UnifeyeDebug.log("ARViewActivity.onResume()");

		if (mWakeLock != null)
			mWakeLock.acquire();

		// make sure to resume the Unifeye surface
		if (mUnifeyeSurfaceView != null)
			mUnifeyeSurfaceView.onResume();

		// Open all sensors
		if (mSensorsManager == null) {
			mSensorsManager = new UnifeyeSensorsManager(getApplicationContext());
			mSensorsManager.registerCallback(this);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		UnifeyeDebug.log("ARViewActivity.onStop()");

		if (mMobileSDK != null) {
			// Disable camera
			mMobileSDK.stopCamera();
		}

		System.runFinalization();
		System.gc();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		UnifeyeDebug.log("ARViewActivity.onDestroy()");

		if (mMobileSDK != null) {
			mMobileSDK.delete();
			mMobileSDK = null;
		}

		if (mSensorsManager != null) {
			mSensorsManager.registerCallback(null);
			mSensorsManager = null;
		}

		MobileSDKExampleApplication
				.unbindDrawables(findViewById(android.R.id.content));

		System.runFinalization();
		System.gc();

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) {
			UnifeyeDebug.log("ARViewActivity touched at: " + event.toString());

			try {
				final int x = (int) event.getX();
				final int y = (int) event.getY();

				// ask the SDK if a geometry has been hit
				IUnifeyeMobileGeometry geometry = mMobileSDK
						.getGeometryFromScreenCoordinates(x, y, true);
				if (geometry != null) {
					onGeometryTouched(geometry);
				}
			} catch (Exception e) {

			}

		}

		// don't ask why we always need to return true contrary to what
		// documentation says
		return true;
	}

	/**
	 * This function will be called, right after the OpenGL context was created.
	 * All calls to UnifeyeMobile must be done after this callback has been
	 * triggered.
	 */
	@Override
	public void onSurfaceCreated() {
		try {
			UnifeyeDebug.log("onSurfaceCreated: " + Thread.currentThread().getId());

			// initialize the renderer
			if (!rendererInitialized) {

				mMobileSDK.initializeRenderer(mUnifeyeSurfaceView.getWidth(),
						mUnifeyeSurfaceView.getHeight(),
						ERENDER_SYSTEM.ERENDER_SYSTEM_OPENGL_ES_2_0);
				loadUnifeyeContents();
				rendererInitialized = true;
			} else
				mMobileSDK.reloadTextures();

			// connect the audio callbacks
			mMobileSDK.registerAudioCallback(mUnifeyeSurfaceView
					.getUnifeyeAudioRenderer());
			mHandler = getMobileSDKCallbackHandler();
			if (mHandler != null)
				mMobileSDK.registerCallback(mHandler);

		} catch (Exception e) {

		}
	}

	/**
	 * Create  MobileSDK instance
	 */
	private void createMobileSDK() {
		try {
			UnifeyeDebug.log("Creating the metaio mobile SDK");

			// Make sure to provide a valid application signature
			mMobileSDK = AS_UnifeyeSDKMobile.CreateUnifeyeMobileAndroid( this, Configuration.signature);
			mMobileSDK.registerSensorsComponent(mSensorsManager);
			
		} catch (Exception e) {
			UnifeyeDebug.log(Log.ERROR,
					"Error creating unifeye mobile: " + e.getMessage());
		}

	}

	protected boolean loadTrackingData(String trackingDataFileName) {
		
		//String filepathTracking = AssetsManager.getAssetPath(trackingDataFileName);
		String filepathTracking = ext_dir + "/iil_files/assets/marker/"+ trackingDataFileName;
		boolean result = mMobileSDK.setTrackingData(filepathTracking);
		UnifeyeDebug.log(Log.ASSERT, "Tracking data loaded: " + result);
		return result;
	}

	/**
	 * Loads a geometry from the assets. Requires a running MobileSDK.
	 * 
	 * @param modelFileName The name of the model as in the assets folder
	 * @return A geometry object on success, null on failure.
	 * @throws FileNotFoundException
	 */
	protected IUnifeyeMobileGeometry loadGeometry(String modelFileName)
			throws FileNotFoundException {
		IUnifeyeMobileGeometry loadedGeometry = null;
		String filepath = AssetsManager.getAssetPath(modelFileName);
		//String filepath = ext_dir + "/iil_files/assets/" + modelFileName;
		if (filepath != null) {
			loadedGeometry = mMobileSDK.loadGeometry(filepath);
			if (loadedGeometry == null) {
				throw new RuntimeException(
						"Could not load the model file named " + modelFileName);
			}
		} else {
			throw new FileNotFoundException("Could not find the model named '"
					+ modelFileName + "' at: " + filepath);
		}
		return loadedGeometry;
	}

	@Override
	public void onDrawFrame() {
		try {
			
			mMobileSDK.render();
			
			//fps_text.setText("fps: " + String.valueOf(mMobileSDK.getTrackingFrameRate()) + " " + String.valueOf(mMobileSDK.getRenderingFrameRate()));
			
			 poses = mMobileSDK.getValidTrackingValues();
			if( poses.size() > 0)
			{ 
				
				// log the detected COS
				int cosID = poses.get(0).getCosID();
				if( cosID!=mDetectedCosID ){
					UnifeyeDebug.log( "detected " +  cosID );
					mDetectedCosID = cosID;
					// My code 7/13/2012 1:12 PM
					if( cosID != 2 )
					{
						mGeometry.setCos(cosID);
						mGeometry.setVisible(true);
						truckGeom.setVisible(false);
					}
					else if(cosID == 2)
					{
						truckGeom.setCos(cosID);
						mGeometry.setVisible(false);
						truckGeom.setVisible(true);
					}
					
				}
			}else{
				// reset the detected COS if nothing has been detected 
				mDetectedCosID = -1;
			}
				
			
		} catch (Exception e) {

		}

	}

	@Override
	public void onSurfaceDestroyed() {
		mUnifeyeSurfaceView = null;
		UnifeyeDebug.log("onSurfaceDestroyed");
	}

	@Override
	public void onSurfaceChanged() {
		UnifeyeDebug.log("onSurfaceChanged");
	}

	@Override
	public void onScreenshot(Bitmap bitmap) {
	}
	
	// My code 7/3/2012 9:35 PM
	public void onTrackBttnClicked(final View eventSource)
	{
		mUnifeyeSurfaceView.queueEvent(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String new_file = AssetsManager.getAssetPath(newTracking);
				mMobileSDK.setTrackingData(new_file);				
			}}
				);
		
	}
	
	public void onDwnldBttnClicked(final View eventSource)
	{
		mUnifeyeSurfaceView.queueEvent(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String new_file = ext_dir + "/iil_files/assets/marker/" + "TrackingData_Cop.xml";
				mMobileSDK.setTrackingData(new_file);				
			}}
				);
	}
	
	public void on3DBttnClicked(final View eventSource)
	{
		
		mUnifeyeSurfaceView.queueEvent(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String new_file = ext_dir + "/iil_files/assets/marker/" + "TrackingData_Car.xml";
				mMobileSDK.setTrackingData(new_file);				
			}}
				);
	}
	
 	public void DownloadImage(String imageUrl, String fileName)
	{
		try {
			URL url = new URL(imageUrl);
			//String ext_dir = Environment.getExternalStorageDirectory().toString();
		
			File file = new File(ext_dir + "/iil_files/" + fileName);
			
			long startTime = System.currentTimeMillis();
			Log.d("ImageManager", "download Beginning");
			Log.d("ImageManager", "download url: " + url);
			Log.d("ImageManager", "download file name: " + fileName);
			
			URLConnection ucon = url.openConnection();
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			
			ByteArrayBuffer baf = new ByteArrayBuffer(250);
			int current = 0;
			
			while((current = bis.read()) != -1)
			{
				baf.append((byte) current);
			}
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();
			Log.d("ImageManager", "download ready in"
                    + ((System.currentTimeMillis() - startTime) / 1000)
                    + " sec");
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
