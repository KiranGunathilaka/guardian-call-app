package com.techWizards.guardianCall;


public class Alarm {
    private int hourOfDay;
    private int minute;
    private int dayOfWeek;
    private int requestCode;
    private String message;

    public Alarm(int hourOfDay, int minute, int dayOfWeek, int requestCode, String message) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.dayOfWeek = dayOfWeek;
        this.requestCode = requestCode;
        this.message = message;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public String getMessage() {
        return message;
    }
}
