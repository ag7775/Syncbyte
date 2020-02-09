package com.example.syncbyte;


import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class TimeModal{

    Long checkInTime;
    Long checkOutTime;
    boolean isCheckedIn;

    public TimeModal(Long checkInTime, Long checkOutTime, boolean isCheckedIn) {
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.isCheckedIn = isCheckedIn;
    }

    public boolean isCheckedIn() {
        return isCheckedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        isCheckedIn = checkedIn;
    }

    public Long getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Long checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Long getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Long checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    //Function Converts the timestamp into Date
    public static String getDate(long timeStamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp * 1000);
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();
        return date;
    }

    //Function Converts the timestamp into Time
    public static String getTime(long timeStamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp * 1000);
        String time = DateFormat.format("HH:mm", cal).toString();
        System.out.println(time);
        return time;
    }

}