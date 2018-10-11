package com.companyname.timerapp.timerClasses;

import android.os.Handler;
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
        this.time = new TimeFormat(21);
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
                            reset();
                            TimerManager.stopRingtone();
                        }
                        doubleTap = 0;
                    }
                }, 300);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    ((MainActivity)view.getContext()).openEditPage(Timer.this);
                }catch (NullPointerException e){
                    throw new NullPointerException("can't open edit page because of missing main activity");
                }
                return false;
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

    public void reset(){
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
        Start.getDbHelper().updateTime(index, time.getTotalSeconds());
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
