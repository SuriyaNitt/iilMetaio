package com.IilMetaio;

import com.IilMetaio.R;
//import com.MyMetaio.GPSActivity;
import com.IilMetaio.simple.*;
import com.metaio.unifeye.ndk.IUnifeyeMobileAndroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class IilMetaioActivity extends Activity {
    /** Called when the activity is first created. */
	
	static 
	{     
		IUnifeyeMobileAndroid.loadNativeLibs();
		
	} 
	
	
	private LinearLayout mMenuContainer;
	private LayoutParams mMenuItemLayoutParamters;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

		setContentView(R.layout.splash);
    }
    
    
    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if ( mMenuContainer == null )
		{
			mMenuContainer = (LinearLayout) findViewById(R.id.menuContainer);
			mMenuItemLayoutParamters = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			
			createMenuItem( "Example 1:\r\nHello Augmented World", HelloAugmentedWorldActivity.class);
			createMenuItem( "GPSActivity", GPSActivity.class);
			createMenuItem( "Test", TestActivity.class);
			findViewById(R.id.textStatus).setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	protected void onStop() {
		
		super.onStop();
		mMenuContainer.removeAllViews();
		mMenuContainer = null;
	}
	
	private void createMenuItem(final String label, final Class<?> targetActivity )
	{
		Button button = new Button(this);
		button.setText(label);
		final Intent intent = new Intent(this, targetActivity);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(intent);
			}
		});
		mMenuContainer.addView(button, mMenuItemLayoutParamters);
	}

    
}