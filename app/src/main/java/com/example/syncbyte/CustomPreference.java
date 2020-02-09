package com.example.syncbyte;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class CustomPreference {

    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    //Mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "SyncbytePref";

    // All Shared Preferences Keys
    private static final String IS_CHECKED_IN = "IsCheckedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_EMPID = "empId";
    public static final String KEY_NAME = "name";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    // Email address (make variable public to access from outside)
    public static final String KEY_LAST_CHECK_IN_REF = "document_ref";

    // Constructor
    public CustomPreference(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String empId,String name,String number,String email,String password){
        editor.putString(KEY_EMPID,empId);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_NAME,name);
        editor.putString(KEY_NUMBER,number);
        editor.putString(KEY_PASSWORD,password);

        editor.commit();
    }
    public HashMap<String,String> getLoginDetails(){

        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_NUMBER, pref.getString(KEY_NUMBER, null));
        user.put(KEY_EMPID, pref.getString(KEY_EMPID, null));
        // return user
        return user;

    }
    public  void updateDetails(String phone, String email,String password){

        editor.putString(KEY_EMAIL,email);
         editor.putString(KEY_NUMBER,phone);
        editor.putString(KEY_PASSWORD,password);
        editor.commit();


    }
    public void setLastCheckRef(String docRef){

        editor.putString(KEY_LAST_CHECK_IN_REF,docRef);
        editor.commit();
    }
    public String getLastCheckRef(){

       return pref.getString(KEY_LAST_CHECK_IN_REF,null);
    }
    public void setIsCheckedIn(int value){

        editor.putInt(IS_CHECKED_IN,value); // 1 = Checked In, 0 = Checked Out
        editor.commit();
    }
    public int getIsCheckedIn(){

        return pref.getInt(IS_CHECKED_IN,2);
    }
    public void logoutUser(){

        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        _context.startActivity(i);
    }
}
