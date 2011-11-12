package net.dagrest.mylocationnotifier;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;

import net.dagrest.gmailsender.GMailSender;
import net.dagrest.mylocationnotifier.log.LogManager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationNotifierService extends Service {

	private static Context context;

    public static final String PREFS_NAME = "MyPreferences";
    private SharedPreferences sharedPreferences;
	private Preferences preferences;
	private String latlong;
    private LocationManager locationManager;
	private List<String> locationProviders;
	private Boolean isLocationProviderAvailable;

	
    @Override
	public IBinder onBind(Intent arg0) {
        LogManager.LogFunctionCall("LocationNotifierService", "onBind()");
        LogManager.LogFunctionExit("LocationNotifierService", "onBind()");
		return null;
	}

    @Override          
    public void onCreate()          
    {                  
    	super.onCreate();
        LogManager.LogFunctionCall("LocationNotifierService", "onCreate()");
    	context = MyLocationNotifierActivity.getContext();
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        preferences = new Preferences(sharedPreferences);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LogManager.LogFunctionExit("LocationNotifierService", "onCreate()");
    }                    
    
	public void sendLocationByMail(String strLocation) { 
        LogManager.LogFunctionCall("LocationNotifierService", "sendLocationByMail()");

//		long millis = System.currentTimeMillis() - startTime;
//		int seconds = (int) (millis / 1000);
//		int minutes = seconds / 60;
//		seconds     = seconds % 60;
//		timeLabel.setText(String.format("%d:%02d", minutes, seconds));
        
//        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
//        preferences = new Preferences(sharedPreferences);
		//String strLocation = preferences.getStringSettingsValue("locationString", location);
        
		String testStr = strLocation;
		String password = "2803notify"; 
		String senderMail = "location.notifier@googlemail.com";
		String recipientMail = "dagrest@gmail.com";
		
        Calendar c = Calendar.getInstance();  
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        String curTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        LogManager.LogInfoMsg("LocationNotifierService", "sendLocationByMail()", "Current time: " + curTime);
       
        String laDeviceId = "351801043779392";
        String daDeviceId = "354957030678174";
        String deviceUid = null;
        deviceUid = preferences.getStringSettingsValue("deviceUid", deviceUid);
        
        if(laDeviceId.equals(deviceUid)){
        	deviceUid = "Larisa";
        }
        if(daDeviceId.equals(deviceUid)){
        	deviceUid = "David";
        }
		if(testStr != null){
	        LogManager.LogInfoMsg("LocationNotifierService", "sendLocationByMail()", "Current time: " + curTime + " Location: " + testStr);
			
	        try {   
                GMailSender sender = new GMailSender(senderMail, password);
	            sender.sendMail(curTime + " - My Location Notifier",   
	                    "http://maps.google.com/maps?q=" + testStr + "&iwloc=A&hl=en   DeviceID:" + deviceUid,   
	                    senderMail,   
	                    recipientMail);   
	        } catch (Exception e) {   
	            LogManager.LogInfoMsg("LocationNotifierService", "sendLocationByMail()", "SendMail EXCEPTION: " + e.getMessage());
	            LogManager.LogException(e, "LocationNotifierService", "sendLocationByMail()");
	        } 
		}
        LogManager.LogFunctionExit("LocationNotifierService", "sendLocationByMail()");
	}
    
	// Define a listener that responds to location updates
	LocationListener locationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {

	    	LogManager.LogFunctionCall("LocationListener", "onLocationChanged()");
	
			//sets latitude/longitude when a location is provided
			latlong = location.getLatitude() + "," + location.getLongitude();
			
			
	        Calendar c = Calendar.getInstance();  
	        int hours = c.get(Calendar.HOUR_OF_DAY);
	        int minutes = c.get(Calendar.MINUTE);
	        int seconds = c.get(Calendar.SECOND);
	        String curTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
	        LogManager.LogInfoMsg("LocationListener", "onLocationChanged()", "@@@Time: " + curTime + " = " + latlong);
	        preferences.setStringSettingsValue("locationString", latlong);
        
	        //sendLocationByMail(latlong);

	        LogManager.LogFunctionExit("LocationListener", "onLocationChanged()");
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	  };

    @Override          
    public void onStart(Intent intent, int startId)           
    {                  
    	super.onStart(intent, startId);
    	
        LogManager.LogFunctionCall("LocationNotifierService", "onStart()");
        
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, locationListener);
        requestLocation(true);
        isLocationProviderAvailable = preferences.getBooleanSettingsValue("isLocationProviderAvailable");
        if(isLocationProviderAvailable){
	        String locationString = null;
	        locationString = preferences.getStringSettingsValue("locationString", locationString);
	        if(!locationString.equals("initial")){
	        	sendLocationByMail(locationString);
	        }
        }
        
        LogManager.LogFunctionExit("LocationNotifierService", "onStart()");
    }                    
    
    @Override          
    public void onDestroy()           
    {                  
    	super.onDestroy();
    	
        LogManager.LogFunctionCall("LocationNotifierService", "onDestroy()");

        //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if(locationManager != null){
			locationManager.removeUpdates(locationListener);
		}
    	
        LogManager.LogFunctionExit("LocationNotifierService", "onDestroy()");
    }  

	private boolean providerAvailable(List<String> providers) {
		if (providers.isEmpty()) {
			return false;
		}
		return true;
	}

	private void requestLocation(boolean forceNetwork) {
        LogManager.LogFunctionCall("LocationNotifierService", "requestLocation()");
		locationManager.removeUpdates(locationListener);
		locationProviders = locationManager.getProviders(true);

		if (providerAvailable(locationProviders)) {
			boolean containsGPS = locationProviders
					.contains(LocationManager.GPS_PROVIDER);
			boolean containsNetwork = locationProviders
					.contains(LocationManager.NETWORK_PROVIDER);

			if ((containsGPS && !forceNetwork) || (containsGPS && !containsNetwork)) {
		        LogManager.LogInfoMsg("LocationNotifierService", "requestLocation()", "GPS_PROVIDER selected.");
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		        preferences.setBoooleanSettingsValue("isLocationProviderAvailable", true);
			} else if (containsNetwork) {
		        LogManager.LogInfoMsg("LocationNotifierService", "requestLocation()", "NETWORK_PROVIDER selected.");
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		        preferences.setBoooleanSettingsValue("isLocationProviderAvailable", true);
			}
		} else {
	        LogManager.LogInfoMsg("LocationNotifierService", "requestLocation()", "No location providers available.");
	        preferences.setBoooleanSettingsValue("isLocationProviderAvailable", false);
		}
        LogManager.LogFunctionExit("LocationNotifierService", "requestLocation()");
	}

}
