package com.peter.worm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

public class main extends AppCompatActivity {
	
	private SensorManager mManager = null;
	private Sensor mSensor = null;
	private Worm worm;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setOverflowShowingAlways();
        //通过xml加载界面
        setContentView(R.layout.main);
        worm = (Worm) findViewById(R.id.worm);
        

    	//重力监听相关代码，有的机器不支持重力监听
      mManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
      mSensor = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	SensorEventListener mListener = new SensorEventListener() {
    		public void onAccuracyChanged(Sensor sensor, int accuracy) {
    	    }
    		public void onSensorChanged(SensorEvent event) {
    			if(worm.gravityCnotrol && worm.canGo){
		    		float x = event.values[SensorManager.DATA_X];
		    		float y = event.values[SensorManager.DATA_Y];
		    		float z = event.values[SensorManager.DATA_Z];
		    		int dir = worm.direction;
		    		if(z>0){
			    		if(x > 0 && y > 0 ){
			    			if(x > y &&  dir != worm.DIR_RIGHT)
			    				worm.direction =worm.DIR_LEFT;
			    			if(x < y && dir != worm.DIR_UP)
			    				worm.direction =worm.DIR_DOWN;
			    		}
			    		if(x > 0 && y < 0){
			    			if(x > -y && dir != worm.DIR_RIGHT)
			    				worm.direction =worm.DIR_LEFT;
			    			if(x < -y &&dir != worm.DIR_DOWN)
			    				worm.direction =worm.DIR_UP;

			    		}
			    		if(x < 0 && y > 0){
			    			if(y > -x && dir != worm.DIR_UP)
			    				worm.direction =worm.DIR_DOWN;
			    			if(y < -x && dir != worm.DIR_LEFT)
			    				worm.direction =worm.DIR_RIGHT;
			    		}
			    		if(x < 0 && y < 0){
			    			if(x > y && dir != worm.DIR_DOWN)
			    				worm.direction =worm.DIR_UP;
			    			if(x < y && dir != worm.DIR_LEFT)
			    				worm.direction =worm.DIR_RIGHT;
			    		}
	    			}
		    		worm.canGo = false;
				}
    		}
    	};
        mManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(worm.go)
		if(keyCode == KeyEvent.KEYCODE_BACK){
			worm.exit = true;
			worm.go = false;
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void showAbout(){
		AlertDialog a = new AlertDialog.Builder(this).setTitle(R.string.about_title).setMessage(R.string.about_text).show();
	}
	
	public void showControl(){
		new AlertDialog.Builder(this).setTitle(R.string.paly_text).
		setNegativeButton(R.string.paly_cancel, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}}).setNeutralButton(R.string.paly_exit, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}}).setPositiveButton(R.string.paly_replay, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					worm.initinate();
				}}).show();
	}
	
	public void choseMode(){
		AlertDialog a = new AlertDialog.Builder(this).setTitle(R.string.mode_text).
		setNegativeButton(R.string.mode_gravity, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				worm.gravityCnotrol = true;
			}}).setPositiveButton(R.string.mode_touch, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					worm.gravityCnotrol = false;
				}}).show();
	}
	
	public void showScore(){
		String scores = worm.wormNum - 6 + "";
		AlertDialog a = new AlertDialog.Builder(this).setTitle(R.string.score_title).setMessage(scores).
		setPositiveButton(R.string.about_sure, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
		
		a.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK){
				}
				return false;
			}});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == android.R.id.home) {
			finish();
		}else if(id == R.id.action_about) {
			showAbout();
		}else if(id == R.id.action_play) {
			showControl();
		}else if(id == R.id.action_mode) {
			choseMode();
		}else if(id == R.id.action_score) {
			showScore();
		}
		return super.onOptionsItemSelected(item);
	}

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Log.i("", "");
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus) {
			worm.go = true;
		}else {
			worm.go = false;
		}
	}
}