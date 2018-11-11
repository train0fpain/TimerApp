package com.companyname.timerapp.timerClasses;

import android.content.ClipData;
import android.os.Handler;
import android.view.View;

import com.companyname.timerapp.MainActivity;
import com.companyname.timerapp.linking.LinkManager;
import com.companyname.timerapp.modesAndStates.TimerState;
import com.companyname.timerapp.util.Start;
import com.companyname.timerapp.views.TimerView;

public class Timer {
    private LinkManager linkManager = LinkManager.getInstance();
    private TimeFormat time;
    private TimerView view;
    private String name = "Timer name";
    private int index;
    private int endClicks = 0;
    private int doubleTap = 0;
    private int linkId = -1;

    private TimerState timerState = TimerState.IDLE;

    public Timer(TimerView view) {
        this.time = new TimeFormat(3);
        setView(view);

    }

    public void update(){
        if (timerState == TimerState.RUNNING || timerState == TimerState.FINISHED) {
            time.decrement();
            view.requestDraw();

            if (timerState == TimerState.RUNNING && time.getCurrentSeconds() <= 0){
                timerState = TimerState.FINISHED;
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
                    switch (TimerManager.getUserMode()){
                        case EDIT:
                            editClick(view, doubleTap);
                            break;
                        case LINK:
                            linkClick(doubleTap);
                            break;
                        case NORMAL:
                            normalClick(doubleTap);
                            break;
                    }
                    doubleTap = 0;
                }
            }, 300);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                switch (TimerManager.getUserMode()){
                    case EDIT:
                        ClipData clipData = ClipData.newPlainText("","");
                        View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);
                        if (android.os.Build.VERSION.SDK_INT > 23) {
                            view.startDragAndDrop(clipData, dragShadowBuilder, view, 0);
                        }else {
                            view.startDrag(clipData, dragShadowBuilder,view, 0);
                        }
                        return true;
                    case NORMAL:
                        try {
                            ((MainActivity) view.getContext()).openEditPage(Timer.this);
                        } catch (NullPointerException e) {
                            throw new NullPointerException("can't open edit page because of missing main activity");
                        }
                        return false;
                    case LINK:
                        if (view.getOwner() != null) {
                            ClipData clipData2 = ClipData.newPlainText("id", Integer.toString(index));
                            View.DragShadowBuilder dragShadowBuilder2 = new View.DragShadowBuilder(null);
                            if (android.os.Build.VERSION.SDK_INT > 23) {
                                view.startDragAndDrop(clipData2, dragShadowBuilder2, null, 0);
                            } else {
                                view.startDrag(clipData2, dragShadowBuilder2, null, 0);
                            }
                            linkManager.setStartTimer(view.getOwner());
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });

        view.requestDraw();
    }

    private void editClick(final View view, int doubleTap){
        switch (doubleTap){
            case 1:
                try {
                    ((MainActivity) view.getContext()).openEditPage(Timer.this);
                } catch (NullPointerException e) {
                    throw new NullPointerException("can't open edit page because of missing main activity");
                }
                break;
            case 2:
                if (timerState == TimerState.FINISHED){
                    TimerManager.stopRingtone();
                }
                TimerManager.deleteTimer(index);
                break;
        }
    }

    private void normalClick(int doubleTap){
        switch (doubleTap){
            case 1:
                if (time.getCurrentSeconds() <= 0) {
                    endClicks++;
                    if (endClicks >= 2) {
                        reset();
                    } else {
                        timerState = TimerState.FINISHED_PAUSE;
                        TimerManager.stopRingtone();
                    }
                } else {
                    if (time.getCurrentSeconds() == time.getTotalSeconds() && linkId >= 0){
                        linkManager.startLinkedTimer(linkId);
                    }else {
                        togglePause();
                    }
                }
                break;
            case 2:
                reset();
                break;
        }
    }

    private void linkClick(int doubleTap){
        switch (doubleTap) {
            case 1:
                break;
            case 2:
                linkManager.removeFromLink(this);
                reset();
                break;
        }
    }

    public void setView(TimerView view) {
        if (view != null) {
            this.view = view;
            addView();
            view.setActive(true);
        }
    }

    public void togglePause(){
        switch (timerState){
            case PAUSED:
                timerState = TimerState.RUNNING;
                break;
            case RUNNING:
                timerState = TimerState.PAUSED;
                break;
            case IDLE:
                timerState = TimerState.RUNNING;
                break;
        }
    }

    public boolean isPlayingAlarm(){
        return timerState == TimerState.FINISHED && endClicks == 0;
    }

    public void reset(){
        if (isPlayingAlarm()){
            TimerManager.stopRingtone();
        }
        time.reset();
        view.reset();
        timerState = TimerState.IDLE;
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

    public int getLinkId() {
        return linkId;
    }

    public void updateLinkInView(){
        view.getLinkIndicator().setLink(linkId);
        view.requestDraw();
        linkManager.linkFromDb(linkId, this);
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;

        if (view != null) {
            view.getLinkIndicator().setLink(linkId);
            view.requestDraw();
            Start.getDbHelper().updateLink(index, linkId);
        }
    }
}
