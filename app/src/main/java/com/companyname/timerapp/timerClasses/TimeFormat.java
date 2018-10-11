package com.companyname.timerapp.timerClasses;

public class TimeFormat {
    private int totalSeconds;
    private int currentSeconds;

    public TimeFormat(int totalSeconds) {
        setTotalSeconds(totalSeconds);
    }

    public TimeFormat(int h, int m, int s){
        this.totalSeconds = s + (m*60) + (h*3600);
        currentSeconds = totalSeconds;
    }

    // getters
    public int getCurrentSeconds() {
        return currentSeconds;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public float getProgress(){
        return 1-(float)currentSeconds / (float)totalSeconds;
    }

    public int getHour(){
        return totalSeconds / 3600;
    }

    public int getMinute(){
        return (totalSeconds%3600) / 60;
    }

    public int getSecond(){
        return (totalSeconds%3600) % 60;
    }



    // setters
    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
        currentSeconds = totalSeconds;
    }

    public void decrement(){
        currentSeconds -= 1;
    }

    public void reset(){
        currentSeconds = totalSeconds;
    }

    // utility
    private String leadingZeros(int number){
        if (Math.abs(number)<10){
            return "0"+number;
        }
        return Integer.toString(number);
    }

    @Override
    public String toString(){
        int hour = currentSeconds / 3600;
        int minute = (currentSeconds%3600) / 60;
        int second = (currentSeconds%3600) % 60;

        if (currentSeconds < 0){
            return "-"+leadingZeros(Math.abs(hour))
                    +":"+leadingZeros(Math.abs(minute))
                    +":"+leadingZeros(Math.abs(second));
        }else {
            return leadingZeros(hour)
                    + ":" + leadingZeros(minute)
                    + ":" + leadingZeros(second);
        }
    }



}
