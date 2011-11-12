package net.dagrest.mylocationnotifier;

import android.content.SharedPreferences;

//http://developer.android.com/guide/topics/data/data-storage.html#pref
public class Preferences {
	
	private SharedPreferences sharedPreferences;
	
	public Preferences(SharedPreferences sharedPreferences){
		this.sharedPreferences = sharedPreferences;
	}
	
    public boolean getBooleanSettingsValue(String valueName){
 		// Restore preferences
    	boolean value = sharedPreferences.getBoolean(valueName, false);
		//setSilent(silent);
		return value;
    }
    
    public void setBoooleanSettingsValue(String valueName, boolean value){
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(valueName, value);
		
		// Commit the edits!
		editor.commit();
    }

    public String getStringSettingsValue(String valueName, String value){
 		// Restore preferences
    	return sharedPreferences.getString(valueName, value);
    }
    
    public void setStringSettingsValue(String valueName, String value){
		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(valueName, value);
		
		// Commit the edits!
		editor.commit();
    }

}
