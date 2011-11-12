package net.dagrest.mylocationnotifier;

import net.dagrest.mylocationnotifier.log.LogManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//http://code.google.com/p/sharemyposition/source/browse/#svn%2Ftrunk%2FShareMyPosition-android%2Fsrc%2Fnet%2Fsylvek%2Fsharemyposition%253Fstate%253Dclosed

public class MyLocationNotifierActivity extends Activity {// implements LocationListener {

    public static final String PREFS_NAME = "MyPreferences";

//  private Geocoder geocoder;
//	private ConnectivityManager connectivityManager;
//	private TelephonyManager telephonyManager;
	private TextView noteText;
	private Button btnToggleNotificationService;
	private LocationServices locationServices;
	private String deviceUid;
	private SharedPreferences sharedPreferences;
	private Preferences preferences;
	private Intent svc;
	private Context context;
	private static Context staticContext;
	private PendingIntent mAlarmSenderService;
	
	public static Context getContext() {
		return staticContext;
	}

	public void setNoteText(String newText) {
		this.noteText.setText(newText);
	}

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("net.dagrest.mylocationnotifier.LocationNotifierService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

	//pauses listener while application is inactive
    @Override
    public void onPause() {
        super.onPause();
        LogManager.LogFunctionCall("MyLocationNotifierActivity", "onPause()");
        LogManager.LogFunctionExit("MyLocationNotifierActivity", "onPause()");
    }
    
    //reactivates listener when app is resumed
    @Override
    public void onResume() {
        super.onResume();
        LogManager.LogFunctionCall("MyLocationNotifierActivity", "onResume()");
        Boolean isMyServiceRunning = isMyServiceRunning();
        LogManager.LogInfoMsg("MyLocationNotifierActivity", "onResume()", "Is service running: " + isMyServiceRunning);
        LogManager.LogFunctionExit("MyLocationNotifierActivity", "onResume()");
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
        LogManager.LogFunctionCall("MyLocationNotifierActivity", "onDestroy()");
        LogManager.LogFunctionExit("MyLocationNotifierActivity", "onDestroy()");
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogManager.LogFunctionCall("MyLocationNotifierActivity", "onCreate()");

        context = this;
        staticContext = this;
        
        setContentView(R.layout.main);

		noteText = (TextView) findViewById(R.id.mytext); // DEBUG ONLY 

		TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); 
        deviceUid = tManager.getDeviceId(); 

        locationServices = new LocationServices();
        locationServices.setContext(context, PREFS_NAME);
        
        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        preferences = new Preferences(sharedPreferences);

        // isNotifierStarted:	false - service is OFF
        // 						true  - service is ON
        preferences.setBoooleanSettingsValue("isNotifierStarted", false);
        preferences.setStringSettingsValue("deviceUid", deviceUid);
        preferences.setStringSettingsValue("locationString", "initial");
        preferences.setBoooleanSettingsValue("isLocationProviderAvailable", false);

        String locationString = null;
        locationString = preferences.getStringSettingsValue("locationString", locationString);
        
        svc = new Intent(MyLocationNotifierActivity.this, LocationNotifierService.class); 
        mAlarmSenderService = PendingIntent.getService(MyLocationNotifierActivity.this,                
        	0, svc, 0);

//		// get a Calendar object with current time
//		 Calendar cal = Calendar.getInstance();
//		 // add 5 minutes to the calendar object
//		 cal.add(Calendar.MINUTE, 1);
//		 Intent intent = new Intent(this, AlarmManagerReceiver.class);
//		 intent.putExtra("alarm_message", "O'Doyle Rules!");
//		 // In reality, you would want to have a static variable for the request code instead of 192837
//		 mAlarmSender = PendingIntent.getBroadcast(this, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//		// Get the AlarmManager service
//		 AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//		 am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), mAlarmSender);

        
        btnToggleNotificationService = (Button) findViewById(R.id.btnToggleService);
        // Register the onClick listener 
        btnToggleNotificationService.setOnClickListener(mBtnToggleServiceListener);
        //btnToggleNotificationService.setTextColor(Color.BLACK);

		if(isMyServiceRunning() == true){
			btnToggleNotificationService.setText(getString(R.string.stopLocationNotifierService));
			btnToggleNotificationService.setTextColor(Color.RED);
	        preferences.setBoooleanSettingsValue("isNotifierStarted", true);
		} else {
			btnToggleNotificationService.setText(getString(R.string.startLocationNotifierService));
			btnToggleNotificationService.setTextColor(Color.BLACK);
	        preferences.setBoooleanSettingsValue("isNotifierStarted", false);
		}
		noteText.setText("isNotifierStarted: " + Boolean.toString(preferences.getBooleanSettingsValue("isNotifierStarted")));

//        geocoder = new Geocoder(this);
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        staticLocationManager = locationManager;
//        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//       telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //locationManager.removeUpdates(this);
        //performLocation(false);
        
        LogManager.LogFunctionExit("MyLocationNotifierActivity", "onCreate()");
    }

	// Create implementation of OnClickListener for "Toggle Notofication Service" button
	private	OnClickListener mBtnToggleServiceListener = new OnClickListener() 
	{    
		public void onClick(View v) {      
			boolean isNotifierStarted = preferences.getBooleanSettingsValue("isNotifierStarted");
			preferences.setBoooleanSettingsValue("isNotifierStarted", !isNotifierStarted);
			//mytext.setText(getString(R.string.runLocationNotifierService));   
			noteText.setText("isNotifierStarted: " + Boolean.toString(!isNotifierStarted));
			
			if(isNotifierStarted == true){
				btnToggleNotificationService.setText(getString(R.string.startLocationNotifierService));
				btnToggleNotificationService.setTextColor(Color.BLACK);

//				LogManager.LogInfoMsg("MyLocationNotifierActivity", "OnClickListener()", "Broadcasting cancel...");
//				System.out.println("Broadcasting cancel...");
//				AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);            
//				am.cancel(mAlarmSender);	

				LogManager.LogInfoMsg("MyLocationNotifierActivity", "OnClickListener()", "Alarm manager for Location Notifier Service STOPPED.");
				AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);            
				am.cancel(mAlarmSenderService);	
				stopService(svc);

				Toast.makeText(context, "Location notifier service STOPPED.", Toast.LENGTH_SHORT).show();

			} else {
				btnToggleNotificationService.setText(getString(R.string.stopLocationNotifierService));
				btnToggleNotificationService.setTextColor(Color.RED);

//				Boolean status = LocationNotifierService.startLocationNotifierService(context);
//				System.out.println("DEBUG ONLY status ===================> " + status);

//	            // We want the alarm to go off 30 seconds from now.            
//				long firstTime = SystemClock.elapsedRealtime();            
//				// Schedule the alarm!            
//				AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);            
//				am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,                            
//					firstTime, 30*1000, mAlarmSender);
				
	            // We want the alarm to go off 30 seconds from now.            
				long firstTime = SystemClock.elapsedRealtime();            
//				// Schedule the alarm!
//				
//				System.out.println("Broadcasting registartion.");
//				LogManager.LogInfoMsg("MyLocationNotifierActivity", "OnClickListener()", "Broadcasting registartion.");
//				AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);            
//				am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,                            
//					firstTime, 30*1000, mAlarmSender);
				
				LogManager.LogInfoMsg("MyLocationNotifierActivity", "OnClickListener()", "Alarm manager for Location Notifier Service ACTIVATED.");
				AlarmManager amService = (AlarmManager)getSystemService(ALARM_SERVICE);            

		        int oneMinute = 1000 * 60;
		        int minutesNumber = 2;
				
				amService.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,                            
					firstTime, oneMinute*minutesNumber, mAlarmSenderService);

				Toast.makeText(context, "Location notifier service ACTIVATED.", Toast.LENGTH_SHORT).show();
			}
		}
	};
}