package edu.wpi.cs3733.D21.teamE;

import java.time.LocalTime;

public class Time implements Comparable<Time>{

    public static final Time DAY_START = new Time(0);
    public static final Time DAY_END = new Time(23, 59, 59);

    private int sec;
    private int min;
    private int hour;
    private boolean isEmpty = true;

    /**
     * Constructor with already known minutes and seconds
     * @param _min Minutes
     * @param _sec Seconds
     */
    public Time(int _min, int _sec){
        this.min = _min;
        this.sec = _sec;
        this.hour = 0;
        isEmpty = false;
    }

    public Time(int _hour, int _min, int _sec){
        this.hour = _hour;
        this.min = _min;
        this.sec = _sec;
        isEmpty = false;
    }

    /**
     * Constructor with total seconds
     * @param _sec Seconds
     */
    public Time(int _sec){
        this.hour = _sec / 60 / 60;
        this.min = _sec / 60 % 60;
        this.sec = _sec % 60 % 60;
        isEmpty = false;
    }

    public Time(LocalTime lTime){
        if(lTime != null){
            hour = lTime.getHour();
            min = lTime.getMinute();
            sec = lTime.getSecond();
            isEmpty = false;
        }
    }

    /**
     * To String Override
     * @return Time formatted as X:XX or XX:XX (no leading 0 for minutes)
     */
    @Override
    public String toString() {
        if(isEmpty){
            return "";
        } else {
            return String.format("%02d:%02d:%02d", hour, min, sec);
        }
    }
    public String hourMinString(){
        if(isEmpty){
            return "";
        } else {
            return String.format("%02d:%02d", hour, min);
        }
    }
    public String minSecString(){
        if(isEmpty){
            return "";
        } else {
            return String.format("%02d:%02d", min, sec);
        }
    }

    public boolean isEmpty(){
        return isEmpty;
    }


    /**
     * Get Seconds
     * @return Seconds field as an int
     */
    public int getSec() {
        return sec;
    }

    /**
     * Get Minutes
     * @return Minutes field as an int
     */
    public int getMin() {
        return min;
    }

    public int getHour(){
        return hour;
    }

    /**
     * Total seconds setter
     * @param seconds Seconds
     */
    public static Time getTime(int seconds){
        int hours = seconds / 60 / 60;
        int mins = seconds / 60 % 60;
        int secs = seconds % 60 % 60;

        return new Time(hours, mins, secs);
    }

    public int getTotalSeconds(){
        return hour*60*60+min*60+sec;
    }

    /**
     * Equals method
     * @param t The time to compare to
     * @return True if both the minutes and seconds are equal
     */
    public boolean equals(Time t){
        return ((t.getHour() == hour) && (t.getMin() == min) && (t.getSec() == sec));
    }

    public static Time parseString(String stringTime){
        try {
            String[] fields = stringTime.split(":");
            int secs;
            if(fields.length == 3){
                secs = Integer.parseInt(fields[2]);
            } else {
                secs = 0;
            }
            int hours = Integer.parseInt(fields[0]);
            int mins = Integer.parseInt(fields[1]);

            return new Time(hours, mins, secs);
        } catch(Exception e){
            System.out.println("Could not parse String " + stringTime + " into a time");
            return new Time(null);
        }
    }

    @Override
    public int compareTo(Time t) {
        if(isEmpty && t.isEmpty){
            return 0;
        } else if(isEmpty){
            return 1;
        } else if(t.isEmpty){
            return -1;
        } else {
            return Integer.compare(getTotalSeconds(), t.getTotalSeconds());
        }
    }

    public LocalTime toLocalTime(){
        return LocalTime.of(hour, min, sec);
    }

}
