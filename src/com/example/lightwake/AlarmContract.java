package com.example.lightwake;

import android.provider.BaseColumns;

public final class AlarmContract { 

	public AlarmContract () {} 

	public static abstract class Alarm implements BaseColumns {
		public static final String TABLE_NAME = "alarm";
		public static final String COLUMN_NAME_ALARM_LABEL = "label";
		public static final String COLUMN_NAME_ALARM_TIME_HOUR = "hour";
		public static final String COLUMN_NAME_ALARM_TIME_MINUTE = "minute";
		public static final String COLUMN_NAME_ALARM_LIGHT_ENABLED = "light";
		public static final String COLUMN_NAME_ALARM_SNOOZE_ENABLED = "snooze";
		public static final String COLUMN_NAME_ALARM_ENABLED = "enabled";
	}

}