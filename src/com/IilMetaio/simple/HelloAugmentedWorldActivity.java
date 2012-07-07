/**
 * ARViewActivitySimple.java
 * Example SDK Internal
 *
 * Created by Arsalan Malik on 08.03.2011
 * Copyright 2011 metaio GmbH. All rights reserved.
 *
 */

package com.IilMetaio.simple;



import org.apache.http.util.ByteArrayBuffer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Environment;
import android.util.Log;

import com.IilMetaio.utility.*;
import com.metaio.tools.io.AssetsManager;
import com.metaio.unifeye.UnifeyeDebug;
import com.metaio.unifeye.UnifeyeSensorsManager;
import com.metaio.unifeye.ndk.LLACoordinate;
import com.metaio.unifeye.ndk.Vector3d;



/**
 * EXAMPLE 1
 * 
 * This is a simple AR activity which shows 2D planar marker tracking with
 * animations. The target pattern and the model is MetaioMan.
 * 
 * Please visit the following link for a detailed explanation. 
 * {@link http://docs.metaio.com/bin/view/Main/HelloAugmentedWorldExample}
 * 
 * @author arsalan.malik, tim.oppermann
 * 
 */

public class HelloAugmentedWorldActivity extends ARViewActivity  {
	
	 
	private final String mTrackingDataFileName = "TrackingData_MarkerlessFast.xml";
	
	//public LLACoordinate lla_cos;
	
		
	/**
	 * Gets called by the super-class after the GLSurface has been created. 
	 * It runs on the OpenGL-thread.
	 */
	@Override
	protected void loadUnifeyeContents() {
		
		try {
			
			// My code 7/5/2012 11:06 AM
			//lla_cos = mSensorsManager.getLocation();
			//latitude_text.setText(String.valueOf(lla_cos.getLatitude()));
			//longitude_text.setText(String.valueOf(lla_cos.getLongitude()));
			
			// My code 7/3/2012 9:54 PM
						DownloadImage("http://192.168.165.1/xampp/iil/marker.zip", "marker.zip");
						String zipFile = Environment.getExternalStorageDirectory() + "/iil_files/marker.zip"; 
						String unzipLocation = Environment.getExternalStorageDirectory() + "/iil_files/assets/marker/"; 
						 
						Decompress d = new Decompress(zipFile, unzipLocation); 
						d.unzip(); 
						
						DownloadImage("http://192.168.165.1/xampp/iil/car.zip", "car.zip");
						 zipFile = Environment.getExternalStorageDirectory() + "/iil_files/car.zip"; 
						 unzipLocation = Environment.getExternalStorageDirectory() + "/iil_files/assets/marker/"; 
						 
						 d = new Decompress(zipFile, unzipLocation); 
						 d.unzip();
						 
						 DownloadImage("http://192.168.165.1/xampp/iil/cop.zip", "cop.zip");
						 zipFile = Environment.getExternalStorageDirectory() + "/iil_files/cop.zip"; 
						 unzipLocation = Environment.getExternalStorageDirectory() + "/iil_files/assets/marker/"; 
						 
						 d = new Decompress(zipFile, unzipLocation); 
						 d.unzip();
						 
						/* DownloadImage("http://192.168.165.1/xampp/iil/cop1.zip", "cop1.zip");
						 zipFile = Environment.getExternalStorageDirectory() + "/iil_files/cop1.zip"; 
						 unzipLocation = Environment.getExternalStorageDirectory() + "/iil_files/assets/marker/"; 
						 
						 d = new Decompress(zipFile, unzipLocation); 
						 d.unzip(); */

			// Load Tracking data
			loadTrackingData( mTrackingDataFileName ); 
		
			mGeometry = loadGeometry("metaioman.md2");
			
			
			//DownloadImage("http://www.thinkgeek.com/images/products/zoom/e554_android_plush_robot.jpg", "google.jpg");
			
			// My code 7/3/2012 1:15 PM
			truckGeom = loadGeometry("truck/truck.obj");
			truckGeom.setVisible(false);
			
			Vector3d orient = mSensorsManager.getOrientationReading();
			mNorth = mMobileSDK.loadImageBillboard(AssetsManager.getAssetPath("North.png"));
			mSouth = mMobileSDK.loadImageBillboard(AssetsManager.getAssetPath("South.png"));
			mEast = mMobileSDK.loadImageBillboard(AssetsManager.getAssetPath("East.png"));
			mWest = mMobileSDK.loadImageBillboard(AssetsManager.getAssetPath("West.png"));
			/*mNorth.setVisible(false);
			mSouth.setVisible(false);
			mEast.setVisible(false);
			mWest.setVisible(false);*/
			
			mGeometry.setMoveScale( new Vector3d(1,1,1) );
			int cos = mGeometry.getCos();
			System.out.println(cos);
			
			//lla_cos.delete();
			//lla_cos = null;
			
		} catch (Exception e) {
			UnifeyeDebug.printStackTrace(Log.ERROR, e);
			Logger.logException(e);
		}
	}

	@Override
	public void onAccelerometerSensorChanged(Vector3d arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationSensorChanged(LLACoordinate arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOrientationSensorChanged(Vector3d arg0) {
		// TODO Auto-generated method stub
		mNorth.setMoveRotation(arg0);
		compass_angle = (float) Math.toDegrees(arg0.getX());
	}

	

	
	

	
}
