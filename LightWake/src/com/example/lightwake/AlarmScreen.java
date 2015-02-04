package com.example.lightwake;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AlarmScreen extends Activity {

	public final String TAG = this.getClass().getSimpleName();
	
	private WakeLock mWakeLock;
	// TODO mplayer
	//private MediaPlayer mPlayer; 
	
	private static final int WAKELOCK_TIMEOUT = 60 * 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// setup layout
		this.setContentView(R.layout.activity_alarm_screen);
		
		String label = getIntent().getStringExtra(AlarmManagerHelper.LABEL);
		int hour = getIntent().getIntExtra(AlarmManagerHelper.HOUR, 0);
		int minute = getIntent().getIntExtra(AlarmManagerHelper.MINUTE, 0);
		
		TextView tvLabel = (TextView) findViewById(R.id.alarm_screen_label);
		tvLabel.setText(label);

		TextView tvTime = (TextView) findViewById(R.id.alarm_screen_time);
		tvTime.setText(String.format("%02d : %02d", hour, minute));
		
		Button dismissButton = (Button) findViewById(R.id.alarm_screen_button);
		dismissButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO: implement sound through mplayer
				//mPlayer.stop();
				finish();
			}
		
		});

		// TODO: add mPlayer to play tone

		Runnable releaseWakelock = new Runnable() {
			
			@Override
			public void run() {
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
				
				if(mWakeLock != null && mWakeLock.isHeld()) {					
					mWakeLock.release();
				}
			}
		
		};
		
		new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);
	}	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		
		// Set the window to keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		// Acquire wakelock
		PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
		if(mWakeLock == null) {
			mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
			
		}
		if(!mWakeLock.isHeld()) {
			mWakeLock.acquire();
			Log.i(TAG, "Wakelock acquired!");
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();			
		}
	}
	
	
}

