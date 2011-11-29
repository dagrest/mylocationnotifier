package net.dagrest.utils;

import java.util.Calendar;

import net.dagrest.mylocationnotifier.Preferences;

import android.location.Location;

public class Utils {

    public static String getCurrentTime(){
    	Calendar c = Calendar.getInstance();  
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static Location getTargetLocation(Preferences preferences){
		Location location = new Location("");
		String destLatitude = null;
		String destLongitude = null;
		destLatitude = preferences.getStringSettingsValue("targetLocationLatitude", destLatitude);
		destLongitude = preferences.getStringSettingsValue("targetLocationLongitude", destLongitude);
		location.setLatitude(Double.valueOf(destLatitude));
		location.setLongitude(Double.valueOf(destLongitude));
		return location;
    }

    // SAP Israel Ra'anana 
    // Hatihar 15
    // latitude = 32.1966003
    // longitude = 34.8843663
    public static void setSAPRaananaLocation(Preferences preferences){
        double latitude = 32.1966003;
        double longitude = 34.8843663;
		preferences.setStringSettingsValue("targetLocationLatitude", Double.toString(latitude));
		preferences.setStringSettingsValue("targetLocationLongitude", Double.toString(longitude));
    }
}
