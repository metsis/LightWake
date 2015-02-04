package com.example.lightwake;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;


public class AlarmListAdapter extends BaseAdapter {

	private Context mContext;
	private List<AlarmDetails> mAlarms;
	
	public AlarmListAdapter(Context context, List<AlarmDetails> alarms) {
		mContext = context;
		mAlarms = alarms;
	}

	public void setAlarms(List<AlarmDetails> alarms) {
		mAlarms = alarms;		
	}

	@Override 
	public int getCount() {
		if(mAlarms != null) {
			return mAlarms.size();			
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(mAlarms != null) {
			return mAlarms.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		if(mAlarms != null) {
			return mAlarms.get(position).id;
		}

		return 0;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		// reuse views if they exist	
		if(view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.alarm_list_item, parent, false);
		}

		AlarmDetails alarm = (AlarmDetails) getItem(position);
		
		TextView txtTime = (TextView) view.findViewById(R.id.alarm_item_time);
		txtTime.setText(String.format("%02d : %02d", alarm.hour, alarm.minute));

		TextView txtLabel = (TextView) view.findViewById(R.id.alarm_item_label);		
		txtLabel.setText(alarm.label);

		// show by text color whether light and snooze are enabled
		updateTextColor((TextView) view.findViewById(R.id.alarm_item_light), alarm.lightEnabled);
		updateTextColor((TextView) view.findViewById(R.id.alarm_item_snooze), alarm.snoozeEnabled);
		
		ToggleButton btnToggle = (ToggleButton) view.findViewById(R.id.alarm_item_toggle);
		btnToggle.setChecked(alarm.enabled);
		btnToggle.setTag(Long.valueOf(alarm.id));
		btnToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				((ListAlarms) mContext).setAlarmEnabled(((Long) buttonView.getTag()).longValue(), isChecked);
			}
		});
		
		view.setTag(Long.valueOf(alarm.id));
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				((ListAlarms) mContext).startAddAlarmActivity(((Long) view.getTag()).longValue());				
			}			

		});
		
/*		view.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View view) {
				((ListAlarms) mContext).promptRemoveAlarm(((Long) view.getTag()).longValue());
				return true;
			}
		});*/
		
		return view;
	}
	
	private void updateTextColor(TextView view, boolean isOn) {
		if(isOn) {
			view.setTextColor(Color.parseColor("#FF8800"));			
		} else {
			view.setTextColor(Color.BLACK);
		}		
	}
	
}