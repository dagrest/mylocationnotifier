package net.dagrest.mylocationnotifier;

import java.util.Calendar;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import net.dagrest.gmailsender.GMailSender;
import net.dagrest.mylocationnotifier.log.LogManager;

public class TimerUtils extends TimerTask {
	
	private MyLocationNotifierActivity activity;
	private Preferences preferences;
	private Context context;
    public static final String PREFS_NAME = "MyPreferences";
	private String location;
	//private LocationServices locationServices;
	
	public TimerUtils(Context context){
 		//this.locationServices = inLocationServices;
		this.activity = activity;
		this.context = context;
	}

	@Override
	public void run() { 
        System.out.println("TIMER_START!!!!!!!!!!!!!!!!!!!!!!!!!!");
        LogManager.LogFunctionCall("TimerUtils", "run()");
        LogManager.LogInfoMsg("TimerUtils", "run()", "TIMER_START!!!!!!!!!!!!!!!!!!!!!!!!!!");

//		long millis = System.currentTimeMillis() - startTime;
//		int seconds = (int) (millis / 1000);
//		int minutes = seconds / 60;
//		seconds     = seconds % 60;
//		timeLabel.setText(String.format("%d:%02d", minutes, seconds));
        
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(PREFS_NAME, 0);
        preferences = new Preferences(sharedPreferences);
		String strLocation = preferences.getStringSettingsValue("locationString", location);
        
		String testStr = strLocation;
		System.out.println("Test coordinates: " + testStr); // TODO - remove this string
		
		String password = "2803notify"; 
		String senderMail = "location.notifier@googlemail.com";
		String recipientMail = "dagrest@gmail.com";
		
        Calendar c = Calendar.getInstance();  
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        String curTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        LogManager.LogInfoMsg("TimerUtils", "run()", "Current time: "+curTime);
       
        String laDeviceId = "351801043779392";
        String daDeviceId = "354957030678174";
        String deviceUid = null;
        deviceUid = preferences.getStringSettingsValue("locationString", deviceUid);
        System.out.println("DEBUG ONLY FROM TIMER: " + deviceUid);
//        String deviceUid = activity.getDeviceUid();
        
//        if(laDeviceId.equals(deviceUid)){
//        	deviceUid = "Larisa";
//        }
//        if(daDeviceId.equals(deviceUid)){
//        	deviceUid = "David";
//        }
        
		if(testStr != null){
	        try {   
                GMailSender sender = new GMailSender(senderMail, password);
	            sender.sendMail(curTime + " - My Location Notifier",   
	                    "http://maps.google.com/maps?q=" + testStr + "&iwloc=A&hl=en   DeviceID:" + deviceUid,   
	                    senderMail,   
	                    recipientMail);   
	        } catch (Exception e) {   
	            Log.e("SendMail", e.getMessage(), e);  
	            System.out.println("SendMail" + e.getMessage());
	            LogManager.LogInfoMsg("TimerUtils", "run()", "SendMail exception: " + e.getMessage());
	            LogManager.LogException(e, "TimerUtils", "run()");
	        } 
		}
        System.out.println("TIMER_END!!!!!!!!!!!!!!!!!!!!!!!!!!");
        LogManager.LogInfoMsg("TimerUtils", "run()", "TIMER_END!!!!!!!!!!!!!!!!!!!!!!!!!!");
        LogManager.LogFunctionExit("TimerUtils", "run()");
	}
}
