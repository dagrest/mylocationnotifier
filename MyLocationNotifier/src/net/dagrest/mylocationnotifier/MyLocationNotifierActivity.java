package net.dagrest.mylocationnotifier;

import net.dagrest.mylocationnotifier.log.LogManager;
import net.dagrest.utils.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//http://code.google.com/p/sharemyposition/source/browse/#svn%2Ftrunk%2FShareMyPosition-android%2Fsrc%2Fnet%2Fsylvek%2Fsharemyposition%253Fstate%253Dclosed

public class MyLocationNotifierActivity extends Activity {// implements LocationListener {

    public static final String PREFS_NAME = "MyPreferences";

	private TextView noteText;
	private ImageView btnToggleNotificationService;
	private String deviceUid;
	private SharedPreferences sharedPreferences;
	private Preferences preferences;
	private Intent svc;
	private Context context;
	private static Context staticContext;
	private PendingIntent mAlarmSenderService;
	private ScaleAnimation mAnimation = null;
	
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
        LogManager.LogInfoMsg("MyLocationNotifierActivity", "onDestroy()", "***************************");
        LogManager.LogInfoMsg("MyLocationNotifierActivity", "onDestroy()", "*** MyLocationNotifier ****");
        LogManager.LogInfoMsg("MyLocationNotifierActivity", "onDestroy()", "********** ENDED **********");
        LogManager.LogInfoMsg("MyLocationNotifierActivity", "onDestroy()", "***************************");
        LogManager.LogFunctionExit("MyLocationNotifierActivity", "onDestroy()");
    }
    
    private void initAnimation()
    {
    // Define animation
            mAnimation = new ScaleAnimation(
            0.9f, 1, 0.9f, 1, // From x, to x, from y, to y
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
            mAnimation.setDuration(100);
            mAnimation.setFillAfter(true); 
            mAnimation.setStartOffset(0);
            mAnimation.setRepeatCount(1);
            mAnimation.setRepeatMode(Animation.REVERSE);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogManager.LogFunctionCall("MyLocationNotifierActivity", "onCreate()");

        LogManager.LogInfoMsg("MyLocationNotifierActivity", "onCreate()", "***************************");
        LogManager.LogInfoMsg("MyLocationNotifierActivity", "onCreate()", "*** MyLocationNotifier ****");
        LogManager.LogInfoMsg("MyLocationNotifierActivity", "onCreate()", "********* STARTED *********");
        LogManager.LogInfoMsg("MyLocationNotifierActivity", "onCreate()", "***************************");
        
        context = this;
        staticContext = this;
        
        setContentView(R.layout.main);

        initAnimation();
        
		noteText = (TextView) findViewById(R.id.mytext); // DEBUG ONLY 

		TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); 
        deviceUid = tManager.getDeviceId(); 
        LogManager.LogInfoMsg("MyLocationNotifierActivity", "onCreate()", "deviceUid = " + deviceUid);

        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        preferences = new Preferences(sharedPreferences);

        // isNotifierStarted:	false - service is OFF
        // 						true  - service is ON
        preferences.setBoooleanSettingsValue("isNotifierStarted", false);
        preferences.setStringSettingsValue("deviceUid", deviceUid);
        
        preferences.setStringSettingsValue("locationStringGPS", "initial");
        preferences.setStringSettingsValue("locationStringNETWORK", "initial");

        preferences.setBoooleanSettingsValue("isLocationProviderAvailable", false);
        preferences.setStringSettingsValue("locationProviderName", "NONE");

        preferences.setStringSettingsValue("laDeviceId", "004999010640000");
        preferences.setStringSettingsValue("daDeviceId", "354957030678174");

        Utils.setSAPRaananaLocation(preferences);
        
        String locationString = null;
        locationString = preferences.getStringSettingsValue("locationStringGPS", locationString);
        
        svc = new Intent(MyLocationNotifierActivity.this, LocationNotifierService.class); 
        mAlarmSenderService = PendingIntent.getService(MyLocationNotifierActivity.this,                
        	0, svc, 0);

        btnToggleNotificationService = (ImageView) findViewById(R.id.btnToggleService);
        // Register the onClick listener 
        btnToggleNotificationService.setOnClickListener(mBtnToggleServiceListener);
		btnToggleNotificationService.setImageResource(R.drawable.start);

		if(isMyServiceRunning() == true){
            // Animation
			if (mAnimation != null)
				btnToggleNotificationService.startAnimation(mAnimation);
			btnToggleNotificationService.setImageResource(R.drawable.stop);
	        preferences.setBoooleanSettingsValue("isNotifierStarted", true);
		} else {
            // Animation
			if (mAnimation != null)
				btnToggleNotificationService.startAnimation(mAnimation);
			btnToggleNotificationService.setImageResource(R.drawable.start);
	        preferences.setBoooleanSettingsValue("isNotifierStarted", false);
		}
		noteText.setText("isNotifierStarted: " + Boolean.toString(preferences.getBooleanSettingsValue("isNotifierStarted")));

        LogManager.LogFunctionExit("MyLocationNotifierActivity", "onCreate()");
    }

	// Create implementation of OnClickListener for "Toggle Notofication Service" button
	private	OnClickListener mBtnToggleServiceListener = new OnClickListener() 
	{    
		public void onClick(View v) {      
			boolean isNotifierStarted = preferences.getBooleanSettingsValue("isNotifierStarted");
			
			if(isNotifierStarted == true){
				//btnToggleNotificationService.setText(getString(R.string.startLocationNotifierService));
				//btnToggleNotificationService.setTextColor(Color.BLACK);
                // Animation
				if (mAnimation != null)
					btnToggleNotificationService.startAnimation(mAnimation);
				btnToggleNotificationService.setImageResource(R.drawable.start);

				LogManager.LogInfoMsg("MyLocationNotifierActivity", "OnClickListener()", "Alarm manager for Location Notifier Service STOPPED.");
				AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);            
				am.cancel(mAlarmSenderService);	
				stopService(svc);

				Toast.makeText(context, "Location notifier service STOPPED.", Toast.LENGTH_SHORT).show();

			} else {
				//btnToggleNotificationService.setText(getString(R.string.stopLocationNotifierService));
				//btnToggleNotificationService.setTextColor(Color.RED);
                // Animation
				if (mAnimation != null)
					btnToggleNotificationService.startAnimation(mAnimation);
				btnToggleNotificationService.setImageResource(R.drawable.stop);
				
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
			preferences.setBoooleanSettingsValue("isNotifierStarted", !isNotifierStarted);
			//mytext.setText(getString(R.string.runLocationNotifierService));   
			noteText.setText("isNotifierStarted: " + Boolean.toString(!isNotifierStarted));
		}
	};
}