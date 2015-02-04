package com.example.lightwake;

import java.util.Calendar;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TimePicker;

public class AddAlarm extends ActionBarActivity {

	private AlarmDBHelper dbHelper = new AlarmDBHelper(this);
	
	private AlarmDetails alarmDetails;
		
	private TimePicker timePicker;
	private EditText alarmLabel;
	private CheckedTextView lightEnabled;
	private CheckedTextView snoozeEnabled;
	// TODO: add alarm tone 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_alarm);

		long id = getIntent().getExtras().getLong("id");	// get id if there is one, if not it is -1
		
		
		timePicker = (TimePicker) findViewById(R.id.alarm_time_picker);
		timePicker.setIs24HourView(true);	// TODO make configurable
		
		
		alarmLabel = (EditText) findViewById(R.id.alarm_label);
		
		lightEnabled = (CheckedTextView) findViewById(R.id.alarm_light_enable);
		lightEnabled.setOnClickListener(new View.OnClickListener() {
		
			@Override
		    public void onClick(View v) {
				if (lightEnabled.isChecked())
					lightEnabled.setChecked(false);
		        else
		        	lightEnabled.setChecked(true);
				}
		});
		
		snoozeEnabled = (CheckedTextView) findViewById(R.id.alarm_snooze_enable);
		snoozeEnabled.setOnClickListener(new View.OnClickListener() {
			
			@Override
		    public void onClick(View v) {
				if (snoozeEnabled.isChecked())
					snoozeEnabled.setChecked(false);
		        else
		        	snoozeEnabled.setChecked(true);
				}
		});

		
		if(id == -1) {	// if the id is -1, create new alarm			
			alarmDetails = new AlarmDetails();
			getActionBar().setTitle("Create New Alarm");		
			Calendar timeNow = Calendar.getInstance();
			timePicker.setCurrentHour(timeNow.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(timeNow.get(Calendar.MINUTE));		

		} else {		// otherwise read alarm data by id from database
			getActionBar().setTitle("Update alarm");		

			alarmDetails = dbHelper.getAlarm(id);
			
			alarmLabel.setText(alarmDetails.label);
			
			timePicker.setCurrentHour(alarmDetails.hour);
			timePicker.setCurrentMinute(alarmDetails.minute);
			
			lightEnabled.setChecked(alarmDetails.lightEnabled);
			snoozeEnabled.setChecked(alarmDetails.snoozeEnabled);
			
		}		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_alarm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) {
			case android.R.id.home: {
				finish();
				break;
			}
			case R.id.action_save_alarm: {
				updateAlarmFromLayout();
        
				AlarmManagerHelper.cancelAlarms(this);
				
				if(alarmDetails.id < 0) {
					dbHelper.createAlarm(alarmDetails);					
				} else {
					dbHelper.updateAlarm(alarmDetails);					
				}
				
				AlarmManagerHelper.setAlarms(this);

				// make sure that if we edit a disabled alarm, we re-enable it 
				// when saving
				if(!alarmDetails.enabled)
					alarmDetails.enabled = true;

				AlarmManagerHelper.toastAlarm(this, alarmDetails);
				setResult(RESULT_OK);
				finish();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateAlarmFromLayout() {
		
		TimePicker timePicker = (TimePicker) findViewById(R.id.alarm_time_picker);
		alarmDetails.hour = timePicker.getCurrentHour().intValue();
		alarmDetails.minute = timePicker.getCurrentMinute().intValue();
				
		CheckedTextView lightEnabled = (CheckedTextView) findViewById(R.id.alarm_light_enable);
		alarmDetails.lightEnabled = lightEnabled.isChecked();
		
		CheckedTextView snoozeEnabled = (CheckedTextView) findViewById(R.id.alarm_snooze_enable);
		alarmDetails.snoozeEnabled = snoozeEnabled.isChecked();
		
	}

	public void promptDiscardChanges() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Discard changes");
		alert.setMessage("Are you sure you want to discard changes?");
	
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// remove alarm
				// TODO: PASS ID and delete
				
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
