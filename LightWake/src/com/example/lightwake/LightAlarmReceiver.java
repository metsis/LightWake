package com.example.lightwake;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class LightAlarmReceiver extends BroadcastReceiver {

	// TODO make these dynamic/configurable in settings
	// and devise an auto-configuration sequence
	private static final String DEVICE_ID = "53ff6f065075535133111687";
	private static final String ACCESS_TOKEN = "f6bc98b340b7c071675685403c8f46e4ca13fd93";
	
	// TODO: create class: LightControl (to control the physical light, with methods such as
	// lightControl.switchOn()
	// lightControl.switchOff()
	
	@Override
	public void onReceive(Context context, Intent intent) {

		/*
		 * The API request will look something like this:
		   	POST /v1/devices/{DEVICE_ID}/led

			# EXAMPLE REQUEST IN TERMINAL
			# Core ID is 0123456789abcdef
			# Your access token is 123412341234
			curl https://api.spark.io/v1/devices/0123456789abcdef/led \
  				-d access_token=123412341234 \
  				-d params=l1,HIGH
		 */				
		postData("HIGH");		
	}
				
	public void postData(String data) {

		// execute http post as an asynctask
		HttpPostTask postTask = new HttpPostTask();
		postTask.execute(new String[] { data });			    
		
	}

	private class HttpPostTask extends AsyncTask<String, Void, String> {
      
		  @Override
		  protected String doInBackground(String... data) {
			  
			  String responseText = "";
			  HttpClient httpclient = new DefaultHttpClient();
			  HttpPost httppost = new HttpPost("https://api.spark.io/v1/devices/" + DEVICE_ID + "/led");
			  
			  try {	
				  
				  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);			
				  nameValuePairs.add(new BasicNameValuePair("access_token", ACCESS_TOKEN));
				  nameValuePairs.add(new BasicNameValuePair("params", data[0]));
				  httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));		
				  HttpResponse response = httpclient.execute(httppost);
    	    	  
    	    	  InputStream content = response.getEntity().getContent();

		          BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		          String s = "";
		          while ((s = buffer.readLine()) != null) {
		            responseText += s;
		          }

		       } catch (Exception e) {
		          e.printStackTrace();
		       }
		      return responseText;
		    }

		    @Override
		    protected void onPostExecute(String result) {
		      //textView.setText(result);
		    }
		  }

	
	
}
