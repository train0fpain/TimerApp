package com.companyname.timerapp.timerClasses;

import android.media.MediaPlayer;
import android.os.Handler;

import com.companyname.timerapp.MainActivity;
import com.companyname.timerapp.R;
import com.companyname.timerapp.util.Start;

public class TimerManager {

    private static Timer[] timers = new Timer[20];
    private static boolean editMode =false;
    private static MediaPlayer mP;
    private static int alarmCount = 0;

    public static void update(){
        if (timers !=null) {
            for (Timer timer : timers) {
                if (timer != null) {
                    timer.update();
                }
            }
        }
    }

    public static void init(){
        mP = MediaPlayer.create(Start.getContext(), R.raw.alarm);
        mP.setLooping(true);

        final Handler h = new Handler();
        final Runnable run = new Runnable() {
            @Override
            public void run() {
                TimerManager.update();
                h.postDelayed(this, 1000);
            }
        };
        // start timer
        h.postDelayed(run, 0);
    }

    public static void playRingtone(){
        alarmCount++;
        if (!(alarmCount > 1)){
            mP.seekTo(0);
            mP.start();
        }
    }

    public static void stopRingtone(){
        alarmCount--;
        if (alarmCount <= 0){
            if (mP != null && mP.isPlaying()) {
                mP.pause();
            }
            alarmCount = 0;
        }
    }

    public static Timer[] getTimers() {
        return timers;
    }

    public static Timer getTimer(int index){
        if (timers.length > index) {
            return timers[index];
        }else {
            return null;
        }
    }

    public static void addTimer(Timer newTimer){
        addTimer(newTimer, true);
    }

    public static void addTimer(Timer newTimer, boolean addData){
        int slot = findFreeSlot();
        addTimer(newTimer, addData, slot);
    }

    public static void addTimer(Timer newTimer, boolean addData, int slot){
        if (slot >= 0) {
            timers[slot] = newTimer;
            newTimer.setIndex(slot);
            newTimer.setView(MainActivity.getViewAt(slot));
            if (addData) {
                addData(slot, newTimer.getName(), newTimer.getTimeSeconds());
            }
        }
    }

    private static void addData(int id, String name, int time){
        Start.getDbHelper().addData(id, name, time);
    }

    public static boolean isEditMode() {
        return editMode;
    }

    public static void setEditMode(boolean editMode) {
        TimerManager.editMode = editMode;
    }

    public static void deleteTimer(int id){
        timers[id].deactivateView();
        timers[id] = null;
        Start.getDbHelper().deleteData(id);
    }

    public static void deleteAllTimers(){
        for (int i=0; i<timers.length; i++){
            if (timers[i] != null) {
                timers[i].deactivateView();
                timers[i] = null;
            }
        }
        Start.getDbHelper().deleteAllData();
    }

    public static void resetAllTimers(){
        for (Timer timer : timers){
            if (timer != null) {
                timer.reset();
            }
        }
    }

    private static int findFreeSlot(){
        for (int i = 0; i< timers.length; i++) {
            if (timers[i] == null){
                return i;
            }
        }
        return -1;
    }
}
