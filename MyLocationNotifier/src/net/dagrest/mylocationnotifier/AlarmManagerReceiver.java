package net.dagrest.mylocationnotifier;

import java.util.Calendar;

import net.dagrest.mylocationnotifier.log.LogManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmManagerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
//		try {
//			     Bundle bundle = intent.getExtras();
//			     String message = bundle.getString("alarm_message");
////			     Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//			        Calendar c = Calendar.getInstance();  
//			        int hours = c.get(Calendar.HOUR_OF_DAY);
//			        int minutes = c.get(Calendar.MINUTE);
//			        int seconds = c.get(Calendar.SECOND);
//			        String curTime = String.format("%d:%d:%d", hours, minutes, seconds);
//
//				 LogManager.LogInfoMsg("AlarmManagerReceiver", "OnClickListener()", "Here should be call of the action... " + curTime);
//
//			    } catch (Exception e) {
////			     Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
////			     e.printStackTrace();
//			        Calendar c = Calendar.getInstance();  
//			        int hours = c.get(Calendar.HOUR_OF_DAY);
//			        int minutes = c.get(Calendar.MINUTE);
//			        int seconds = c.get(Calendar.SECOND);
//			        String curTime = String.format("%d:%d:%d", hours, minutes, seconds);
//
//			        LogManager.LogInfoMsg("AlarmManagerReceiver", "exception", "Broadcasting cancel: " + curTime);
//
//	    }
	}
}
