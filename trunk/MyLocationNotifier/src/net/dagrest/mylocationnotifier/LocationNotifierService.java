package net.dagrest.mylocationnotifier;

import java.util.Calendar;
import java.util.List;

import net.dagrest.gmailsender.GMailSender;
import net.dagrest.mylocationnotifier.log.LogManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationNotifierService extends Service {

	private static Context context;

    public static final String PREFS_NAME = "MyPreferences";
    private SharedPreferences sharedPreferences;
	private Preferences preferences;
	private String latlong;
    private LocationManager locationManager;
	private List<String> locationProviders;
	private Boolean isLocationProviderAvailable;
	float accuracy;
	float speed;
	String locationProvider;

	
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
        if(context == null){
        	context = MyLocationNotifierActivity.getContext();
        }
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        }
        if(preferences == null){
            preferences = new Preferences(sharedPreferences);
        }
        if(locationManager == null){
        	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        LogManager.LogFunctionExit("LocationNotifierService", "onCreate()");
    }                    
    
	public void sendLocationByMail(String strLocation, String provider) { 
        LogManager.LogFunctionCall("LocationNotifierService", "sendLocationByMail()");

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
	        LogManager.LogInfoMsg("LocationNotifierService", "GMailSender()", "Location: " + testStr);
			
	        try {   
                GMailSender sender = new GMailSender(senderMail, password);
	            sender.sendMail(curTime + " - My Location Notifier",   
	                    "http://maps.google.com/maps?q=" + testStr + "&iwloc=A&hl=en \nDeviceID:" + 
	                    	deviceUid + "\nProvider: " + locationProvider + "\nAccuracy[meters]: " + accuracy +
	                    	"\nSpeed[km/h]:" + (speed*3600/1000),
	                    senderMail,   
	                    recipientMail);   

	            preferences.setStringSettingsValue("locationString", "initial");
	            preferences.setStringSettingsValue("locationStringNetwork", "initial");
	            preferences.setStringSettingsValue("locationStringGPS", "initial");
	            preferences.setStringSettingsValue("locationStringNETWORK", "initial");

	        } catch (Exception e) {   
	            LogManager.LogInfoMsg("LocationNotifierService", "sendLocationByMail()", "SendMail EXCEPTION: " + e.getMessage());
	            LogManager.LogException(e, "LocationNotifierService", "sendLocationByMail()");
	        } 
		}
        LogManager.LogFunctionExit("LocationNotifierService", "sendLocationByMail()");
	}
    
	// Define a listener that responds to location updates
	LocationListener locationListenerGPS = new LocationListener() {
	    public void onLocationChanged(Location location) {

	    	LogManager.LogFunctionCall("LocationListener", "onLocationChanged()");

	    	preferences.setStringSettingsValue("locationProviderName", "GPS");

	    	double latitude = 0, longitude = 0;
	    	latitude = location.getLatitude();
	    	longitude = location.getLongitude();
	    	if(latitude == 0 || longitude == 0){
	    		return;
	    	}
	    	accuracy = location.getAccuracy();
	    	locationProvider = location.getProvider();
	    	speed = location.getSpeed();

	    	preferences.setStringSettingsValue("locationProviderName", locationProvider);
	    	
			//sets latitude/longitude when a location is provided
			latlong = location.getLatitude() + "," + location.getLongitude();
			
	        LogManager.LogInfoMsg("LocationListener", "onLocationChanged()", "@@@NEW_LOCATION_GPS: " + latlong);
	        preferences.setStringSettingsValue("locationString", latlong);
	        preferences.setStringSettingsValue("locationStringGPS", latlong);
        
	        //sendLocationByMail(latlong);

	        LogManager.LogFunctionExit("LocationListener", "onLocationChanged()");
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	  };

	// Define a listener that responds to location updates
	LocationListener locationListenerNetwork = new LocationListener() {
	    public void onLocationChanged(Location location) {

	    	LogManager.LogFunctionCall("locationListenerNetwork", "onLocationChanged()");
	    	
	    	preferences.setStringSettingsValue("locationProviderName", "NETWORK");
	    	
	    	double latitude = 0, longitude = 0;
	    	latitude = location.getLatitude();
	    	longitude = location.getLongitude();
	    	if(latitude == 0 || longitude == 0){
	    		return;
	    	}
	    	accuracy = location.getAccuracy();
	    	locationProvider = location.getProvider();
	    	speed = location.getSpeed();
	    	
	    	preferences.setStringSettingsValue("locationProviderName", locationProvider);

	    	//sets latitude/longitude when a location is provided
			latlong = location.getLatitude() + "," + location.getLongitude();
			
	        LogManager.LogInfoMsg("locationListenerNetwork", "onLocationChanged()", "@@@NEW_LOCATION_NETWORK: " + latlong);
	        preferences.setStringSettingsValue("locationStringNetwork", latlong);
	        preferences.setStringSettingsValue("locationStringNETWORK", latlong);
       
	        //sendLocationByMail(latlong);

	        LogManager.LogFunctionExit("locationListenerNetwork", "onLocationChanged()");
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
        String locationProviderName = null;
        isLocationProviderAvailable = preferences.getBooleanSettingsValue("isLocationProviderAvailable");
        if(isLocationProviderAvailable){
	        String locationString = null;
	        String locationStringNetwork = null;
	        String locationStringGPS = null;
	        String locationStringNETWORK = null;
	        
	        locationString = preferences.getStringSettingsValue("locationString", locationString);
	        locationStringNetwork = preferences.getStringSettingsValue("locationStringNetwork", locationStringNetwork);

        	locationProviderName = preferences.getStringSettingsValue("locationProviderName", locationProviderName);

        	if(!locationString.equals("initial")){
	        	LogManager.LogInfoMsg("LocationNotifierService", "onStart()", "locationGPS: " + locationString);
	        	sendLocationByMail(locationString, locationProvider);
	        } else if(!locationStringNetwork.equals("initial")){
	        	LogManager.LogInfoMsg("LocationNotifierService", "onStart()", "locationNETWORK: " + locationStringNetwork);
	        	sendLocationByMail(locationStringNetwork, locationProvider);
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
			locationManager.removeUpdates(locationListenerGPS);
			locationManager.removeUpdates(locationListenerNetwork);
		}
    	
        LogManager.LogFunctionExit("LocationNotifierService", "onDestroy()");
    }  

	private boolean providerAvailable(List<String> providers) {
		if (providers.isEmpty()) {
			return false;
		}
		return true;
	}

	private void requestLocation(boolean forceGps) {
        LogManager.LogFunctionCall("LocationNotifierService", "requestLocation()");
		locationManager.removeUpdates(locationListenerGPS);
		locationProviders = locationManager.getProviders(true);
		LogManager.LogInfoMsg("LocationNotifierService", "requestLocation()", "Providers list: " + locationProviders.toString());

		if (providerAvailable(locationProviders)) {
			boolean containsGPS = locationProviders.contains(LocationManager.GPS_PROVIDER);
			LogManager.LogInfoMsg("LocationNotifierService", "requestLocation()", "containsGPS: " + containsGPS);

			boolean containsNetwork = locationProviders.contains(LocationManager.NETWORK_PROVIDER);
			LogManager.LogInfoMsg("LocationNotifierService", "requestLocation()", "containsNetwork: " + containsNetwork);

			if (containsGPS && forceGps) {
		        LogManager.LogInfoMsg("LocationNotifierService", "requestLocation()", "GPS_PROVIDER selected.");
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListenerGPS);
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListenerNetwork);
		        preferences.setBoooleanSettingsValue("isLocationProviderAvailable", true);
		        //preferences.setStringSettingsValue("locationProviderName", "GPS");
			} else if (containsNetwork) {
		        LogManager.LogInfoMsg("LocationNotifierService", "requestLocation()", "NETWORK_PROVIDER selected.");
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListenerNetwork);
		        preferences.setBoooleanSettingsValue("isLocationProviderAvailable", true);
		        //preferences.setStringSettingsValue("locationProviderName", "NETWORK");
			}
		} else {
	        LogManager.LogInfoMsg("LocationNotifierService", "requestLocation()", "No location providers available.");
	        preferences.setBoooleanSettingsValue("isLocationProviderAvailable", false);
		}
        LogManager.LogFunctionExit("LocationNotifierService", "requestLocation()");
	}

}
