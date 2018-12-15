package com.companyname.timerapp.linking;

import android.widget.Toast;

import com.companyname.timerapp.timerClasses.Timer;
import com.companyname.timerapp.util.Start;

import java.util.ArrayList;
import java.util.List;

public class LinkManager {
    private static final LinkManager ourInstance = new LinkManager();

    public static LinkManager getInstance() {
        return ourInstance;
    }


    private LinkLine linkLine;
    private List<Timer>[] links = new List[10];

    private Timer startTimer;

    private LinkManager() {
        initLink();
    }

    public LinkLine getLinkLine() {
        return linkLine;
    }

    public void setLinkLine(LinkLine linkLine) {
        this.linkLine = linkLine;
    }

    public void linkTimer(Timer endTimer){
        if (startTimer != endTimer) {
            if (startTimer == null || endTimer == null) {
                Toast.makeText(Start.getContext(), "#Link-error: Timer reference was not valid.",
                        Toast.LENGTH_LONG).show();
            } else {
                if (endTimer.getLinkId() == -1) {
                    if (startTimer.getLinkId() == -1) {
                        newLink(endTimer, startTimer);
                    } else {
                        addToLink(startTimer.getLinkId(), endTimer);
                    }
                } else {
                    addToLink(endTimer.getLinkId(), startTimer);
                }
            }
        }
        startTimer = null;
    }

    private void newLink(Timer timer1, Timer timer2){
        if (timer1 != null && timer2 != null) {
            int emptyLink = findEmptyLink();
            if (emptyLink >= 0 && emptyLink < links.length) {
                links[emptyLink].add(timer1);
                links[emptyLink].add(timer2);
                timer1.setLinkId(emptyLink);
                timer2.setLinkId(emptyLink);
            }
        }
        startTimer = null;
    }

    private void addToLink(int id, Timer timer){
        if (timer.getLinkId() != id) {
            if (timer.getLinkId() != -1) {
                removeFromLink(timer);
            }
            if (links.length > id && id >= 0) {
                links[id].add(timer);
                timer.setLinkId(id);
            }
        }
    }

    public void linkFromDb(int id, Timer timer){
        if (id >= 0) {
            if (links[id] == null){
                initLink();
            }
            links[id].add(timer);
        }
    }

    public void removeFromLink(Timer timer){
        int linkId= timer.getLinkId();
        if (linkId >= links.length){
            timer.setLinkId(-1);
        }else if (linkId >= 0){
            List<Timer> tempTimers = links[linkId];
            if (tempTimers.size() <= 2) {
                for (Timer tmpTimer : tempTimers) {
                    tmpTimer.setLinkId(-1);
                }
                links[linkId] = new ArrayList<>();
            } else {
                timer.setLinkId(-1);
                tempTimers.remove(timer);
            }
        }
    }

    public void startLinkedTimer(int id){
        for (Timer timer : links[id]) {
            timer.togglePause();
        }
    }

    public void setStartTimer(Timer startTimer) {
        this.startTimer = startTimer;
    }

    private int findEmptyLink(){
        for (int i = 0; i < links.length; i++) {
            if (links[i].isEmpty()){
                return i;
            }
        }
        return -1;
    }

    private void initLink(){
        for (int i = 0; i < links.length; i++) {
            links[i] = new ArrayList<>();
        }
    }
}
