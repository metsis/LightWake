package com.example.lightwake;

public class AlarmDetails {
	
	public long id = -1;
	public int hour, minute;
	public String label;
	
	public boolean lightEnabled;	// whether to use external lamp to wake up
	public boolean snoozeEnabled;	// whether the snooze is enabled or not
	public boolean enabled;			// whether the alarm is enabled or not
	
	public AlarmDetails() {

		// initialize new instance with default values
		
		hour = minute = 0;
		label = "";
		
		lightEnabled = true;
		snoozeEnabled = true;

		enabled = true;			// by default the alarm is enabled
	}
		
}