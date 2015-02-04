package com.example.lightwake;

import java.util.ArrayList;
import java.util.List;

import com.example.lightwake.AlarmContract.Alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmDBHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "lightwake.db";
	
	private static final String SQL_CREATE_ALARM =
			"CREATE TABLE " + Alarm.TABLE_NAME + " (" + 
					Alarm._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					Alarm.COLUMN_NAME_ALARM_LABEL + " TEXT," +
					Alarm.COLUMN_NAME_ALARM_TIME_HOUR + " INTEGER," +
					Alarm.COLUMN_NAME_ALARM_TIME_MINUTE + " INTEGER," +
					Alarm.COLUMN_NAME_ALARM_LIGHT_ENABLED + " BOOLEAN," +
					Alarm.COLUMN_NAME_ALARM_SNOOZE_ENABLED + " BOOLEAN," +
					Alarm.COLUMN_NAME_ALARM_ENABLED + " BOOLEAN" + " )";

	private static final String SQL_DELETE_ALARM = 
			"DROP TABLE IF EXISTS " + Alarm.TABLE_NAME;
	
	public AlarmDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ALARM);		
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ALARM);
		onCreate(db);
	}
	
	private AlarmDetails populateModel(Cursor c) {
		AlarmDetails alarm = new AlarmDetails();
		
		alarm.id = c.getLong(c.getColumnIndex(Alarm._ID));
		alarm.label = c.getString(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_LABEL));
		alarm.hour = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TIME_HOUR));
		alarm.minute = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE));
		alarm.lightEnabled = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_LIGHT_ENABLED)) == 0 ? false : true;
		alarm.snoozeEnabled = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_SNOOZE_ENABLED)) == 0 ? false : true;
		alarm.enabled = c.getInt(c.getColumnIndex(Alarm.COLUMN_NAME_ALARM_ENABLED)) == 0 ? false : true;
		
		return alarm;
		
	}
	
	private ContentValues populateContent(AlarmDetails alarm) {
		ContentValues values = new ContentValues();
		values.put(Alarm.COLUMN_NAME_ALARM_LABEL, alarm.label);
		values.put(Alarm.COLUMN_NAME_ALARM_TIME_HOUR, alarm.hour);
		values.put(Alarm.COLUMN_NAME_ALARM_TIME_MINUTE, alarm.minute);
		values.put(Alarm.COLUMN_NAME_ALARM_LIGHT_ENABLED, alarm.lightEnabled);
		values.put(Alarm.COLUMN_NAME_ALARM_SNOOZE_ENABLED, alarm.snoozeEnabled);
		values.put(Alarm.COLUMN_NAME_ALARM_ENABLED, alarm.enabled);		

		return values;
	}
	
	public long createAlarm(AlarmDetails alarm) {
		ContentValues values = populateContent(alarm);
		return getWritableDatabase().insert(Alarm.TABLE_NAME, null, values);
		
	}

	public AlarmDetails getAlarm(long id) {
		SQLiteDatabase db = getReadableDatabase();
		
		String select = "SELECT * FROM " + Alarm.TABLE_NAME + " WHERE " + Alarm._ID + " = " + id;
		
		Cursor c = db.rawQuery(select, null);
		
		if( c.moveToNext()) {
			return populateModel(c);
		}

		// TODO: check whether Cursor c should be closed
		
		return null;
		
	}

	public long updateAlarm(AlarmDetails alarm) {
		ContentValues values = populateContent(alarm);
		return getWritableDatabase().update(Alarm.TABLE_NAME, values, Alarm._ID + " = ?", new String[] { String.valueOf(alarm.id) } );	
	}

	public int deleteAlarm(long alarmID) {
		return getWritableDatabase().delete(Alarm.TABLE_NAME, Alarm._ID + " = ?", new String[] { String.valueOf(alarmID) } );		
	}
	

	public List<AlarmDetails> getAlarms() {
		SQLiteDatabase db = getReadableDatabase();

		String select = "SELECT * FROM " + Alarm.TABLE_NAME;
		
		Cursor c = db.rawQuery(select, null);
		
		List<AlarmDetails> alarmList = new ArrayList<AlarmDetails>();

		while (c.moveToNext()) {
			alarmList.add(populateModel(c));			
		}
		
		if(!alarmList.isEmpty()) {
			return alarmList;
		}

		return null;
	}
	
}
