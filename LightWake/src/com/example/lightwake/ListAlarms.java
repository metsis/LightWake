package com.example.lightwake;

import java.util.Calendar;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class ListAlarms extends ListActivity {
	
	private AlarmListAdapter mAdapter;
	private AlarmDBHelper dbHelper = new AlarmDBHelper(this);
	private Context mContext;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		setContentView(R.layout.activity_list_alarms);
		
		mAdapter = new AlarmListAdapter(this, dbHelper.getAlarms());

		setListAdapter(mAdapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_alarms, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		switch( item.getItemId() ) {
			case R.id.action_add_new_alarm: {	// if the new alarm button is clicked in the menubar
				startAddAlarmActivity(-1);		// call the AddAlarmActivity with -1 (non-existent id)
				break; 
			}
		
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if( resultCode == RESULT_OK) {
			mAdapter.setAlarms(dbHelper.getAlarms());
			mAdapter.notifyDataSetChanged();			
		}
		
	}
		
	public void addNewAlarm(View view) {
		Intent intent = new Intent(this, AddAlarm.class);
		startActivity(intent);
	}
	
	public void setAlarmEnabled(long id, boolean isEnabled) {
		AlarmManagerHelper.cancelAlarms(this);
		
		AlarmDetails alarm = dbHelper.getAlarm(id);
		alarm.enabled = isEnabled;
		dbHelper.updateAlarm(alarm);				
		mAdapter.setAlarms(dbHelper.getAlarms());
		mAdapter.notifyDataSetChanged();
		
		AlarmManagerHelper.setAlarms(this);	

		// Toast if the alarm was enabled
		if(isEnabled) 
			AlarmManagerHelper.toastAlarm(this, alarm);
	}
	
	public void startAddAlarmActivity(long id) {
		Intent intent = new Intent(this, AddAlarm.class);
		intent.putExtra("id", id);
		startActivityForResult(intent, 0);
	}

	public void promptRemoveAlarm(final Long id) {
		// TODO Auto-generated method stub
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Confirm remove");
		alert.setMessage("Are you sure you want to remove the alarm?");
	
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// remove alarm
				// TODO: PASS ID and delete
				dbHelper.deleteAlarm(id);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
				// do nothing, return to alarm list
			}
		});

		alert.show();
	}	
	
}
