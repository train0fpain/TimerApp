package com.companyname.timerapp.timerClasses;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.companyname.timerapp.MainActivity;
import com.companyname.timerapp.linking.LinkManager;
import com.companyname.timerapp.modesAndStates.TimerState;
import com.companyname.timerapp.modesAndStates.UserMode;
import com.companyname.timerapp.util.ImprovedOnTouchInterface;
import com.companyname.timerapp.util.ImprovedOnTouchListener;
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

    @SuppressLint("ClickableViewAccessibility")
    private void addView(){
        view.setOwner(this);

        view.setOnTouchListener(new ImprovedOnTouchListener(new ImprovedOnTouchInterface() {

            @Override
            public void onClick() {
                doubleTap++;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (TimerManager.getUserMode()){
                            case EDIT:
                                editClick(doubleTap);
                                break;
                            case LINK:
                                break;
                            case NORMAL:
                                normalClick(doubleTap);
                                break;
                        }
                        doubleTap = 0;
                    }
                }, 300);
            }

            @Override
            public void onLongClick() {
                switch (TimerManager.getUserMode()){
                    case EDIT:
                        break;
                    case NORMAL:
                        try {
                            ((MainActivity) view.getContext()).openEditPage(Timer.this);
                        } catch (NullPointerException e) {
                            Toast.makeText(Start.getContext(), "#Activity-error: Can't open edit page", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case LINK:
                        break;
                }
            }

            @Override
            public void onDrag() {
                switch (TimerManager.getUserMode()){
                    case EDIT:
                        TimerManager.setUserMode(UserMode.LINK);
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
                        break;
                    case NORMAL:
                        break;
                }
            }

            @Override
            public void onLongDrag() {
                switch (TimerManager.getUserMode()){
                    case EDIT:
                        ClipData clipData = ClipData.newPlainText("","");
                        View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);
                        if (android.os.Build.VERSION.SDK_INT > 23) {
                            view.startDragAndDrop(clipData, dragShadowBuilder, view, 0);
                        }else {
                            view.startDrag(clipData, dragShadowBuilder,view, 0);
                        }
                        break;
                    case NORMAL:
                        break;
                }
            }
        }));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        view.requestDraw();
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
                        view.setColorForFinishedPause();
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

    private void editClick(int doubleTap){
        switch (doubleTap) {
            case 1:
                // edit
                try {
                    ((MainActivity) view.getContext()).openEditPage(Timer.this);
                } catch (NullPointerException e) {
                    Toast.makeText(Start.getContext(), "#Activity-error: Can't open edit page", Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                linkManager.removeFromLink(this);
                break;
        }
    }

    public void setView(TimerView view) {
        if (view != null) {
            this.view = view;
            addView();
            view.setActive(true);
            switch (timerState){
                case FINISHED:
                    view.setColorForFinishedRunning();
                    break;
                case FINISHED_PAUSE:
                    view.setColorForFinishedPause();
                    break;
                default:
                    view.reset();
                    break;
            }
        }
    }

    public TimerView getView() {
        return view;
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
        view.setViewToEmpty();
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

    public TimerState getTimerState() {
        return timerState;
    }
}
