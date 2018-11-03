package com.companyname.timerapp.timerClasses;

import android.media.MediaPlayer;
import android.os.Handler;

import com.companyname.timerapp.MainActivity;
import com.companyname.timerapp.R;
import com.companyname.timerapp.util.LinkLine;
import com.companyname.timerapp.util.Start;
import com.companyname.timerapp.util.UserMode;
import com.companyname.timerapp.views.TimerView;

import java.util.ArrayList;
import java.util.List;

public class TimerManager {

    private static Timer[] timers = new Timer[20];
    private static UserMode userMode = UserMode.NORMAL;
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
                h.postDelayed(this, 100);
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

    public static void resetRingtone(){
        alarmCount = 0;
        if (mP != null && mP.isPlaying()) {
            mP.pause();
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

    public static Timer addTimer(Timer newTimer){
        return addTimer(newTimer, true);
    }

    public static Timer addTimer(Timer newTimer, boolean addData){
        int slot = findFreeSlot();
        return addTimer(newTimer, addData, slot);
    }

    public static Timer addTimer(Timer newTimer, boolean addData, int slot){
        if (userMode != UserMode.LINK && slot >= 0) {
            timers[slot] = newTimer;
            newTimer.setIndex(slot);
            newTimer.setView(MainActivity.getViewAt(slot));
            if (addData) {
                addData(slot, newTimer.getName(), newTimer.getTimeSeconds());
            }
            return timers[slot];
        }
        return null;
    }

    private static void addData(int id, String name, int time){
        Start.getDbHelper().addData(id, name, time);
    }

    public static UserMode getUserMode() {
        return userMode;
    }

    public static void setUserMode(UserMode userMode) {
        TimerManager.userMode = userMode;
    }

    public static void deleteTimer(int id){
        if (timers[id].isPlayingAlarm()){
            stopRingtone();
        }
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
        resetRingtone();
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

    public static void dropIntoSlot(TimerView dropped, TimerView freeSlot){
        Timer owner = dropped.getOwner();

        Start.getDbHelper().deleteData(owner.getIndex());

        timers[owner.getIndex()] = null;
        timers[freeSlot.getIndex()] = owner;

        owner.setIndex(freeSlot.getIndex());

        dropped.getOwner().setView(freeSlot);
        dropped.setOwner(null);
        dropped.setActive(false);
        dropped.requestDraw();

        Start.getDbHelper().addData(owner.getIndex(), owner.getName(), owner.getTimeSeconds());
    }

    public static void swappSlot(TimerView dropped, TimerView target){
        Timer droppedOwner = dropped.getOwner();
        Timer targetOwner = target.getOwner();

        int droppedIndex = droppedOwner.getIndex();
        int targetIndex = targetOwner.getIndex();

        Start.getDbHelper().deleteData(droppedIndex);
        Start.getDbHelper().deleteData(targetIndex);

        timers[droppedIndex] = targetOwner;
        timers[targetIndex] = droppedOwner;

        droppedOwner.setIndex(targetIndex);
        targetOwner.setIndex(droppedIndex);

        droppedOwner.setView(target);
        targetOwner.setView(dropped);

        Start.getDbHelper().addData(droppedOwner.getIndex(), droppedOwner.getName(), droppedOwner.getTimeSeconds());
        Start.getDbHelper().addData(targetOwner.getIndex(), targetOwner.getName(), targetOwner.getTimeSeconds());
    }

}
