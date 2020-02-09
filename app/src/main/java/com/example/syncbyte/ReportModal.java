package com.example.syncbyte;

public class ReportModal {

    public ReportModal(String checkInDate, String checkInTime, String checkOutDate, String checkOutTime) {
        this.checkInDate = checkInDate;
        this.checkInTime = checkInTime;
        this.checkOutDate = checkOutDate;
        this.checkOutTime = checkOutTime;
    }
    public ReportModal(String checkInDate, String checkInTime, String checkOutDate, String checkOutTime,long checkInTimeFromDatabase) {
        this.checkInDate = checkInDate;
        this.checkInTime = checkInTime;
        this.checkOutDate = checkOutDate;
        this.checkOutTime = checkOutTime;
        this.checkInTimeFromDatabase = checkInTimeFromDatabase;
    }

    String checkInDate;
    String checkInTime;
    String checkOutDate;
    String checkOutTime;

    public long getCheckInTimeFromDatabase() {
        return checkInTimeFromDatabase;
    }

    public void setCheckInTimeFromDatabase(long checkInTimeFromDatabase) {
        this.checkInTimeFromDatabase = checkInTimeFromDatabase;
    }

    long checkInTimeFromDatabase;

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}
