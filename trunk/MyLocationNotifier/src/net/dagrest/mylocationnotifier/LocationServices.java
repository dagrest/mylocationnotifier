package net.dagrest.mylocationnotifier;

import java.util.Calendar;

import net.dagrest.mylocationnotifier.log.LogManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationServices implements LocationListener {
	
	private String latlong;
	private Context context;
	private String prefName;
	private Preferences preferences;
	
	public void setContext(Context inContext, String inPrefName){
		this.context = inContext;
		this.prefName = inPrefName;
	}
	
	public String getLatlong() {
		return latlong;
	}

	public void setLatlong(String latlong) {
		this.latlong = latlong;
	}

	public void onLocationChanged(Location location) {
        Calendar c = Calendar.getInstance();  
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        String curTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

 		LogManager.LogFunctionCall("LocationServices", "onLocationChanged(): " + curTime);
        

		//sets latitude/longitude when a location is provided
		this.latlong = location.getLatitude() + "," + location.getLongitude();
		
		if(this.context != null){
	        preferences = new Preferences(this.context.getSharedPreferences(prefName, 0));
			preferences.setStringSettingsValue("locationString", this.latlong);
		} else {
			// Cannot save locations anymore - context is unavailable
		}
		
        c = Calendar.getInstance();  
        hours = c.get(Calendar.HOUR_OF_DAY);
        minutes = c.get(Calendar.MINUTE);
        seconds = c.get(Calendar.SECOND);
        curTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        LogManager.LogFunctionExit("LocationServices", "onLocationChanged(): " + curTime);
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}