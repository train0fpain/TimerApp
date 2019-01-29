package com.companyname.timerapp.timerClasses;

import android.media.MediaPlayer;
import android.os.Handler;

import com.companyname.timerapp.MainActivity;
import com.companyname.timerapp.R;
import com.companyname.timerapp.linking.LinkManager;
import com.companyname.timerapp.modesAndStates.UserMode;
import com.companyname.timerapp.util.Start;
import com.companyname.timerapp.views.TimerView;

import java.util.Arrays;

public class TimerManager {

    private static Timer[] timers = new Timer[20];
    private static UserMode userMode = UserMode.NORMAL;
    private static MediaPlayer mP;
    private static int alarmCount = 0;
    private static LinkManager linkManager = LinkManager.getInstance();

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
                addData(slot, newTimer.getName(), newTimer.getTimeSeconds(), newTimer.getLinkId());
            }
            return timers[slot];
        }
        return null;
    }

    private static void addData(int id, String name, int time, int link){
        Start.getDbHelper().addData(id, name, time, link);
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
        linkManager.removeFromLink(timers[id]);
        timers[id].deactivateView();
        timers[id] = null;
        Start.getDbHelper().deleteData(id);
    }

    public static void deleteAllTimers(){
        for (int i=0; i<timers.length; i++){
            if (timers[i] != null) {
                linkManager.removeFromLink(timers[i]);
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
        owner.setLinkId(owner.getLinkId());

        dropped.setViewToEmpty();

        Start.getDbHelper().addData(owner.getIndex(), owner.getName(), owner.getTimeSeconds(), owner.getLinkId());
    }

    public static void swappSlot(TimerView dropped, TimerView target){
        newSwapSlot(dropped, target);
    }

    public static void newSwapSlot(TimerView dropped, TimerView target){
        Timer[] newTimers = new Timer[timers.length];
        System.arraycopy(timers, 0, newTimers, 0, timers.length);
        int targetIndex = target.getIndex();
        int originIndex = dropped.getIndex();
        timers[originIndex] = null;
        Start.getDbHelper().deleteData(originIndex);

        if (targetIndex > originIndex){
            int nullIndex = getNullIndex(timers, targetIndex, originIndex);
            System.arraycopy(timers, originIndex + 1, newTimers, originIndex, targetIndex - originIndex);
            System.arraycopy(timers, targetIndex, newTimers, targetIndex, timers.length - 1 - targetIndex);
            newTimers[targetIndex] = dropped.getOwner();
            dropped.getOwner().deactivateView();

            for (int i = Math.max(originIndex, nullIndex); i <= targetIndex; i++) {
                swapSlotLoop(i, newTimers);
            }

        }else{
            int nullIndex = originIndex;
            for (int i = targetIndex; i < originIndex; i++) {
                Start.getDbHelper().deleteData(i);

                if (timers[i] == null){
                    nullIndex = i;
                    break;
                }
            }
            System.arraycopy(timers, 0, newTimers, 0, targetIndex);
            System.arraycopy(timers, targetIndex, newTimers, targetIndex + 1, originIndex - targetIndex);
            newTimers[targetIndex] = dropped.getOwner();
            dropped.getOwner().deactivateView();

            for (int i = targetIndex; i <= Math.min(originIndex, nullIndex); i++) {
                swapSlotLoop(i, newTimers);
            }
        }
    }

    private static int getNullIndex(Timer[] array, int from, int to){
        for (int i = from; i > to; i--) {
            Start.getDbHelper().deleteData(i);

            if (timers[i] == null){
                return i;
            }
        }
        return 0;
    }

    private static void swapSlotLoop(int i, Timer[] newTimers){
        Timer localTimer = newTimers[i];
        if (localTimer != null) {
            localTimer.setIndex(i);
            localTimer.setView(MainActivity.getViewAt(i));
            localTimer.setLinkId(localTimer.getLinkId());

            timers[i] = localTimer;

            Start.getDbHelper().addData(localTimer);
        }

    }


}
