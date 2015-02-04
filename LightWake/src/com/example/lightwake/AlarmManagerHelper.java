package com.example.lightwake;

import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmManagerHelper extends BroadcastReceiver {

	private static final String TAG = "com.example.lightwake.AlarmManagerHelper";
	
	public static final String ID = "id";
	public static final String LABEL = "label";
	public static final String HOUR = "hour";
	public static final String MINUTE = "minute";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		setAlarms(context);		
	}

	public static void setAlarms(Context context) {
		Log.i(TAG, "setAlarms");

		cancelAlarms(context);
		
		AlarmDBHelper dbHelper = new AlarmDBHelper(context);
		
		List<AlarmDetails> alarms = dbHelper.getAlarms();
		
		for(AlarmDetails alarm : alarms) {
			if(alarm.enabled) { 
			
				PendingIntent alarmIntent, lightIntent;
				
				// instantiate both variables below with current date & time
				Calendar alarmTime = Calendar.getInstance();

				Calendar timeNow = Calendar.getInstance();
				
				// modify alarmTime to match desired alarm time
				alarmTime.set(Calendar.HOUR_OF_DAY, alarm.hour);
				alarmTime.set(Calendar.MINUTE, alarm.minute);
				alarmTime.set(Calendar.SECOND, 00);		

				// TODO: implement alarms per day
						
				// check if the time has passed for today -> set alarm for tomorrow				
				if(alarmTime.compareTo(timeNow) <= 0) {
					alarmTime.add(Calendar.DATE, 1);
				} 

				alarmIntent = createPendingIntent(context, alarm, true, AlarmService.class);
				setAlarm(context, alarmTime, alarmIntent);
				
				// if enabled, create an alarm to set the light on before the alarm 
				if(alarm.lightEnabled) {	
					lightIntent = createPendingIntent(context, alarm, false, LightAlarmReceiver.class);					
					Calendar lightAlarmTime = (Calendar) alarmTime.clone();
					// TODO: implement light alarm delta through settings, now 30 minutes
					lightAlarmTime.add(Calendar.MINUTE, -30);
					setAlarm(context, lightAlarmTime, lightIntent);
				}
				
				
			}
		}
				
	}
	
	public static void cancelAlarms(Context context) {
		AlarmDBHelper dbHelper = new AlarmDBHelper(context);
		
		List<AlarmDetails> alarms = dbHelper.getAlarms();
		
		if(alarms != null) {
			
			for(AlarmDetails alarm : alarms) {
				if(alarm.enabled) {
					PendingIntent alarmIntent = createPendingIntent(context, alarm, true, AlarmService.class); 
					
					AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					alarmManager.cancel(alarmIntent);
					
					// 
					if(alarm.lightEnabled) {
						PendingIntent lightIntent = createPendingIntent(context, alarm, false, LightAlarmReceiver.class);
						alarmManager.cancel(lightIntent);						
						
					}
					
				}
				
			}
			
		}
		
	}

	public static void toastAlarm(Context context, AlarmDetails alarm) {
		
		// instantiate both variables below with current date & time
		Calendar alarmTime = Calendar.getInstance();
		Calendar timeNow = Calendar.getInstance();
		
		// modify alarmTime to match desired alarm time
		alarmTime.set(Calendar.HOUR_OF_DAY, alarm.hour);
		alarmTime.set(Calendar.MINUTE, alarm.minute);
		alarmTime.set(Calendar.SECOND, 00);		

		// TODO: implement alarms per day
				
		int hoursFromNow, minutesFromNow;
		
		// check if the time has passed for today -> set alarm for tomorrow				
		if(alarmTime.compareTo(timeNow) <= 0) {
			alarmTime.add(Calendar.DATE, 1);
			hoursFromNow = 24 - (timeNow.get(Calendar.HOUR_OF_DAY) - alarm.hour);
		} else {
			hoursFromNow = alarm.hour - timeNow.get(Calendar.HOUR_OF_DAY);
		}

		if(alarm.minute >= timeNow.get(Calendar.MINUTE)) {
			minutesFromNow = alarm.minute - timeNow.get(Calendar.MINUTE);
		}
		else {
			hoursFromNow--;
			minutesFromNow = timeNow.get(Calendar.MINUTE) - alarm.minute;
		}
		
		CharSequence text = "Alarm set for " + String.format("%d hours, %d minutes from now (%02d:%02d).", hoursFromNow, minutesFromNow, alarm.hour, alarm.minute);
		int duration = Toast.LENGTH_SHORT;

		// show text that alarm was set
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();		
		
	}
	
	private static PendingIntent createPendingIntent(Context context, AlarmDetails alarm, boolean service, Class<?> piClass) {
		Log.i(TAG, "createPendingIntent");

		Intent intent = new Intent(context, piClass);
					
		intent.putExtra(ID, alarm.id);
		intent.putExtra(LABEL, alarm.label);
		intent.putExtra(HOUR, alarm.hour);
		intent.putExtra(MINUTE, alarm.minute);
		
		if(service)
			return PendingIntent.getService(context, (int) alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		else
			return PendingIntent.getBroadcast(context, 0, intent, 0);
		
	}

	private static void setAlarm(Context context, Calendar calendar, PendingIntent pIntent) {
		Log.i(TAG, "setAlarm");
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		// TODO: add support for Android KitKat
		//		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
		//		alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);			
		//	} else {
			alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
		//}
	}
	
}
