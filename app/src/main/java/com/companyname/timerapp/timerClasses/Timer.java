package com.companyname.timerapp.timerClasses;

import android.content.ClipData;
import android.os.Handler;
import android.view.DragEvent;
import android.view.View;

import com.companyname.timerapp.MainActivity;
import com.companyname.timerapp.util.Start;
import com.companyname.timerapp.views.TimerView;

public class Timer {
    private TimeFormat time;
    private TimerView view;
    private String name = "Timer name";
    private boolean pause = true;
    private boolean end = false;
    private int index;
    private int endClicks = 0;
    private int doubleTap = 0;

    public Timer(TimerView view) {
        this.time = new TimeFormat(3);
        setView(view);

    }

    public void update(){
        if (!pause) {
            time.decrement();
            view.requestDraw();

            if (!end && time.getCurrentSeconds() <= 0){
                end = true;
                TimerManager.playRingtone();
            }
        }
    }

    private void addView(){
        view.setOwner(this);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                doubleTap++;
                Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (doubleTap == 1){
                            if (TimerManager.isEditMode()) {
                                try {
                                    ((MainActivity) view.getContext()).openEditPage(Timer.this);
                                } catch (NullPointerException e) {
                                    throw new NullPointerException("can't open edit page because of missing main activity");
                                }
                            } else {
                                if (time.getCurrentSeconds() <= 0) {
                                    endClicks++;
                                    if (endClicks >= 2) {
                                        reset();
                                    } else {
                                        setPause(true);
                                        TimerManager.stopRingtone();
                                    }
                                } else {
                                    setPause(!pause);
                                }
                            }
                        } else if (doubleTap == 2){
                            if (TimerManager.isEditMode()){
                                if (end){
                                    TimerManager.stopRingtone();
                                }
                                TimerManager.deleteTimer(index);
                            }else {
                                reset();
                            }
                        }
                        doubleTap = 0;
                    }
                }, 300);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (TimerManager.isEditMode()){
                    ClipData clipData = ClipData.newPlainText("","");
                    View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);
                    if (android.os.Build.VERSION.SDK_INT > 23) {
                        view.startDragAndDrop(clipData, dragShadowBuilder, view, 0);
                    }else {
                        view.startDrag(clipData, dragShadowBuilder,view, 0);
                    }
                    return true;
                } else {
                    try {
                        ((MainActivity) view.getContext()).openEditPage(Timer.this);
                    } catch (NullPointerException e) {
                        throw new NullPointerException("can't open edit page because of missing main activity");
                    }
                    return false;
                }
            }
        });

        view.requestDraw();
    }

    public void setView(TimerView view) {
        if (view != null) {
            this.view = view;
            addView();
            view.setActive(true);
        }
    }

    public void setPause(boolean pause){
        this.pause = pause;
    }

    public boolean isPlayingAlarm(){
        return end && endClicks == 0;
    }

    public void reset(){
        if (isPlayingAlarm()){
            TimerManager.stopRingtone();
        }
        time.reset();
        view.reset();
        pause = true;
        end = false;
        endClicks = 0;
        view.requestDraw();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
        Start.getDbHelper().updateName(index, name);
    }

    public String getTimeString() {
        return time.toString();
    }
    public int getTimeSeconds(){
        return time.getTotalSeconds();
    }
    public void setTime(TimeFormat time) {
        this.time = time;
        Start.getDbHelper().updateTime(index, (int)time.getTotalSeconds());
    }

    public float getProgress(){
        return time.getProgress();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void deactivateView(){
        view.setActive(false);
        view.setOwner(null);
        view.requestDraw();
    }

    public int getHour(){
        return time.getHour();
    }

    public int getMinute(){
        return time.getMinute();
    }

    public int getSecond(){
        return time.getSecond();
    }
}
