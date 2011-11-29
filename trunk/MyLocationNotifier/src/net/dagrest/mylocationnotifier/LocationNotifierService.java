package net.dagrest.mylocationnotifier;

import java.util.List;

import net.dagrest.gmailsender.GMailSender;
import net.dagrest.mylocationnotifier.log.LogManager;
import net.dagrest.utils.Utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;

public class LocationNotifierService extends Service {

	private static final String DELIMITER = "####";
	private static Context context;

    public static final String PREFS_NAME = "MyPreferences";
    private SharedPreferences sharedPreferences;
	private Preferences preferences;
	private String latlong;
    private LocationManager locationManager;
	private List<String> locationProviders;
	private Boolean isLocationProviderAvailable;
	private float accuracy;
	private float speed;
	private String locationProvider;
	private PowerManager.WakeLock wl;
	private PowerManager pm;
	private Boolean toReleaseWakeLock;

	
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
    	
    	try{
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
	        toReleaseWakeLock = false;
	        LogManager.LogFunctionExit("LocationNotifierService", "onCreate()");
    	} catch (Exception e) {
	        LogManager.LogException(e, "LocationNotifierService", "onCreate()");
    	}
    }                    
    
	private void sendLocationByMail(String strLocation, String provider) { 

		try{
	        LogManager.LogFunctionCall("LocationNotifierService", "sendLocationByMail()");

	        String[] strArray = strLocation.split(DELIMITER);
	        
			String password = "2803notify"; 
			String senderMail = "location.notifier@googlemail.com";
			String recipientMail = "dagrest@gmail.com";

	        String curTime = Utils.getCurrentTime();
	        LogManager.LogInfoMsg("LocationNotifierService", "sendLocationByMail()", "Current time: " + curTime);
	       
	        String laDeviceId = "004999010640000";
	        String daDeviceId = "354957030678174";
	        String deviceUid = null;
	        deviceUid = preferences.getStringSettingsValue("deviceUid", deviceUid);
	        
	        if(laDeviceId.equals(deviceUid)){
	        	deviceUid = "Larisa";
	        }
	        if(daDeviceId.equals(deviceUid)){
	        	deviceUid = "David";
	        }
			if(strArray[0] != null){
		        LogManager.LogInfoMsg("LocationNotifierService", "GMailSender()", "Location: " + strArray[0]);
				
		        try {   
	                GMailSender sender = new GMailSender(senderMail, password);
		            sender.sendMail(curTime + " - " + String.format("%1$1.3f", Double.valueOf(strArray[2])) + " km to target", "Location taken at: " + strArray[1] + "\n" +
		                    "http://maps.google.com/maps?q=" + strArray[0] + "&iwloc=A&hl=en \nDeviceID:" + 
		                    	deviceUid + "\nProvider: " + locationProvider + "\nAccuracy[meters]: " + accuracy +
		                    	"\nSpeed[km/h]:" + (speed*3600/1000),
		                    senderMail,   
		                    recipientMail);   
	
		            preferences.setStringSettingsValue("locationStringGPS", "initial");
		            preferences.setStringSettingsValue("locationStringNETWORK", "initial");
	
		        } catch (Exception e) {   
		            LogManager.LogException(e, "LocationNotifierService", "sendLocationByMail()");
		        } 
			}
	        LogManager.LogFunctionExit("LocationNotifierService", "sendLocationByMail()");
    	} catch (Exception e) {
	        LogManager.LogException(e, "LocationNotifierService", "sendLocationByMail()");
    	}
	}
    
	// Define a listener that responds to location updates
	LocationListener locationListenerGPS = new LocationListener() {
	    public void onLocationChanged(Location location) {

	    	try{
		    	LogManager.LogFunctionCall("LocationListener", "onLocationChanged()");

		    	preferences.setStringSettingsValue("locationProviderName", "GPS");
	
		    	double latitude = 0, longitude = 0;
		    	latitude = location.getLatitude();
		    	longitude = location.getLongitude();
		    	if(latitude == 0 || longitude == 0){
		    		return;
		    	}

		    	Float distanceToTarget = location.distanceTo(Utils.getTargetLocation(preferences))/1000;
		    	accuracy = location.getAccuracy();
		    	locationProvider = location.getProvider();
		    	speed = location.getSpeed();
	
		    	preferences.setStringSettingsValue("locationProviderName", locationProvider);
		    	
				//sets latitude/longitude when a location is provided
				latlong = location.getLatitude() + "," + location.getLongitude() + DELIMITER + 
						  Utils.getCurrentTime() + DELIMITER + distanceToTarget;
				
		        LogManager.LogInfoMsg("locationListenerGPS", "onLocationChanged()", "@@@NEW_LOCATION_GPS: " + latlong);
		        preferences.setStringSettingsValue("locationStringGPS", latlong);
	        
		        //sendLocationByMail(latlong);
	
		        if(wl != null && wl.isHeld()){
			        LogManager.LogInfoMsg("locationListenerGPS", "onLocationChanged()", "WAKE LOCK - READY TO BE RELEASED.");
			        toReleaseWakeLock = true;
		        	LogManager.LogInfoMsg("locationListenerGPS", "onLocationChanged()", "WAKE LOCK isHeld: " + wl.isHeld());
	//	        	wl.release();
	//		        LogManager.LogInfoMsg("locationListenerGPS", "onLocationChanged()", "WAKE LOCK - HAS BEEN RELEASED.");
		        }
		        LogManager.LogFunctionExit("locationListenerGPS", "onLocationChanged()");
	    	} catch (Exception e) {
		        LogManager.LogException(e, "locationListenerGPS", "onLocationChanged()");
	    	}
	        
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	  };

	// Define a listener that responds to location updates
	LocationListener locationListenerNetwork = new LocationListener() {
	    public void onLocationChanged(Location location) {
	    	
	    	try{
	    		LogManager.LogFunctionCall("locationListenerNetwork", "onLocationChanged()");
		    	preferences.setStringSettingsValue("locationProviderName", "NETWORK");
		    	
		    	double latitude = 0, longitude = 0;
		    	latitude = location.getLatitude();
		    	longitude = location.getLongitude();
		    	if(latitude == 0 || longitude == 0){
		    		return;
		    	}

		    	Float distanceToTarget = location.distanceTo(Utils.getTargetLocation(preferences))/1000;
		    	accuracy = location.getAccuracy();
		    	locationProvider = location.getProvider();
		    	speed = location.getSpeed();
		    	
		    	preferences.setStringSettingsValue("locationProviderName", locationProvider);
	
				//sets latitude/longitude when a location is provided
				latlong = location.getLatitude() + "," + location.getLongitude() + DELIMITER + 
					      Utils.getCurrentTime() + DELIMITER + distanceToTarget;
				
		        LogManager.LogInfoMsg("locationListenerNetwork", "onLocationChanged()", "@@@NEW_LOCATION_NETWORK: " + latlong);
		        preferences.setStringSettingsValue("locationStringNETWORK", latlong);
	       
		        if(wl != null){
		        	LogManager.LogInfoMsg("locationListenerNetwork", "onLocationChanged()", "WAKE LOCK isHeld: " + wl.isHeld());
		        }
		        LogManager.LogFunctionExit("locationListenerNetwork", "onLocationChanged()");
	    	} catch (Exception e) {
		        LogManager.LogException(e, "locationListenerNetwork", "onLocationChanged()");
	    	}
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	  };

	@Override          
    public void onStart(Intent intent, int startId)           
    {                  
    	super.onStart(intent, startId);

    	try{
	        LogManager.LogFunctionCall("LocationNotifierService", "onStart()");
	        
	        pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
	        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyLocationWake");

	        LogManager.LogInfoMsg("LocationNotifierService", "onStart()", "Before if - WAKE LOCK isHeld: " + wl.isHeld());
	        String locProvName = null; 
	        locProvName = preferences.getStringSettingsValue("locationProviderName", locProvName);
	        LogManager.LogInfoMsg("LocationNotifierService", "onStart()", "Location provider name: " + locProvName);
	        if(wl.isHeld() == false && locProvName.equalsIgnoreCase("gps")){
	        	wl.acquire(); 
	            LogManager.LogInfoMsg("LocationNotifierService", "onStart()", "WAKE LOCK - HAS BEEN ACUIRED.");
	        }
	        
	        if(toReleaseWakeLock){
	        	if(wl != null && wl.isHeld()){
	        		wl.release();
	        		LogManager.LogInfoMsg("LocationNotifierService", "onStart()", "WAKE LOCK - HAS BEEN RELEASED.");
	                toReleaseWakeLock = false;
	        	}
	        }
	        
	        //wl.release();        
	        
	        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, locationListener);
	        
	        requestLocation(true);
	        isLocationProviderAvailable = preferences.getBooleanSettingsValue("isLocationProviderAvailable");
	        if(isLocationProviderAvailable){
		        String locationStringGPS = null;
		        String locationStringNETWORK = null;
		        
		        locationStringGPS = preferences.getStringSettingsValue("locationStringGPS", locationStringGPS);
		        locationStringNETWORK = preferences.getStringSettingsValue("locationStringNETWORK", locationStringNETWORK);
	
	        	if(!locationStringGPS.equals("initial")){
		        	LogManager.LogInfoMsg("LocationNotifierService", "onStart()", "locationGPS: " + locationStringGPS);
		        	sendLocationByMail(locationStringGPS, locationProvider);
		        } else if(!locationStringNETWORK.equals("initial")){
		        	LogManager.LogInfoMsg("LocationNotifierService", "onStart()", "locationNETWORK: " + locationStringNETWORK);
		        	sendLocationByMail(locationStringNETWORK, locationProvider);
		        }
	        }
	        
	        LogManager.LogFunctionExit("LocationNotifierService", "onStart()");
    	} catch (Exception e) {
	        LogManager.LogException(e, "LocationNotifierService", "onStart()");
    	}
    }                    
    
    @Override          
    public void onDestroy()           
    {                  
    	super.onDestroy();
    
    	try{
	        LogManager.LogFunctionCall("LocationNotifierService", "onDestroy()");
	
	        //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if(locationManager != null){
				locationManager.removeUpdates(locationListenerGPS);
				LogManager.LogInfoMsg("LocationNotifierService", "onDestroy", "locationListenerGPS - Updates removed");
				locationManager.removeUpdates(locationListenerNetwork);
				LogManager.LogInfoMsg("LocationNotifierService", "onDestroy", "locationListenerNetwork - Updates removed");
			}
			if(wl != null){
				wl.release();
				LogManager.LogInfoMsg("LocationNotifierService", "onDestroy", "WAKE LOCK - HAS BEEN REMOVED.");
			}
	    	
	        LogManager.LogFunctionExit("LocationNotifierService", "onDestroy()");
    	} catch (Exception e) {
	        LogManager.LogException(e, "LocationNotifierService", "onDestroy()");
    	}

    }  

	private boolean providerAvailable(List<String> providers) {
		if (providers.isEmpty()) {
			return false;
		}
		return true;
	}

	private void requestLocation(boolean forceGps) {
		try{
	        LogManager.LogFunctionCall("LocationNotifierService", "requestLocation()");
			locationManager.removeUpdates(locationListenerGPS);
			locationManager = null;
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
    	} catch (Exception e) {
	        LogManager.LogException(e, "LocationNotifierService", "requestLocation()");
    	}
	}
}
