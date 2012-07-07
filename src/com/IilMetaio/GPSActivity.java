package com.IilMetaio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

public class GPSActivity extends Activity{

	public TextView text1;
	public TextView text2;
	public LocationManager location;
	public String TAG = "GPS_Activity";
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_screen);
	
	String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	if(provider.equals(""))
	{
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        startActivity(intent);
	}
	text1 = (TextView) findViewById(R.id.textView1);
    text2 = (TextView) findViewById(R.id.textView2);
    
   
    location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    Location loc = location.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
   
    text1.setText(String.valueOf(loc.getLatitude()));
    text2.setText(String.valueOf(loc.getLongitude()));
   
    LocationListener listener = new LocationListener()
    {

		@Override
		public void onLocationChanged(Location loca) {
			// TODO Auto-generated method stub
			Log.i(TAG, "on_location_changed:" + " " + ((Double)loca.getLatitude()).toString() + " " + ((Double)loca.getLongitude()).toString());
			String LAT =  String.valueOf(loca.getLatitude()) ;
			String LONG =  String.valueOf(loca.getLongitude());
			text1.setText(LAT);
			text2.setText(LONG);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Log.i(TAG,"on_Provider_Disabled: "+ provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Log.i(TAG, "on_Provider_Enabled: " +provider);
		}

		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			// TODO Auto-generated method stub
			Log.i(TAG, "on_Status_Changed: " + provider);
		}};
		
		{
			location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);	
		}
       }

	@Override
	public void onPause()
	{
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		System.runFinalization();
		System.gc();
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		MobileSDKExampleApplication.unbindDrawables(findViewById(android.R.id.content));
		System.runFinalization();
		System.gc();
	}
}



