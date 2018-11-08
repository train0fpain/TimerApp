package com.companyname.timerapp.util;

import com.companyname.timerapp.timerClasses.Timer;

import java.util.ArrayList;
import java.util.List;

public class LinkManager {
    private static final LinkManager ourInstance = new LinkManager();

    public static LinkManager getInstance() {
        return ourInstance;
    }


    private LinkLine linkLine;
    private List<List<Timer>> links = new ArrayList<>();

    private Timer startTimer;

    private LinkManager() {
    }

    public LinkLine getLinkLine() {
        return linkLine;
    }

    public void setLinkLine(LinkLine linkLine) {
        this.linkLine = linkLine;
    }

    public void linkTimer(Timer endTimer){
        if (startTimer == endTimer || startTimer == null || endTimer == null){
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

    private void newLink(Timer timer1, Timer timer2){
        int id = links.size();
        links.add(new ArrayList<Timer>());
        links.get(id).add(timer1);
        links.get(id).add(timer2);
        timer1.setLinkId(id);
        timer2.setLinkId(id);
    }

    private void addToLink(int id, Timer timer){
        if (timer.getLinkId() != -1){
            removeFromLink(timer);
        }
        if (links.size() > id && id >= 0) {
            links.get(id).add(timer);
            timer.setLinkId(id);
        }
    }

    public void linkFromDb(int id, Timer timer){
        if (id >= 0) {
            while (links.size() <= id) {
                links.add(new ArrayList<Timer>());
            }
            links.get(id).add(timer);
        }
    }

    public void removeFromLink(Timer timer){
        int linkId= timer.getLinkId();
        if (linkId >= links.size()){
            timer.setLinkId(-1);
        }else {
            List<Timer> tempTimers = links.get(linkId);
            if (tempTimers.size() <= 2) {
                for (Timer tmpTimer : tempTimers) {
                    tmpTimer.setLinkId(-1);
                }
                links.remove(linkId);
            } else {
                timer.setLinkId(-1);
                tempTimers.remove(timer);
            }
        }
    }

    public void startLinkedTimer(int id){
        for (Timer timer : links.get(id)) {
            timer.setPause(false);
        }
    }

    public void setStartTimer(Timer startTimer) {
        this.startTimer = startTimer;
    }
}
